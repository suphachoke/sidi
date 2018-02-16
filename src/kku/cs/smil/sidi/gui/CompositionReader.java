/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author PLE
 */
public class CompositionReader {

    OntModel mod = ModelFactory.createOntologyModel();

    public CompositionReader() {
        //ไฟล์ OWL-S ที่มีข้อมูล Web Services ที่เราต้องการเรียกใช้งาน
        mod.read("file:/C:/composition.owl");
        mod.read("file:/C:/composition-profile.owl");
        //ไฟล์ OWL-S schema ที่ปรับปรุงไว้แล้ว
        mod.read("http://202.28.94.50/owl-s/1.1/mod/process.owl");
        mod.read("http://202.28.94.50/owl-s/1.1/mod/profile.owl");
    }

    public String[] getComposition() {
        String[] ret = null;
        ArrayList arr = new ArrayList();
        Iterator it = mod.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#CompositeProcess").listInstances();
        while (it.hasNext()) {
            arr.add(it.next().toString());
        }
        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public String[] getCPInput(String uri) {
        String[] ret = null;
        Iterator indit = mod.listIndividuals(mod.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#Profile"));
        ArrayList arr = new ArrayList();
        while (indit.hasNext()) {
            Individual ind = (Individual) indit.next();
            if (uri.equals(ind.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#has_process")).toString())) {
                Iterator it = ind.listPropertyValues(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#hasInput"));
                while (it.hasNext()) {
                    Individual indI = mod.getIndividual(it.next().toString());
                    arr.add(indI.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterType")).toString());
                }
            }
        }
        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public String[] getCPOutput(String uri) {
        String[] ret = null;
        Iterator indit = mod.listIndividuals(mod.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#Profile"));
        ArrayList arr = new ArrayList();
        while (indit.hasNext()) {
            Individual ind = (Individual) indit.next();
            if (uri.equals(ind.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#has_process")).toString())) {
                Iterator it = ind.listPropertyValues(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#hasOutput"));
                while (it.hasNext()) {
                    Individual indO = mod.getIndividual(it.next().toString());
                    arr.add(indO.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterType")).toString());
                }
            }
        }
        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public String getControlConstruct(String uri) {
        String ret = "";
        Iterator indit = mod.listIndividuals(mod.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#Profile"));
        while (indit.hasNext()) {
            Individual ind = (Individual) indit.next();
            if (uri.equals(ind.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#has_process")).toString())) {
                Individual indCP = (Individual) mod.getIndividual(ind.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#has_process")).toString());
                Individual indS = (Individual) mod.getIndividual(indCP.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#composedOf")).toString());
                Individual indSL = mod.getIndividual(indS.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components")).toString());
                ret = indSL.toString();
            }
        }
        return ret;
    }

    public ArrayList getSpjStack(String uri, ArrayList Mseq) {
        String[] ret = null;
        Individual indL = mod.getIndividual(uri);
        Individual indSpj = mod.getIndividual(indL.getPropertyValue(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first")).toString());
        Individual indSpjB = mod.getIndividual(indSpj.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components")).toString());
        Individual indSpjBFseq = mod.getIndividual(indSpjB.getPropertyValue(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first")).toString());
        String fSeq = getSeqLink(indSpjBFseq.toString());
        ArrayList arr = new ArrayList();
        arr.add(fSeq);
        Iterator itrest = indSpjB.listPropertyValues(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest"));
        while (itrest.hasNext()) {
            Individual indSpjR = (Individual) mod.getIndividual(itrest.next().toString());
            String rSeq = getSeqLink(indSpjR.toString());
            arr.add(rSeq);
        }
        ret = new String[arr.size()];
        arr.toArray(ret);
        if (ret != null) {
            Mseq.add(ret);
        }
        if (indL.getPropertyValue(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest")) != null) {
            Individual indLR = mod.getIndividual(indL.getPropertyValue(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest")).toString());
            Mseq = getSpjStack(indLR.toString(), Mseq);
        }
        return Mseq;
    }

    private String getSeqLink(String uri) {
        String ret = "";
        Individual ind = mod.getIndividual(uri);
        Individual indL = mod.getIndividual(ind.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components")).toString());
        Individual indLF = mod.getIndividual(indL.getPropertyValue(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first")).toString());
        Individual indLFP = mod.getIndividual(indLF.getPropertyValue(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#process")).toString());
        ret += indLFP.toString().split("#")[indLFP.toString().split("#").length - 1];
        Iterator itrest = indL.listPropertyValues(mod.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest"));
        if (itrest.hasNext()) {
            Individual indLR = (Individual) itrest.next();
            ret += "-->" + getSeqLink(indLR.toString());
        }

        return ret;
    }
}
