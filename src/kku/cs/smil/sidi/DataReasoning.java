/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.RDF;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.JessBridgeCreator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kku.cs.smil.jena.rulesys.builtins.Average;
import kku.cs.smil.jena.rulesys.builtins.Concatenate;
import kku.cs.smil.jena.rulesys.builtins.ConvertToCO;
import kku.cs.smil.jena.rulesys.builtins.Count;
import kku.cs.smil.jena.rulesys.builtins.DataFormatConverter;
import kku.cs.smil.jena.rulesys.builtins.DataTypeConverter;
import kku.cs.smil.jena.rulesys.builtins.DateFormat;
import kku.cs.smil.jena.rulesys.builtins.InstanceExtension;
import kku.cs.smil.jena.rulesys.builtins.InstanceExtensions;
import kku.cs.smil.jena.rulesys.builtins.NameSimilarity;
import kku.cs.smil.jena.rulesys.builtins.ScalingConverter;
import kku.cs.smil.jena.rulesys.builtins.SearchMultiplier;
import kku.cs.smil.jena.rulesys.builtins.StringToDate;
import kku.cs.smil.jena.rulesys.builtins.StringToDouble;
import kku.cs.smil.jena.rulesys.builtins.Sum;
import org.mindswap.pellet.jena.vocabulary.SWRL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 *
 * @author Administrator
 */
public class DataReasoning {

    private OntModel mod;
    private long inferTime;
    private Model infMod;
    private String dataFile;
    private OWLOntology owlmod;

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public DataReasoning(String[] files) {
        mod = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
        OWLOntologyManager owlman = OWLManager.createOWLOntologyManager();
        for (String f : files) {
            if (f != null && !f.equals("")) {
                mod.read(f);
                try {
                    owlmod = owlman.loadOntologyFromOntologyDocument(IRI.create(f));
                } catch (OWLOntologyCreationException ex) {
                    Logger.getLogger(DataReasoning.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void inferJess() {
        StringWriter sw = new StringWriter();
        mod.write(sw);
        JessBridgeCreator jess = new JessBridgeCreator();
        try {
            OWLModel owlm = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(sw.toString()));
            SWRLRuleEngineBridge swrl = jess.create(owlm);
            long startT = System.currentTimeMillis();
            System.out.println("Start reasoning...");
            swrl.infer();
            long endT = System.currentTimeMillis();
            inferTime = endT - startT;
            System.out.println("Number of infered axioms: " + swrl.getInferredAxioms().size());
            //owlm.getOntModel().write(System.out);
            infMod = owlm.getOntModel();
        } catch (Exception ex) {
            Logger.getLogger(DataReasoning.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inferPellet() {
        long startT = System.currentTimeMillis();
        System.out.println("Start reasoning...");

        OWLReasonerFactory resonerf = new PelletReasonerFactory();
        OWLReasoner reasoner = resonerf.createNonBufferingReasoner(owlmod);
        if (reasoner.isConsistent()) {
            Reasoner res = org.mindswap.pellet.jena.PelletReasonerFactory.theInstance().create();
            res.getReasonerCapabilities().removeAll();
            InfModel infm = ModelFactory.createInfModel(res, mod);

            OntModel newM = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
            newM.add(infm);

            removeRules(newM);
            removeByRDFTypeNS(newM, "http://202.28.94.50/ontologies/bridges.owl");
            removeByRDFTypeNS(newM, "http://www.w3.org/2002/07/owl#DatatypeProperty");
            removeByRDFTypeNS(newM, "http://www.w3.org/2002/07/owl#ObjectProperty");
            removeByRDFTypeNS(newM, "http://www.w3.org/2002/07/owl#Class");
            infMod = newM;
        } else {
            System.out.println("Ontology Inconsistency");
            infMod = mod;
        }

        long endT = System.currentTimeMillis();
        inferTime = endT - startT;
    }

    public void inferJenaRule(File rule) {
        long startT = System.currentTimeMillis();
        System.out.println("Start reasoning...");
        try {
            BufferedReader buff = new BufferedReader(new FileReader(rule));
            Rule.Parser parser = Rule.rulesParserFromReader(buff);
            List<Rule> rules = Rule.parseRules(parser);

            //Set extended built-ins
            BuiltinRegistry.theRegistry.register(new DateFormat());
            BuiltinRegistry.theRegistry.register(new StringToDate());
            BuiltinRegistry.theRegistry.register(new StringToDouble());
            BuiltinRegistry.theRegistry.register(new DataTypeConverter());
            BuiltinRegistry.theRegistry.register(new DataFormatConverter());
            BuiltinRegistry.theRegistry.register(new SearchMultiplier());
            BuiltinRegistry.theRegistry.register(new ScalingConverter());
            BuiltinRegistry.theRegistry.register(new ConvertToCO());
            BuiltinRegistry.theRegistry.register(new Count());
            BuiltinRegistry.theRegistry.register(new Sum());
            BuiltinRegistry.theRegistry.register(new Average());
            BuiltinRegistry.theRegistry.register(new Concatenate());
            BuiltinRegistry.theRegistry.register(new InstanceExtension());
            BuiltinRegistry.theRegistry.register(new InstanceExtensions());
            BuiltinRegistry.theRegistry.register(new NameSimilarity());

            System.out.println("Number of rules : " + rules.size());
            GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
            InfModel infm = ModelFactory.createInfModel(reasoner, mod);
            infMod = infm;
            //infm.write(System.out);
        } catch (Exception e) {
            e.printStackTrace();

        }
        long endT = System.currentTimeMillis();
        inferTime = endT - startT;
    }

    public void inferSPARQL(File rule) throws FileNotFoundException, IOException {
        long startT = System.currentTimeMillis();
        System.out.println("Start reasoning...");
        BufferedReader br = new BufferedReader(new FileReader(rule));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        br.close();

        Query query = QueryFactory.create(sb.toString());
        QueryExecution exec = SparqlDLExecutionFactory.create(query, mod);
        mod.add(exec.execConstruct());
        infMod = mod;

        long endT = System.currentTimeMillis();
        inferTime = endT - startT;
    }

    public long getInferTime() {
        return inferTime;
    }

    public Model getInfMod() {
        return infMod;
    }

    private void removeRules(Model rawModel) {
        ArrayList<Resource> rArr = new ArrayList<Resource>();
        StmtIterator it = rawModel.listStatements(new SimpleSelector(null, RDF.type, (RDFNode) null));
        while (it.hasNext()) {
            Statement s = it.next();
            if (s.getObject().asResource().getURI().contains(SWRL.getURI())) {
                rArr.add(s.getSubject());
            }
        }
        for (Resource r : rArr) {
            rawModel.remove(r.listProperties());
        }
    }

    private Model removeByRDFTypeNS(Model rawModel, String uri) {
        ArrayList<Resource> rArr = new ArrayList<Resource>();
        StmtIterator it = rawModel.listStatements(new SimpleSelector(null, RDF.type, (RDFNode) null));
        while (it.hasNext()) {
            Statement s = it.next();
            if (s.getObject().asResource().getURI().contains(uri)) {
                rArr.add(s.getSubject());
            }
        }
        for (Resource r : rArr) {
            rawModel.remove(r.listProperties());
        }
        return rawModel;
    }

    public static void main(String[] args) {
        DataReasoning dr = new DataReasoning(new String[]{"file:/E:/lms/RuleReasoner/result2.owl"});
        dr.inferPellet();
        System.out.println("Total elapsed time: " + dr.getInferTime() + " millisec.");
        dr.getInfMod().write(System.out);
    }
}
