/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import kku.cs.smil.sidi.WSExecution;
import kku.cs.smil.sidi.WSExecutionFast2;

/**
 *
 * @author Administrator
 */
public class WSExecutionFrame extends JFXPanel {

    WSReader ws_reader;
    private File wsFile;
    private String cpFile;
    private ObservableList<String> allList;
    private ObservableList<String> compList;
    private ObservableList<String> atomList;
    private ObservableList<String> atomNoInputList;
    private ListView<String> select_proc;
    private AtomicProcessGraph service_graph;
    private CompositeProcessGraph compserv_graph;
    private StackPane service_graph_stack;
    private VBox main_vb;
    private VBox right_vb;
    private GridPane invoke_pane;
    private Button invoke_btn;
    private OntModel data_mod;
    private final FileChooser fc;
    private final ProgressBar progress;
    private String[] invoke_out_str;
    private Tab invoke_out;
    private Tab soap_out;
    private Tab owl_out;
    private WSExecutionFast2 wsexe;
    private String exns;
    private List<File> input_files;
    private TextField iirFileTxt;
    private TextField infile_txt;
    private TextField goFileTxt;

    public WSExecutionFrame() {

        super();

        fc = new FileChooser();
        ws_reader = new WSReader();
        data_mod = ModelFactory.createOntologyModel();
        progress = new ProgressBar();
        progress.setProgress(0.0f);

        service_graph = new AtomicProcessGraph(null, null);
        compserv_graph = new CompositeProcessGraph();
        Platform.runLater(new Runnable() {
            public void run() {
                setScene(createScene());
            }
        });
    }

    private Scene createScene() {
        Group root = new Group();
        final Scene ret = new Scene(root);
        BorderPane main_p = new BorderPane();
        main_p.setPadding(new Insets(10, 10, 10, 10));
        main_p.setStyle("-fx-background-color:#c8ddf2;");
        root.getChildren().add(main_p);

        GridPane selFilesGrid = new GridPane();
        selFilesGrid.setPadding(new Insets(0, 0, 10, 0));
        selFilesGrid.setHgap(2);
        selFilesGrid.setVgap(3);
        final TextField wsRegTxt = new TextField();
        wsRegTxt.setPrefColumnCount(14);
        wsRegTxt.setDisable(true);
        Button wsRegBtn = new Button("Browse");
        wsRegBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                wsFile = fc.showOpenDialog(ret.getWindow());
                if (wsFile != null) {
                    wsRegTxt.setText(wsFile.getAbsolutePath());
                }
            }
        });
        selFilesGrid.add(new Label("WSs Registry"), 0, 0);
        selFilesGrid.add(wsRegTxt, 1, 0);
        selFilesGrid.add(wsRegBtn, 2, 0);
        final TextField cpTxt = new TextField();
        cpTxt.setPrefColumnCount(14);
        cpTxt.setDisable(true);
        Button cpBtn = new Button("Browse");
        cpBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                File f = fc.showOpenDialog(ret.getWindow());
                if (f != null) {
                    String ft = f.getAbsolutePath();
                    cpFile = "file:/" + ft.replace("\\", "/");
                    cpTxt.setText(cpFile);
                }
            }
        });
        selFilesGrid.add(new Label("WSs Composition File"), 0, 1);
        selFilesGrid.add(cpTxt, 1, 1);
        selFilesGrid.add(cpBtn, 2, 1);
        selFilesGrid.add(new Label(" "), 3, 0);
        goFileTxt = new TextField();
        goFileTxt.setPrefColumnCount(14);
        Button goFileBtn = new Button("Browse");
        goFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                File f = fc.showOpenDialog(ret.getWindow());
                if (f != null) {
                    String fstr = "file:/" + f.getAbsolutePath().replace("\\", "/");
                    goFileTxt.setText(fstr);
                }
            }
        });
        selFilesGrid.add(new Label("Global Ontology"), 4, 0);
        selFilesGrid.add(goFileTxt, 5, 0);
        selFilesGrid.add(goFileBtn, 6, 0);
        iirFileTxt = new TextField();
        iirFileTxt.setPrefColumnCount(14);
        Button iirFileBtn = new Button("Browse");
        iirFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                File f = fc.showOpenDialog(ret.getWindow());
                if (f != null) {
                    String fstr = "file:/" + f.getAbsolutePath().replace("\\", "/");
                    iirFileTxt.setText(fstr);
                }
            }
        });
        selFilesGrid.add(new Label("Information Integration Rules(IIR)"), 4, 1);
        selFilesGrid.add(iirFileTxt, 5, 1);
        selFilesGrid.add(iirFileBtn, 6, 1);
        Button loadD = new Button("Load Data");
        loadD.setPrefHeight(42);
        loadD.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 10);
                        allList = FXCollections.observableArrayList();
                        if (wsFile != null) {
                            System.out.println("Reading WSs registry file.");
                            ws_reader.readAtomic(wsFile);
                            atomList = FXCollections.observableArrayList(ws_reader.getAtomicProcess());
                            atomNoInputList = FXCollections.observableArrayList(ws_reader.getAtomicProcessNoInput());
                            allList.addAll(ws_reader.getAtomicProcess());
                        }
                        if (cpFile != null && !cpFile.equals("")) {
                            System.out.println("Reading Composite Process data.");
                            ws_reader.readComposite(cpFile);
                            compList = FXCollections.observableArrayList(ws_reader.getCompositeProcess());
                            allList.addAll(ws_reader.getCompositeProcess());
                        }
                        if (goFileTxt != null && !goFileTxt.getText().equals("")) {
                            System.out.println("Reading Global Ontology file.");
                            //data_mod.read(goFileTxt.getText());
                            ws_reader.setGlobalOnt(goFileTxt.getText());
                        }
                        if (iirFileTxt != null && !iirFileTxt.getText().equals("")) {
                        }
                        updateProgress(0, 10);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        select_proc.setItems(allList);
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        selFilesGrid.add(loadD, 7, 0, 1, 2);
        main_p.setTop(selFilesGrid);

        main_vb = new VBox();
        main_vb.setSpacing(2);
        main_vb.setPadding(new Insets(0, 10, 10, 0));
        final ChoiceBox<String> select_stype = new ChoiceBox<String>();
        final ObservableList<String> style_list = FXCollections.observableArrayList(new String[]{"Composite Process", "Atomic Process", "Atomic Process with No Input"});
        select_stype.setItems(style_list);
        select_stype.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if (t1.equals("Composite Process")) {
                    select_proc.setItems(compList);
                    invoke_btn.setDisable(false);
                } else if (t1.equals("Atomic Process with No Input")) {
                    select_proc.setItems(atomNoInputList);
                    invoke_btn.setDisable(true);
                } else {
                    select_proc.setItems(atomList);
                    invoke_btn.setDisable(false);
                }
            }
        });
        select_proc = new ListView<String>();
        select_proc.setPrefHeight(240);
        select_proc.setPrefWidth(300);
        select_proc.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                service_graph_stack.getChildren().clear();
                if (t1 != null) {
                    if (ws_reader.getInd(t1).getRDFType().getURI().equals("http://202.28.94.50/owl-s/1.1/mod/Process.owl#AtomicProcess")) {
                        service_graph.setRootNd(ws_reader.getInd(t1));
                        service_graph.setModel(ws_reader.getMod2());
                        service_graph.redraw();
                        service_graph_stack.getChildren().add(service_graph);
                    } else {
                        compserv_graph.setRootNd(ws_reader.getInd(t1));
                        compserv_graph.setModel(ws_reader.getMod());
                        compserv_graph.setModel2(ws_reader.getMod2());
                        compserv_graph.redraw();
                        service_graph_stack.getChildren().add(compserv_graph);
                    }
                }
            }
        });
        HBox sel_stype_box = new HBox();
        sel_stype_box.setSpacing(2);
        sel_stype_box.getChildren().addAll(new Label("Service type:"), select_stype);
        main_vb.getChildren().addAll(sel_stype_box, select_proc);

        right_vb = new VBox();
        TextField isearch_txt = new TextField();
        isearch_txt.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                TextField obj = (TextField) t.getSource();
                String[] res = null;
                if (select_stype.getSelectionModel().getSelectedItem().equals("Atomic Process")) {
                    res = ws_reader.queryAPByInput(obj.getText());
                } else {
                    res = ws_reader.queryCPByInput(obj.getText());
                }
                if (res != null) {
                    ObservableList lst = FXCollections.observableArrayList(res);
                    select_proc.setItems(lst);
                }
            }
        });
        HBox searchHb = new HBox();
        searchHb.setSpacing(2);
        searchHb.getChildren().addAll(new Label("Search WSs:"), isearch_txt);
        invoke_pane = new GridPane();
        invoke_pane.setPadding(new Insets(5, 0, 0, 0));
        invoke_pane.setVgap(5);
        invoke_pane.setHgap(5);
        infile_txt = new TextField();
        infile_txt.setPrefColumnCount(19);
        infile_txt.setDisable(true);
        Button infile_btn = new Button("Single File");
        infile_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);
                        File f = fc.showOpenDialog(ret.getWindow());
                        if (f != null) {
                            String sf = "file:/" + f.getAbsolutePath().replace("\\", "/");
                            infile_txt.setText(sf);
                            data_mod.read(sf);
                            exns = f.getName();
                        }
                        updateProgress(0, 1);
                        return true;
                    }
                };
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        Button infilemulti_btn = new Button("Multi. Files");
        infilemulti_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);
                        input_files = fc.showOpenMultipleDialog(ret.getWindow());
                        infile_txt.setText("Multiple File (" + input_files.size() + ")");
                        updateProgress(0, 1);
                        return true;
                    }
                };
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        invoke_btn = new Button("Invoke Web Services");
        invoke_btn.setFont(javafx.scene.text.Font.font("Tahoma", FontWeight.BOLD, 10));
        invoke_btn.setPrefHeight(24);
        invoke_btn.setPrefWidth(150);
        //invoke_btn.setDisable(true);
        invoke_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);
                        if (select_stype.getSelectionModel().getSelectedItem().equals("Atomic Process")) {
                            if (infile_txt.getText().contains("Multiple File")) {
                                invoke_out_str = invokeMultiAP(select_proc.getSelectionModel().getSelectedItems().get(0), input_files);
                            } else {
                                invoke_out_str = invokeAP(select_proc.getSelectionModel().getSelectedItems().get(0));
                            }
                        } else if (select_stype.getSelectionModel().getSelectedItem().equals("Composite Process")) {
                            if (infile_txt.getText().contains("Multiple File")) {
                                invoke_out_str = invokeMultiCP(select_proc.getSelectionModel().getSelectedItems().get(0), input_files);
                            } else {
                                invoke_out_str = invokeCP(select_proc.getSelectionModel().getSelectedItems().get(0));
                            }
                        }
                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        ListView tab1 = (ListView) invoke_out.getContent();
                        tab1.setItems(FXCollections.observableArrayList(invoke_out_str));
                        if (wsexe != null) {
                            String[] soapAr = wsexe.getOper_series().split("\r\n");
                            ListView tab2 = (ListView) soap_out.getContent();
                            tab2.setItems(FXCollections.observableArrayList(soapAr));
                            StringWriter sw = new StringWriter();
                            wsexe.getData().write(sw);
                            String[] owlAr = sw.toString().split("\r\n");
                            ListView tab3 = (ListView) owl_out.getContent();
                            tab3.setItems(FXCollections.observableArrayList(owlAr));
                        }
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        invoke_pane.add(new Label("Select Input Data:"), 0, 0);
        invoke_pane.add(infile_txt, 1, 0);
        invoke_pane.add(infile_btn, 2, 0);
        invoke_pane.add(infilemulti_btn, 3, 0);
        invoke_pane.add(invoke_btn, 0, 1, 4, 1);
        ScrollPane service_graph_pane = new ScrollPane();
        service_graph_pane.setPrefWidth(460);
        service_graph_pane.setPrefHeight(185);
        service_graph_stack = new StackPane();
        service_graph_stack.setPrefWidth(450);
        service_graph_stack.setPrefHeight(175);
        service_graph_stack.getChildren().add(service_graph);
        service_graph_pane.setContent(service_graph_stack);
        right_vb.getChildren().addAll(searchHb, service_graph_pane, invoke_pane);

        TabPane outtab_pane = new TabPane();
        outtab_pane.setPrefHeight(176);
        invoke_out = new Tab("Invoke WSs Result:");
        ListView<String> invoke_out_tbl = new ListView<String>();
        invoke_out.setContent(invoke_out_tbl);
        soap_out = new Tab("SOAP Result:");
        ListView<String> soap_out_tbl = new ListView<String>();
        soap_out.setContent(soap_out_tbl);
        owl_out = new Tab("OWL Result:");
        ListView<String> owl_out_tbl = new ListView<String>();
        owl_out.setContent(owl_out_tbl);
        outtab_pane.getTabs().addAll(invoke_out, soap_out, owl_out);
        Button saveowl_btn = new Button("SAVE OWL");
        saveowl_btn.setFont(javafx.scene.text.Font.font("Tahoma", FontWeight.BOLD, 10));
        saveowl_btn.setPrefHeight(24);
        saveowl_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);
                        File f = fc.showSaveDialog(ret.getWindow());
                        if (f != null) {
                            FileOutputStream fos = new FileOutputStream(f);
                            if (wsexe != null && wsexe.getData() != null) {
                                wsexe.getData().write(fos);
                            }
                            fos.close();
                        }
                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        HBox botHb = new HBox();
        botHb.getChildren().addAll(saveowl_btn, new Label("Progress: "), progress);
        botHb.getChildren().get(1).setTranslateX(550);
        botHb.getChildren().get(2).setTranslateX(550);
        botHb.getChildren().get(2).setTranslateY(2);
        VBox out_vb = new VBox();
        out_vb.setSpacing(5);
        out_vb.getChildren().addAll(outtab_pane, botHb);

        main_p.setCenter(main_vb);
        main_p.setRight(right_vb);
        main_p.setBottom(out_vb);

        return ret;
    }

    public String[] invokeAP(String s) {
        ArrayList retArr = new ArrayList();
        long startTime = System.currentTimeMillis();
        //StringWriter sw = new StringWriter();
        try {
            //FileOutputStream fos = new FileOutputStream(data_file);
            //สร้าง execution engine ให้ดูที่ไฟล์ WSExcution.java
            Ontology defOn = data_mod.getOntology("http://202.28.94.50/ontologies/healthcare/data" + infile_txt.getText().substring(infile_txt.getText().lastIndexOf("/")));
            Ontology on = (defOn == null) ? data_mod.createOntology("http://202.28.94.50/ontologies/healthcare/data" + infile_txt.getText().substring(infile_txt.getText().lastIndexOf("/")))
                    : defOn;
            if (iirFileTxt.getText() != null && !iirFileTxt.getText().equals("")) {
                on.addImport(new ResourceImpl(iirFileTxt.getText()));
            }
            wsexe = new WSExecutionFast2(data_mod, null, ws_reader.getMod2(), exns);
            wsexe.executeAtomic(s);
            //wsexe.getData().write(fos, "RDF/XML-ABBREV");
            long endTime = System.currentTimeMillis();
            retArr.add("Total elapsed time in WS Excution is :" + (endTime - startTime) + "millisec.");
            //wsexe.getData().write(sw);
            //fos.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        String[] ret = new String[retArr.size()];
        retArr.toArray(ret);
        return ret;
    }

    public String[] invokeCP(String s) {
        ArrayList retArr = new ArrayList();
        long startTime = System.currentTimeMillis();
        //StringWriter sw = new StringWriter();
        try {
            //FileOutputStream fos = new FileOutputStream(data_file);
            //สร้าง execution engine ให้ดูที่ไฟล์ WSExcution.java
            Ontology on = data_mod.createOntology("http://202.28.94.50/ontologies/healthcare/data" + infile_txt.getText().substring(infile_txt.getText().lastIndexOf("/")));
            if (iirFileTxt.getText() != null && !iirFileTxt.getText().equals("")) {
                on.addImport(new ResourceImpl(iirFileTxt.getText()));
            }
            wsexe = new WSExecutionFast2(data_mod, null, ws_reader.getMod(), exns);
            wsexe.execute(wsexe.getProcessSequenceInd(s));
            //wsexe.getData().write(fos, "RDF/XML-ABBREV");
            long endTime = System.currentTimeMillis();
            retArr.add("Total elapsed time in WS Excution is :" + (endTime - startTime) + "millisec.");
            //wsexe.getData().write(sw);
            //fos.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        String[] ret = new String[retArr.size()];
        retArr.toArray(ret);
        return ret;
    }

    public String[] invokeMultiAP(String s, List<File> fs) {
        ArrayList retArr = new ArrayList();
        int count = 1;
        for (File file : fs) {
            try {
                long startTime = System.currentTimeMillis();
                OntModel mod = ModelFactory.createOntologyModel();
                mod.read("file:/" + file.getAbsolutePath());
                Ontology on = mod.createOntology("http://202.28.94.50/ontologies/healthcare/data/" + file.getName());
                if (iirFileTxt.getText() != null && !iirFileTxt.getText().equals("")) {
                    on.addImport(new ResourceImpl(iirFileTxt.getText()));
                }
                OntModel mod2 = ModelFactory.createOntologyModel();
                mod2.add(ws_reader.getMod2());
                WSExecutionFast2 wse = new WSExecutionFast2(mod, null, mod2, file.getName());
                wse.executeAtomic(s);
                FileOutputStream fos = new FileOutputStream(file);
                wse.getData().write(fos);
                fos.close();
                long endTime = System.currentTimeMillis();
                retArr.add("Total elapsed time in WS Excution round(" + count + ") is :" + (endTime - startTime) + "millisec.");
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String[] ret = new String[retArr.size()];
        retArr.toArray(ret);
        return ret;
    }

    public String[] invokeMultiCP(String s, List<File> fs) {
        ArrayList retArr = new ArrayList();
        int count = 1;
        for (File file : fs) {
            try {
                long startTime = System.currentTimeMillis();
                OntModel mod = ModelFactory.createOntologyModel();
                mod.read("file:/" + file.getAbsolutePath());
                Ontology on = mod.createOntology("http://202.28.94.50/ontologies/healthcare/data/" + file.getName());
                if (iirFileTxt.getText() != null && !iirFileTxt.getText().equals("")) {
                    on.addImport(new ResourceImpl(iirFileTxt.getText()));
                }
                OntModel mod2 = ModelFactory.createOntologyModel();
                /*String profile = cpFile.substring(0, cpFile.lastIndexOf(".")) + "-profile.owl";
                 mod2.read(cpFile);
                 mod2.read(profile);
                 mod2.read(goFileTxt.getText());
                 for(String m:ws_reader.getModUri()){
                 mod2.read(m);
                 }
                 for(String o:ws_reader.getOntoUri()){
                 mod2.read(o);
                 }*/
                StringWriter sw = new StringWriter();
                ws_reader.getMod().write(sw);
                mod2.read(new StringReader(sw.toString()), null);
                WSExecutionFast2 wse = new WSExecutionFast2(mod, null, mod2, file.getName());
                wse.execute(wse.getProcessSequenceInd(s));
                FileOutputStream fos = new FileOutputStream(file);
                wse.getData().write(fos);
                fos.close();
                long endTime = System.currentTimeMillis();
                retArr.add("Total elapsed time in WS Excution round(" + count + ") is :" + (endTime - startTime) + "millisec.");
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String[] ret = new String[retArr.size()];
        retArr.toArray(ret);
        return ret;
    }
}
