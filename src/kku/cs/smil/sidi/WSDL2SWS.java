/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Administrator
 */
public class WSDL2SWS {

    public void parseWSDL(String path) {
        OntModel mod = ModelFactory.createOntologyModel();
        OntModel mod2 = ModelFactory.createOntologyModel();

        try {
            DocumentBuilder build = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = build.parse(path);
            Node rt = doc.getDocumentElement();

            NodeList nds = rt.getChildNodes();
            for (int i = 0; i < nds.getLength(); i++) {
                if (nds.item(i).getNodeName().equals("definitions")) {
                    NodeList nds2 = nds.item(i).getChildNodes();
                    for (int j = 0; j < nds2.getLength(); j++) {
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processWSC(String path) {
        String ns = path.replace("\\", "/");
        ns = "file:/" + ns.substring(0, ns.indexOf(".")) + ".owl#";
        String nso = "http://localhost" + ns.substring(ns.lastIndexOf("/"));
        System.out.println(ns);
        OntModel mods = ModelFactory.createOntologyModel();
        mods.read("http://202.28.94.50/owl-s/1.1/mod/Process.owl");
        OntModel mod = ModelFactory.createOntologyModel();
        mod.setNsPrefix("", nso);
        OntModel mod2 = ModelFactory.createOntologyModel();
        mod2.setNsPrefix("", nso);

        try {
            DocumentBuilder build = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = build.parse(path);
            Node rt = doc.getDocumentElement();

            NodeList nds = rt.getChildNodes();
            for (int i = 0; i < nds.getLength(); i++) {
                if (nds.item(i).getNodeName().equals("service")) {
                    String sname = nds.item(i).getAttributes().getNamedItem("name").getNodeValue();
                    Individual ap = mod.createIndividual(nso + sname, mods.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#AtomicProcess"));
                    NodeList nds2 = nds.item(i).getChildNodes();
                    for (int j = 0; j < nds2.getLength(); j++) {
                        if (nds2.item(j).getNodeName().equals("inputs")) {
                            NodeList nds21 = nds2.item(j).getChildNodes();
                            int c = 0;
                            for (int k = 0; k < nds21.getLength(); k++) {
                                if (nds21.item(k).getNodeName().equals("instance")) {
                                    String iname = nds21.item(k).getAttributes().getNamedItem("name").getNodeValue();
                                    Individual inp = mod.createIndividual(nso + sname + "_in" + c, mods.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Input"));
                                    ap.addProperty(mods.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasInput"), inp);
                                    DatatypeProperty prop = mod2.createDatatypeProperty(nso + iname);
                                    prop.addLabel(iname, "en");
                                    inp.addProperty(mods.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterType"), prop);
                                    c++;
                                }
                            }
                        } else if (nds2.item(j).getNodeName().equals("outputs")) {
                            NodeList nds21 = nds2.item(j).getChildNodes();
                            int c = 0;
                            for (int k = 0; k < nds21.getLength(); k++) {
                                if (nds21.item(k).getNodeName().equals("instance")) {
                                    String oname = nds21.item(k).getAttributes().getNamedItem("name").getNodeValue();
                                    Individual onp = mod.createIndividual(nso + sname + "_out" + c, mods.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Output"));
                                    ap.addProperty(mods.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#hasOutput"), onp);
                                    DatatypeProperty prop = mod2.createDatatypeProperty(nso + oname);
                                    prop.addLabel(oname, "en");
                                    onp.addProperty(mods.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterType"), prop);
                                    c++;
                                }
                            }
                        }
                    }
                }
            }

            String file1 = ns.substring(6, ns.length() - 5) + "-sws.owl";
            String file2 = ns.substring(6, ns.length() - 5) + ".owl";
            System.out.println("Writing file: " + file1);
            mod.write(new FileOutputStream(file1));
            System.out.println("Writing file: " + file2);
            mod2.write(new FileOutputStream(file2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WSDL2SWS w = new WSDL2SWS();
        w.processWSC("D:/WSC2009_Testsets/Testset05/services-sws.owl");
    }
}
