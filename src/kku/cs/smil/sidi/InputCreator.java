/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class InputCreator {

    private OntModel schema;
    private String ns;

    public InputCreator(String namespace) {
        ns = namespace;
        schema = ModelFactory.createOntologyModel();
        schema.read("http://202.28.94.50/ontologies/healthcare/hl7.owl");
    }

    public void GenerateInputFileFromList(String path, String[] cls) {
        OntModel mod = ModelFactory.createOntologyModel();
        mod.read(path);
        ArrayList<String> clses = new ArrayList<String>();
        if (cls != null && cls.length > 0) {
            for (String c : cls) {
                clses.add(c);
            }
        } else {
            Iterator<OntClass> cs = mod.listClasses();
            while (cs.hasNext()) {
                OntClass c = cs.next();
                clses.add(c.getURI());
            }
        }
        for (int i = 0; i < clses.size(); i++) {
            Iterator it = mod.listIndividuals(mod.getOntClass(clses.get(i)));
            System.out.println(clses.get(i));
            while (it.hasNext()) {
                String fpath = path.substring(6, path.lastIndexOf("/"));
                fpath = fpath.replace("/", "\\");
                Individual ind = (Individual) it.next();
                if (ind.getURI().contains("http://202.28.94.50/ontologies/healthcare/data/")) {
                    String fname = (ind.getLocalName().length() == 1) ? ind.getLocalName() + ".owl" : ind.getLocalName().substring(1, ind.getLocalName().length() - 1) + ".owl";
                    String newUri = (ind.getLocalName().length() == 1) ? "http://202.28.94.50/ontologies/healthcare/data/" + fname + "#_" + ind.getLocalName() : "http://202.28.94.50/ontologies/healthcare/data/" + fname + "#_" + ind.getLocalName().substring(1, ind.getLocalName().length() - 1);
                    OntModel mod2 = ModelFactory.createOntologyModel();
                    Individual ind2 = mod2.createIndividual(newUri, ind.getOntClass());
                    Iterator<Statement> sit = ind.listProperties();
                    while (sit.hasNext()) {
                        Statement stmt = sit.next();
                        ind2.addProperty(stmt.getPredicate(), stmt.getObject());
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(fpath + "\\data\\" + fname);
                        mod2.write(fos);
                        fos.close();
                    } catch (Exception ex) {
                        Logger.getLogger(InputCreator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("--" + ind2.getURI());
                }
            }
        }
    }

    public void GenerateInputFile(String path, double start, double size, int step) {
        double rnd = start + (size / step);
        for (int i = (int) start; i < rnd; i++) {
            int offset = (int) ((i - 1) * step);
            System.out.println("Rount " + (i) + ": " + offset + ", " + step);
            OntModel mod = ModelFactory.createOntologyModel();
            Individual ind = mod.createIndividual(ns + path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf(".")) + "_retrieval_" + (i), schema.getOntClass(ns + "Data_Retrieval"));
            ind.addProperty(schema.getProperty(ns + "offset"), String.valueOf(offset));
            ind.addProperty(schema.getProperty(ns + "limit"), String.valueOf(step));
            try {
                FileOutputStream fos = new FileOutputStream(path.substring(0, path.lastIndexOf(".")) + (i) + ".owl");
                mod.write(fos);
                fos.close();
            } catch (Exception ex) {
                Logger.getLogger(InputCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        InputCreator inp = new InputCreator("http://202.28.94.50/ontologies/healthcare/hl7.owl#");
        //inp.GenerateInputFile("D:\\ontology\\experiments\\exec\\patient\\patient.owl", 501, 10000, 100);
        //inp.GenerateInputFileFromList("file:/D:/ontology/experiments/exec/patient/patient1.owl", null);
    }
}

class InputFilesClearing {

    public static void main(String[] args) {
        File dir = new File(args[0] + ":\\ontology\\experiments\\exec\\patient\\data");
        if (dir.isDirectory()) {
            File[] fls = dir.listFiles();
            int count = 0;
            for (int i = 0; i < fls.length; i++) {
                if (fls[i].getName().contains("_") || fls[i].getName().length() < 13) {
                    if (fls[i].delete()) {
                        count++;
                        System.out.println("Delete " + fls[i].getName() + " succeed.");
                    } else {
                        System.out.println("Delete " + fls[i].getName() + " fail.");
                    }
                }
            }
            System.out.println(count + " files deleted.");
        } else {
            System.out.println("Fail to load directory");
        }
    }
}