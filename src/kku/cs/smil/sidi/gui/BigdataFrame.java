/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import java.io.File;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kku.cs.smil.sidi.bigdata.Connector;
import kku.cs.smil.sidi.bigdata.OWLParser;

/**
 *
 * @author Administrator
 */
public class BigdataFrame extends Application {

    private ProgressBar progress;
    private String[] res;
    private List<File> files;

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        final Scene scn = new Scene(root);
        progress = new ProgressBar(0);

        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setSpacing(5);

        final TextField txt_host = new TextField();
        HBox hb0 = new HBox();
        hb0.getChildren().addAll(new Label("Host IP: "), txt_host);

        final TextField txt = new TextField();
        txt.setEditable(false);
        txt.setPrefColumnCount(30);

        Button btn1 = new Button("Browse");
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                FileChooser fc = new FileChooser();
                /*File f = fc.showOpenDialog(scn.getWindow());
                 if (f != null) {
                 txt.setText("file:/" + f.getAbsolutePath().replace("\\", "/"));
                 }*/
                files = fc.showOpenMultipleDialog(scn.getWindow());
                if (files != null) {
                    txt.setText("Multiple files...");
                }
            }
        });
        HBox hb = new HBox();
        hb.getChildren().addAll(new Label("Select File: "), txt, btn1);

        Button btn2 = new Button("Commit");
        btn2.setPrefWidth(100);
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                if (txt.getText() != null && !txt.getText().equals("") && !txt_host.getText().equals("")) {
                    Task tsk = new Task() {
                        @Override
                        protected Object call() {
                            updateProgress(-1, 1);

                            for (File f : files) {
                                try {
                                    res = OWLParser.listStatements("file:/" + f.getAbsolutePath().replaceAll("\\\\", "/"), "");
                                    System.out.println("Size: " + res.length + " triples");
                                    Connector bcon = new Connector(txt_host.getText(), 8080);
                                    int round = (res.length / 100);
                                    for (int i = 0; i <= round; i++) {
                                        int offset = (i * 100);
                                        ArrayList<String> arr = new ArrayList<String>();
                                        int max = (res.length > (offset + 100)) ? 100 : (res.length - offset);
                                        for (int j = 0; j < max; j++) {
                                            arr.add(res[offset + j]);
                                        }
                                        String[] send = new String[arr.size()];
                                        arr.toArray(send);
                                        System.out.println("Sending round: " + (i + 1) + "/statement offset: " + offset + ", 100");
                                        bcon.insertData(send);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
            }
        });

        HBox hb2 = new HBox();
        hb2.setAlignment(Pos.TOP_RIGHT);
        hb2.getChildren().addAll(new Label("progress: "), progress);

        root.getChildren().addAll(hb0, hb, btn2, hb2);
        stage.setScene(scn);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
