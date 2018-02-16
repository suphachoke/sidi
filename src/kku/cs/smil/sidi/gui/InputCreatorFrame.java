/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kku.cs.smil.sidi.InputCreator;

/**
 *
 * @author Administrator
 */
public class InputCreatorFrame extends Application {

    private FileChooser fc;
    private List<File> ls;

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        fc = new FileChooser();
        final Scene scn = new Scene(root);

        HBox hb1 = new HBox();
        hb1.setSpacing(3);
        final TextField txt1 = new TextField("please select files...");
        txt1.setEditable(false);

        Button btn1 = new Button("Browse");
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                ls = fc.showOpenMultipleDialog(scn.getWindow());
                if (ls != null) {
                    txt1.setText("selected " + ls.size() + " files");
                }
            }
        });
        hb1.getChildren().addAll(txt1, btn1);

        HBox hb2 = new HBox();
        hb2.setSpacing(3);
        final TextField txt2 = new TextField();
        hb2.getChildren().addAll(new Label("Class:"), txt2);

        Button btn3 = new Button("Start");
        btn3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                for (File f : ls) {
                    InputCreator inp = new InputCreator("http://202.28.94.50/ontologies/healthcare/hl7.owl#");
                    inp.GenerateInputFileFromList("file:/" + f.getAbsolutePath().replace("\\", "/"), new String[]{txt2.getText()});
                }
            }
        });

        root.getChildren().addAll(hb1, hb2, btn3);
        stage.setScene(scn);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
