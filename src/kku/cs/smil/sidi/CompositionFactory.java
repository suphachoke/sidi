/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Administrator
 */
public class CompositionFactory {

    public static void initControlConstruct(FileOutputStream fos, String ns, String[] atomic, String[] at_pfx, String mapping, String rules) {
        try {
            OntModel m = ModelFactory.createOntologyModel();
            m.read("http://202.28.94.50/owl-s/1.1/mod/Process.owl");
            for (int i = 0; i < atomic.length; i++) {
                m.read(atomic[i]);
            }
            OntModel mod = ModelFactory.createOntologyModel();
            mod.setNsPrefix("", ns + "#");
            mod.setNsPrefix("process", "http://202.28.94.50/owl-s/1.1/mod/Process.owl#");
            mod.setNsPrefix("list", "http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#");
            mod.setNsPrefix("mp1", mapping + "#");
            mod.setNsPrefix("swsr", rules + "#");
            String nsProc = "http://202.28.94.50/owl-s/1.1/mod/Process.owl#";
            String nsList = "http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#";
            for (int i = 0; i < at_pfx.length; i++) {
                mod.setNsPrefix("p" + (i + 1), at_pfx[i] + "#");
                Individual in_sp_seq = mod.createIndividual(ns + "#p" + (i + 1) + "SP_Seq", m.getOntClass(nsProc + "Sequence"));
                Individual in_sp_seq_list = mod.createIndividual(ns + "#p" + (i + 1) + "SP_Seq_List", m.getOntClass(nsProc + "ControlConstructList"));
                in_sp_seq.addProperty(m.getProperty(nsProc + "components"), in_sp_seq_list);
                Individual in_sp = mod.createIndividual(ns + "#p" + (i + 1) + "SP", m.getOntClass(nsProc + "Split-Join"));
                in_sp_seq_list.addProperty(m.getProperty(nsList + "first"), in_sp);
                Individual in_sp_bag = mod.createIndividual(ns + "#p" + (i + 1) + "SP_Bag", m.getOntClass(nsProc + "ControlConstructBag"));
                in_sp.addProperty(m.getProperty(nsProc + "components"), in_sp_bag);
                Individual in_seq = mod.createIndividual(ns + "#p" + (i + 1) + "Seq", m.getOntClass(nsProc + "Sequence"));
                in_sp_bag.addProperty(m.getProperty(nsList + "first"), in_seq);
                Individual in_seq_list = mod.createIndividual(ns + "#p" + (i + 1) + "Seq_List", m.getOntClass(nsProc + "ControlConstructList"));
                in_seq.addProperty(m.getProperty(nsProc + "components"), in_seq_list);
                Individual in_seq_per = mod.createIndividual(ns + "#p" + (i + 1) + "Per", m.getOntClass(nsProc + "Perform"));
                in_seq_list.addProperty(m.getProperty(nsList + "first"), in_seq_per);
                Individual in_atom = m.getIndividual(at_pfx[i] + "#" + at_pfx[i].substring(at_pfx[i].lastIndexOf("/") + 1, at_pfx[i].length() - 4));
                in_seq_per.addProperty(m.getProperty(nsProc + "process"), in_atom);
            }
            StringWriter sw = new StringWriter();
            mod.write(sw, "RDF/XML-ABBREV");

            OWLModel owlm = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(sw.toString()));
            OWLOntology ont = owlm.getDefaultOWLOntology();
            ont.addImports("http://202.28.94.50/owl-s/1.1/mod/Process.owl");
            ont.addImports(mapping);
            ont.addImports(rules);
            for (int i = 0; i < atomic.length; i++) {
                ont.addImports(atomic[i]);
            }
            ont.rename(ns);
            owlm.getOntModel().write(new FileOutputStream(new File("C:\\sidi\\init_composition.owl")), "RDF/XML-ABBREV");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String[] initControlConstruct(String[] atomic, String mapping, String rules) {
        ArrayList<String> err = new ArrayList<String>();
        err.add("Error:");
        boolean is_err = false;
        if (mapping.equals("")) {
            err.add("\t-No Semantic Bridge Ontolog(SBO) location.");
            is_err = true;
        }
        if (rules.equals("")) {
            err.add("\t-No Composition Rule(CR) location");
            is_err = true;
        }

        String[] outputResult = null;
        if (!is_err) {
            try {
                OntModel m = ModelFactory.createOntologyModel();
                m.read("http://202.28.94.50/owl-s/1.1/mod/Process.owl");
                for (int i = 0; i < atomic.length; i++) {
                    m.read(atomic[i]);
                }
                OntModel mod = ModelFactory.createOntologyModel();
                mod.setNsPrefix("process", "http://202.28.94.50/owl-s/1.1/mod/Process.owl#");
                mod.setNsPrefix("list", "http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#");
                mod.setNsPrefix("mp1", mapping + "#");
                mod.setNsPrefix("swsr", rules + "#");
                String nsProc = "http://202.28.94.50/owl-s/1.1/mod/Process.owl#";
                String nsList = "http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#";

                ArrayList<String> outArr = new ArrayList();
                Iterator<Individual> inds = m.listIndividuals(m.getOntClass(nsProc + "AtomicProcess"));
                while (inds.hasNext()) {
                    Individual in_atom = m.getIndividual(inds.next().getURI());
                    String ns = in_atom.getNameSpace();
                    String pname = in_atom.getLocalName();
                    Individual in_sp_seq = mod.createIndividual(ns + pname + "_SP_Seq", m.getOntClass(nsProc + "Sequence"));
                    outArr.add("Create: (" + ns + pname + "_SP_Seq,rdf:type," + nsProc + "Sequence" + ")");
                    Individual in_sp_seq_list = mod.createIndividual(ns + pname + "_SP_Seq_List", m.getOntClass(nsProc + "ControlConstructList"));
                    outArr.add("Create: (" + ns + pname + "_SP_Seq_List,rdf:type," + nsProc + "ControlConstructList" + ")");
                    in_sp_seq.addProperty(m.getProperty(nsProc + "components"), in_sp_seq_list);
                    Individual in_sp = mod.createIndividual(ns + pname + "_SP", m.getOntClass(nsProc + "Split-Join"));
                    outArr.add("Create: (" + ns + pname + "_SP,rdf:type," + nsProc + "Split-Join" + ")");
                    in_sp_seq_list.addProperty(m.getProperty(nsList + "first"), in_sp);
                    Individual in_sp_bag = mod.createIndividual(ns + pname + "_SP_Bag", m.getOntClass(nsProc + "ControlConstructBag"));
                    outArr.add("Create: (" + ns + pname + "_SP_Bag,rdf:type," + nsProc + "ControlConstructBag" + ")");
                    in_sp.addProperty(m.getProperty(nsProc + "components"), in_sp_bag);
                    Individual in_per = mod.createIndividual(ns + pname + "_Per", m.getOntClass(nsProc + "Perform"));
                    outArr.add("Create: (" + ns + pname + "_Per,rdf:type," + nsProc + "Perform" + ")");
                    in_sp_bag.addProperty(m.getProperty(nsList + "first"), in_per);
                    in_per.addProperty(m.getProperty(nsProc + "process"), in_atom);
                }

                outputResult = new String[outArr.size()];
                outArr.toArray(outputResult);

                StringWriter sw = new StringWriter();
                mod.write(sw, "RDF/XML-ABBREV");

                OWLModel owlm = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(sw.toString()));
                OWLOntology ont = owlm.getDefaultOWLOntology();
                ont.addImports("http://202.28.94.50/owl-s/1.1/mod/Process.owl");
                ont.addImports(mapping);
                ont.addImports(rules);
                for (int i = 0; i < atomic.length; i++) {
                    ont.addImports(atomic[i]);
                }
                ont.rename("http://202.28.94.50/ontologies/composition.owl");
                owlm.getOntModel().write(new FileOutputStream(new File("C:\\sidi\\init_composition.owl")), "RDF/XML-ABBREV");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            outputResult = new String[err.size()];
            err.toArray(outputResult);
        }

        return outputResult;
    }

    public static void main(String[] args) {
        String[] ats = new String[]{"file:/F:/work/recent_work/ws/wsdl/local2/selectp_person.owl",
            "file:/F:/work/recent_work/ws/wsdl/local2/selectp_ptdata.owl",
            "file:/F:/work/recent_work/ws/wsdl/local2/selectp_home.owl",
            "file:/F:/work/recent_work/ws/wsdl/local1/selectPeople.owl",
            "file:/F:/work/recent_work/ws/wsdl/local1/selectPatient.owl",
            "file:/F:/work/recent_work/ws/wsdl/local1/selectPatientAE.owl"};
        String[] ats_pfx = new String[]{"http://61.19.108.37/kkuproject/services/selectp_person.owl",
            "http://61.19.108.37/kkuproject/services/selectp_ptdata.owl",
            "http://61.19.108.37/kkuproject/services/selectp_home.owl",
            "http://61.19.108.39/wsengine/services/selectPeople.owl",
            "http://61.19.108.39/wsengine/services/selectPatient.owl",
            "http://61.19.108.39/wsengine/services/selectPatientAE.owl"};
        CompositionFactory.initControlConstruct(null, "http://202.28.94.50/ontologies/healthcare/compo1.owl",
                ats, ats_pfx, "http://202.28.94.50/ontologies/healthcare/mapping1.owl",
                "http://202.28.94.50/ontologies/sws-rules.owl");
    }
}
