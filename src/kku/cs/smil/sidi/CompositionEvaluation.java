/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BNK
 */
public class CompositionEvaluation {

    private OntModel comp_owls_test;
    private OntModel comp_owls_ref;
    private String test;
    private String ref;

    public CompositionEvaluation(String test, String ref) throws Exception {
        this.test = test;
        this.ref = ref;
        this.comp_owls_test = ModelFactory.createOntologyModel();
        this.comp_owls_test.read(new FileInputStream(new File("C:\\evaluation\\composition\\" + test + ".owl")), null);
        this.comp_owls_test.read("http://localhost/owl-s/1.1/mod/Process.owl");
        this.comp_owls_ref = ModelFactory.createOntologyModel();
        this.comp_owls_ref.read(new FileInputStream(new File("C:\\evaluation\\composition\\" + ref + ".owl")), null);
        this.comp_owls_ref.read("http://localhost/owl-s/1.1/mod/Process.owl");
    }

    public String evaluate() throws Exception {
        String ret = "";
        Iterator cp_it = this.comp_owls_ref.listIndividuals(this.comp_owls_ref.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#CompositeProcess"));
        Iterator cp2_it = this.comp_owls_test.listIndividuals(this.comp_owls_test.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#CompositeProcess"));

        double c1 = 0;
        ArrayList c1list = new ArrayList();
        while (cp_it.hasNext()) {
            c1list.add((Individual) cp_it.next());
            c1++;
        }

        double c2 = 0;
        ArrayList c2list = new ArrayList();
        while (cp2_it.hasNext()) {
            c2list.add((Individual) cp2_it.next());
            c2++;
        }

        double m = 0;
        for (int i = 0; i < c1list.size(); i++) {
            for (int j = 0; j < c2list.size(); j++) {
                Individual c1i = (Individual) c1list.get(i);
                Individual c2i = (Individual) c2list.get(j);
                if (c1i.getURI().equals(c2i.getURI())) {
                    m++;
                }
            }
        }

        Date dt = new Date();
        ret += ">[Evaluation] " + dt.toLocaleString() + "\r\n";
        ret += "Number of Composition result :" + c2 + "\r\n";
        ret += "Number of Composition references :" + c1 + "\r\n";
        ret += "Number of Match relevant :" + m + "\r\n";
        ret += "Precision = " + (m / c1) + "\r\n";
        ret += "Recall = " + (m / c2) + "\r\n";
        return ret;
    }
}
