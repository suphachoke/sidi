/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Administrator
 */
public class OWLSFactory {

    public String[] parseWS(String[] paths) {
        String[] ret = null;
        ArrayList arr = new ArrayList();
        OntModel mod = ModelFactory.createOntologyModel();
        for (int i = 0; i < paths.length; i++) {
            mod.read(paths[i]);
            arr.add("Read file " + (i + 1) + ": " + paths[i]);
            arr.add(countTriples(paths[i], "Triples size: "));
        }

        Iterator<Individual> inds = mod.listIndividuals(mod.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#AtomicProcess"));
        double count_ap = 0;
        double total_in = 0;
        double total_out = 0;
        double total_params = 0;
        double min_params = 999;
        double max_params = 0;
        while (inds.hasNext()) {
            Individual ind = inds.next();
            Iterator<RDFNode> inps = ind.listPropertyValues(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasInput"));
            double ap_params = 0;
            while (inps.hasNext()) {
                RDFNode inp = inps.next();
                total_in++;
                total_params++;
                ap_params++;
            }
            Iterator<RDFNode> outs = ind.listPropertyValues(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasOutput"));
            while (outs.hasNext()) {
                RDFNode out = outs.next();
                total_out++;
                total_params++;
                ap_params++;
            }
            if (ap_params < min_params) {
                min_params = ap_params;
            }
            if (ap_params > max_params) {
                max_params = ap_params;
            }
            count_ap++;
        }
        arr.add("Number of atomic services: " + count_ap);
        arr.add("Number of inputs: " + total_in);
        arr.add("Number of outputs: " + total_out);
        arr.add("Number of total parameters: " + total_params);
        arr.add("Parameters per service: " + (total_params / count_ap));
        arr.add("Number of parameters range: " + min_params + "-" + max_params);

        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public String[] parseWS(OntModel mod) {
        String[] ret = null;
        ArrayList arr = new ArrayList();

        Iterator<Individual> inds = mod.listIndividuals(mod.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#AtomicProcess"));
        double count_ap = 0;
        double total_in = 0;
        double total_out = 0;
        double total_params = 0;
        double min_params = 999;
        double max_params = 0;
        while (inds.hasNext()) {
            Individual ind = inds.next();
            Iterator<RDFNode> inps = ind.listPropertyValues(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasInput"));
            double ap_params = 0;
            while (inps.hasNext()) {
                RDFNode inp = inps.next();
                total_in++;
                total_params++;
                ap_params++;
            }
            Iterator<RDFNode> outs = ind.listPropertyValues(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasOutput"));
            while (outs.hasNext()) {
                RDFNode out = outs.next();
                total_out++;
                total_params++;
                ap_params++;
            }
            if (ap_params < min_params) {
                min_params = ap_params;
            }
            if (ap_params > max_params) {
                max_params = ap_params;
            }
            count_ap++;
        }
        arr.add("Number of atomic services: " + count_ap);
        arr.add("Number of inputs: " + total_in);
        arr.add("Number of outputs: " + total_out);
        arr.add("Number of total parameters: " + total_params);
        arr.add("Parameters per service: " + (total_params / count_ap));
        arr.add("Number of parameters range: " + min_params + "-" + max_params);

        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public String countTriples(String path, String term) {
        String ret = term;
        OntModel mod = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
        mod.read(path);
        long countS = 0;
        /*Iterator it = mod.listStatements();
         while (it.hasNext()) {
         it.next();
         countS++;
         }*/
        countS = mod.size();
        /*OntModel mod2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
         mod2.read(path);
         long countB = 0;
         Iterator it2 = mod2.listStatements();
         while (it2.hasNext()) {
         it2.next();
         countB++;
         }
         countB = mod2.size();*/
        ret += countS + " (RDFS)";
        return ret;
    }

    public static void main(String[] args) {
        OWLSFactory owlsf = new OWLSFactory();
        String[] res = owlsf.parseWS(new String[]{"file:/D:/ontology/experiments/rnd1/1/init_composition.owl"});
        for (String a : res) {
            System.out.println(a);
        }
    }
}
