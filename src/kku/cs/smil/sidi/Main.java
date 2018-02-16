/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.JessBridgeCreator;
import java.io.File;
import java.io.FileOutputStream;
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
public class Main {

    public String NS;
    public String NSProfile;
    public OntModel modCP;
    public OntModel modCPProfile;
    public OntClass compositeP;
    public OntClass profile;
    public Property has_process;
    public OntClass perF;
    public Property compOf;
    public OntClass seq;
    public OntClass contList;
    public OntClass contBag;
    public Property components;
    public Property LFirst;
    public Property LRest;
    public Property process;
    public Property hasin;
    public Property hasout;
    public Property hasin_profile;
    public Property hasout_profile;
    public OntClass splitJ;
    public OntClass inp;
    public OntClass outp;
    public Property param;

    /**
     * @param args the command line arguments
     */
    public Main(String uri, String uri_p) {
        OntModel m = ModelFactory.createOntologyModel();
        m.read("http://202.28.94.50/owl-s/1.1/mod/Process.owl");
        m.read("http://202.28.94.50/owl-s/1.1/mod/Profile.owl");
        this.profile = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#Profile");
        this.perF = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Perform");
        this.seq = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence");
        this.splitJ = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Split-Join");
        this.contList = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#ControlConstructList");
        this.contBag = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#ControlConstructBag");
        this.has_process = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#has_process");
        this.compOf = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#composedOf");
        this.components = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components");
        this.LFirst = m.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first");
        this.LRest = m.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest");
        this.process = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#process");
        this.hasin = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasInput");
        this.hasout = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasOutput");
        this.hasin_profile = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#hasInput");
        this.hasout_profile = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#hasOutput");
        this.inp = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Input");
        this.outp = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Output");
        this.param = m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterType");
        this.modCP = ModelFactory.createOntologyModel();
        this.NS = uri;
        this.modCP.setNsPrefix("", this.NS);
        this.modCPProfile = ModelFactory.createOntologyModel();
        this.NSProfile = uri_p;
        this.modCPProfile.setNsPrefix("", this.NSProfile);
    }

    public static void main(String[] args) {
        String[] sw = new String[]{"http://61.19.108.39/wsengine/loc1_getPatient.owl",
            "http://61.19.108.39/wsengine/loc1_getDiagnosis.owl",
            "http://61.19.108.39/wsengine/loc1_getDoctor.owl",
            "http://61.19.108.37/wssoap/loc2_get_drdx.owl",
            "http://61.19.108.37/wssoap/loc2_get_dx.owl",
            "http://61.19.108.37/wssoap/loc2_get_dxdiag.owl",
            "http://61.19.108.37/wssoap/loc2_get_patient.owl"};
        String[] imp = new String[]{
            "http://202.28.94.50/owl-s/1.1/mod/Service.owl",
            "http://202.28.94.50/owl-s/1.1/mod/Process.owl",
            "http://202.28.94.50/owl-s/1.1/mod/Profile.owl",
            "http://202.28.94.50/owl-s/1.1/mod/Grounding.owl",
            "http://www.w3.org/2002/07/owl",
            "http://www.w3.org/2000/01/rdf-schema",
            "http://swrl.stanford.edu/ontologies/3.3/swrla.owl",
            "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl",
            "http://202.28.94.50/ontologies/sws-rules.owl",
            "http://202.28.94.50/ontologies/map1.owl",
            "http://202.28.94.50/ontologies/bridges.owl",
            "http://61.19.108.39/wsengine/local1.owl",
            "http://61.19.108.37/wssoap/local2.owl",
            "http://202.28.94.50/ontologies/healthcare.owl"
        };
        String[] imp2 = new String[]{
            "http://202.28.94.50/owl-s/1.1/mod/Service.owl",
            "http://202.28.94.50/owl-s/1.1/mod/Process.owl",
            "http://202.28.94.50/owl-s/1.1/mod/Profile.owl",
            "http://202.28.94.50/ontologies/map1.owl",
            "http://202.28.94.50/ontologies/sws-rules.owl",};

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        /*OntModel m2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        m2.read("http://202.28.94.50/ontologies/cp1.owl");*/
        for (int i = 0; i < sw.length; i++) {
            m.read(sw[i]);
        }
        for (int j = 0; j < imp.length; j++) {
            m.read(imp[j]);
        }

        OntModel mnew = ModelFactory.createOntologyModel();
        mnew.setNsPrefix("", "http://202.28.94.50/ontologies/cpx.owl#");
        mnew.setNsPrefix("process", "http://202.28.94.50/owl-s/1.1/mod/Process.owl#");
        mnew.setNsPrefix("list", "http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#");
        Iterator itap = m.listIndividuals(m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#AtomicProcess"));
        while (itap.hasNext()) {
            Ontology mnewo = mnew.createOntology("http://202.28.94.50/ontologies/cpx.owl");
            for (int k = 0; k < imp2.length; k++) {
                mnewo.addImport(mnew.createResource(imp2[k]));
            }
            for (int k = 0; k < sw.length; k++) {
                mnewo.addImport(mnew.createResource(sw[k]));
            }
            com.hp.hpl.jena.ontology.impl.IndividualImpl indap = (IndividualImpl) itap.next();
            Individual inds = mnew.createIndividual("http://202.28.94.50/ontologies/cpx.owl#Seq_" + indap.getLocalName(), m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
            Individual indsl = mnew.createIndividual("http://202.28.94.50/ontologies/cpx.owl#list_" + indap.getLocalName(), m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#ControlConstructList"));
            inds.addProperty(m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components"), indsl);
            Individual indper = mnew.createIndividual("http://202.28.94.50/ontologies/cpx.owl#Perform_" + indap.getLocalName(), m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Perform"));
            indsl.addProperty(m.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"), indper);
            indper.addProperty(m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#process"), indap);
            Individual indspj = mnew.createIndividual("http://202.28.94.50/ontologies/cpx.owl#SeqSP_" + indap.getLocalName(), m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
            Individual indspjl = mnew.createIndividual("http://202.28.94.50/ontologies/cpx.owl#listSP_" + indap.getLocalName(), m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#ControlConstructList"));
            indspj.addProperty(m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components"), indspjl);
            Individual indpj = mnew.createIndividual("http://202.28.94.50/ontologies/cpx.owl#Split-Join_" + indap.getLocalName(), m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Split-Join"));
            indspjl.addProperty(m.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"), indpj);
            Individual indpjb = mnew.createIndividual("http://202.28.94.50/ontologies/cpx.owl#bag_" + indap.getLocalName(), m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#ControlConstructBag"));
            indpj.addProperty(m.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components"), indpjb);
            indpjb.addProperty(m.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"), inds);
        }
        //mnew.write(System.out, "RDF/XML-ABBREV");

        //m2.write(System.out, "RDF/XML-ABBREV");
        StringWriter strw = new StringWriter();
        mnew.write(strw, "RDF/XML-ABBREV");
        //System.out.println(strw.toString());
        JessBridgeCreator jess = new JessBridgeCreator();
        try {
            OWLModel owlm = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(strw.toString()));
            SWRLRuleEngineBridge swrl = jess.create(owlm);
            swrl.infer();
            //System.out.println(swrl.getNumberOfInferredAxioms());
            OntModel mi = owlm.getOntModel();
            mi.write(new FileOutputStream(new File("C:\\tmp-comp.owl")), "RDF/XML-ABBREV");
            /*Iterator it = mi.listIndividuals(mi.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
            Main mn = new Main();
            System.out.println(mn.CompositeRecursive(1, new ArrayList(), it, mi, 0));*/
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int CompositeRecursive(int depth, ArrayList beforeNd, Iterator atomicSeqs, OntModel mi, int num) {
        while (atomicSeqs.hasNext()) {
            ArrayList beforeNds = new ArrayList();
            beforeNds.addAll(beforeNd);
            Object obj = atomicSeqs.next();
            com.hp.hpl.jena.ontology.impl.IndividualImpl ind = null;
            if (obj.getClass().equals(com.hp.hpl.jena.rdf.model.impl.ResourceImpl.class)) {
                com.hp.hpl.jena.rdf.model.impl.ResourceImpl resobj = (com.hp.hpl.jena.rdf.model.impl.ResourceImpl) obj;
                ind = (IndividualImpl) mi.getIndividual(resobj.getURI());
            } else {
                ind = (IndividualImpl) obj;
            }
            RDFNode rd = ind.getPropertyValue(mi.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components"));
            com.hp.hpl.jena.ontology.impl.IndividualImpl ind2 = (IndividualImpl) mi.getIndividual(rd.toString());
            RDFNode rdf20 = ind2.getPropertyValue(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"));
            beforeNds.add(mi.getIndividual(rdf20.toString()));
            Iterator itrd2 = ind2.listPropertyValues(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest"));
            if (itrd2.hasNext()) {
                num = CompositeRecursive((depth + 1), beforeNds, itrd2, mi, num);
            } else if (depth > 1) {
                this.GenerateComposition((depth + 1), beforeNds, mi, num + 1);
                num++;
            } else if (depth == 1 && mi.getIndividual(rdf20.toString()).getRDFType().getLocalName().equals("Split-Join")) {
                beforeNds.add(mi.getIndividual(rdf20.toString()));
                this.GenerateComposition((depth + 1), beforeNds, mi, num + 1);
                num++;
            }
        }
        return num;
    }

    public void GenerateComposition(int depth, ArrayList beforeNds, OntModel model, int num) {
        IndividualImpl cp = (IndividualImpl) this.modCP.createIndividual(this.NS + "Composition" + num, this.compositeP);
        IndividualImpl cp_profile = (IndividualImpl) this.modCPProfile.createIndividual(this.NSProfile + "Composition" + num, this.profile);
        cp_profile.addProperty(this.has_process, cp);
        IndividualImpl beforNd = null;
        for (int i = 0; i < beforeNds.size(); i++) {
            IndividualImpl in = (IndividualImpl) beforeNds.get(i);
            IndividualImpl sq = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_Seq" + (i + 1), this.seq);
            IndividualImpl list = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_List" + (i + 1), this.contList);
            if (i == 0) {
                cp.addProperty(this.compOf, sq);
                beforNd = list;
            } else {
                beforNd.addProperty(this.LRest, sq);
                beforNd = list;
            }
            sq.addProperty(this.components, list);
            if (in.getRDFType().getLocalName().equals("Perform")) {
                //IndividualImpl per = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_" + (i + 1) + in.getLocalName(), this.perF);
                IndividualImpl per = (IndividualImpl) this.modCP.createIndividual(this.perF);
                IndividualImpl proc0 = (IndividualImpl) model.getIndividual(in.getPropertyValue(this.process).toString());
                per.addProperty(this.process, proc0);
                list.addProperty(this.LFirst, per);
                Iterator ins = proc0.listPropertyValues(this.hasin);
                this.createInput(cp_profile, ins, model, num);
                Iterator outs = proc0.listPropertyValues(this.hasout);
                this.createOutput(cp_profile, outs, model, num);
            } else if (in.getRDFType().getLocalName().equals("Split-Join")) {
                IndividualImpl spjb = (IndividualImpl) model.getIndividual(in.getPropertyValue(this.components).toString());
                IndividualImpl spjf = (IndividualImpl) model.getIndividual(spjb.getPropertyValue(this.LFirst).toString());
                IndividualImpl spjfseq = this.trimSplitSequence(i, cp_profile, spjf, beforeNds, model, this.modCP, num);
                //IndividualImpl nspj = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_Seq" + (i + 1) + "_SPJ", this.splitJ);
                //IndividualImpl nspjb = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_Seq" + (i + 1) + "_Bag", this.contBag);
                IndividualImpl nspj = (IndividualImpl) this.modCP.createIndividual(this.splitJ);
                IndividualImpl nspjb = (IndividualImpl) this.modCP.createIndividual(this.contBag);
                nspj.addProperty(this.components, nspjb);
                if (spjfseq != null) {
                    nspjb.addProperty(this.LFirst, spjfseq);
                }
                Iterator it0 = spjb.listPropertyValues(this.LRest);
                while (it0.hasNext()) {
                    IndividualImpl spjr = (IndividualImpl) model.getIndividual(it0.next().toString());
                    IndividualImpl spjrseq = this.trimSplitSequence(i, cp_profile, spjr, beforeNds, model, this.modCP, num);
                    if (spjrseq != null) {
                        nspjb.addProperty(this.LRest, spjrseq);
                    }
                }
                list.addProperty(this.LFirst, nspj);
            }
            /*if (i == 0) {
            System.out.print(in.getLocalName());
            } else {
            System.out.print("-->" + in.getLocalName());
            }*/
        }
        //System.out.println("");
    }

    public IndividualImpl trimSplitSequence(int num, IndividualImpl cp, IndividualImpl ind, ArrayList bfn, OntModel mod, OntModel outMod, int comnum) {
        IndividualImpl ret = null;
        boolean state = true;
        IndividualImpl seql = (IndividualImpl) mod.getIndividual(ind.getPropertyValue(this.components).toString());
        IndividualImpl perf = (IndividualImpl) mod.getIndividual(seql.getPropertyValue(this.LFirst).toString());
        if (perf.getRDFType().getLocalName().equals("Perform")) {
            IndividualImpl procf = (IndividualImpl) mod.getIndividual(perf.getPropertyValue(this.process).toString());
            for (int i = (bfn.size() - 1); i > num; i--) {
                IndividualImpl in = (IndividualImpl) bfn.get(i);
                if (in.getRDFType().getLocalName().equals("Perform")) {
                    if (in.getURI().equals(perf.getURI())) {
                        state = false;
                    }
                } else if (in.getRDFType().getLocalName().equals("Split-Join")) {
                    IndividualImpl inc = (IndividualImpl) mod.getIndividual(in.getPropertyValue(this.components).toString());
                    IndividualImpl incsf = (IndividualImpl) mod.getIndividual(inc.getPropertyValue(this.LFirst).toString());
                    IndividualImpl incsfc = (IndividualImpl) mod.getIndividual(incsf.getPropertyValue(this.components).toString());
                    IndividualImpl incsfcp = (IndividualImpl) mod.getIndividual(incsfc.getPropertyValue(this.LFirst).toString());
                    if (incsfcp.getURI().equals(perf.getURI())) {
                        state = false;
                    }
                    Iterator itr = inc.listPropertyValues(this.LRest);
                    while (itr.hasNext()) {
                        IndividualImpl incsr = (IndividualImpl) mod.getIndividual(itr.next().toString());
                        IndividualImpl incsrc = (IndividualImpl) mod.getIndividual(incsr.getPropertyValue(this.components).toString());
                        IndividualImpl incsrcp = (IndividualImpl) mod.getIndividual(incsrc.getPropertyValue(this.LFirst).toString());
                        if (incsrcp.getURI().equals(perf.getURI())) {
                            state = false;
                        }
                    }
                }
            }

            if (state) {
                ret = (IndividualImpl) outMod.createIndividual(this.seq);
                IndividualImpl ls = (IndividualImpl) outMod.createIndividual(this.contList);
                IndividualImpl p = (IndividualImpl) outMod.createIndividual(this.perF);
                ret.addProperty(this.components, ls);
                ls.addProperty(this.LFirst, p);
                p.addProperty(this.process, procf);
                Iterator ins = procf.listPropertyValues(this.hasin);
                this.createInput(cp, ins, mod, comnum);
                Iterator outs = procf.listPropertyValues(this.hasout);
                this.createOutput(cp, outs, mod, comnum);
                if (seql.getPropertyValue(this.LRest) != null) {
                    IndividualImpl seqr = (IndividualImpl) mod.getIndividual(seql.getPropertyValue(this.LRest).toString());
                    IndividualImpl nx = this.trimSplitSequence(num, cp, seqr, bfn, mod, outMod, comnum);
                    if (nx != null) {
                        ls.addProperty(this.LRest, nx);
                    }
                }
            }
        }
        return ret;
    }

    public void createInput(IndividualImpl cp, Iterator ins, OntModel model, int num) {
        while (ins.hasNext()) {
            IndividualImpl input = (IndividualImpl) model.getIndividual(ins.next().toString());
            Iterator itin = input.listPropertyValues(this.param);
            ResourceImpl selin = null;
            while (itin.hasNext()) {
                ResourceImpl rin = (ResourceImpl) itin.next();
                if (rin.getURI().split("#")[0].equals("http://202.28.94.50/ontologies/healthcare.owl")) {
                    selin = rin;
                }
            }
            IndividualImpl ninput = (IndividualImpl) this.modCPProfile.createIndividual(this.NS + "C" + num + "_IN_" + selin.getLocalName(), this.inp);
            ninput.addProperty(this.param, model.getIndividual(selin.getURI()));
            cp.addProperty(this.hasin_profile, ninput);
        }
    }

    public void createOutput(IndividualImpl cp, Iterator outs, OntModel model, int num) {
        while (outs.hasNext()) {
            IndividualImpl output = (IndividualImpl) model.getIndividual(outs.next().toString());
            Iterator itout = output.listPropertyValues(this.param);
            ResourceImpl selout = null;
            while (itout.hasNext()) {
                ResourceImpl rout = (ResourceImpl) itout.next();
                if (rout.getURI().split("#")[0].equals("http://202.28.94.50/ontologies/healthcare.owl")) {
                    selout = rout;
                }
            }
            IndividualImpl noutput = (IndividualImpl) this.modCPProfile.createIndividual(this.NS + "C" + num + "_OUT_" + selout.getLocalName(), this.outp);
            noutput.addProperty(this.param, model.getIndividual(selout.getURI()));
            cp.addProperty(this.hasout_profile, noutput);
        }
    }

    public OntModel getModCP() {
        return modCP;
    }

    public void setModCP(OntModel modCP) {
        this.modCP = modCP;
    }

    public OntModel getModCPProfile() {
        return modCPProfile;
    }

    public void setModCPProfile(OntModel modCPProfile) {
        this.modCPProfile = modCPProfile;
    }
}
