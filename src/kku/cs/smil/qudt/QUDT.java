/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.qudt;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author BNK
 */
public class QUDT {

    private OntModel mod;

    public QUDT() {
        this.mod = ModelFactory.createOntologyModel();
        mod.read(this.getClass().getResourceAsStream("qudt.owl"), null);
    }

    public String[] getUnits(String cls) {
        String[] ret = null;
        OntClass c = mod.getOntClass("http://qudt.org/schema/qudt#" + cls);
        ArrayList<String> arr = new ArrayList<String>();

        Iterator<Individual> it = mod.listIndividuals(c);
        while (it.hasNext()) {
            Individual i = it.next();
            arr.add(i.getURI());
        }
        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public String getMultiplier(String unit) {
        String ret = "-";
        Individual ind = mod.getIndividual(unit);
        RDFNode nd = ind.getPropertyValue(mod.getProperty("http://qudt.org/schema/qudt#conversionMultiplier"));
        ret = (nd != null) ? nd.asLiteral().getString() : "1";
        return ret;
    }

    public String searchMultiplier(String unit) {
        String ret = "-";
        Individual ind = mod.getIndividual("http://qudt.org/vocab/unit#" + unit);
        RDFNode nd = null;
        if (ind != null) {
            nd = ind.getPropertyValue(mod.getProperty("http://qudt.org/schema/qudt#conversionMultiplier"));
        }
        ret = (nd != null) ? nd.asLiteral().getString() : "1";
        return ret;
    }

    public OntModel getModel() {
        return this.mod;
    }

    public static void main(String[] args) {
        QUDT qudt = new QUDT();
        String[] res = qudt.getUnits("LengthUnit");
        for (String s : res) {
            System.out.println(s);
        }
    }
}
