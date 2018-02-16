/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLSameIndividualAtom;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BNK
 */
public class SWRLReasoner {

    public static void main(String[] args) {
        SWRLReasoner rn = new SWRLReasoner("file:/D:/ontology/ii-rules-ext.owl");
        int count = 1;
        for (SPARQLCompositor c : rn.getSPARQLCompositors()) {
            System.out.println("Query " + count + ":" + c.getBodySPARQL());
            c.callSelectRDFStore();
            c.callInsertRDFStore();
            System.out.println("Update " + count + ":" + c.getHeadSPARQL());
            System.out.println("Return " + count + ":" + c.getInsertResult());
            count++;
        }
    }
    private SWRLFactory swrl;
    private Collection<SPARQLCompositor> compositors;

    public SWRLReasoner(String uri) {
        try {
            OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(uri);
            swrl = new SWRLFactory(model);
            compositors = new ArrayList<SPARQLCompositor>();
            for (SWRLImp r : swrl.getImps()) {
                SPARQLCompositor com = new SPARQLCompositor();
                compositors.add(com);
                SWRLAtomList body = r.getBody();
                SWRLAtomList head = r.getHead();
                SPARQLfromSWRLBody((DefaultRDFList) body, com);
                com.parseBodySPARQL();
                SPARQLfromSWRLHead((DefaultRDFList) head, com);
                com.parseHeadSPARQL();
            }
        } catch (OntologyLoadException ex) {
            Logger.getLogger(SWRLReasoner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Collection<SWRLImp> listRules() {
        return swrl.getImps();
    }

    public void SPARQLfromSWRLBody(DefaultRDFList atomlist, SPARQLCompositor com) {
        Object obj = atomlist.getFirst();
        if (obj != null) {
            if (obj.getClass().equals(DefaultSWRLClassAtom.class)) {
                SWRLClassAtom c = (SWRLClassAtom) obj;
                com.addVar(c.getArgument1().getLocalName());
                com.addBodyAtom("?" + c.getArgument1().getLocalName() + " rdf:type <" + c.getClassPredicate().getURI() + ">");
            } else if (obj.getClass().equals(DefaultSWRLDatavaluedPropertyAtom.class)) {
                SWRLDatavaluedPropertyAtom d = (SWRLDatavaluedPropertyAtom) obj;
                com.addVar(d.getArgument1().getLocalName());
                if (d.getArgument2().getBrowserText().contains("?")) {
                    com.addVar(d.getArgument2().getBrowserText().substring(1));
                }
                com.addBodyAtom("?" + d.getArgument1().getLocalName() + " <" + d.getPropertyPredicate().getURI() + "> " + d.getArgument2().getBrowserText());
            } else if (obj.getClass().equals(DefaultSWRLIndividualPropertyAtom.class)) {
                SWRLIndividualPropertyAtom i = (SWRLIndividualPropertyAtom) obj;
                com.addVar(i.getArgument1().getLocalName());
                if (!i.getArgument2().getBrowserText().contains("?")) {
                    com.addBodyAtom("?" + i.getArgument1().getLocalName() + " <" + i.getPropertyPredicate().getURI() + "> <" + i.getArgument2().getURI() + ">");
                } else {
                    com.addVar(i.getArgument2().getLocalName());
                    com.addBodyAtom("?" + i.getArgument1().getLocalName() + " <" + i.getPropertyPredicate().getURI() + "> ?" + i.getArgument2().getLocalName());
                }
            }
            DefaultRDFList restAtomList = (DefaultRDFList) atomlist.getRest();
            if (restAtomList != null) {
                SPARQLfromSWRLBody(restAtomList, com);
            }
        }
    }

    public void SPARQLfromSWRLHead(DefaultRDFList atomlist, SPARQLCompositor com) {
        Object obj = atomlist.getFirst();
        if (obj != null) {
            if (obj.getClass().equals(DefaultSWRLClassAtom.class)) {
                SWRLClassAtom c = (SWRLClassAtom) obj;
                com.addHeadAtom("?" + c.getArgument1().getLocalName() + " rdf:type <" + c.getClassPredicate().getURI() + ">");
            } else if (obj.getClass().equals(DefaultSWRLDatavaluedPropertyAtom.class)) {
                SWRLDatavaluedPropertyAtom d = (SWRLDatavaluedPropertyAtom) obj;
                com.addHeadAtom("?" + d.getArgument1().getLocalName() + " <" + d.getPropertyPredicate().getURI() + "> " + d.getArgument2().getBrowserText());
            } else if (obj.getClass().equals(DefaultSWRLIndividualPropertyAtom.class)) {
                SWRLIndividualPropertyAtom i = (SWRLIndividualPropertyAtom) obj;
                if (!i.getArgument2().getBrowserText().contains("?")) {
                    com.addHeadAtom("?" + i.getArgument1().getLocalName() + " <" + i.getPropertyPredicate().getURI() + "> <" + i.getArgument2().getURI() + ">");
                } else {
                    com.addHeadAtom("?" + i.getArgument1().getLocalName() + " <" + i.getPropertyPredicate().getURI() + "> ?" + i.getArgument2().getLocalName());
                }
            } else if (obj.getClass().equals(DefaultSWRLSameIndividualAtom.class)) {
                SWRLSameIndividualAtom s = (SWRLSameIndividualAtom) obj;
                com.addHeadAtom("?" + s.getArgument1().getLocalName() + " owl:sameAs ?" + s.getArgument2().getLocalName());
            }
            DefaultRDFList restAtomList = (DefaultRDFList) atomlist.getRest();
            if (restAtomList != null) {
                SPARQLfromSWRLBody(restAtomList, com);
            }
        }
    }

    public Collection<SPARQLCompositor> getSPARQLCompositors() {
        return compositors;
    }
}

class SPARQLCompositor {

    private ArrayList<String> varList;
    private ArrayList<String> headAtomtList;
    private String bodySPARQL;
    private String headSPARQL;
    private ArrayList<String> bodyAtomtList;
    private ResultSet queryResult;
    private String insertResult;

    public SPARQLCompositor() {
        varList = new ArrayList<String>();
        headAtomtList = new ArrayList<String>();
        bodyAtomtList = new ArrayList<String>();
    }

    public void addVar(String v) {
        varList.add(v);
    }

    public void addHeadAtom(String atom) {
        headAtomtList.add(atom);
    }

    public void addBodyAtom(String atom) {
        bodyAtomtList.add(atom);
    }

    public void parseBodySPARQL() {
        String ret = "";
        String select = "";
        for (String s : varList) {
            select += (!select.contains(s)) ? " ?" + s : "";
        }
        String where = "";
        for (String s : bodyAtomtList) {
            where += (where.equals("")) ? s : ". \r\n" + s;
        }
        ret = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \r\n"
                + "SELECT " + select + "\r\n"
                + "WHERE \r\n"
                + "{" + where + "}";
        bodySPARQL = ret;
    }

    public void parseHeadSPARQL() {
        String ret = "";
        for (String s : headAtomtList) {
            ret += (ret.equals("")) ? s : ". \r\n" + s;
        }
        headSPARQL = ret;
    }

    public void callSelectRDFStore() {
        try {
            String fullmsg = "query=" + URLEncoder.encode(bodySPARQL, "UTF-8") + "&namespace=&xhtml=true";
            Socket sock = new Socket("127.0.0.1", 8080);
            String msg = "POST /bigdata/sparql HTTP/1.0\r\n"
                    + "Host: 127.0.0.1:8080\r\n"
                    + "Content-Length: " + fullmsg.length() + "\r\n"
                    + "Content-Type: application/x-www-form-urlencoded\r\n"
                    + "\r\n"
                    + fullmsg;
            PrintWriter os = new PrintWriter(sock.getOutputStream());
            os.println(msg);
            os.flush();

            InputStreamReader is = new InputStreamReader(sock.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(is);
            String outmsg = "";
            String line;
            while ((line = br.readLine()) != null) {
                outmsg += line;
            }

            os.close();
            is.close();
            sock.close();

            queryResult = ResultSetFactory.fromXML("<?xml version=" + outmsg.split("<?xml version=")[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void callInsertRDFStore() {
        String fullhead = "";
        while (queryResult.hasNext()) {
            String linehead = headSPARQL;
            Binding bi = queryResult.nextBinding();
            for (String s : varList) {
                Node n = bi.get(Var.alloc(s));
                if (n != null) {
                    if (n.isURI()) {
                        linehead = linehead.replace("?" + s, "<" + n.getURI() + ">");
                    } else {
                        linehead = linehead.replace("?" + s, n.getLiteralLexicalForm());
                    }
                }
            }
            fullhead += (fullhead.equals("")) ? linehead : ".\r\n" + linehead;
        }

        if (!fullhead.equals("")) {
            fullhead = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\r\n"
                    + "INSERT DATA {\r\n"
                    + fullhead
                    + "\r\n}";
            try {
                String fullmsg = "update=" + URLEncoder.encode(fullhead, "UTF-8");
                String msg = "POST /bigdata/sparql HTTP/1.0\r\n"
                        + "Host: 127.0.0.1:8080\r\n"
                        + "Content-Length: " + fullmsg.length() + "\r\n"
                        + "Content-Type: application/x-www-form-urlencoded\r\n"
                        + "\r\n"
                        + fullmsg;

                headSPARQL = fullhead;
                //System.out.println(fullhead);

                Socket sock = new Socket("127.0.0.1", 8080);
                PrintWriter os = new PrintWriter(sock.getOutputStream());
                os.println(msg);
                os.flush();

                InputStreamReader is = new InputStreamReader(sock.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(is);
                String outmsg = "";
                String line;
                while ((line = br.readLine()) != null) {
                    outmsg += line;
                }

                os.close();
                is.close();
                sock.close();

                insertResult = outmsg;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getBodySPARQL() {
        return bodySPARQL;
    }

    public String getHeadSPARQL() {
        return headSPARQL;
    }

    public String getInsertResult() {
        return insertResult;
    }
}
