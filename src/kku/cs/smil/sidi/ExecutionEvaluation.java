/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.JessBridgeCreator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class ExecutionEvaluation {

    private String casename;

    public static void main(String[] args) throws Exception {
        ExecutionEvaluation ex = new ExecutionEvaluation("case1");
        String out1 = "";

        out1 = ex.invokeWS();
        System.out.println(out1);
        FileOutputStream fos = new FileOutputStream(new File("C:\\evaluation\\execution\\case1.txt"));
        fos.write(out1.getBytes());
        fos.close();

        out1 += ex.evaluate();
        System.out.println(out1);
        fos = new FileOutputStream(new File("C:\\evaluation\\execution\\case1.txt"));
        fos.write(out1.getBytes());
        fos.close();

    }

    public ExecutionEvaluation(String cs) {
        this.casename = cs;
    }

    public String evaluate() throws Exception {
        String ret = ">[Evaluation]\r\n";
        OntModel mod1 = ModelFactory.createOntologyModel();
        mod1.read(new FileReader(new File("C:\\evaluation\\execution\\" + this.casename + "_data.owl")), null);
        OntModel mod2 = ModelFactory.createOntologyModel();
        mod2.read(new FileReader(new File("C:\\evaluation\\execution\\" + this.casename + ".owl")), null);

        FileInputStream fis = new FileInputStream(new File("C:\\evaluation\\execution\\" + this.casename + ".txt"));
        StringBuffer sb = new StringBuffer();
        byte[] bts = new byte[1024];
        while (fis.read(bts) != -1) {
            sb.append(new String(bts));
        }
        String str = sb.toString().trim();

        int dat_ref = 0;
        int dat_exe = 0;
        int dat_rev = 0;

        String[] out = str.split(">")[5].split("\n");

        ClosableIterator it1 = mod1.listStatements();
        ClosableIterator it2 = mod2.listStatements();
        ArrayList it1a = new ArrayList();
        while (it1.hasNext()) {
            StatementImpl st = (StatementImpl) it1.next();
            for (int i = 1; i < out.length; i++) {
                if (out[i].trim().equals(st.getPredicate().toString())) {
                    it1a.add(st.toString());
                    dat_exe++;
                }
            }
        }
        ArrayList it2a = new ArrayList();
        while (it2.hasNext()) {
            StatementImpl st = (StatementImpl) it2.next();
            for (int i = 1; i < out.length; i++) {
                if (out[i].trim().equals(st.getPredicate().toString())) {
                    it2a.add(st.toString());
                    dat_ref++;
                }
            }
        }
        for (int i = 0; i < it1a.size(); i++) {
            for (int j = 0; j < it2a.size(); j++) {
                if (it1a.get(i).toString().equals(it2a.get(j).toString())) {
                    dat_rev++;
                }
            }
        }
        it1.close();
        it2.close();


        ret += "Execution data statements :" + dat_exe + "\r\n";
        ret += "Reference data statements :" + dat_ref + "\r\n";
        ret += "Relevant data statements :" + dat_rev + "\r\n";
        ret += "Precision of data retrieval :" + ((double) dat_rev / (double) dat_ref) + "\r\n";
        ret += "Recall of data retrieval :" + ((double) dat_rev / (double) dat_exe) + "\r\n";

        return ret = ">" + str.split(">")[1] + ">" + str.split(">")[2] + ">" + str.split(">")[3] + ">" + str.split(">")[4] + ">" + str.split(">")[5] + ">" + str.split(">")[6] + ret + ">[Log]" + str.split(">\\[Log]")[1] + "\r\n";
    }

    public String invokeWS() throws Exception {
        String ret = "";
        FileInputStream fis = new FileInputStream(new File("C:\\evaluation\\execution\\" + this.casename + ".txt"));
        StringBuffer sb = new StringBuffer();
        byte[] bts = new byte[1024];
        while (fis.read(bts) != -1) {
            sb.append(new String(bts));
        }
        String str = sb.toString().trim();


        String p = str.split(">")[1].split("\n")[1];
        //String p = "http://61.19.108.39/wsengine/loc1_getPatient.owl#getPatient";
        OntModel mod = ModelFactory.createOntologyModel();
        mod.read("http://localhost/ontologies/healthcare.owl");
        mod.read("http://61.19.108.39/wsengine/local1.owl");
        mod.read("http://61.19.108.37/wssoap/local2.owl");
        mod.read("http://localhost/ontologies/ii-rules.owl");
        mod.setNsPrefix("", "http://202.28.94.50/ontologies/" + this.casename + "_data.owl#");

        String[] classes = str.split(">")[2].split("\n");
        String[] types = str.split(">")[3].split("\n");
        String[] values = str.split(">")[4].split("\n");

        IndividualImpl pa = (IndividualImpl) mod.createIndividual("http://202.28.94.50/ontologies/" + this.casename + "_data.owl#_" + values[1].trim(), mod.getOntClass(classes[1].trim()));
        for (int i = 1; i < classes.length; i++) {
            pa.addRDFType(mod.getOntClass(classes[i].trim()));
        }
        for (int i = 1; i < types.length; i++) {
            pa.addProperty(mod.getProperty(types[i].trim()), values[i].trim());
        }

        long startTime = System.currentTimeMillis();
        StringWriter sw = new StringWriter();
        mod.write(sw, "RDF/XML-ABBREV");
        JessBridgeCreator jess = new JessBridgeCreator();
        JessBridgeCreator jess_out = new JessBridgeCreator();
        try {
            OWLModel owlm = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(sw.toString()));
            owlm.getNamespaceManager().setDefaultNamespace("http://202.28.94.50/ontologies/" + this.casename + "_data.owl#");
            SWRLRuleEngineBridge swrl = jess.create(owlm);
            swrl.infer();
            ////System.out.println(swrl.getNumberOfInferredAxioms());
            OntModel mi = owlm.getOntModel();
            StringWriter sw2 = new StringWriter();
            mi.write(sw2, "RDF/XML-ABBREV");
            /*Iterator it = mi.listIndividuals(mi.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
             Main mn = new Main();
             //System.out.println(mn.CompositeRecursive(1, new ArrayList(), it, mi, 0));*/
            OntModel dat = ModelFactory.createOntologyModel();
            dat.read(new StringReader(sw2.toString()), null);
            //System.out.println(sw2.toString());
            dat.setNsPrefix("", "http://202.28.94.50/ontologies/" + this.casename + "_data.owl#");
            WSExecution wsexe = new WSExecution(dat, "case1", null, null);
            wsexe.execute(wsexe.getProcessSequenceInd(p.trim()));
            //wsexe.executeAtomic(p);
            //wsexe.getData().write(new FileOutputStream(new File("C:\\data-output.owl")), "RDF/XML-ABBREV");
            StringWriter sw3 = new StringWriter();
            wsexe.getData().write(sw3, "RDF/XML-ABBREV");
            OWLModel owlm_out = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(sw3.toString()));
            SWRLRuleEngineBridge swrl_out = jess_out.create(owlm_out);
            swrl_out.infer();
            OntModel m_out = owlm_out.getOntModel();
            //m_out.write(System.out, "RDF/XML-ABBREV");
            m_out.write(new FileOutputStream(new File("C:\\evaluation\\execution\\" + this.casename + "_data.owl")), "RDF/XML-ABBREV");

            long endTime = System.currentTimeMillis();
            ret += ">" + str.split(">")[1] + ">" + str.split(">")[2] + ">" + str.split(">")[3] + ">" + str.split(">")[4] + ">" + str.split(">")[5];
            ret += ">[Results]\r\n";
            ret += "Web services invoke :" + wsexe.getOper_series2().substring(1) + "\r\n";
            ret += "Total elapsed time in WS Excution is :" + (endTime - startTime) + " millisec.\r\n";
            ret += ">[Log]";
            ret += wsexe.getOper_series() + "\r\n";
        } catch (Exception ex) {
            Logger.getLogger(_WSComposition.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }
}
