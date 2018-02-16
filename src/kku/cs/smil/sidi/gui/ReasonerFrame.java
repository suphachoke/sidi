/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kku.cs.smil.sidi.DataReasoning;

/**
 *
 * @author Administrator
 */
public class ReasonerFrame extends Application {

    private File saveFile;
    private ProgressBar progress;
    private DataReasoning dr;
    private DataReasoning[] drMulti;
    private ToggleGroup tgroup;
    private List<File> files;
    private List<File> rulefiles;

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        final Scene scn = new Scene(root);

        progress = new ProgressBar(0);

        final FileChooser fc = new FileChooser();

        HBox hb1 = new HBox();
        hb1.setSpacing(5);
        final TextField txt1 = new TextField();
        Button btn1 = new Button("Browse");
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                /*File f = fc.showOpenDialog(scn.getWindow());
                 if (f != null) {
                 txt1.setText("file:/" + f.getAbsolutePath().replace("\\", "/"));
                 }*/
                files = fc.showOpenMultipleDialog(scn.getWindow());
                if (files != null) {
                    txt1.setText("selected " + files.size() + " data files.");
                }
            }
        });
        Label lab1 = new Label("Data file: ");
        lab1.setPrefWidth(60);
        hb1.getChildren().addAll(lab1, txt1, btn1);

        HBox hb2 = new HBox();
        hb2.setSpacing(5);
        final TextField txt2 = new TextField();
        Button btn2 = new Button("Browse");
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                rulefiles = fc.showOpenMultipleDialog(scn.getWindow());
                if (rulefiles != null) {
                    txt2.setText("selected " + rulefiles.size() + " rule files.");
                }
            }
        });
        Label lab2 = new Label("Rule files: ");
        lab2.setPrefWidth(60);
        hb2.getChildren().addAll(lab2, txt2, btn2);

        HBox hb3 = new HBox();
        hb3.setSpacing(5);
        final TextField txt3 = new TextField();
        Button btn3 = new Button("Browse");
        btn3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                saveFile = fc.showSaveDialog(scn.getWindow());
                if (saveFile != null) {
                    txt3.setText(saveFile.getAbsolutePath());
                }
            }
        });
        Label lab3 = new Label("Save to: ");
        lab3.setPrefWidth(60);
        hb3.getChildren().addAll(lab3, txt3, btn3);

        HBox hbbtn = new HBox();
        Button btn4 = new Button("Execute");
        btn4.setPrefWidth(60);
        btn4.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);

                        ArrayList<String> file_arr = new ArrayList<String>();
                        for (File f : files) {
                            file_arr.add("file:/" + f.getAbsolutePath().replaceAll("\\\\", "/"));
                        }
                        if (rulefiles != null) {
                            for (File f : rulefiles) {
                                file_arr.add("file:/" + f.getAbsolutePath().replaceAll("\\\\", "/"));
                            }
                        }
                        String[] read_files = new String[file_arr.size()];
                        file_arr.toArray(read_files);

                        dr = new DataReasoning(read_files);
                        if (tgroup.getSelectedToggle() == null || tgroup.getSelectedToggle().getUserData().equals("jess")) {
                            System.out.println("Reasoner: Jess");
                            dr.inferJess();
                        } else {
                            System.out.println("Reasoner: Pellet");
                            dr.inferPellet();
                        }

                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        System.out.println("Total elapsed time: " + dr.getInferTime() + " millisec.");
                        try {
                            FileOutputStream fos = new FileOutputStream(saveFile);
                            dr.getInfMod().write(fos);
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        Button btn5 = new Button("Execute Multiple");
        btn5.setPrefWidth(100);
        btn5.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);

                        ArrayList<DataReasoning> dr_arr = new ArrayList<DataReasoning>();
                        for (File f : files) {
                            ArrayList<String> file_arr = new ArrayList<String>();
                            file_arr.add("file:/" + f.getAbsolutePath().replaceAll("\\\\", "/"));
                            if (rulefiles != null) {
                                for (File ff : rulefiles) {
                                    file_arr.add("file:/" + ff.getAbsolutePath().replaceAll("\\\\", "/"));
                                }
                            }
                            String[] read_files = new String[file_arr.size()];
                            file_arr.toArray(read_files);

                            dr = new DataReasoning(read_files);
                            dr.setDataFile(f.getAbsolutePath());
                            dr_arr.add(dr);
                            if (tgroup.getSelectedToggle().getUserData().equals("jess")) {
                                System.out.println("Reasoner: Jess");
                                dr.inferJess();
                            } else {
                                System.out.println("Reasoner: Pellet");
                                dr.inferPellet();
                            }
                        }
                        drMulti = new DataReasoning[dr_arr.size()];
                        dr_arr.toArray(drMulti);

                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        for (DataReasoning dr : drMulti) {
                            System.out.println("Total elapsed time for " + dr.getDataFile() + ": " + dr.getInferTime() + " millisec.");
                            try {
                                FileOutputStream fos = new FileOutputStream(new File(dr.getDataFile()));
                                dr.getInfMod().write(fos);
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        hbbtn.getChildren().addAll(btn4, btn5);

        HBox hb4 = new HBox();
        hb4.setAlignment(Pos.TOP_RIGHT);
        hb4.getChildren().addAll(progress);

        tgroup = new ToggleGroup();
        RadioButton rd1 = new RadioButton("JESS");
        rd1.setUserData("jess");
        rd1.setToggleGroup(tgroup);
        RadioButton rd2 = new RadioButton("Pellet");
        rd2.setUserData("pellet");
        rd2.setToggleGroup(tgroup);

        root.setPadding(new Insets(10, 10, 10, 10));
        root.setSpacing(5);
        root.getChildren().addAll(hb1, hb2, hb3, rd1, rd2, hbbtn, hb4);
        stage.setScene(scn);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
