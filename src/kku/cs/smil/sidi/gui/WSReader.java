/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.gui;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author PLE
 */
public class WSReader {

    private OntModel mod = ModelFactory.createOntologyModel();
    private OntModel mod2 = ModelFactory.createOntologyModel();
    private OntModel dat2 = ModelFactory.createOntologyModel();
    private String[] modUri;
    private String[] ontoUri;
    private String[] compositeProcess;
    private String[] atomicProcess;
    private String[] atomicProcessNoInput;
    private String globalOnt;

    public WSReader() {
    }

    public void readAtomic(File file) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);
            Element root = doc.getDocumentElement();
            NodeList root_nds = root.getChildNodes();
            ArrayList<String> modUriArr = new ArrayList<String>();
            ArrayList<String> ontoUriArr = new ArrayList<String>();
            for (int i = 0; i < root_nds.getLength(); i++) {
                if (root_nds.item(i).getNodeName().equals("provider")) {
                    NodeList prov_nds = root_nds.item(i).getChildNodes();
                    for (int j = 0; j < prov_nds.getLength(); j++) {
                        if (prov_nds.item(j).getNodeName().equals("service")) {
                            NodeList serv_nds = prov_nds.item(j).getChildNodes();
                            for (int k = 0; k < serv_nds.getLength(); k++) {
                                if (serv_nds.item(k).getNodeName().equals("owls")) {
                                    mod2.read(serv_nds.item(k).getChildNodes().item(0).getNodeValue());
                                    modUriArr.add(serv_nds.item(k).getChildNodes().item(0).getNodeValue());
                                } else if (serv_nds.item(k).getNodeName().equals("onto")) {
                                    mod2.read(serv_nds.item(k).getChildNodes().item(0).getNodeValue());
                                    dat2.read(serv_nds.item(k).getChildNodes().item(0).getNodeValue());
                                    ontoUriArr.add(serv_nds.item(k).getChildNodes().item(0).getNodeValue());
                                }
                            }
                        }
                    }
                }
            }
            modUri = new String[modUriArr.size()];
            modUriArr.toArray(modUri);
            ontoUri = new String[ontoUriArr.size()];
            ontoUriArr.toArray(ontoUri);
        } catch (Exception ex) {
            Logger.getLogger(WSReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        atomicProcess = listAtomic();
    }

    public void readComposite(String file) {

        //ไฟล์ OWL-S ที่มีข้อมูล Web Services ที่เราต้องการเรียกใช้งาน
        String profile = file.substring(0, file.lastIndexOf(".")) + "-profile.owl";
        mod.read(file);
        mod.read(profile);
        //mod.read("file:/C:/composition-profile.owl");
        //ไฟล์ OWL-S schema ที่ปรับปรุงไว้แล้ว
        //mod.read("http://202.28.94.50/owl-s/1.1/mod/process.owl");
        //mod.read("http://202.28.94.50/owl-s/1.1/mod/profile.owl");

        compositeProcess = listComposition();
    }

    public void setGlobalOnt(String globalOnt) {
        this.globalOnt = globalOnt;
        mod.read(globalOnt);
    }

    public void readComposite(OntModel md) {

        //ไฟล์ OWL-S ที่มีข้อมูล Web Services ที่เราต้องการเรียกใช้งาน
        mod.add(md);
        //mod.read("file:/C:/composition-profile.owl");
        //ไฟล์ OWL-S schema ที่ปรับปรุงไว้แล้ว
        //mod.read("http://202.28.94.50/owl-s/1.1/mod/process.owl");
        //mod.read("http://202.29.94.50/owl-s/1.1/mod/profile.owl");

        compositeProcess = listComposition();
    }

    private String[] listComposition() {
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

    private String[] listAtomic() {
        String[] ret = null;
        ArrayList arr = new ArrayList();
        ArrayList arr2 = new ArrayList();
        OntClass cls = mod2.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#AtomicProcess");
        if (cls != null) {
            Iterator it = cls.listInstances();
            while (it.hasNext()) {
                Individual ind = (Individual) it.next();
                if (!ind.hasProperty(mod2.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasInput"))) {
                    arr2.add(ind.toString());
                }
                arr.add(ind.toString());
            }
            ret = new String[arr.size()];
            arr.toArray(ret);
            atomicProcessNoInput = new String[arr2.size()];
            arr2.toArray(atomicProcessNoInput);
        }
        return ret;
    }

    public String[] getAtomicProcess() {
        return atomicProcess;
    }

    public String[] getCompositeProcess() {
        return compositeProcess;
    }

    public String[] getAtomicProcessNoInput() {
        return atomicProcessNoInput;
    }

    public Individual getInd(String uri) {
        Individual ret = null;
        if (uri != null) {
            ret = mod.getIndividual(uri);
            if (ret == null) {
                ret = mod2.getIndividual(uri);
            }
        }
        return ret;
    }

    public OntModel getMod() {
        return mod;
    }

    public OntModel getMod2() {
        return mod2;
    }

    public OntModel getDat2() {
        return dat2;
    }

    public String[] getOntoUri() {
        return ontoUri;
    }

    public String[] getModUri() {
        return modUri;
    }

    public String[] queryAPByInput(String in) {
        String[] ret = null;

        String queryBegin =
                "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"
                + "PREFIX service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>\r\n"
                + "PREFIX process: <http://202.28.94.50/owl-s/1.1/mod/Process.owl#>\r\n"
                + "PREFIX grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>\r\n"
                + "PREFIX profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>\r\n"
                + "\r\n"
                + "SELECT ?s \r\n"
                + "WHERE {?s rdf:type process:AtomicProcess. ?s process:hasOutput ?i. ?i process:parameterType ?t. ?t rdfs:label ?l FILTER regex(?l,\"" + in + "\",\"i\")}\r\n"
                + "GROUP BY ?s";

        System.out.println(queryBegin);
        QueryExecution engine = SparqlDLExecutionFactory.create(QueryFactory.create(queryBegin), mod2);

        long startT = System.nanoTime();
        ResultSet res = engine.execSelect();
        long endT = System.nanoTime();
        System.out.println("Query time: " + ((endT - startT) / 1000000.0) + " millisec.");

        ArrayList arr = new ArrayList();
        while (res.hasNext()) {
            ResultBinding r = (ResultBinding) res.next();
            arr.add(r.getResource("s").getURI());
        }
        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public String[] queryCPByInput(String in) {
        String[] ret = null;

        String queryBegin =
                "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"
                + "PREFIX service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>\r\n"
                + "PREFIX process: <http://202.28.94.50/owl-s/1.1/mod/Process.owl#>\r\n"
                + "PREFIX grounding: <http://www.daml.org/services/owl-s/1.1/Grounding.owl#>\r\n"
                + "PREFIX profile: <http://202.28.94.50/owl-s/1.1/mod/Profile.owl#>\r\n"
                + "\r\n"
                + "SELECT ?s \r\n"
                + "WHERE {?pf profile:has_process ?s. ?pf profile:hasOutput ?i. ?i process:parameterType ?t. ?t rdfs:label ?l FILTER regex(?l,\"" + in + "\",\"i\")}\r\n"
                + "GROUP BY ?s";

        System.out.println(queryBegin);

        QueryExecution engine = SparqlDLExecutionFactory.create(QueryFactory.create(queryBegin), mod);

        long startT = System.nanoTime();
        ResultSet res = engine.execSelect();
        long endT = System.nanoTime();
        System.out.println("Query time: " + ((endT - startT) / 1000000.0) + " millisec.");

        ArrayList arr = new ArrayList();
        while (res.hasNext()) {
            ResultBinding r = (ResultBinding) res.next();
            arr.add(r.getResource("s").getURI());
        }
        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public int getTripleSize() {
        int ret = 0;
        Iterator it = mod2.listReifiedStatements();
        while (it.hasNext()) {
            it.next();
            ret += 1;
        }
        return ret;
    }
}
