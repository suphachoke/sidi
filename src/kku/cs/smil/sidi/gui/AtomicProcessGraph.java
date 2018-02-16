/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 *
 * @author Administrator
 */
public class AtomicProcessGraph extends Group {

    private Individual rootNd;
    private OntModel model;
    double outsideX;
    double outsideY;
    double outsideW;
    double outsideH;
    double insideX;
    double insideY;
    double insideW;
    double insideH;
    double pSpaceIn;
    double pSpace;
    double pRadial;
    double pOutStartXIn;
    double pOutEndXIn;
    double pInStartXIn;
    double pInEndXIn;
    double pOutStartXOut;
    double pOutEndXOut;
    double pInStartXOut;
    double pInEndXOut;
    int cir_count;
    ArrayList<String> outArr;

    public AtomicProcessGraph(Individual ind, OntModel mod) {
        super();

        model = mod;
        rootNd = ind;

        outsideX = 0;
        outsideY = 0;
    }

    public void redraw() {
        resizeShape();
        getChildren().clear();
        if (rootNd != null) {
            Rectangle outRec = new Rectangle(outsideW, outsideH);
            outRec.setFill(Color.TRANSPARENT);
            outRec.setStroke(Color.BLACK);
            outRec.getStrokeDashArray().add(3d);
            getChildren().add(outRec);
            if (rootNd.getRDFType().getURI().equals("http://202.28.94.50/owl-s/1.1/mod/Process.owl#AtomicProcess")) {
                Rectangle rec = new Rectangle(insideW, insideH, Color.BROWN);
                rec.setLayoutX(insideX);
                rec.setLayoutY(insideY);
                Label lab = new Label(rootNd.getURI().split("#")[1]);
                lab.setTextFill(Color.WHITE);
                lab.setLayoutX(insideX);
                lab.setLayoutY(insideY);
                lab.setPrefWidth(insideW);
                lab.setPrefHeight(insideH);
                lab.setWrapText(true);
                lab.setAlignment(Pos.CENTER);
                double outStartXpos = pOutStartXOut + ((pOutEndXOut - pOutStartXOut) / 2) - (((cir_count / 2) * pSpace) / 2);
                double outStartXposI = pOutStartXIn + ((pOutEndXIn - pOutStartXIn) / 2) - (((cir_count / 2) * pSpaceIn) / 2);
                double lineAngle = ((insideY - outsideY - pRadial) / (Math.round((double) cir_count / 2) + 1));
                for (int i = 0; i < (int) (cir_count / 2); i++) {
                    String str = outArr.get(i);
                    double labpos = (i % 2 != 0) ? (1) : -((pRadial + 10));
                    Circle cir = new Circle(outStartXpos + (i * pSpace), outsideY, pRadial, Color.YELLOW);
                    cir.setOpacity(0.85);
                    Label clab = new Label(str);
                    clab.setTextFill(Color.BROWN);
                    clab.setFont(Font.font("tahoma", 8));
                    clab.setTextOverrun(OverrunStyle.LEADING_WORD_ELLIPSIS);
                    clab.setTooltip(new Tooltip(str));
                    clab.setPrefWidth(pSpace * 1.8);
                    clab.setLayoutX(outStartXpos + (i * pSpace) - 20);
                    clab.setLayoutY(outsideY + labpos);
                    Polyline pl = new Polyline();
                    pl.getPoints().addAll(new Double[]{
                        outStartXposI + (i * pSpaceIn), insideY,
                        outStartXposI + (i * pSpaceIn), (outsideY + (pRadial) + ((i + 1) * lineAngle)),
                        outStartXpos + (i * pSpace), (outsideY + (pRadial) + ((i + 1) * lineAngle)),
                        outStartXpos + (i * pSpace), outsideY + pRadial,
                        (outStartXpos + (i * pSpace)) - 3, (outsideY + pRadial) + 3,
                        outStartXpos + (i * pSpace), outsideY + pRadial,
                        (outStartXpos + (i * pSpace)) + 3, (outsideY + pRadial) + 3
                    });
                    getChildren().addAll(cir, clab, pl);
                }
                for (int i = (int) (cir_count / 2); i < cir_count; i++) {
                    String str = outArr.get(i);
                    double labpos = (i % 2 != 0) ? (pRadial) : -(pRadial + 4);
                    int j = i - (int) (cir_count / 2);
                    Circle cir = new Circle(outStartXpos + (j * pSpace), outsideY + outsideH, pRadial, Color.YELLOW);
                    cir.setOpacity(0.85);
                    Label clab = new Label(str);
                    clab.setTextFill(Color.BROWN);
                    clab.setFont(Font.font("tahoma", 8));
                    clab.setTextOverrun(OverrunStyle.LEADING_WORD_ELLIPSIS);
                    clab.setTooltip(new Tooltip(str));
                    clab.setPrefWidth(pSpace * 1.8);
                    clab.setLayoutX(outStartXpos + (j * pSpace) - 20);
                    clab.setLayoutY(outsideY + outsideH + labpos);
                    Polyline pl = new Polyline();
                    pl.getPoints().addAll(new Double[]{
                        outStartXposI + (j * pSpaceIn), insideY + insideH,
                        outStartXposI + (j * pSpaceIn), (outsideY + outsideH - (pRadial) - ((j + 1) * lineAngle)),
                        outStartXpos + (j * pSpace), (outsideY + outsideH - (pRadial) - ((j + 1) * lineAngle)),
                        outStartXpos + (j * pSpace), outsideY + outsideH - pRadial,
                        (outStartXpos + (j * pSpace)) - 3, (outsideY + outsideH - pRadial) - 3,
                        outStartXpos + (j * pSpace), outsideY + outsideH - pRadial,
                        (outStartXpos + (j * pSpace)) + 3, (outsideY + outsideH - pRadial) - 3
                    });
                    getChildren().addAll(cir, clab, pl);
                }
                Iterator in_nds = rootNd.listPropertyValues(model.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasInput"));
                cir_count = 0;
                ArrayList<String> inArr = new ArrayList();
                while (in_nds.hasNext()) {
                    Individual out = (Individual) model.getIndividual(in_nds.next().toString());
                    RDFNode pt = out.getPropertyValue(model.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterType"));
                    if (pt != null) {
                        inArr.add(pt.toString().split("#")[1]);
                        cir_count++;
                    }
                }
                double inStartXpos = pInStartXOut + ((pInEndXOut - pInStartXOut) / 2) - (((cir_count / 2) * pSpace) / 2);
                double inStartXposI = pInStartXIn + ((pInEndXIn - pInStartXIn) / 2) - (((cir_count / 2) * pSpaceIn) / 2);
                lineAngle = ((insideY - outsideY - pRadial) / (Math.round((double) cir_count / 2) + 1));
                for (int i = 0; i < (int) (cir_count / 2); i++) {
                    String str = inArr.get(i);
                    double labpos = (i % 2 != 0) ? (1) : -((pRadial + 10));
                    Circle cir = new Circle(inStartXpos + (i * pSpace), outsideY, pRadial, Color.YELLOW);
                    cir.setOpacity(0.85);
                    Label clab = new Label(str);
                    clab.setTextFill(Color.BROWN);
                    clab.setFont(Font.font("tahoma", 8));
                    clab.setTextOverrun(OverrunStyle.LEADING_WORD_ELLIPSIS);
                    clab.setTooltip(new Tooltip(str));
                    clab.setPrefWidth(pSpace * 1.8);
                    clab.setLayoutX(inStartXpos + (i * pSpace) - 20);
                    clab.setLayoutY(outsideY + labpos);
                    Polyline pl = new Polyline();
                    pl.getPoints().addAll(new Double[]{
                        inStartXposI + (i * pSpaceIn) - 3, insideY - 3,
                        inStartXposI + (i * pSpaceIn), insideY,
                        inStartXposI + (i * pSpaceIn) + 3, insideY - 3,
                        inStartXposI + (i * pSpaceIn), insideY,
                        inStartXposI + (i * pSpaceIn), (outsideY + (pRadial) + ((i + 1) * lineAngle)),
                        inStartXpos + (i * pSpace), (outsideY + (pRadial) + ((i + 1) * lineAngle)),
                        inStartXpos + (i * pSpace), outsideY + pRadial
                    });
                    getChildren().addAll(cir, clab, pl);
                }
                for (int i = (int) (cir_count / 2); i < cir_count; i++) {
                    String str = inArr.get(i);
                    double labpos = (i % 2 != 0) ? (pRadial) : -(pRadial + 4);
                    int j = i - (int) (cir_count / 2);
                    Circle cir = new Circle(inStartXpos + (j * pSpace), outsideY + outsideH, pRadial, Color.YELLOW);
                    cir.setOpacity(0.85);
                    Label clab = new Label(str);
                    clab.setTextFill(Color.BROWN);
                    clab.setFont(Font.font("tahoma", 8));
                    clab.setTextOverrun(OverrunStyle.LEADING_WORD_ELLIPSIS);
                    clab.setTooltip(new Tooltip(str));
                    clab.setPrefWidth(pSpace * 1.8);
                    clab.setLayoutX(inStartXpos + (j * pSpace) - 20);
                    clab.setLayoutY(outsideY + outsideH + labpos);
                    Polyline pl = new Polyline();
                    pl.getPoints().addAll(new Double[]{
                        inStartXposI + (j * pSpaceIn) - 3, insideY + insideH + 3,
                        inStartXposI + (j * pSpaceIn), insideY + insideH,
                        inStartXposI + (j * pSpaceIn) + 3, insideY + insideH + 3,
                        inStartXposI + (j * pSpaceIn), insideY + insideH,
                        inStartXposI + (j * pSpaceIn), (outsideY + outsideH - (pRadial) - ((j + 1) * lineAngle)),
                        inStartXpos + (j * pSpace), (outsideY + outsideH - (pRadial) - ((j + 1) * lineAngle)),
                        inStartXpos + (j * pSpace), outsideY + outsideH - pRadial
                    });
                    getChildren().addAll(cir, clab, pl);
                }
                Circle cirI = new Circle(outsideX + ((insideX - outsideX) / 2), outsideH / 2, pRadial, Color.YELLOW);
                cirI.setOpacity(0.85);
                Label clabI = new Label("i");
                clabI.setTextFill(Color.BROWN);
                clabI.setFont(Font.font("tahoma", 9));
                clabI.setPrefWidth(pSpace * 1.8);
                clabI.setLayoutX(outsideX + ((insideX - outsideX) / 2) - 1);
                clabI.setLayoutY((outsideH / 2) - 6);
                Polyline plI = new Polyline();
                plI.getPoints().addAll(new Double[]{
                    outsideX + ((insideX - outsideX) / 2) + pRadial, (outsideH / 2),
                    insideX + pRadial, (outsideH / 2),
                    insideX - 3, (outsideH / 2) + 3,
                    insideX, (outsideH / 2),
                    insideX - 3, (outsideH / 2) - 3
                });
                getChildren().addAll(cirI, clabI, plI);
                Circle cirO = new Circle(outsideW - ((insideX - outsideX) / 2), outsideH / 2, pRadial, Color.YELLOW);
                cirO.setOpacity(0.85);
                Label clabO = new Label("o");
                clabO.setTextFill(Color.BROWN);
                clabO.setFont(Font.font("tahoma", 9));
                clabO.setPrefWidth(pSpace * 1.8);
                clabO.setLayoutX(outsideW - ((insideX - outsideX) / 2) - 1);
                clabO.setLayoutY((outsideH / 2) - 6);
                Polyline plO = new Polyline();
                plO.getPoints().addAll(new Double[]{
                    insideX + insideW, (outsideH / 2),
                    outsideW - ((insideX - outsideX) / 2) - pRadial, (outsideH / 2),
                    outsideW - ((insideX - outsideX) / 2) - pRadial - 3, (outsideH / 2) + 3,
                    outsideW - ((insideX - outsideX) / 2) - pRadial, (outsideH / 2),
                    outsideW - ((insideX - outsideX) / 2) - pRadial - 3, (outsideH / 2) - 3
                });
                getChildren().addAll(cirO, clabO, plO);

                getChildren().addAll(rec, lab);
            }
        }
    }

    public void setRootNd(Individual rootNd) {
        this.rootNd = rootNd;
    }

    public void setModel(OntModel model) {
        this.model = model;
    }

    private void resizeShape() {
        if (this.rootNd != null) {
            Iterator out_nds = rootNd.listPropertyValues(model.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasOutput"));
            cir_count = 0;
            outArr = new ArrayList();
            while (out_nds.hasNext()) {
                Individual out = (Individual) model.getIndividual(out_nds.next().toString());
                RDFNode pt = out.getPropertyValue(model.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterType"));
                if (pt != null) {
                    outArr.add(pt.toString().split("#")[1]);
                    cir_count++;
                }
            }
            double mulW = (cir_count > 4) ? (cir_count / 4) : 1;
            double mulH = (cir_count > 7) ? (cir_count / 7) : 1;
            outsideW = 200;
            outsideH = 100;
            outsideW *= mulW;
            outsideH *= mulH;
            insideW = (outsideW / 3);
            insideH = (outsideH / 3);
            insideX = ((outsideW / 2) - (insideW / 2)) + outsideX;
            insideY = ((outsideH / 2) - (insideH / 2)) + outsideY;

            pSpace = 40;
            pRadial = 10;
            pSpaceIn = pSpace / 3;
            pOutStartXOut = outsideX + (outsideW / 3) + (pSpace / 2);
            pOutEndXOut = outsideX + outsideW - (pSpace / 2);
            pInStartXOut = outsideX;
            pInEndXOut = outsideX + (outsideW / 3) - 5;
            pOutStartXIn = insideX + (insideW / 3) + (pSpaceIn / 2);
            pOutEndXIn = insideX + insideW - (pSpaceIn / 2);
            pInStartXIn = insideX;
            pInEndXIn = insideX + (insideW / 3) - 5;
        }
    }

    public double getWidth() {
        return outsideW + 50;
    }

    public double getHeight() {
        return outsideH + 50;
    }

    public void setOutsideX(double outsideX) {
        this.outsideX = outsideX;
    }

    public void setOutsideY(double outsideY) {
        this.outsideY = outsideY;
    }
}
