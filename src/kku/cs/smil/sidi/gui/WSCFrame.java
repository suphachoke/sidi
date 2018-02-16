/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import java.io.File;
import javafx.application.Application;
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
import kku.cs.smil.sidi.WSDL2SWS;

/**
 *
 * @author BNK
 */
public class WSCFrame extends Application {

    private FileChooser fc;
    private ProgressBar progress;

    @Override
    public void start(Stage stage) throws Exception {
        fc = new FileChooser();
        progress = new ProgressBar(0);
        VBox root = new VBox();
        final Scene scn = new Scene(root);
        root.setSpacing(5);
        root.setPadding(new Insets(10));

        HBox hb1 = new HBox();
        hb1.setSpacing(5);
        Label lab1 = new Label("XML file :");
        lab1.setPrefWidth(70);
        final TextField txt1 = new TextField();
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

        Button btn3 = new Button("Start");
        btn3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Task tsk = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        updateProgress(-1, 1);

                        WSDL2SWS w = new WSDL2SWS();
                        w.processWSC(txt1.getText());

                        updateProgress(0, 1);
                        return true;
                    }
                };
                progress.progressProperty().unbind();
                progress.progressProperty().bind(tsk.progressProperty());
                new Thread(tsk).start();
            }
        });

        root.getChildren().addAll(hb1, btn3, progress);
        root.setAlignment(Pos.TOP_RIGHT);

        stage.setScene(scn);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
