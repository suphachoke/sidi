/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import kku.cs.smil.sidi.CompositionFactory;
import kku.cs.smil.sidi.WSComposition;

/**
 *
 * @author Administrator
 */
public class WSCompositonFrame extends JFXPanel {

    private WSReader wsreader;
    private WSComposition wscom_engine;
    private File wsFile;
    private String crFile;
    private String goFile;
    private String sboFile;
    private TextField wsFileTxt;
    private TextField crFileTxt;
    private ListView<String> apList;
    private ObservableList<String> apListData;
    private ListView<String> cpList;
    private TabPane resPane;
    private Tab initTab;
    private Tab runTab;
    private final FileChooser fc;
    private final ProgressBar progress;
    private String[] icc_res;
    private String[] cr_res;
    private String[] xc_res;
    final VBox cpVb = new VBox();

    public WSCompositonFrame() {
        super();

        fc = new FileChooser();
        apListData = FXCollections.observableArrayList();
        wsreader = new WSReader();
        wscom_engine = new WSComposition("http://202.28.94.50/ontologies/composition.owl#", "http://202.28.94.50/ontologies/composition-profile.owl#");
        progress = new ProgressBar();
        progress.setProgress(0.0f);

        Platform.runLater(new Runnable() {
            public void run() {
                setScene(createScene());
            }
        });
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setStyle("-fx-background-color:#c8ddf2;");
        final Scene scn = new Scene(root);
        scn.getStylesheets().add("kku/cs/smil/sidi/gui/style.css");

        wsFileTxt = new TextField();
        wsFileTxt.setDisable(true);
        wsFileTxt.setPrefColumnCount(17);
        Button wsFileBtn = new Button("Browse");
        wsFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                wsFile = fc.showOpenDialog(scn.getWindow());
                if (wsFile != null) {
                    wsFileTxt.setText(wsFile.getAbsolutePath());
                }
            }
        });
        crFileTxt = new TextField();
        crFileTxt.setStyle("-fx-text-alignment:right");
        crFileTxt.setPrefColumnCount(17);
        Button crFileBtn = new Button("Browse");
        crFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                File f = fc.showOpenDialog(scn.getWindow());
                if (f != null) {
                    crFile = "file:/" + f.getAbsolutePath();
                    crFileTxt.setText(crFile.replace("\\", "/"));
                }
            }
        });
        final TextField goFileTxt = new TextField();
        goFileTxt.setPrefColumnCount(17);
        Button goFileBtn = new Button("Browse");
        goFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                File f = fc.showOpenDialog(scn.getWindow());
                if (f != null) {
                    goFile = "file:/" + f.getAbsolutePath();
                    goFileTxt.setText(goFile.replace("\\", "/"));
                }
            }
        });
        final TextField sboFileTxt = new TextField();
        sboFileTxt.setPrefColumnCount(17);
        Button sboFileBtn = new Button("Browse");
        sboFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                File f = fc.showOpenDialog(scn.getWindow());
                if (f != null) {
                    sboFile = "file:/" + f.getAbsolutePath();
                    sboFileTxt.setText(sboFile.replace("\\", "/"));
                }
            }
        });
        GridPane loadGrid = new GridPane();
        loadGrid.setPadding(new Insets(0, 0, 10, 0));
        loadGrid.setHgap(2);
        loadGrid.setVgap(3);
        loadGrid.add(new Label("WSs Registry"), 0, 0);
        loadGrid.add(wsFileTxt, 1, 0);
        loadGrid.add(wsFileBtn, 2, 0);
        loadGrid.add(new Label("   "), 3, 0);
        loadGrid.add(new Label("Composition Rules(CRs)"), 0, 1);
        loadGrid.add(crFileTxt, 1, 1);
        loadGrid.add(crFileBtn, 2, 1);
        loadGrid.add(new Label("Global Ontology"), 4, 0);
        loadGrid.add(goFileTxt, 5, 0);
        loadGrid.add(goFileBtn, 6, 0);
        loadGrid.add(new Label("Semantic Bridge Ontology(SBO)"), 4, 1);
        loadGrid.add(sboFileTxt, 5, 1);
        loadGrid.add(sboFileBtn, 6, 1);
        root.setTop(loadGrid);

        apList = new ListView<String>();
        apList.setPrefWidth(300);
        apList.setMaxHeight(250);
        final VBox apVb = new VBox();
        apVb.getChildren().addAll(new Label("Atomic Process List:"), apList);
        root.setLeft(apVb);

        VBox ctrlVb = new VBox();
        ctrlVb.setMaxWidth(200);
        ctrlVb.setSpacing(5);
        ctrlVb.setPadding(new Insets(7, 20, 0, 20));
        Button loadBtn = new Button("Load WSs");
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.setFont(Font.font("tahoma", FontWeight.BOLD, 10));
        loadBtn.setPrefHeight(24);
        loadBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);
                        if (wsFile != null) {
                            wsreader.readAtomic(wsFile);
                            apListData.clear();
                            apListData.setAll(wsreader.getAtomicProcess());
                            System.out.println("Number of triples: " + wsreader.getTripleSize());
                        }
                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        apList.setItems(apListData);
                        Label l = (Label) apVb.getChildren().get(0);
                        l.setText("Atomic Process List (" + apListData.size() + "):");
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        Button initBtn = new Button("Create ICC");
        initBtn.setMaxWidth(Double.MAX_VALUE);
        initBtn.setFont(Font.font("tahoma", FontWeight.BOLD, 10));
        initBtn.setPrefHeight(24);
        initBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);
                        icc_res = CompositionFactory.initControlConstruct(wsreader.getModUri(), sboFileTxt.getText(), crFileTxt.getText());

                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        ListView<String> lst = (ListView<String>) initTab.getContent();
                        lst.setItems(FXCollections.observableArrayList(icc_res));
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        Button runBtn = new Button("Run CR");
        runBtn.setMaxWidth(Double.MAX_VALUE);
        runBtn.setFont(Font.font("tahoma", FontWeight.BOLD, 10));
        runBtn.setPrefHeight(24);
        runBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);
                        cr_res = wscom_engine.executeRules("file:/C:/sidi/init_composition.owl");
                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        ListView<String> lst = (ListView<String>) runTab.getContent();
                        lst.setItems(FXCollections.observableArrayList(cr_res));
                        resPane.getSelectionModel().select(runTab);
                        try {
                            FileWriter fw = new FileWriter(new File("C:\\sidi\\composition_result.txt"));
                            for (int i = 0; i < cr_res.length; i++) {
                                fw.write(cr_res[i] + "\r\n");
                            }
                            fw.close();
                        } catch (IOException ex) {
                            Logger.getLogger(WSCompositonFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        Button extBtn = new Button("Extract CP");
        extBtn.setMaxWidth(Double.MAX_VALUE);
        extBtn.setFont(Font.font("tahoma", FontWeight.BOLD, 10));
        extBtn.setPrefHeight(24);
        extBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 0);
                        xc_res = wscom_engine.compositionExtraction(sboFile);
                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        try {
                            FileWriter fw = new FileWriter("C:\\sidi\\extraction_result.txt");
                            for (int i = 0; i < xc_res.length; i++) {
                                fw.write(xc_res[i] + "\r\n");
                            }
                            fw.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        wsreader.readComposite(wscom_engine.modCP);
                        cpList.setItems(FXCollections.observableArrayList(wsreader.getCompositeProcess()));
                        Label l = (Label) cpVb.getChildren().get(0);
                        l.setText("Composite Process(CP) Result (" + cpList.getItems().size() + "):");
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        Button saveBtn = new Button("Save Composition");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setFont(Font.font("tahoma", FontWeight.BOLD, 10));
        saveBtn.setPrefHeight(24);
        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 0);
                        try {
                            FileChooser fc = new FileChooser();
                            File f = fc.showSaveDialog(scn.getWindow());
                            FileOutputStream fos = new FileOutputStream(f);
                            wscom_engine.getModCP().write(fos, "RDF/XML-ABBREV");
                            String f2 = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + "-profile.owl";
                            FileOutputStream fos2 = new FileOutputStream(f2);
                            wscom_engine.getModCPProfile().write(fos2);
                            fos2.close();
                        } catch (Exception e) {
                            e.printStackTrace();
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
        ctrlVb.getChildren().addAll(loadBtn, initBtn, runBtn, extBtn, saveBtn);
        root.setCenter(ctrlVb);

        cpList = new ListView<String>();
        cpList.setPrefWidth(300);
        cpList.setMaxHeight(250);

        cpVb.getChildren().addAll(new Label("Composite Process(CP) Result:"), cpList);
        root.setRight(cpVb);

        resPane = new TabPane();
        resPane.setPrefHeight(167);
        initTab = new Tab("Create Initial Control Construct(ICC) Result:");
        ListView<String> initTabRes = new ListView<String>();
        initTabRes.setStyle("-fx-font:9.7 tahoma;");
        initTab.setContent(initTabRes);
        runTab = new Tab("Run Composition Rules(CR) Result:");
        ListView<String> runTabRes = new ListView<String>();
        runTab.setContent(runTabRes);
        resPane.getTabs().addAll(initTab, runTab);
        HBox botHb = new HBox();
        botHb.getChildren().addAll(new Label("Progress: "), progress);
        botHb.setPadding(new Insets(5, 0, 0, 0));
        botHb.setAlignment(Pos.CENTER_RIGHT);
        VBox botVb = new VBox();
        botVb.getChildren().addAll(resPane, botHb);
        root.setBottom(botVb);

        return scn;
    }
}
