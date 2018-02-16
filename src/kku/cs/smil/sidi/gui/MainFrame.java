/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Administrator
 */
public class MainFrame implements Runnable {

    private JFrame frame = new JFrame();
    private JPanel panel1 = new JPanel(new BorderLayout());
    private JPanel panel2 = new JPanel(new BorderLayout());
    private WSCompositonFrame panel3 = new WSCompositonFrame();
    private WSExecutionFrame panel4 = new WSExecutionFrame();
    private JTabbedPane tpane = new JTabbedPane();

    public void run() {
        frame.setResizable(false);
        frame.setTitle("SIDI - Semantic Interoperability for Data Integration");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        tpane.addTab("WS & Annotation", null, panel1);
        tpane.addTab("Ontology & Mapping", null, panel2);
        tpane.addTab("WS Composition", null, panel3);
        tpane.addTab("WS Execution", null, panel4);
        frame.getContentPane().add(tpane);
        frame.setVisible(true);
    }
}
