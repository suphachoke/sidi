/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import kku.cs.smil.sidi.WSExecution;

/**
 *
 * @author Administrator
 */
public class _MainFrame implements Runnable {

    private JFrame frame = new JFrame();
    private JPanel panel1 = new JPanel(new BorderLayout());
    private JPanel panel2 = new JPanel(new BorderLayout());
    private JPanel panel3 = new JPanel(new BorderLayout());
    private JFXPanel panel4 = new JFXPanel();
    private JTabbedPane tpane = new JTabbedPane();

    public void run() {
        frame.setPreferredSize(new Dimension(1027, 768));
        frame.setMinimumSize(new Dimension(600, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tpane.addTab("WS & Annotation.", null, panel1);
        tpane.addTab("Global ontology & Mapping", null, panel2);
        tpane.addTab("WS Composition", null, panel3);
        tpane.addTab("WS Execution", null, panel4);
        frame.getContentPane().add(tpane);
        frame.setVisible(true);
        Platform.runLater(new Runnable() {
            public void run() {
                panel4.setScene(createExecutionFrame());
            }
        });
    }
    VBox constBox = new VBox();
    VBox inputBox = new VBox();
    VBox outputBox = new VBox();
    TextField[] inputs = null;
    String[] inputLabels = null;
    String[] outputLabels = null;
    Button invokeBut = new Button("INVOKE SERVICES");
    String selectCP = null;
    String invokeOut = null;
    Label invokeOutLab = new Label();
    ToggleButton tab1 = new ToggleButton("SOAP");
    ToggleButton tab2 = new ToggleButton("RAW DATA");
    ToggleButton tab3 = new ToggleButton("INFERRED DATA");
    HBox tabGBox = new HBox();
    Label tabLabel = new Label();
    ScrollPane scrollP = new ScrollPane();

    private Scene createExecutionFrame() {
        Group root = new Group();
        Scene ret = new Scene(root);

        //เรียกคลาส CompositionReader เพื่อหา array ของ composite processes ทั้งหมดที่มีอยู่ ให้ดูที่ไฟล์ CompositionReader.java
        final CompositionReader comrd = new CompositionReader();
        String[] cps = comrd.getComposition();
        ChoiceBox<String> choices = new ChoiceBox<String>();
        final ObservableList<String> obslist = FXCollections.observableArrayList(cps);
        choices.setItems(obslist);
        choices.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                selectCP = obslist.get(t1.intValue());
                String lst = comrd.getControlConstruct(obslist.get(t1.intValue()));
                String cpDiag = createCompositeSeq(comrd.getSpjStack(lst, new ArrayList()), new Group());
                Label labConst = new Label(cpDiag);
                constBox.getChildren().removeAll(constBox.getChildren());
                constBox.getChildren().add(new Label("COMPOSITION"));
                constBox.getChildren().add(labConst);

                inputLabels = comrd.getCPInput(obslist.get(t1.intValue()));
                inputBox.getChildren().removeAll(inputBox.getChildren());
                inputBox.getChildren().add(new Label("INPUTS"));
                inputs = new TextField[inputLabels.length];
                for (int i = 0; i < inputLabels.length; i++) {
                    Label lab = new Label(inputLabels[i].split("#")[1]);
                    lab.setPrefWidth(100);
                    inputs[i] = new TextField();
                    HBox hb = new HBox();
                    hb.getChildren().addAll(lab, inputs[i]);
                    inputBox.getChildren().add(hb);
                }
                outputLabels = comrd.getCPOutput(obslist.get(t1.intValue()));
                outputBox.getChildren().removeAll(outputBox.getChildren());
                outputBox.getChildren().add(new Label("OUTPUTS"));
                for (int i = 0; i < outputLabels.length; i++) {
                    Label lab = new Label(outputLabels[i].split("#")[1]);
                    outputBox.getChildren().add(lab);
                }
                invokeBut.setVisible(true);
                invokeOutLab.setVisible(false);
                tabGBox.setVisible(false);
                scrollP.setVisible(false);
            }
        });
        invokeBut.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                invokeOut = invokeCP();
                invokeOutLab.setText("RESULTS\r\n" + invokeOut);
                invokeOutLab.setVisible(true);
                tabGBox.setVisible(true);
                scrollP.setVisible(true);
                tabLabel.setText((String) tab1.getUserData());
            }
        });
        ToggleGroup tabG = new ToggleGroup();
        tabG.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                if (t1 == null) {
                    scrollP.setVisible(false);
                } else {
                    scrollP.setVisible(true);
                    tabLabel.setText((String) t1.getUserData());
                }
            }
        });
        tab1.setToggleGroup(tabG);
        tab1.setSelected(true);
        tab2.setToggleGroup(tabG);
        tab3.setToggleGroup(tabG);
        tabGBox.getChildren().addAll(tab1, tab2, tab3);
        VBox choiceBox = new VBox();
        choiceBox.getChildren().addAll(new Label("SELECT COMPOSITE PROCESS"), choices);
        HBox hb1 = new HBox();
        hb1.setSpacing(10);
        hb1.getChildren().addAll(inputBox, outputBox);
        VBox vb1 = new VBox();
        scrollP.setContent(tabLabel);
        scrollP.setPrefHeight(300);
        scrollP.setPrefWidth(600);
        vb1.setSpacing(10);
        vb1.getChildren().addAll(choiceBox, constBox, hb1, invokeBut, invokeOutLab, tabGBox, scrollP);
        invokeBut.setVisible(false);
        invokeOutLab.setVisible(false);
        tabGBox.setVisible(false);
        scrollP.setVisible(false);
        root.getChildren().add(vb1);
        root.setLayoutX(10);
        root.setLayoutY(10);

        return ret;
    }

    private String createCompositeSeq(ArrayList seq, Group group) {
        String out = "";
        for (int i = 0; i < seq.size(); i++) {
            out += (i == 0) ? "o-->[" : "-->[";
            String[] spj = (String[]) seq.get(i);
            for (int j = 0; j < spj.length; j++) {
                out += (j == 0) ? spj[j] : "," + spj[j];
            }
            out += "]-->o";
        }
        return out;
    }

    private String invokeCP() {
        String ret = "";
        String p = selectCP;
        OntModel mod = ModelFactory.createOntologyModel();

        //อ่านไฟล์ ontology ที่ต้องการให้เป็น output data และเป็น ontology ที่ใช้ annotate Web Services
        mod.read("http://202.28.94.50/ontologies/healthcare/general.owl");
        mod.read("file:/G:/work/recent_work/ws/wsdl/local1/local.owl");
        mod.read("file:/G:/work/recent_work/ws/wsdl/local2/local.owl");
        mod.read("file:/G:/work/recent_work/owl/2012-06-13/ii-rules1.owl");
        mod.setNsPrefix("", "http://202.28.94.50/ontologies/healthcare/dat.owl#");

        Individual pp = mod.getIndividual(inputLabels[0]);
        OntClass cls = mod.getOntClass(pp.getPropertyValue(mod.getProperty("http://www.w3.org/2000/01/rdf-schema#domain")).toString());
        IndividualImpl pa = (IndividualImpl) mod.createIndividual("http://202.28.94.50/ontologies/healthcare/dat.owl#_" + inputs[0].getText(), cls);
        for (int i = 0; i < inputs.length; i++) {
            pa.addProperty(mod.getProperty(inputLabels[i]), inputs[i].getText());
        }

        long startTime = System.currentTimeMillis();
        StringWriter sw = new StringWriter();
        try {

            //สร้าง execution engine ให้ดูที่ไฟล์ WSExcution.java
            WSExecution wsexe = new WSExecution(mod, null, null, null);
            wsexe.execute(wsexe.getProcessSequenceInd(p));
            wsexe.getData().write(new FileOutputStream(new File("C:\\data-output.owl")), "RDF/XML-ABBREV");
            long endTime = System.currentTimeMillis();
            System.out.println("Total elapsed time in WS Excution is :" + (endTime - startTime) + "millisec.");
            ret += "Total elapsed time in WS Excution is :" + (endTime - startTime) + "millisec.";
            tab1.setUserData(wsexe.getOper_series());
            wsexe.getData().write(sw);
            tab2.setUserData(sw.toString());
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return ret;
    }
}
