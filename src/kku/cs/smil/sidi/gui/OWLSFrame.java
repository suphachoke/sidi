/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kku.cs.smil.sidi.OWLSFactory;

/**
 *
 * @author BNK
 */
public class OWLSFrame extends Application {
    
    private ProgressBar progress;
    private FileChooser fc;
    private Label mainLab;
    private String[] res;
    private File saveFile;
    
    @Override
    public void start(Stage stage) throws Exception {
        progress = new ProgressBar(0);
        mainLab = new Label();
        mainLab.setPrefWidth(400);
        mainLab.setPrefHeight(200);
        mainLab.setWrapText(true);
        mainLab.setAlignment(Pos.TOP_CENTER);
        fc = new FileChooser();
        VBox root = new VBox();
        final Scene scn = new Scene(root);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        HBox hb1 = new HBox();
        Label lab1 = new Label("OWL-S file :");
        lab1.setPrefWidth(70);
        final TextField txt1 = new TextField();
        txt1.setPrefColumnCount(25);
        Button btn1 = new Button("Browse");
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                File f = fc.showOpenDialog(scn.getWindow());
                if (f != null) {
                    txt1.setText(f.getAbsolutePath());
                }
            }
        });
        hb1.getChildren().addAll(lab1, txt1, btn1);
        
        HBox hb2 = new HBox();
        Label lab2 = new Label("Save to :");
        lab2.setPrefWidth(70);
        final TextField txt2 = new TextField();
        txt2.setPrefColumnCount(25);
        Button btn2 = new Button("Browse");
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                saveFile = fc.showSaveDialog(scn.getWindow());
                if (saveFile != null) {
                    txt2.setText(saveFile.getAbsolutePath());
                }
            }
        });
        
        Button btn3 = new Button("Start");
        btn3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);
                        OWLSFactory owlsf = new OWLSFactory();
                        res = owlsf.parseWS(new String[]{"file:/" + txt1.getText().replace("\\", "/")});
                        updateProgress(0, 1);
                        return true;
                    }
                };
                tsk.workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        mainLab.setText("");
                        try {
                            FileWriter fw = new FileWriter(saveFile);
                            for (String a : res) {
                                mainLab.setText(mainLab.getText() + a + "\r\n");
                                fw.write(a + "\r\n");
                            }
                            fw.close();
                        } catch (IOException ex) {
                            Logger.getLogger(OWLSFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });
        hb2.getChildren().addAll(lab2, txt2, btn2);
        
        root.getChildren().addAll(hb1, hb2, btn3, mainLab, progress);
        root.setAlignment(Pos.TOP_RIGHT);
        
        stage.setScene(scn);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
