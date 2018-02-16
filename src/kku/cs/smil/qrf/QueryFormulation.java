/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.qrf;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 *
 * @author BNK
 */
public class QueryFormulation {

    OntModel mod;
    String ns = "http://smiil.cs.kku.ac.th/ontologies/qfo#";
    ArrayList<String> sparqlRule;
    ArrayList<String> sparqlQuery;

    public QueryFormulation(String file) {
        mod = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        mod.read(this.getClass().getResourceAsStream("qfo.owl"), null);
        if (file != null) {
            mod.read(file);
        }
        mod.add(ModelFactory.createInfModel(PelletReasonerFactory.theInstance().create(), mod));

        sparqlRule = new ArrayList<String>();
        sparqlQuery = new ArrayList<String>();
    }

    public void queryGeneration() {
        ExtendedIterator<Individual> qs = mod.listIndividuals(ResourceFactory.createResource(ns + "Query"));
        while (qs.hasNext()) {
            ArrayList<String> chkVar = new ArrayList<String>();
            String query = "{\r\n";
            Individual q = qs.next();
            NodeIterator fs = q.listPropertyValues(ResourceFactory.createProperty(ns + "whereClause"));
            while (fs.hasNext()) {
                RDFNode f = fs.next();
                if (f.isResource() && f.asResource().hasProperty(RDF.type, ResourceFactory.createResource(ns + "FilteringPattern"))) {
                    StmtIterator ss = f.asResource().listProperties(ResourceFactory.createProperty(ns + "hasStatement"));
                    while (ss.hasNext()) {
                        RDFNode s = ss.next().getObject();
                        if (s.isResource() && s.asResource().hasProperty(RDF.type, RDF.Seq)) {
                            Seq seq = mod.getSeq(s.asResource());
                            for (int i = 1; i <= seq.size(); i++) {
                                Resource st = seq.getSeq(i);
                                if (st.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Triple"))) {
                                    Resource sbj = st.getPropertyResourceValue(ResourceFactory.createProperty(ns + "subject"));
                                    String sbjS = (sbj.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Variable"))) ? "?" + sbj.getLocalName() : "<" + sbj.getURI() + ">";
                                    if (sbj.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Variable")) && !chkVar.contains(sbjS)) {
                                        chkVar.add(sbjS);
                                    }
                                    Resource pre = st.getPropertyResourceValue(ResourceFactory.createProperty(ns + "predicate"));
                                    String preS = (pre.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Variable"))) ? "?" + pre.getLocalName() : "<" + pre.getURI() + ">";
                                    if (pre.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Variable")) && !chkVar.contains(preS)) {
                                        chkVar.add(preS);
                                    }
                                    Resource obj = st.getPropertyResourceValue(ResourceFactory.createProperty(ns + "object"));
                                    String objS = (obj.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Variable"))) ? "?" + obj.getLocalName() : "<" + obj.getURI() + ">";
                                    if (obj.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Variable")) && !chkVar.contains(objS)) {
                                        chkVar.add(objS);
                                    }
                                    String vars = "";
                                    for (String v : chkVar) {
                                        vars += (vars.equals("")) ? v : " " + v;
                                    }
                                    query += " { SELECT " + vars + " WHERE {" + sbjS + " " + preS + " " + objS + ".} }\r\n";
                                } else if (st.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Binder"))) {
                                    Resource out = st.getPropertyResourceValue(ResourceFactory.createProperty(ns + "bindingOutput"));
                                    Resource ins = st.getPropertyResourceValue(ResourceFactory.createProperty(ns + "bindingInput"));
                                    Resource fnc = st.getPropertyResourceValue(ResourceFactory.createProperty(ns + "bindingFunction"));
                                    String bind = "";
                                    if (fnc != null) {
                                        bind += "";
                                        Seq seqi = mod.getSeq(ins);
                                        for (int ii = 1; ii <= seqi.size(); ii++) {
                                            String bb = (seqi.getResource(ii).hasProperty(RDF.value)) ? "\"" + seqi.getResource(ii).getProperty(RDF.value).getObject().asLiteral().getString() + "\""
                                                    : "?" + seqi.getResource(ii).getLocalName();
                                            bind += (bind.equals("")) ? bb : "," + bb;
                                        }
                                        bind += "<" + fnc.getURI() + ">(" + bind + ")";
                                    } else {
                                        bind += (mod.getSeq(ins).getResource(1).hasProperty(RDF.value)) ? "\"" + mod.getSeq(ins).getResource(1).getProperty(RDF.value).getObject().asLiteral().getString() + "\""
                                                : "?" + mod.getSeq(ins).getResource(1).getLocalName();
                                    }
                                    query += " BIND(" + bind + " AS ?" + out.getLocalName() + ").\r\n";
                                } else if (st.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Filter"))) {
                                    Resource fnc = st.getPropertyResourceValue(ResourceFactory.createProperty(ns + "filteringFunction"));
                                    if (fnc.hasProperty(RDF.type, ResourceFactory.createResource(ns + "Test"))) {
                                        String symbol = "";
                                        if (fnc.getLocalName().equals("Equal")) {
                                            symbol = "=";
                                        } else if (fnc.getLocalName().equals("NotEqual")) {
                                            symbol = "!=";
                                        } else if (fnc.getLocalName().equals("LessThan")) {
                                            symbol = "<";
                                        } else if (fnc.getLocalName().equals("LessThanOrEqual")) {
                                            symbol = "<=";
                                        } else if (fnc.getLocalName().equals("GreaterThan")) {
                                            symbol = ">";
                                        } else if (fnc.getLocalName().equals("GreaterThanOrEqual")) {
                                            symbol = ">=";
                                        }
                                        Resource ins = st.getPropertyResourceValue(ResourceFactory.createProperty(ns + "filteringInput"));
                                        Seq seqi = mod.getSeq(ins);
                                        if (seqi.getResource(1) != null && seqi.getResource(2) != null) {
                                            String ll = (seqi.getResource(1).hasProperty(RDF.value)) ? "\"" + seqi.getResource(1).getProperty(RDF.value).getObject().asLiteral().getString() + "\"" : "?" + seqi.getResource(1).getLocalName();
                                            String rr = (seqi.getResource(2).hasProperty(RDF.value)) ? "\"" + seqi.getResource(2).getProperty(RDF.value).getObject().asLiteral().getString() + "\"" : "?" + seqi.getResource(2).getLocalName();
                                            query += " FILTER( " + ll + " " + symbol + " " + rr + ").\r\n";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            query += "}";
            sparqlQuery.add(query);
        }
    }

    public static void main(String[] args) {
        QueryFormulation qf = new QueryFormulation("file:/D:/manuscript/Manuscript5/ex1.owl");
        qf.queryGeneration();
        int idx = 1;
        for (String s : qf.sparqlQuery) {
            try {
                FileOutputStream fos = new FileOutputStream(new File("C:\\rule" + idx + ".txt"));
                fos.write(s.getBytes());
                fos.close();
            } catch (Exception e) {

            }
        }
    }
}
