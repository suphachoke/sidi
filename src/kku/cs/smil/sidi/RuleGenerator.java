/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import edu.stanford.smi.protege.model.DefaultCls;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class RuleGenerator {

    private Map nspfx;

    public static void main(String[] args) {
        String dir = (args.length > 0 && !args[0].equals("")) ? args[0] : "D";
        String[] fs = new String[]{"file:/" + dir + ":/ontology/map1-6.owl"};
        RuleGenerator rg = new RuleGenerator();
        try {
            rg.generateRulesFromBridges(new FileOutputStream(new File(dir + ":\\ontology\\ii-rules1-6.owl")), fs, "http://202.28.94.50/ontologies/ii-rules.owl#");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RuleGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void generateRulesFromBridges(OutputStream out, String[] files, String ns) {
        try {
            OntModel sb = ModelFactory.createOntologyModel();
            for (String f : files) {
                sb.read(f);
            }
            StringWriter stw = new StringWriter();
            sb.write(stw);
            OWLModel sbo = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(stw.toString()));
            OWLModel rules;
            rules = ProtegeOWL.createJenaOWLModel();
            rules.getNamespaceManager().setDefaultNamespace(ns);

            SWRLFactory swrlf = new SWRLFactory(rules);
            //create data inference rules from equivalent bridges
            SWRLVariable x = swrlf.createVariable("x");
            SWRLVariable c = swrlf.createVariable("c");
            ArrayList a_arr = new ArrayList();
            a_arr.add(c);
            SWRLBuiltin built_strConc = swrlf.getBuiltin("http://www.w3.org/2003/11/swrlb#stringConcat");

            //EquivalentBridge
            Iterator it0 = sb.listIndividuals(sb.getOntClass("http://202.28.94.50/ontologies/bridges.owl#EquivalentBridge"));
            while (it0.hasNext()) {
                IndividualImpl ind = (IndividualImpl) it0.next();
                System.out.println(ind.getURI());
                OWLDatatypeProperty src_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#sourceProperty")).toString());
                Object[] src_cc = src_p.getDomains(true).toArray();
                OWLDatatypeProperty dest_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#destinationProperty")).toString());
                Object[] dest_cc = dest_p.getDomains(true).toArray();
                for (int i = 0; i < src_cc.length; i++) {
                    for (int j = 0; j < dest_cc.length; j++) {
                        RDFSNamedClass dest_c = null;
                        if (dest_cc[j].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) dest_cc[j];
                            dest_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            dest_c = (RDFSNamedClass) dest_cc[j];
                        }
                        RDFSNamedClass src_c = null;
                        if (src_cc[i].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) src_cc[i];
                            src_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            src_c = (RDFSNamedClass) src_cc[i];
                        }
                        SWRLClassAtom head_c = swrlf.createClassAtom(dest_c, x);
                        SWRLDatavaluedPropertyAtom head_p = swrlf.createDatavaluedPropertyAtom(dest_p, x, c);
                        SWRLClassAtom body_c = swrlf.createClassAtom((RDFSNamedClass) src_c, x);
                        SWRLDatavaluedPropertyAtom body_p = swrlf.createDatavaluedPropertyAtom(src_p, x, c);
                        SWRLAtomList head2 = swrlf.createAtomList();
                        head2.setFirst(head_p);
                        SWRLAtomList head = swrlf.createAtomList();
                        head.setFirst(head_p);
                        //head.setFirst(head_c);
                        //head.setRest(head2);
                        SWRLAtomList body2 = swrlf.createAtomList();
                        body2.setFirst(body_p);
                        SWRLAtomList body = swrlf.createAtomList();
                        body.setFirst(body_p);
                        //body.setFirst(body_c);
                        //body.setRest(body2);

                        SWRLImp imp = swrlf.createImp(head, body);
                    }
                }
            }

            //EquivalentBridge Inverse
            Iterator it1 = sb.listIndividuals(sb.getOntClass("http://202.28.94.50/ontologies/bridges.owl#EquivalentBridge"));
            while (it1.hasNext()) {
                IndividualImpl ind = (IndividualImpl) it1.next();
                OWLDatatypeProperty dest_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#sourceProperty")).toString());
                Object[] dest_cc = dest_p.getDomains(true).toArray();
                OWLDatatypeProperty src_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#destinationProperty")).toString());
                Object[] src_cc = src_p.getDomains(true).toArray();
                for (int i = 0; i < src_cc.length; i++) {
                    for (int j = 0; j < dest_cc.length; j++) {
                        RDFSNamedClass dest_c = null;
                        if (dest_cc[j].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) dest_cc[j];
                            dest_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            dest_c = (RDFSNamedClass) dest_cc[j];
                        }
                        RDFSNamedClass src_c = null;
                        if (src_cc[i].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) src_cc[i];
                            src_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            src_c = (RDFSNamedClass) src_cc[i];
                        }
                        SWRLClassAtom head_c = swrlf.createClassAtom(dest_c, x);
                        SWRLDatavaluedPropertyAtom head_p = swrlf.createDatavaluedPropertyAtom(dest_p, x, c);
                        SWRLClassAtom body_c = swrlf.createClassAtom((RDFSNamedClass) src_c, x);
                        SWRLDatavaluedPropertyAtom body_p = swrlf.createDatavaluedPropertyAtom(src_p, x, c);
                        SWRLAtomList head2 = swrlf.createAtomList();
                        head2.setFirst(head_p);
                        SWRLAtomList head = swrlf.createAtomList();
                        head.setFirst(head_p);
                        //head.setFirst(head_c);
                        //head.setRest(head2);
                        SWRLAtomList body2 = swrlf.createAtomList();
                        body2.setFirst(body_p);
                        SWRLAtomList body = swrlf.createAtomList();
                        body.setFirst(body_p);
                        //body.setFirst(body_c);
                        //body.setRest(body2);

                        SWRLImp imp = swrlf.createImp(head, body);
                    }
                }
            }

            //AggregationBridge
            Iterator it2 = sb.listIndividuals(sb.getOntClass("http://202.28.94.50/ontologies/bridges.owl#AggregationBridge"));
            while (it2.hasNext()) {
                IndividualImpl ind = (IndividualImpl) it2.next();

                OWLDatatypeProperty dest_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#destinationProperty")).toString());
                Object[] dest_cc = dest_p.getDomains(false).toArray();
                IndividualImpl ind_a = (IndividualImpl) sb.getIndividual(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#aggregateProperties")).toString());

                ArrayList src_list = this.aggregateRecursive(new ArrayList(), ind_a, sb, sbo);
                if (a_arr.size() < src_list.size()) {
                    for (int i = 0; i < src_list.size(); i++) {
                        a_arr.add(swrlf.createVariable("a" + i));
                    }
                }
                OWLProperty prop = (OWLProperty) src_list.get(0);
                Object[] src_cc = prop.getDomains(false).toArray();
                for (int j = 0; j < src_cc.length; j++) {
                    for (int k = 0; k < dest_cc.length; k++) {
                        RDFSNamedClass src_c = null;
                        if (src_cc[j].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) src_cc[j];
                            src_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            src_c = (RDFSNamedClass) src_cc[j];
                        }
                        ArrayList atmlst = new ArrayList();
                        SWRLClassAtom src_ca = swrlf.createClassAtom(src_c, x);
                        for (int ie = 0; ie < src_list.size(); ie++) {
                            SWRLAtomList atl = swrlf.createAtomList();
                            SWRLVariable v_i = (SWRLVariable) a_arr.get(ie + 1);
                            OWLDatatypeProperty p_i = (OWLDatatypeProperty) src_list.get(ie);
                            SWRLDatavaluedPropertyAtom prop_src = swrlf.createDatavaluedPropertyAtom(p_i, x, v_i);
                            atl.setFirst(prop_src);
                            if (ie > 0) {
                                SWRLAtomList atl0 = (SWRLAtomList) atmlst.get(ie - 1);
                                atl0.setRest(atl);
                            }
                            atmlst.add(atl);
                        }
                        SWRLAtomList body = swrlf.createAtomList();
                        SWRLBuiltinAtom built_a = swrlf.createBuiltinAtom(built_strConc, a_arr.iterator());
                        SWRLAtomList built_al = swrlf.createAtomList();
                        built_al.setFirst(built_a);
                        SWRLAtomList last = (SWRLAtomList) atmlst.get(atmlst.size() - 1);
                        last.setRest(built_al);
                        body.setFirst(src_ca);
                        body.setRest((SWRLAtomList) atmlst.get(0));
                        SWRLDatavaluedPropertyAtom prop_dest = swrlf.createDatavaluedPropertyAtom(dest_p, x, c);
                        SWRLAtomList prop_dest_atm = swrlf.createAtomList();
                        prop_dest_atm.setFirst(prop_dest);
                        RDFSNamedClass dest_c = null;
                        if (dest_cc[k].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) dest_cc[k];
                            dest_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            dest_c = (RDFSNamedClass) dest_cc[k];
                        }
                        SWRLClassAtom dest_ca = swrlf.createClassAtom(dest_c, x);
                        SWRLAtomList head = swrlf.createAtomList();
                        head.setFirst(dest_ca);
                        head.setRest(prop_dest_atm);
                        swrlf.createImp(head, body);
                    }
                }
            }

            //DataMediatorBridge
            Iterator it3 = sb.listIndividuals(sb.getOntClass("http://202.28.94.50/ontologies/bridges.owl#DataMediatorBridge"));
            while (it3.hasNext()) {
                IndividualImpl ind = (IndividualImpl) it3.next();
                OWLIndividual indo = sbo.getOWLIndividual(ind.getURI());
                OWLDatatypeProperty src_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#sourceProperty")).toString());
                RDFSLiteral src_v = indo.getPropertyValueLiteral(sbo.getOWLProperty("http://202.28.94.50/ontologies/bridges.owl#sourceValue"));
                Object[] src_cc = src_p.getDomains(true).toArray();
                OWLDatatypeProperty dest_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#destinationProperty")).toString());
                RDFSLiteral dest_v = indo.getPropertyValueLiteral(sbo.getOWLProperty("http://202.28.94.50/ontologies/bridges.owl#destinationValue"));
                Object[] dest_cc = dest_p.getDomains(true).toArray();
                for (int i = 0; i < src_cc.length; i++) {
                    for (int j = 0; j < dest_cc.length; j++) {
                        RDFSNamedClass dest_c = null;
                        if (dest_cc[j].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) dest_cc[j];
                            dest_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            dest_c = (RDFSNamedClass) dest_cc[j];
                        }
                        RDFSNamedClass src_c = null;
                        if (src_cc[i].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) src_cc[i];
                            src_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            src_c = (RDFSNamedClass) src_cc[i];
                        }
                        SWRLClassAtom head_c = swrlf.createClassAtom(dest_c, x);
                        SWRLDatavaluedPropertyAtom head_p = swrlf.createDatavaluedPropertyAtom(dest_p, x, dest_v);
                        SWRLClassAtom body_c = swrlf.createClassAtom((RDFSNamedClass) src_c, x);
                        SWRLDatavaluedPropertyAtom body_p = swrlf.createDatavaluedPropertyAtom(src_p, x, src_v);
                        SWRLAtomList head2 = swrlf.createAtomList();
                        head2.setFirst(head_p);
                        SWRLAtomList head = swrlf.createAtomList();
                        head.setFirst(head_c);
                        head.setRest(head2);
                        SWRLAtomList body2 = swrlf.createAtomList();
                        body2.setFirst(body_p);
                        SWRLAtomList body = swrlf.createAtomList();
                        body.setFirst(body_c);
                        body.setRest(body2);

                        SWRLImp imp = swrlf.createImp(head, body);
                    }
                }
            }

            //DataMediatorBridge Inverse
            Iterator it4 = sb.listIndividuals(sb.getOntClass("http://202.28.94.50/ontologies/bridges.owl#DataMediatorBridge"));
            while (it4.hasNext()) {
                IndividualImpl ind = (IndividualImpl) it4.next();
                OWLIndividual indo = sbo.getOWLIndividual(ind.getURI());
                OWLDatatypeProperty dest_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#sourceProperty")).toString());
                RDFSLiteral dest_v = indo.getPropertyValueLiteral(sbo.getOWLProperty("http://202.28.94.50/ontologies/bridges.owl#sourceValue"));
                Object[] dest_cc = dest_p.getDomains(true).toArray();
                OWLDatatypeProperty src_p = sbo.getOWLDatatypeProperty(ind.getPropertyValue(sb.getProperty("http://202.28.94.50/ontologies/bridges.owl#destinationProperty")).toString());
                RDFSLiteral src_v = indo.getPropertyValueLiteral(sbo.getOWLProperty("http://202.28.94.50/ontologies/bridges.owl#destinationValue"));
                Object[] src_cc = src_p.getDomains(true).toArray();
                for (int i = 0; i < src_cc.length; i++) {
                    for (int j = 0; j < dest_cc.length; j++) {
                        RDFSNamedClass dest_c = null;
                        if (dest_cc[j].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) dest_cc[j];
                            dest_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            dest_c = (RDFSNamedClass) dest_cc[j];
                        }
                        RDFSNamedClass src_c = null;
                        if (src_cc[i].getClass().equals(edu.stanford.smi.protege.model.DefaultCls.class)) {
                            DefaultCls sc = (DefaultCls) src_cc[i];
                            src_c = (RDFSNamedClass) sbo.getRDFSNamedClass(sc.toString());
                        } else {
                            src_c = (RDFSNamedClass) src_cc[i];
                        }
                        SWRLClassAtom head_c = swrlf.createClassAtom(dest_c, x);
                        SWRLDatavaluedPropertyAtom head_p = swrlf.createDatavaluedPropertyAtom(dest_p, x, dest_v);
                        SWRLClassAtom body_c = swrlf.createClassAtom((RDFSNamedClass) src_c, x);
                        SWRLDatavaluedPropertyAtom body_p = swrlf.createDatavaluedPropertyAtom(src_p, x, src_v);
                        SWRLAtomList head2 = swrlf.createAtomList();
                        head2.setFirst(head_p);
                        SWRLAtomList head = swrlf.createAtomList();
                        head.setFirst(head_c);
                        head.setRest(head2);
                        SWRLAtomList body2 = swrlf.createAtomList();
                        body2.setFirst(body_p);
                        SWRLAtomList body = swrlf.createAtomList();
                        body.setFirst(body_c);
                        body.setRest(body2);

                        SWRLImp imp = swrlf.createImp(head, body);
                    }
                }
            }

            OntModel ont = rules.getOntModel();
            Ontology on = ont.createOntology("http://202.28.94.50/ontologies/ii-rules.owl");
            for (String f : files) {
                on.addImport(ont.createResource(f));
            }
            ont.setNsPrefix("", "http://202.28.94.50/ontologies/ii-rules.owl#");
            /*ont.setNsPrefixes(this.nspfx);*/
            ArrayList<Ontology> arrO = new ArrayList();
            Iterator iton = ont.listOntologies();
            while (iton.hasNext()) {
                Ontology onn = (Ontology) iton.next();
                if (onn.getURI().contains("www.owl-ontologies.com")) {
                    arrO.add(onn);
                }
            }
            for (int i = 0; i < arrO.size(); i++) {
                ont.removeAll(arrO.get(i), null, null);
            }
            ont.write(out, "RDF/XML-ABBREV");
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }

    }

    public ArrayList aggregateRecursive(ArrayList list, IndividualImpl ag, OntModel mod, OWLModel owl) {
        ArrayList pp = list;
        IndividualImpl p_first = (IndividualImpl) mod.getIndividual(ag.getPropertyValue(mod.getProperty("http://202.28.94.50/ontologies/bridges.owl#first")).toString());
        pp.add(owl.getOWLProperty(p_first.getURI()));
        if (ag.getPropertyValue(mod.getProperty("http://202.28.94.50/ontologies/bridges.owl#rest")) != null) {
            IndividualImpl ag_rest = (IndividualImpl) mod.getIndividual(ag.getPropertyValue(mod.getProperty("http://202.28.94.50/ontologies/bridges.owl#rest")).toString());
            pp = this.aggregateRecursive(pp, ag_rest, mod, owl);
        }

        return pp;
    }

    public void setNSPrefixs(Map ns) {
        this.nspfx = ns;
    }
}
