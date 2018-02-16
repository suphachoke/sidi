/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.bigdata;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class OWLParser {

    public static String[] listStatements(String file, String filter) {
        String[] ret = null;

        OntModel mod = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
        if (file.substring(file.lastIndexOf(".") + 1).equals("nt")) {
            try {
                FileInputStream fis = new FileInputStream(new File(file.substring(6)));
                mod.read(fis, "", "N-TRIPLES");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OWLParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            mod.read(file);
        }
        Iterator it = mod.listStatements();
        ArrayList<String> arr = new ArrayList<String>();
        while (it.hasNext()) {
            Statement stmt = (Statement) it.next();
            String obj = (stmt.getObject().isResource()) ? "<" + stmt.getObject().toString() + ">"
                    : "\"" + stmt.getObject().asLiteral().getString() + "\"";
            String datatype = (stmt.getObject().isLiteral()) ? stmt.getObject().asLiteral().getDatatypeURI() : null;
            if (stmt.getObject().isLiteral() && datatype != null && !datatype.split("#")[1].equals("string")) {
                obj += "^^xsd:" + datatype.split("#")[1];
            }
            String sbj = (stmt.getSubject().isAnon()) ? stmt.getSubject().getId().getLabelString() : stmt.getSubject().getURI();
            String pre = stmt.getPredicate().getURI();
            String line = "<" + sbj + "> <" + pre + "> " + obj;
            if (pre.contains(filter) || pre.contains("type")) {
                arr.add(line);
            }
        }

        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public static String[] listStatements(OntModel mod, String filter) {
        String[] ret = null;

        Iterator it = mod.listStatements();
        ArrayList<String> arr = new ArrayList<String>();
        while (it.hasNext()) {
            Statement stmt = (Statement) it.next();
            String obj = (stmt.getObject().isResource()) ? "<" + stmt.getObject().toString() + ">"
                    : "\"" + stmt.getObject().asLiteral().getString() + "\"";
            if (stmt.getObject().isLiteral() && !stmt.getObject().asLiteral().getDatatypeURI().split("#")[1].equals("string")) {
                obj += "^^xsd:" + stmt.getObject().asLiteral().getDatatypeURI().split("#")[1];
            }
            String sbj = (stmt.getSubject().isAnon()) ? stmt.getSubject().getId().getLabelString() : stmt.getSubject().getURI();
            String pre = stmt.getPredicate().getURI();
            String line = "<" + sbj + "> <" + pre + "> " + obj;
            if (pre.contains(filter) || pre.contains("type")) {
                arr.add(line);
            }
        }

        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public static String[] listIndividuals(OntModel mod) {
        String[] ret = null;

        Iterator it = mod.listIndividuals();
        ArrayList<String> arr = new ArrayList<String>();
        while (it.hasNext()) {
            Individual ind = (Individual) it.next();
            String line = "<" + ind.getURI() + ">";
            arr.add(line);
        }

        ret = new String[arr.size()];
        arr.toArray(ret);
        return ret;
    }

    public static void main(String[] args) {
        String[] res = OWLParser.listStatements("file:/C:/sidi/data/1309900335053.owl", "http://202.28.94.50/ontologies/healthcare/hl7.owl#");
        for (String r : res) {
            System.out.println(r);
        }
        /*Connector bcon = new Connector("202.28.94.50", 8080);
         bcon.insertData(res);
         System.out.println("Size: " + res.length + " triples");*/
    }
}
