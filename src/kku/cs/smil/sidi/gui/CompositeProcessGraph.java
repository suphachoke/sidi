/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.Iterator;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Administrator
 */
public class CompositeProcessGraph extends Group {

    private Individual rootNd;
    private OntModel mod;
    private OntModel mod2;
    private double border;

    public CompositeProcessGraph() {
        super();

        border = 25;
    }

    public void redraw() {
        this.getChildren().clear();
        if (mod != null && rootNd != null) {
            Individual seqN = mod.getIndividual(rootNd.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#composedOf")).toString());
            Individual listN = mod.getIndividual(seqN.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components")).toString());
            double[] wh = recursiveDraw(listN, 0, 0);
            Rectangle bound = new Rectangle(wh[0], wh[1]);
            bound.setFill(Color.TRANSPARENT);
            bound.setStroke(Color.BLUE);
            bound.getStrokeDashArray().add(3d);
            this.getChildren().add(bound);
        }
    }

    public void setRootNd(Individual rootNd) {
        this.rootNd = rootNd;
    }

    public void setModel(OntModel mod) {
        this.mod = mod;
    }

    public void setModel2(OntModel mod) {
        this.mod2 = mod;
    }

    private double[] recursiveDraw(Individual list, double leftIndex, double topIndex) {
        Individual spjN = mod.getIndividual(list.getPropertyValue(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first")).toString());
        Individual cspjN = mod.getIndividual(spjN.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components")).toString());
        Iterator it = cspjN.listPropertyValues(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"));
        double newTop = 0;
        double newleftIndex = 0;
        while (it.hasNext()) {
            RDFNode n = (RDFNode) it.next();
            Individual indP = mod.getIndividual(n.toString());
            System.out.println(indP.getURI());
            Individual ind = mod2.getIndividual(indP.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#process")).toString());
            AtomicProcessGraph aG = new AtomicProcessGraph(ind, mod2);
            aG.redraw();
            aG.setLayoutX(leftIndex + border);
            aG.setLayoutY(newTop + border);
            this.getChildren().add(aG);
            newTop += aG.getHeight();
            newleftIndex = (aG.getWidth() > newleftIndex) ? aG.getWidth() : newleftIndex;
        }
        leftIndex += newleftIndex;
        topIndex = (newTop > topIndex) ? newTop : topIndex;
        RDFNode nextNd = list.getPropertyValue(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest"));
        double[] ret = new double[2];
        ret[0] = leftIndex;
        ret[1] = topIndex;
        if (nextNd != null) {
            Individual nextSeq = mod.getIndividual(nextNd.toString());
            System.out.println(nextSeq.getURI());
            ret = recursiveDraw(nextSeq, leftIndex, topIndex);
        }
        return ret;
    }
}
