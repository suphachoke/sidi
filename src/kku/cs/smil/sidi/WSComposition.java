/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.JessBridgeCreator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class WSComposition {

    public String NS;
    public String NSProfile;
    public OntModel m;
    public OntModel modCP;
    public Ontology modCPOnt;
    public OntModel modCPProfile;
    public Ontology modCPProfileOnt;
    public OntModel modCPProfileAP;
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
    public ArrayList checkComposition;

    /**
     * @param args the command line arguments
     */
    public WSComposition(String uri, String uri_p) {
        m = ModelFactory.createOntologyModel();
        m.read("file:" + this.getClass().getResource("owls/Process.owl").getPath());
        m.read("file:" + this.getClass().getResource("owls/Profile.owl").getPath());
        this.profile = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#Profile");
        this.compositeP = m.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#CompositeProcess");
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
        this.modCP.setNsPrefix("", this.NS.substring(0, this.NS.length() - 1));
        this.modCPOnt = this.modCP.createOntology(this.NS);
        this.modCPProfile = ModelFactory.createOntologyModel();
        this.NSProfile = uri_p;
        this.modCPProfile.setNsPrefix("", this.NSProfile.substring(0, this.NSProfile.length() - 1));
        this.modCPProfileOnt = this.modCPProfile.createOntology(this.NSProfile);
        this.modCPProfileAP = ModelFactory.createOntologyModel();
        this.checkComposition = new ArrayList();
    }

    public void executeRules(String init_compo, String fos) {
        JessBridgeCreator jess = new JessBridgeCreator();
        try {
            OWLModel owlm = ProtegeOWL.createJenaOWLModelFromURI(init_compo);
            SWRLRuleEngineBridge swrl = jess.create(owlm);
            swrl.infer();
            //System.out.println(swrl.getNumberOfInferredAxioms());

            OntModel mi = owlm.getOntModel();
            this.generateAtomic(mi);
            mi.write(new FileOutputStream(new File("C:\\tmp-comp.owl")), "RDF/XML-ABBREV");
            /*
             * Iterator it =
             * mi.listIndividuals(mi.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
             * Main mn = new Main(); System.out.println(mn.CompositeRecursive(1,
             * new ArrayList(), it, mi, 0));
             */
        } catch (Exception ex) {
            Logger.getLogger(WSComposition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] executeRules(String init_compo) {

        String[] ret = null;
        ArrayList<String> retArr = new ArrayList<String>();
        long st = System.currentTimeMillis();
        JessBridgeCreator jess = new JessBridgeCreator();
        try {
            OWLModel owlm = ProtegeOWL.createJenaOWLModelFromURI(init_compo);
            SWRLRuleEngineBridge swrl = jess.create(owlm);
            swrl.infer();
            System.out.println(swrl.getNumberOfInferredAxioms());
            retArr.add("Number of inferred Axioms: " + swrl.getNumberOfInferredAxioms());

            Set<OWLAxiom> infs = swrl.getInferredAxioms();
            for (OWLAxiom ax : infs) {
                retArr.add("\t" + ax.toString());
            }

            OntModel mi = owlm.getOntModel();
            this.generateAtomic(mi);
            FileOutputStream fos = new FileOutputStream(new File("C:\\sidi\\composition_graph.owl"));
            mi.write(fos, "RDF/XML-ABBREV");
            fos.close();
            /*
             * Iterator it =
             * mi.listIndividuals(mi.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
             * Main mn = new Main(); System.out.println(mn.CompositeRecursive(1,
             * new ArrayList(), it, mi, 0));
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long et = System.currentTimeMillis();
        retArr.add("Total elapsed time in WS Composition Rules Inferencing: " + (et - st) + " millisec.");
        ret = new String[retArr.size()];
        retArr.toArray(ret);

        return ret;
    }

    public int CompositeRecursive(int depth, ArrayList beforeNd, ArrayList chkNds, Individual atomicSeq, OntModel mi, int num) throws Exception {

        ArrayList beforeNds = beforeNd;
        Individual ind = atomicSeq;
        System.out.println(ind.getURI());
        RDFNode rd = ind.getPropertyValue(mi.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components"));
        Individual ind2 = (rd != null) ? (Individual) mi.getIndividual(rd.toString()) : ind;
        RDFNode rdf20 = ind2.getPropertyValue(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"));
        Individual nd = mi.getIndividual(rdf20.toString());
        if (nd.getRDFType().getLocalName().equals("Split-Join")) {
            //System.out.println(chkExist);
            System.out.println(nd.getURI());
            Iterator itrd2 = ind2.listPropertyValues(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest"));
            boolean chkExist = nodeExist(nd, chkNds, mi);
            beforeNds.add(nd);
            if (itrd2.hasNext() && !chkExist) {
                chkNds.add(nd);
                while (itrd2.hasNext()) {
                    Object objNx = itrd2.next();
                    Individual indNx = null;
                    if (objNx.getClass().equals(com.hp.hpl.jena.rdf.model.impl.ResourceImpl.class)) {
                        com.hp.hpl.jena.rdf.model.impl.ResourceImpl resobj = (com.hp.hpl.jena.rdf.model.impl.ResourceImpl) objNx;
                        indNx = (Individual) mi.getIndividual(resobj.getURI());
                    } else if (objNx.getClass().equals(com.hp.hpl.jena.ontology.impl.OntResourceImpl.class)) {
                        com.hp.hpl.jena.ontology.impl.OntResourceImpl resobj = (com.hp.hpl.jena.ontology.impl.OntResourceImpl) objNx;
                        indNx = (Individual) mi.getIndividual(resobj.getURI());
                    } else {
                        indNx = (Individual) objNx;
                    }
                    RDFNode rdNx = indNx.getPropertyValue(mi.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components"));
                    Individual ind2Nx = (rdNx != null) ? (Individual) mi.getIndividual(rdNx.toString()) : indNx;
                    RDFNode rdf20Nx = ind2Nx.getPropertyValue(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"));
                    Individual ndNx = mi.getIndividual(rdf20Nx.toString());
                    System.out.println("Go recursive.");
                    num = CompositeRecursive((depth + 1), beforeNds, chkNds, indNx, mi, num);
                    chkNds.add(ndNx);
                }
            } else {
                System.out.println("End and start generate CP.");
                num += this.GenerateComposition((depth + 1), beforeNds, mi, num + 1);
            }
        }
        return num;
    }

    public boolean nodeExist(Individual in, ArrayList before, OntModel mi) {
        boolean ret = false;
        ArrayList nds_chk = new ArrayList();
        RDFNode rdfin_b = in.getPropertyValue(mi.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components"));
        Individual in_b = mi.getIndividual(rdfin_b.toString());
        RDFNode rdfin_bf = in_b.getPropertyValue(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"));
        Individual in_bf = mi.getIndividual(rdfin_bf.toString());
        nds_chk.add(in_bf);
        Iterator in_brest_it = in_b.listPropertyValues(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest"));
        while (in_brest_it.hasNext()) {
            nds_chk.add(in_brest_it.next());
        }
        for (int ii = 0; ii < nds_chk.size(); ii++) {
            Object ob = nds_chk.get(ii);
            com.hp.hpl.jena.ontology.impl.IndividualImpl in_chk = null;
            if (ob.getClass().equals(com.hp.hpl.jena.rdf.model.impl.ResourceImpl.class)) {
                com.hp.hpl.jena.rdf.model.impl.ResourceImpl resobj = (com.hp.hpl.jena.rdf.model.impl.ResourceImpl) ob;
                in_chk = (IndividualImpl) mi.getIndividual(resobj.getURI());
            } else if (ob.getClass().equals(com.hp.hpl.jena.ontology.impl.OntResourceImpl.class)) {
                com.hp.hpl.jena.ontology.impl.OntResourceImpl resobj = (com.hp.hpl.jena.ontology.impl.OntResourceImpl) ob;
                in_chk = (IndividualImpl) mi.getIndividual(resobj.getURI());
            } else {
                in_chk = (IndividualImpl) ob;
            }
            for (int i = before.size() - 1; i >= 0; i--) {
                Individual ib = (Individual) before.get(i);
                RDFNode rdfnd = ib.getPropertyValue(mi.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components"));
                Individual ibc = mi.getIndividual(rdfnd.toString());
                /*RDFNode rdfnd2 = ibc.getPropertyValue(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"));
                 Individual ibf = mi.getIndividual(rdfnd2.toString());
                 //System.out.println("---" + ibf.getURI() + ":" + in_chk.getURI());
                 if (ibf.equals(in_chk)) {
                 //System.out.println("---exist");
                 ret = true;
                 }*/
                Iterator irest = ibc.listPropertyValues(mi.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first"));
                while (irest.hasNext()) {
                    Object obj = irest.next();
                    com.hp.hpl.jena.ontology.impl.IndividualImpl ind = null;
                    if (obj.getClass().equals(com.hp.hpl.jena.rdf.model.impl.ResourceImpl.class)) {
                        com.hp.hpl.jena.rdf.model.impl.ResourceImpl resobj = (com.hp.hpl.jena.rdf.model.impl.ResourceImpl) obj;
                        ind = (IndividualImpl) mi.getIndividual(resobj.getURI());
                    } else if (obj.getClass().equals(com.hp.hpl.jena.ontology.impl.OntResourceImpl.class)) {
                        com.hp.hpl.jena.ontology.impl.OntResourceImpl resobj = (com.hp.hpl.jena.ontology.impl.OntResourceImpl) obj;
                        ind = (IndividualImpl) mi.getIndividual(resobj.getURI());
                    } else {
                        ind = (IndividualImpl) obj;
                    }
                    System.out.println("---" + ind.getURI() + ":" + in_chk.getURI());
                    if (ind.equals(in_chk)) {
                        System.out.println("---" + "exist");
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }

    public int GenerateComposition(int depth, ArrayList beforeNds, OntModel model, int num) throws Exception {
        int ret = 0;
        Individual inchk = (Individual) beforeNds.get(0);
        if (depth > 0 && inchk.getRDFType().getLocalName().equals("Split-Join")) {
            ret = 1;
            System.out.println(inchk.getURI());
            System.out.println("-- Generating composition " + (num));
            checkComposition.add(beforeNds);
            IndividualImpl cp = (IndividualImpl) this.modCP.createIndividual(this.NS + "Composition" + num, this.compositeP);
            IndividualImpl cp_profile = (IndividualImpl) this.modCPProfile.createIndividual(this.NSProfile + "Composition" + num, this.profile);
            cp_profile.addProperty(this.has_process, cp);
            IndividualImpl beforNd = null;
            for (int i = 0; i < beforeNds.size(); i++) {
                IndividualImpl in = (IndividualImpl) beforeNds.get(i);
                IndividualImpl list = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_List" + (i + 1), this.contList);
                if (i == 0) {
                    IndividualImpl sq = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_Seq" + (i + 1), this.seq);
                    cp.addProperty(this.compOf, sq);
                    sq.addProperty(this.components, list);
                    beforNd = list;
                } else {
                    beforNd.addProperty(this.LRest, list);
                    beforNd = list;
                }

                if (in.getRDFType().getLocalName().equals("Split-Join")) {
                    IndividualImpl spjb = (IndividualImpl) model.getIndividual(in.getPropertyValue(this.components).toString());
                    IndividualImpl spjf = (IndividualImpl) model.getIndividual(spjb.getPropertyValue(this.LFirst).toString());
                    IndividualImpl nspj = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_Seq" + (i + 1) + "_SPJ", this.splitJ);
                    IndividualImpl nspjb = (IndividualImpl) this.modCP.createIndividual(this.NS + "C" + num + "_Seq" + (i + 1) + "_Bag", this.contBag);
                    //IndividualImpl nspj = (IndividualImpl) this.modCP.createIndividual(this.splitJ);
                    //IndividualImpl nspjb = (IndividualImpl) this.modCP.createIndividual(this.contBag);
                    nspj.addProperty(this.components, nspjb);
                    int count = 1;
                    ArrayList beforeSeq = new ArrayList();
                    beforeSeq.add(spjf);
                    System.out.println((i + 1) + "." + spjb.getURI() + ".." + spjf.getURI());
                    IndividualImpl spjfseq = this.trimSplitSequence(i, cp_profile, spjf, beforeSeq, beforeNds, model, this.modCP, num, 1, count);
                    if (spjfseq != null) {
                        nspjb.addProperty(this.LFirst, spjfseq);
                        count++;
                    }
                    Iterator it0 = spjb.listPropertyValues(this.LRest);
                    while (it0.hasNext()) {
                        IndividualImpl spjr = (IndividualImpl) model.getIndividual(it0.next().toString());
                        beforeSeq = new ArrayList();
                        beforeSeq.add(spjr);
                        IndividualImpl spjrseq = this.trimSplitSequence(i, cp_profile, spjr, beforeSeq, beforeNds, model, this.modCP, num, 1, count);
                        if (spjrseq != null) {
                            nspjb.addProperty(this.LFirst, spjrseq);
                            count++;
                        }
                    }
                    list.addProperty(this.LFirst, nspj);
                }
                /*
                 * if (i == 0) { System.out.print(in.getLocalName()); } else {
                 * System.out.print("-->" + in.getLocalName()); }
                 */
            }
            //System.out.println("");
        }
        return ret;
    }

    public IndividualImpl trimSplitSequence(int num, IndividualImpl cp, IndividualImpl ind, ArrayList bfs, ArrayList bfn, OntModel mod, OntModel outMod, int comnum, int depth, int spjnum) throws Exception {
        IndividualImpl ret = null;
        boolean state = true;
        boolean chkBFS = false;
        for (int i = 0; i < bfs.size(); i++) {
            if (bfs.get(i).equals(ind)) {
                chkBFS = true;
            }
        }
        //System.out.println(num + ": " + depth + ": " + ind.getURI() + ":" + bfs);
        //IndividualImpl seql = (IndividualImpl) mod.getIndividual(ind.getPropertyValue(this.components).toString());
        //IndividualImpl perf = (IndividualImpl) mod.getIndividual(seql.getPropertyValue(this.LFirst).toString());
        IndividualImpl perf = ind;
        if (perf.getRDFType().getLocalName().equals("Perform")) {
            IndividualImpl procf = (IndividualImpl) mod.getIndividual(perf.getPropertyValue(this.process).toString());
            for (int i = (bfn.size() - 1); i >= (num); i--) {
                IndividualImpl in = (IndividualImpl) bfn.get(i);
                //System.out.println("-" + in.getURI());
                if (in.getRDFType().getLocalName().equals("Perform")) {
                    if ((in.getURI().equals(perf.getURI()) || chkBFS) && depth > 1) {
                        state = false;
                    }
                } else if (in.getRDFType().getLocalName().equals("Split-Join")) {
                    IndividualImpl inc = (IndividualImpl) mod.getIndividual(in.getPropertyValue(this.components).toString());
                    IndividualImpl incsf = (IndividualImpl) mod.getIndividual(inc.getPropertyValue(this.LFirst).toString());
                    //IndividualImpl incsfc = (IndividualImpl) mod.getIndividual(incsf.getPropertyValue(this.components).toString());
                    //IndividualImpl incsfcp = (IndividualImpl) mod.getIndividual(incsfc.getPropertyValue(this.LFirst).toString());
                    //System.out.println("--" + incsfcp.getURI() + ":" + perf.getURI());
                    if ((incsf.getURI().equals(perf.getURI()) || chkBFS) && depth > 1) {
                        state = false;
                    }
                    Iterator itr = inc.listPropertyValues(this.LRest);
                    while (itr.hasNext()) {
                        IndividualImpl incsr = (IndividualImpl) mod.getIndividual(itr.next().toString());
                        //IndividualImpl incsrc = (IndividualImpl) mod.getIndividual(incsr.getPropertyValue(this.components).toString());
                        //IndividualImpl incsrcp = (IndividualImpl) mod.getIndividual(incsrc.getPropertyValue(this.LFirst).toString());
                        //System.out.println("--" + incsrcp.getURI() + ":" + perf.getURI());
                        if ((incsr.getURI().equals(perf.getURI()) || chkBFS) && depth > 1) {
                            state = false;
                        }
                    }
                }
            }
            //System.out.println(state);
            if (state) {
                /*
                 * IndividualImpl ret_list = (IndividualImpl)
                 * outMod.createIndividual(this.NS + "C" + comnum + "_Seq" +
                 * (num + 1) + "_Spj" + spjnum + "_List" + (depth),
                 * this.contList); if (depth == 1) { IndividualImpl ret_seq =
                 * (IndividualImpl) outMod.createIndividual(this.NS + "C" +
                 * comnum + "_Seq" + (num + 1) + "_Spj" + spjnum + "_Seq",
                 * this.seq); ret_seq.addProperty(this.components, ret_list);
                 * ret = ret_seq; } else { ret = ret_list; }
                 */
                IndividualImpl p = (IndividualImpl) outMod.createIndividual(this.NS + "C" + comnum + "_Seq" + (num + 1) + "_SPJ_Perform" + spjnum, this.perF);
                //ret_list.addProperty(this.LFirst, p);
                p.addProperty(this.process, procf);
                System.out.println((num + 1) + "." + spjnum + "." + p.getURI() + "..." + procf.getURI());
                ret = p;
                Iterator ins = procf.listPropertyValues(this.hasin);
                this.createInput(cp, ins, mod, comnum);
                Iterator outs = procf.listPropertyValues(this.hasout);
                this.createOutput(cp, outs, mod, comnum);
                /*
                 * Iterator seqrl = seql.listPropertyValues(this.LRest); while
                 * (seqrl.hasNext()) { IndividualImpl seqr = (IndividualImpl)
                 * mod.getIndividual(seqrl.next().toString()); if
                 * (!ind.equals(seqr)) { bfs.add(seqr); IndividualImpl nx =
                 * this.trimSplitSequence(num, cp, seqr, bfs, bfn, mod, outMod,
                 * comnum, (depth + 1), spjnum); if (nx != null) {
                 * ret_list.addProperty(this.LRest, nx); } } }
                 */
            }
        }
        return ret;
    }

    public void createInput(IndividualImpl cp, Iterator ins, OntModel model, int num) throws Exception {
        while (ins.hasNext()) {
            IndividualImpl input = (IndividualImpl) model.getIndividual(ins.next().toString());
            Iterator itin = input.listPropertyValues(this.param);
            ResourceImpl selin = null;
            while (itin.hasNext()) {
                ResourceImpl rin = (ResourceImpl) itin.next();
                if (rin.getURI().split("#")[0].contains("http://202.28.94.50/ontologies/healthcare")) {
                    selin = rin;
                }
            }
            if (selin != null) {
                IndividualImpl ninput = (IndividualImpl) this.modCPProfile.createIndividual(this.NS + "C" + num + "_IN_" + selin.getLocalName(), this.inp);
                ninput.addProperty(this.param, model.getIndividual(selin.getURI()));
                cp.addProperty(this.hasin_profile, ninput);
            }
        }
    }

    public void createOutput(IndividualImpl cp, Iterator outs, OntModel model, int num) throws Exception {
        while (outs.hasNext()) {
            IndividualImpl output = (IndividualImpl) model.getIndividual(outs.next().toString());
            Iterator itout = output.listPropertyValues(this.param);
            ResourceImpl selout = null;
            while (itout.hasNext()) {
                ResourceImpl rout = (ResourceImpl) itout.next();
                if (rout.getURI().split("#")[0].contains("http://202.28.94.50/ontologies/healthcare")) {
                    selout = rout;
                }
            }
            if (selout != null) {
                IndividualImpl noutput = (IndividualImpl) this.modCPProfile.createIndividual(this.NS + "C" + num + "_OUT_" + selout.getLocalName(), this.outp);
                noutput.addProperty(this.param, model.getIndividual(selout.getURI()));
                cp.addProperty(this.hasout_profile, noutput);
            }
        }
    }

    public void generateAtomic(OntModel model) throws Exception {
        Iterator itap = model.listIndividuals(model.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#AtomicProcess"));
        while (itap.hasNext()) {
            IndividualImpl proc0 = (IndividualImpl) itap.next();
            IndividualImpl ap_profile = (IndividualImpl) this.modCPProfile.createIndividual(this.NSProfile + proc0.getLocalName() + "_Profile", this.profile);
            ap_profile.addProperty(this.has_process, proc0);
            IndividualImpl ap_profileb = (IndividualImpl) this.modCPProfileAP.createIndividual(this.NSProfile + proc0.getLocalName() + "_Profile", this.profile);
            ap_profileb.addProperty(this.has_process, proc0);
            Iterator ins = proc0.listPropertyValues(this.hasin);
            while (ins.hasNext()) {
                IndividualImpl input = (IndividualImpl) model.getIndividual(ins.next().toString());
                Iterator itin = input.listPropertyValues(this.param);
                ResourceImpl selin = null;
                while (itin.hasNext()) {
                    ResourceImpl rin = (ResourceImpl) itin.next();
                    if (rin.getURI().split("#")[0].contains("http://202.28.94.50/ontologies/healthcare")) {
                        selin = rin;
                    }
                }
                if (selin != null) {
                    IndividualImpl ninput = (IndividualImpl) this.modCPProfile.createIndividual(this.NS + proc0.getLocalName() + "_IN_" + selin.getLocalName(), this.inp);
                    ninput.addProperty(this.param, model.getIndividual(selin.getURI()));
                    ap_profile.addProperty(this.hasin_profile, ninput);
                    IndividualImpl ninputb = (IndividualImpl) this.modCPProfileAP.createIndividual(this.NS + proc0.getLocalName() + "_IN_" + selin.getLocalName(), this.inp);
                    ninputb.addProperty(this.param, model.getIndividual(selin.getURI()));
                    ap_profileb.addProperty(this.hasin_profile, ninputb);
                }
            }
            Iterator outs = proc0.listPropertyValues(this.hasout);
            while (outs.hasNext()) {
                IndividualImpl output = (IndividualImpl) model.getIndividual(outs.next().toString());
                Iterator itout = output.listPropertyValues(this.param);
                ResourceImpl selout = null;
                while (itout.hasNext()) {
                    ResourceImpl rout = (ResourceImpl) itout.next();
                    if (rout.getURI().split("#")[0].contains("http://202.28.94.50/ontologies/healthcare")) {
                        selout = rout;
                    }
                }
                if (selout != null) {
                    IndividualImpl noutput = (IndividualImpl) this.modCPProfile.createIndividual(this.NS + proc0.getLocalName() + "_OUT_" + selout.getLocalName(), this.outp);
                    noutput.addProperty(this.param, model.getIndividual(selout.getURI()));
                    ap_profile.addProperty(this.hasout_profile, noutput);
                    IndividualImpl noutputb = (IndividualImpl) this.modCPProfileAP.createIndividual(this.NS + proc0.getLocalName() + "_OUT_" + selout.getLocalName(), this.outp);
                    noutputb.addProperty(this.param, model.getIndividual(selout.getURI()));
                    ap_profileb.addProperty(this.hasout_profile, noutputb);
                }
            }
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

    public OntModel getModCPProfileAP() {
        return modCPProfileAP;
    }

    public void setModCPProfile(OntModel modCPProfile) {
        this.modCPProfile = modCPProfile;
    }

    public void redundantElimination(String path, String mapping) {
        OntModel mod = ModelFactory.createOntologyModel();
        mod.read(path);
        OntModel modInf = ModelFactory.createOntologyModel();
        modInf.read(mapping);
        Iterator it = (Iterator) mod.listIndividuals(this.seq);
        ArrayList sp_seq_list = new ArrayList();
        ArrayList remove_stmt = new ArrayList();
        while (it.hasNext()) {
            Individual in = (Individual) it.next();
            Individual in_bag = mod.getIndividual(mod.getIndividual(in.getPropertyValue(this.components).toString()).getPropertyValue(this.LFirst).toString());
            if (in_bag.getRDFType().getLocalName().equals("Split-Join")) {
                System.out.println(in_bag.getURI());
                ArrayList parallel = new ArrayList();
                Individual infirst = mod.getIndividual(mod.getIndividual(in_bag.getPropertyValue(this.components).toString()).getPropertyValue(LFirst).toString());
                parallel.add(infirst);
                ArrayList out_list = new ArrayList();
                Individual in_per = mod.getIndividual(mod.getIndividual(mod.getIndividual(in_bag.getPropertyValue(this.components).toString()).getPropertyValue(LFirst).toString()).getPropertyValue(process).toString());
                Iterator it_out = in_per.listPropertyValues(hasout);
                while (it_out.hasNext()) {
                    Individual out = mod.getIndividual(it_out.next().toString());
                    Iterator paramV = out.listPropertyValues(param);
                    while (paramV.hasNext()) {
                        RDFNode out_clsNd = (RDFNode) paramV.next();
                        if (out_clsNd != null) {
                            Individual out_cls = mod.getIndividual(out_clsNd.toString());
                            if (true) {
                                out_list.add(out_cls);
                            }
                        }
                    }
                }
                Iterator it_rest = mod.getIndividual(in_bag.getPropertyValue(this.components).toString()).listPropertyValues(LRest);
                while (it_rest.hasNext()) {
                    Individual inrest = mod.getIndividual(it_rest.next().toString());
                    System.out.println("-" + inrest.getURI());
                    //Individual in_per_r = mod.getIndividual(mod.getIndividual(mod.getIndividual(inrest.getPropertyValue(components).toString()).getPropertyValue(LFirst).toString()).getPropertyValue(process).toString());
                    Individual in_per_r = mod.getIndividual(inrest.getPropertyValue(process).toString());
                    if (!in_per_r.equals(in_per)) {
                        Iterator it_out_r = in_per_r.listPropertyValues(hasout);
                        double match_a_score = 0;
                        double weight_a = 0;
                        double match_b_score = 0;
                        double weight_b = 0;
                        while (it_out_r.hasNext()) {
                            Individual out = mod.getIndividual(it_out_r.next().toString());
                            Iterator paramV = out.listPropertyValues(param);
                            while (paramV.hasNext()) {
                                RDFNode out_clsNd = (RDFNode) paramV.next();
                                if (out_clsNd != null) {
                                    Individual out_cls = mod.getIndividual(out_clsNd.toString());
                                    RDFNode ndeq = (modInf.getIndividual(out_cls.toString()) != null) ? modInf.getIndividual(out_cls.toString()).getPropertyValue(modInf.getProperty("http://www.w3.org/2002/07/owl#equivalentClass")) : null;
                                    System.out.println("--" + out_cls.toString() + ":" + ndeq);
                                    if (ndeq != null) {
                                        boolean match = false;
                                        for (int j = 0; j < out_list.size(); j++) {
                                            Individual first_out = (Individual) out_list.get(j);
                                            RDFNode first_ndeq = (modInf.getIndividual(first_out.toString()) != null) ? modInf.getIndividual(first_out.toString()).getPropertyValue(modInf.getProperty("http://www.w3.org/2002/07/owl#equivalentClass")) : null;
                                            if (ndeq.equals(first_ndeq)) {
                                                System.out.println("---" + first_out.toString() + ":" + first_ndeq);
                                                match = true;
                                            }
                                        }
                                        match_b_score += (match) ? 1 : 0;
                                        weight_b += 1;
                                    } else {
                                        boolean match = false;
                                        for (int j = 0; j < out_list.size(); j++) {
                                            Individual first_out = (Individual) out_list.get(j);
                                            if ((out_cls.equals(first_out))) {
                                                System.out.println("---" + first_out.toString());
                                                match = true;
                                            }
                                        }
                                        match_a_score += (match) ? 1 : 0;
                                        weight_a += 1;
                                    }
                                }
                            }
                        }
                        //double score = ((match_a_score / weight_a) + (match_a_score / (double) out_list.size())) / 2;
                        double score = (match_b_score / weight_b);
                        System.out.println("---Match score : " + score);
                        if (score > 0.9) {
                            parallel.add(inrest);
                        } else {
                            Statement stmt = mod.asStatement(Triple.create(mod.getIndividual(in_bag.getPropertyValue(this.components).toString()).asNode(), LRest.asNode(), inrest.asNode()));
                            //remove_stmt.add(stmt);
                            parallel.add(inrest);
                        }
                    } else {
                        Statement stmt = mod.asStatement(Triple.createMatch(mod.getIndividual(in_bag.getPropertyValue(this.components).toString()).asNode(), LRest.asNode(), inrest.asNode()));
                        Statement stmt2 = mod.asStatement(Triple.create(mod.getIndividual(in.getPropertyValue(this.components).toString()).asNode(), LRest.asNode(), mod.getIndividual(in.getPropertyValue(this.components).toString()).asNode()));
                        remove_stmt.add(stmt);
                        remove_stmt.add(stmt2);
                    }
                }
                boolean chk_all = false;
                for (int i = 0; i < sp_seq_list.size(); i++) {
                    ArrayList parallel_prev = (ArrayList) sp_seq_list.get(i);
                    boolean chk_foreach = true;
                    if (parallel.size() == parallel_prev.size()) {
                        for (int j = 0; j < parallel_prev.size(); j++) {
                            boolean chk_atom = false;
                            for (int k = 0; k < parallel.size(); k++) {
                                chk_atom = chk_atom | (parallel.get(k).equals(parallel_prev.get(j)));
                            }
                            chk_foreach = chk_foreach & chk_atom;
                        }
                    } else {
                        chk_foreach = false;
                    }
                    chk_all = chk_all | chk_foreach;
                }
                //System.out.println(chk_all);
                if (!chk_all) {
                    sp_seq_list.add(parallel);
                    //System.out.println(parallel);
                } else {
                    //remove_stmt.add(in);
                    //Statement stmt = mod.asStatement(Triple.create(in.asNode(), mod.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type").asNode(), seq.asNode()));
                    remove_stmt.add(in);
                    remove_stmt.add(mod.getIndividual(in.getPropertyValue(this.components).toString()));
                }
            }
        }
        for (int i = 0; i < remove_stmt.size(); i++) {
            if (remove_stmt.get(i).getClass().equals(com.hp.hpl.jena.ontology.impl.IndividualImpl.class)) {
                com.hp.hpl.jena.ontology.impl.IndividualImpl obj = (IndividualImpl) remove_stmt.get(i);
                //System.out.println(obj);
                obj.remove();
            } else if (remove_stmt.get(i).getClass().equals(com.hp.hpl.jena.rdf.model.impl.StatementImpl.class)) {
                com.hp.hpl.jena.rdf.model.impl.StatementImpl obj = (com.hp.hpl.jena.rdf.model.impl.StatementImpl) remove_stmt.get(i);
                //System.out.println(obj);
                mod.remove(obj);
            }
        }
        try {
            mod.write(new FileOutputStream(new File("C:/sidi/tmp-comp.owl")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WSComposition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] compositionExtraction(String map) {
        String[] ret = null;
        ArrayList<String> retArr = new ArrayList<String>();
        long start11 = System.currentTimeMillis();
        redundantElimination("file:/C:/sidi/composition_graph.owl", map);
        long end11 = System.currentTimeMillis();
        System.out.println("Total elapsed time in WS Composition - Redundant Elimination is :" + (end11 - start11) + " millisec.");
        retArr.add("Total elapsed time in WS Composition - Redundant Elimination is :" + (end11 - start11) + " millisec.");

        try {
            long start2 = System.currentTimeMillis();
            OntModel mi = ModelFactory.createOntologyModel();
            mi.read(new FileInputStream(new File("C:/sidi/tmp-comp.owl")), null);
            System.out.println("Start generation...");
            retArr.add("Start generation...");
            Iterator it = mi.listIndividuals(mi.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
            int numOfComp = 0;
            while (it.hasNext()) {
                Object obj = it.next();
                Individual ind = null;
                if (obj.getClass().equals(com.hp.hpl.jena.rdf.model.impl.ResourceImpl.class)) {
                    com.hp.hpl.jena.rdf.model.impl.ResourceImpl resobj = (com.hp.hpl.jena.rdf.model.impl.ResourceImpl) obj;
                    ind = (Individual) mi.getIndividual(resobj.getURI());
                } else if (obj.getClass().equals(com.hp.hpl.jena.ontology.impl.OntResourceImpl.class)) {
                    com.hp.hpl.jena.ontology.impl.OntResourceImpl resobj = (com.hp.hpl.jena.ontology.impl.OntResourceImpl) obj;
                    ind = (Individual) mi.getIndividual(resobj.getURI());
                } else {
                    ind = (Individual) obj;
                }
                numOfComp = CompositeRecursive(1, new ArrayList(), new ArrayList(), ind, mi, 0);
            }
            System.out.println(numOfComp + " of Composition");
            retArr.add(numOfComp + " of Composition");
            long end2 = System.currentTimeMillis();
            System.out.println("Total elapsed time in WS Composition - Composite Processes Generation is :" + (end2 - start2) + " millisec.");
            retArr.add("Total elapsed time in WS Composition - Composite Processes Generation is :" + (end2 - start2) + " millisec.");
            Individual inont = mi.getIndividual("http://202.28.94.50/ontologies/composition.owl");
            Iterator itont = inont.listPropertyValues(mi.getProperty("http://www.w3.org/2002/07/owl#imports"));
            while (itont.hasNext()) {
                String obj = itont.next().toString();
                modCPOnt.addImport(getModCP().createResource(obj));
                modCPProfileOnt.addImport(getModCPProfile().createResource(obj));
            }
            //mn.modCPOnt.addImport(mn.getModCP("").createResource("file:/G:/work/recent_work/owl/2012-06-13/ii-rules1.owl"));
            modCPProfileOnt.addImport(getModCPProfile().createResource("http://202.28.94.50/owl-s/1.1/mod/Profile.owl"));
            //FileOutputStream fos2 = new FileOutputStream(new File("C:\\composition-profile.owl"));
            //getModCPProfile().write(fos2, "RDF/XML-ABBREV");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ret = new String[retArr.size()];
        retArr.toArray(ret);
        return ret;
    }

    public String[] listCompositeProcesses() {
        String[] ret = null;
        ArrayList retArr = new ArrayList();
        Iterator<Individual> it = modCP.listIndividuals(modCP.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#CompositeProcess"));
        while (it.hasNext()) {
            retArr.add(it.next().getURI());
        }
        ret = new String[retArr.size()];
        retArr.toArray(ret);
        return ret;
    }

    public static void main(String[] args) {
        try {
            WSComposition mn = new WSComposition("http://202.28.94.50/ontologies/composition.owl#", "http://202.28.94.50/ontologies/composition-profile.owl#");

            /*
             * long start1 = System.currentTimeMillis();
             * mn.executeRules("file:/F:/work/recent_work/owl/2012-06-13/compo1.owl",
             * null); long end1 = System.currentTimeMillis();
             * System.out.println("Total elapsed time in WS Composition -
             * Inference Rules is :" + (end1 - start1) + " millisec.");
             */

            long start11 = System.currentTimeMillis();
            mn.redundantElimination("file:/D:/ontology/experiments/rnd1/composition_graph.owl", "file:/D:/ontology/map1.owl");
            long end11 = System.currentTimeMillis();
            System.out.println("Total elapsed time in WS Composition - Redundant Elimination is :" + (end11 - start11) + " millisec.");

            long start2 = System.currentTimeMillis();
            OntModel mi = ModelFactory.createOntologyModel();
            mi.read(new FileInputStream(new File("C:\\sidi\\tmp-comp.owl")), null);
            System.out.println("Start generation...");
            Iterator it = mi.listIndividuals(mi.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
            int numOfComp = 0;
            while (it.hasNext()) {
                Object obj = it.next();
                Individual ind = null;
                if (obj.getClass().equals(com.hp.hpl.jena.rdf.model.impl.ResourceImpl.class)) {
                    com.hp.hpl.jena.rdf.model.impl.ResourceImpl resobj = (com.hp.hpl.jena.rdf.model.impl.ResourceImpl) obj;
                    ind = (Individual) mi.getIndividual(resobj.getURI());
                } else if (obj.getClass().equals(com.hp.hpl.jena.ontology.impl.OntResourceImpl.class)) {
                    com.hp.hpl.jena.ontology.impl.OntResourceImpl resobj = (com.hp.hpl.jena.ontology.impl.OntResourceImpl) obj;
                    ind = (Individual) mi.getIndividual(resobj.getURI());
                } else {
                    ind = (Individual) obj;
                }
                numOfComp = mn.CompositeRecursive(1, new ArrayList(), new ArrayList(), ind, mi, numOfComp);
            }

            System.out.println(numOfComp + " of Composition");
            long end2 = System.currentTimeMillis();
            System.out.println("Total elapsed time in WS Composition - Composite Processes Generation is :" + (end2 - start2) + " millisec.");
            Individual inont = mi.getIndividual("http://202.28.94.50/ontologies/composition.owl");
            Iterator itont = inont.listPropertyValues(mi.getProperty("http://www.w3.org/2002/07/owl#imports"));
            while (itont.hasNext()) {
                String obj = itont.next().toString();
                mn.modCPOnt.addImport(mn.getModCP().createResource(obj));
                mn.modCPProfileOnt.addImport(mn.getModCPProfile().createResource(obj));
            }
            //mn.modCPOnt.addImport(mn.getModCP("").createResource("file:/G:/work/recent_work/owl/2012-06-13/ii-rules1.owl"));
            mn.modCPProfileOnt.addImport(mn.getModCPProfile().createResource("http://202.28.94.50/owl-s/1.1/mod/Profile.owl"));
            FileOutputStream fos = new FileOutputStream(new File("C:\\sidi\\composition.owl"));
            mn.getModCP().write(fos, "RDF/XML-ABBREV");
            FileOutputStream fos2 = new FileOutputStream(new File("C:\\sidi\\composition-profile.owl"));
            mn.getModCPProfile().write(fos2, "RDF/XML-ABBREV");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
