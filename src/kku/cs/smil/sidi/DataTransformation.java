/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Administrator
 */
public class DataTransformation {

    private OntModel mod;

    public DataTransformation(String url) {
        mod = ModelFactory.createOntologyModel();
        mod.read("file:/C:/data-output.owl");
    }

    public void transform(FileOutputStream fos) {
        OntClass cls = mod.getOntClass("http://202.28.94.50/ontologies/healthcare/general.owl#Patient");
        Iterator it = cls.listInstances();
        ArrayList arr = listClassProperties(cls);
        while (it.hasNext()) {
            Individual in = (Individual) it.next();
            System.out.println(in.getURI());
        }
    }

    public Node xmlBuilding(Node nd, Individual ind, OntClass cls) {

        return nd;
    }

    public ArrayList listClassProperties(OntClass cls) {
        ArrayList ret = new ArrayList();

        return ret;
    }

    public static void main(String[] args) {
        DataTransformation dt = new DataTransformation("file:/C:/data-output.owl");
        dt.transform(null);
    }
}
