/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.OWL;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.JessBridgeCreator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.mortbay.util.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Administrator
 */
public class WSExecution {

    private OntModel owls;
    private Property owls_composedOf;
    private Property owls_components;
    private OntModel data;
    private String oper_series = "";
    private String oper_series2 = "";
    private String process;
    private Property owls_Lfirst;
    private Property owls_Lrest;
    private Property owls_process;
    private Property owls_owlsProcess;
    private Property owls_wsdlOper;
    private Property owls_groundingOper;
    private Property owls_wsdlinput;
    private Property owls_wsdloutput;
    private Property owls_owlsParam;
    private Property owls_messpart;
    private Property owls_paramType;
    private Property owls_paramClsType;
    private OWLModel protege_data;
    private Thread[] threds;
    private ResourceImpl[] thred_seq;
    private String casename;
    private String exns;

    public String getOper_series() {
        return oper_series;
    }

    public void setOper_series(String oper_series) {
        this.oper_series = oper_series;
    }

    public String getOper_series2() {
        return oper_series2;
    }

    public void setOper_series2(String oper_series2) {
        this.oper_series2 = oper_series2;
    }

    public String getCasename() {
        return casename;
    }

    public void setCasename(String casename) {
        this.casename = casename;
    }

    public static void main(String[] args) throws FileNotFoundException {

        String p = "http://202.28.94.50/ontologies/composition.owl#Composition3";
        //String p = "http://61.19.108.37/kkuproject/services/selectp_ptdata.owl#selectp_ptdata";
        OntModel mod = ModelFactory.createOntologyModel();
        mod.read("http://202.28.94.50/ontologies/healthcare/general.owl");
        mod.read("file:/G:/work/recent_work/ws/wsdl/local1/local.owl");
        mod.read("file:/G:/work/recent_work/ws/wsdl/local2/local.owl");
        mod.read("file:/G:/work/recent_work/owl/2012-06-13/ii-rules1.owl");
        mod.setNsPrefix("", "http://202.28.94.50/ontologies/healthcare/dat.owl#");

        IndividualImpl pa = (IndividualImpl) mod.createIndividual("http://202.28.94.50/ontologies/healthcare/dat.owl#_3302100349475", mod.getOntClass("http://202.28.94.50/ontologies/healthcare/general.owl#Patient"));
        pa.addRDFType(mod.getOntClass("http://202.28.94.50/ontologies/healthcare/general.owl#Person"));
        pa.addProperty(mod.getProperty("http://202.28.94.50/ontologies/healthcare/general.owl#citizen_id"), "3302100349475");

        long startTime = System.currentTimeMillis();
        StringWriter sw = new StringWriter();
        mod.write(sw, "RDF/XML-ABBREV");
        //JessBridgeCreator jess = new JessBridgeCreator();
        //JessBridgeCreator jess_out = new JessBridgeCreator();
        try {
            /*
             * OWLModel owlm = ProtegeOWL.createJenaOWLModelFromReader(new
             * StringReader(sw.toString()));
             * owlm.getNamespaceManager().setDefaultNamespace("http://202.28.94.50/ontologies/healthcare/dat.owl#");
             * SWRLRuleEngineBridge swrl = jess.create(owlm); swrl.infer();
             */
            ////System.out.println(swrl.getNumberOfInferredAxioms());
            /*
             * OntModel mi = owlm.getOntModel(); StringWriter sw2 = new
             * StringWriter(); mi.write(sw2, "RDF/XML-ABBREV");
             */
            /*
             * Iterator it =
             * mi.listIndividuals(mi.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
             * Main mn = new Main();
             * //System.out.println(mn.CompositeRecursive(1, new ArrayList(),
             * it, mi, 0));
             */
            /*
             * OntModel dat = ModelFactory.createOntologyModel(); dat.read(new
             * StringReader(sw2.toString()), null);
             */
            //System.out.println(sw2.toString());
            //dat.setNsPrefix("", "http://202.28.94.50/ontologies/healthcare/dat.owl#");
            WSExecution wsexe = new WSExecution(mod, null, null, null);
            wsexe.execute(wsexe.getProcessSequenceInd(p));
            //wsexe.executeAtomic(p);
            wsexe.getData().write(new FileOutputStream(new File("C:\\data-output.owl")), "RDF/XML-ABBREV");
            /*
             * StringWriter sw3 = new StringWriter(); wsexe.getData().write(sw3,
             * "RDF/XML-ABBREV"); OWLModel owlm_out =
             * ProtegeOWL.createJenaOWLModelFromReader(new
             * StringReader(sw3.toString())); SWRLRuleEngineBridge swrl_out =
             * jess_out.create(owlm_out); swrl_out.infer(); OntModel m_out =
             * owlm_out.getOntModel(); //m_out.write(System.out,
             * "RDF/XML-ABBREV"); m_out.write(new FileOutputStream(new
             * File("C:\\data-output-infer.owl")), "RDF/XML-ABBREV");
             * //System.out.println(wsexe.oper_series2);
             * //System.out.println(wsexe.oper_series);
             */
            long endTime = System.currentTimeMillis();
            System.out.println("Total elapsed time in WS Excution is :" + (endTime - startTime) + "millisec.");
        } catch (Exception ex) {
            Logger.getLogger(_WSComposition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public WSExecution(OntModel dat, String cn, OntModel owlsm, String ns) {
        this.exns = ns;
        StringWriter sw = new StringWriter();

        dat.write(sw, "RDF/XML-ABBREV");
        //System.out.println(sw.toString());
        JessBridgeCreator jess = new JessBridgeCreator();
        //JessBridgeCreator jess_out = new JessBridgeCreator();
        try {
            OWLModel owlm = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(sw.toString()));
            int cc = 0;
            for (String o : owlm.getAllImports()) {
                owlm.getNamespaceManager().setPrefix(o + "#", "ii" + cc);
                cc++;
            }
            if (owlm.getNamespaceManager().getDefaultNamespace() != null) {
                owlm.getNamespaceManager().setPrefix("http://202.28.94.50/ontologies/healthcare/data/" + ns + "#", "ii" + cc);
            } else {
                owlm.getNamespaceManager().setDefaultNamespace("http://202.28.94.50/ontologies/healthcare/data/" + ns + "#");
            }
            SWRLRuleEngineBridge swrl = jess.create(owlm);
            swrl.infer();
            this.data = owlm.getOntModel();
            //System.out.println(swrl.getNumberOfInferredAxioms());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.casename = cn;
        if (owlsm != null) {
            this.owls = owlsm;
        } else {
            this.owls = ModelFactory.createOntologyModel();
            this.owls.read("file:/C:/composition.owl");
        }
        try {
            this.owls.read(this.getClass().getResource("owls/Process.owl").openStream(), null);
            this.owls.read(this.getClass().getResource("owls/Grounding.owl").openStream(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * try { if (this.casename != null) { this.owls.read(new
         * FileInputStream(new File("C:\\evaluation\\composition\\" +
         * this.casename + "_composition.owl")), null); } else {
         * this.owls.read(new FileInputStream(new File("C:\\composition.owl")),
         * null); } } catch (FileNotFoundException ex) {
         * //Logger.getLogger(WSExecution.class.getName()).log(Level.SEVERE,
         * null, ex); } String[] sw = new
         * String[]{"http://61.19.108.39/wsengine/loc1_getPatient.owl",
         * "http://61.19.108.39/wsengine/loc1_getDiagnosis.owl",
         * "http://61.19.108.39/wsengine/loc1_getDoctor.owl",
         * "http://61.19.108.37/wssoap/loc2_get_drdx.owl",
         * "http://61.19.108.37/wssoap/loc2_get_dx.owl",
         * "http://61.19.108.37/wssoap/loc2_get_dxdiag.owl",
         * "http://61.19.108.37/wssoap/loc2_get_patient.owl"}; for (int i = 0; i
         * < sw.length; i++) { this.owls.read(sw[i]); }
         */
        this.owls_composedOf = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#composedOf");
        this.owls_components = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#components");
        this.owls_Lfirst = this.owls.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#first");
        this.owls_Lrest = this.owls.getProperty("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#rest");
        this.owls_process = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#process");
        this.owls_owlsProcess = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Grounding.owl#owlsProcess");
        this.owls_wsdlOper = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Grounding.owl#wsdlOperation");
        this.owls_groundingOper = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Grounding.owl#operation");
        this.owls_wsdlinput = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Grounding.owl#wsdlInput");
        this.owls_wsdloutput = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Grounding.owl#wsdlOutput");
        this.owls_owlsParam = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Grounding.owl#owlsParameter");
        this.owls_paramType = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterType");
        this.owls_paramClsType = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Process.owl#parameterClassType");
        this.owls_messpart = this.owls.getProperty("http://202.28.94.50/owl-s/1.1/mod/Grounding.owl#wsdlMessagePart");
    }

    public void executeAtomic(String s) {
        IndividualImpl ap = (IndividualImpl) this.owls.getIndividual(s);
        try {
            invoke(ap);
        } catch (Exception ex) {
            Logger.getLogger(WSExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void execute(Object s) throws Exception {
        ResourceImpl per = null;
        ResourceImpl rest = null;
        System.out.println(s.getClass() + ":" + s.toString());
        if (s.getClass().equals(com.hp.hpl.jena.ontology.impl.IndividualImpl.class)) {
            IndividualImpl seq = (IndividualImpl) s;
            System.out.println(seq.getRDFType().getURI());
            if (seq.getRDFType().getURI().equals("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence")) {
                IndividualImpl list = (IndividualImpl) this.owls.getIndividual(seq.getPropertyValue(this.owls_components).toString());
                per = (ResourceImpl) list.getPropertyValue(this.owls_Lfirst);
                rest = (ResourceImpl) list.getPropertyValue(this.owls_Lrest);
            } else if (seq.getRDFType().getURI().equals("http://202.28.94.50/owl-s/1.1/mod/Process.owl#ControlConstructList")) {
                IndividualImpl list = (IndividualImpl) seq;
                per = (ResourceImpl) list.getPropertyValue(this.owls_Lfirst);
                rest = (ResourceImpl) list.getPropertyValue(this.owls_Lrest);
            } else if (seq.getRDFType().getURI().equals("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Perform")) {
                per = seq;
            }
        } else if (s.getClass().equals(com.hp.hpl.jena.ontology.impl.OntResourceImpl.class) || s.getClass().equals(com.hp.hpl.jena.rdf.model.impl.ResourceImpl.class)) {
            ResourceImpl seq = (ResourceImpl) s;
            ResourceImpl list = seq;
            //System.out.println(seq.hasProperty(this.owls.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), this.owls.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence")));
            if (seq.hasProperty(this.owls.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), this.owls.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Perform"))) {
                per = seq;
            } else if (seq.hasProperty(this.owls.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), this.owls.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"))) {
                list = (ResourceImpl) this.owls.listObjectsOfProperty(seq, this.owls_components).next();
            }
            if (this.owls.listObjectsOfProperty(list, this.owls_Lfirst).hasNext()) {
                per = (ResourceImpl) this.owls.listObjectsOfProperty(list, this.owls_Lfirst).next();
            }
            if (this.owls.listObjectsOfProperty(list, this.owls_Lrest).hasNext()) {
                rest = (ResourceImpl) this.owls.listObjectsOfProperty(list, this.owls_Lrest).next();
            }
        }

        if (per != null) {
            System.out.println(per.getProperty(this.owls.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")).asTriple());
            if (per.hasProperty(this.owls.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), this.owls.getIndividual("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Perform"))) {
                Iterator it = this.owls.listObjectsOfProperty(per, this.owls_process);
                String nstr = it.next().toString();
                IndividualImpl apro = (IndividualImpl) this.owls.getIndividual(nstr);
                System.out.println("Process :" + nstr);
                this.invoke(apro);
            } else if (per.hasProperty(this.owls.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), this.owls.getIndividual("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Split-Join"))) {
                if (this.owls.listObjectsOfProperty(per, this.owls_components).hasNext()) {
                    ResourceImpl spj_bag = (ResourceImpl) this.owls.listObjectsOfProperty(per, this.owls_components).next();
                    //ArrayList spj_seqs = new ArrayList();
                    Iterator it0 = this.owls.listObjectsOfProperty(spj_bag, this.owls_Lfirst);
                    while (it0.hasNext()) {
                        //spj_seqs.add((ResourceImpl) it0.next());
                        this.execute((ResourceImpl) it0.next());
                    }
                    Iterator it = this.owls.listObjectsOfProperty(spj_bag, this.owls_Lrest);
                    while (it.hasNext()) {
                        //spj_seqs.add((ResourceImpl) it.next());
                        this.execute((ResourceImpl) it.next());
                    }
                    /*this.threds = new Thread[spj_seqs.size()];
                     this.thred_seq = new ResourceImpl[spj_seqs.size()];
                     for (int i = 0; i < spj_seqs.size(); i++) {
                     this.thred_seq[i] = ((ResourceImpl) spj_seqs.get(i));
                     Thread t = new Thread(this, String.valueOf(i));
                     t.start();
                     this.threds[i] = t;
                     this.execute(spj_seqs.get(i));
                     }*/
                    /*boolean complete = true;
                     try {
                     do {
                     boolean tmp_state = true;
                     for (int j = 0; j < this.threds.length; j++) {
                     if (this.threds[j].isAlive()) {
                     tmp_state = tmp_state & false;
                     } else {
                     tmp_state = tmp_state & true;
                     }
                     }
                     complete = tmp_state;
                     Thread.sleep(500);
                     } while (!complete);
                     } catch (Exception ex) {
                     ex.printStackTrace(System.out);
                     }*/
                }
            }
        }

        if (rest != null) {
            this.execute(rest);
        }

    }

    public void invoke(ResourceImpl ap) throws Exception {
        Iterator it2 = this.owls.listSubjectsWithProperty(this.owls_owlsProcess, ap);
        IndividualImpl wsdlg = (IndividualImpl) this.owls.getIndividual(it2.next().toString());
        //System.out.println(wsdlg.getURI());
        IndividualImpl oref = (IndividualImpl) this.owls.getIndividual(wsdlg.getPropertyValue(this.owls_wsdlOper).toString());
        ArrayList message_list = this.wsdlBinding(oref);
        for (int ei = 0; ei < message_list.size(); ei++) {
            System.out.println("Start invoke: " + ap.getURI() + " " + (ei + 1) + " time");
            InputBinding inpb = (InputBinding) message_list.get(ei);
            String message = inpb.getMess();
            String addr = message.split("\\^\\^")[0];
            String host_begin = addr.substring(addr.indexOf("http://") + 7);
            String mess_out = "";
            try {
                Socket sock = new Socket(host_begin.substring(0, host_begin.indexOf("/")), 80);
                /*
                 * DataOutputStream os = new
                 * DataOutputStream(sock.getOutputStream());
                 * os.writeUTF("test");
                 */
                String cont = "<?xml version='1.0'?>" + message.split("\\^\\^")[1];
                //this.oper_series += inpb.getOper() + "\r\n";
                PrintWriter os = new PrintWriter(sock.getOutputStream());
                os.println("POST " + host_begin.substring(host_begin.indexOf("/")) + " HTTP/1.0");
                os.println("Content-Type: text/xml");
                os.println("Content-Length: " + cont.length());
                os.println("");
                os.println(cont);
                os.flush();

                cont = cont.replaceAll("<(?!/)", "\r\n<");
                System.out.println(inpb.getOper() + "\r\n" + "Message in: " + cont);
                this.oper_series += inpb.getOper() + "\r\n" + "Message in: " + cont + "\r\n\r\n";
                /*
                 * DataInputStream oi = new
                 * DataInputStream(sock.getInputStream());
                 * //System.out.println((String)oi.readLine());
                 */
                InputStreamReader oi = new InputStreamReader(sock.getInputStream());
                BufferedReader bf = new BufferedReader(oi);
                String line;
                while ((line = bf.readLine()) != null) {
                    mess_out += line;
                }

                os.close();
                oi.close();
                sock.close();
            } catch (UnknownHostException ex) {
                ex.printStackTrace(System.out);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }

            mess_out = (mess_out.split("<?xml version=").length == 1) ? mess_out : "<?xml version=" + mess_out.split("<?xml version=")[1];
            System.out.println(inpb.getOper() + "\r\n" + "Message out: " + mess_out);
            this.oper_series += inpb.getOper() + "\r\n" + "Message out: " + mess_out.replaceAll("<(?!/)", "\r\n<") + "\r\n\r\n";
            try {
                DocumentBuilderFactory buildf = DocumentBuilderFactory.newInstance();
                DocumentBuilder build = buildf.newDocumentBuilder();
                Document doc = build.parse(new InputSource(new StringReader(mess_out)));
                NodeList nds = doc.getChildNodes();
                for (int i = 0; i < nds.getLength(); i++) {
                    if (nds.item(i).getNodeType() == 1 && nds.item(i).getNodeName().equals("SOAP-ENV:Envelope")) {
                        NodeList nds1 = nds.item(i).getChildNodes();
                        for (int j = 0; j < nds1.getLength(); j++) {
                            if (nds1.item(j).getNodeType() == 1 && nds1.item(j).getNodeName().equals("SOAP-ENV:Body")) {
                                NodeList nds2 = nds1.item(j).getChildNodes();
                                for (int k = 0; k < nds2.getLength(); k++) {
                                    NodeList nds3 = nds2.item(k).getChildNodes();
                                    for (int l = 0; l < nds3.getLength(); l++) {
                                        if (nds3.item(l).getNodeType() == 1) {
                                            boolean complex = (nds3.item(l).getNodeName().substring(nds3.item(l).getNodeName().length() - 4).equals("List")) ? false : true;
                                            NodeList nds3chks = nds3.item(l).getChildNodes();
                                            /*
                                             * for (int ll = 0; ll <
                                             * nds3chks.getLength(); ll++) { if
                                             * (nds3chks.item(ll).getNodeType()
                                             * == 1) { NodeList nds32chks =
                                             * nds3chks.item(ll).getChildNodes();
                                             * for (int lll = 0; lll <
                                             * nds32chks.getLength(); lll++) {
                                             * if
                                             * (nds32chks.item(lll).getNodeType()
                                             * == 1) { complex = true; } } } }
                                             */
                                            OntClass cls = null;
                                            Iterator itw = wsdlg.listPropertyValues(this.owls_wsdloutput);
                                            ArrayList itw_arr = new ArrayList();
                                            while (itw.hasNext()) {
                                                Object oo = itw.next();
                                                //System.out.println(oo.toString());
                                                itw_arr.add(oo);
                                            }
                                            for (int ll = 0; ll < nds3chks.getLength(); ll++) {
                                                ////System.out.println(nds3chks.item(ll).getNodeName());
                                                if (nds3chks.item(ll).getNodeType() == 1) {
                                                    String genid = "";
                                                    String genid2 = "";
                                                    HashMap props = new HashMap();
                                                    ArrayList propsV = new ArrayList();
                                                    NodeList nds4 = nds3chks.item(ll).getChildNodes();
                                                    for (int m = 0; m < nds4.getLength(); m++) {
                                                        if (nds4.item(m).getNodeType() == 1) {
                                                            String nds_name = (nds4.item(m).getNodeName().contains(":")) ? nds4.item(m).getNodeName().split(":")[1] : nds4.item(m).getNodeName();
                                                            if (nds4.item(m).getChildNodes().item(0) != null) {
                                                                propsV.add(nds_name + "::=" + nds4.item(m).getChildNodes().item(0).getNodeValue());
                                                                //genid += nds_name + "-" + nds4.item(m).getChildNodes().item(0).getNodeValue() + "_";
                                                                genid += nds4.item(m).getChildNodes().item(0).getNodeValue() + "_";
                                                                System.out.println(nds_name + "::=" + nds4.item(m).getChildNodes().item(0).getNodeValue());
                                                            }
                                                        }
                                                    }
                                                    //genid = (genid.length() > 100) ? genid.substring(0, 100) : genid;
                                                    for (int jj = 0; jj < itw_arr.size(); jj++) {
                                                        IndividualImpl outm = (IndividualImpl) this.owls.getIndividual(itw_arr.get(jj).toString());
                                                        String nd_name = (nds3chks.item(ll).getNodeName().contains(":")) ? nds3chks.item(ll).getNodeName().split(":")[1] : nds3chks.item(ll).getNodeName();
                                                        //System.out.println(outm.getPropertyValue(this.owls_messpart).toString().split("\\^\\^")[0] + ":" + (nd_name));
                                                        if (outm.getPropertyValue(this.owls_messpart).toString().split("\\^\\^")[0].equals(nd_name)) {
                                                            IndividualImpl param = (IndividualImpl) this.owls.getIndividual(outm.getPropertyValue(this.owls_owlsParam).toString());
                                                            if (param.getPropertyValue(this.owls_paramClsType) != null) {
                                                                cls = this.owls.getOntClass(param.getPropertyValue(this.owls_paramClsType).toString());
                                                                //genid = cls.getLocalName() + "_" + genid;
                                                                genid = "_" + genid;
                                                                genid2 = nd_name + "_" + cls.getLocalName() + genid2;
                                                                //System.out.println("Class:" + cls.getURI());
                                                            }
                                                        } else {
                                                            for (int n = 0; n < propsV.size(); n++) {
                                                                if (outm.getPropertyValue(this.owls_messpart).toString().split("\\^\\^")[0].equals(propsV.get(n).toString().split("::=")[0])) {
                                                                    IndividualImpl param = (IndividualImpl) this.owls.getIndividual(outm.getPropertyValue(this.owls_owlsParam).toString());
                                                                    if (param.getPropertyValue(this.owls_paramType) != null) {
                                                                        Property prop = this.owls.getProperty(param.getPropertyValue(this.owls_paramType).toString());
                                                                        ArrayList prop_arr = new ArrayList();
                                                                        /*Iterator pit = param.listPropertyValues(this.owls_paramType);
                                                                         while (pit.hasNext()) {
                                                                         ResourceImpl obj = (ResourceImpl) pit.next();
                                                                         if (this.owls.getOntClass(obj.getURI()) != null) {
                                                                         prop_arr.add(obj.getURI());
                                                                         }
                                                                         }*/
                                                                        if (prop_arr.size() > 1) {
                                                                            props.put(prop_arr, propsV.get(n).toString().split("::=")[1]);
                                                                        } else {
                                                                            props.put(prop, propsV.get(n).toString().split("::=")[1]);
                                                                        }
                                                                        if (this.owls.getDatatypeProperty(prop.getURI()).hasRDFType(OWL.FunctionalProperty)) {
                                                                            genid2 += "_" + propsV.get(n).toString().split("::=")[1];
                                                                        }
                                                                        //System.out.println(prop.getURI() + ":" + propsV.get(n).toString().split("::=")[1]);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    ////System.out.println(cls);
                                                    //this.modelTransform();
                                                    String uri = (this.casename != null) ? "http://202.28.94.50/ontologies/" + this.casename + "/data/" + exns + "#" : "http://202.28.94.50/ontologies/healthcare/data/" + exns + "#";
                                                    HashMap itmp_prop = inpb.getProps();
                                                    /*
                                                     * if (itmp_prop != null) {
                                                     * Iterator itmp_it =
                                                     * itmp_prop.keySet().iterator();
                                                     * while (itmp_it.hasNext())
                                                     * { Property pk =
                                                     * (Property)
                                                     * itmp_it.next(); String pv
                                                     * = (String)
                                                     * itmp_prop.get(pk); if
                                                     * (pv.contains("<")) { pv =
                                                     * pv.substring(pv.indexOf("<")
                                                     * + 1,
                                                     * pv.lastIndexOf(">")); pv
                                                     * =
                                                     * pv.substring(pv.indexOf(">")
                                                     * + 1,
                                                     * pv.lastIndexOf("<")); }
                                                     * itmp_prop.put(pk, pv);
                                                     * uri += pv;
                                                     * //System.out.println(uri);
                                                     * ////System.out.println(pk
                                                     * + ":" + pv); } } else {
                                                     * uri += "GEN:" + new
                                                     * Date().getTime() + ":" +
                                                     * Math.random(); }
                                                     */
                                                    //uri += genid;
                                                    //uri += (complex) ? "GEN" + new Date().getTime() + (int) (Math.random() * 100) : genid;
                                                    genid = genid.replaceAll(" ", "");
                                                    genid = genid.replaceAll("/", "_");
                                                    genid = genid.replaceAll("\\%", "-p-");
                                                    genid = genid.replaceAll("\\$", "-d-");
                                                    genid = genid.replaceAll("#", "-S-");
                                                    genid = genid.replaceAll("\\?", "-q-");
                                                    genid = genid.replaceAll("\\+", "--");
                                                    genid = genid.replaceAll("\\*", "-s-");
                                                    genid = genid.replaceAll(":", "-c-");

                                                    genid2 = genid2.replaceAll(" ", "");
                                                    genid2 = genid2.replaceAll("/", "_");
                                                    genid2 = genid2.replaceAll("\\%", "-p-");
                                                    genid2 = genid2.replaceAll("\\$", "-d-");
                                                    genid2 = genid2.replaceAll("#", "-S-");
                                                    genid2 = genid2.replaceAll("\\?", "-q-");
                                                    genid2 = genid2.replaceAll("\\+", "--");
                                                    genid2 = genid2.replaceAll("\\*", "-s-");
                                                    genid2 = genid2.replaceAll(":", "-c-");

                                                    uri += (complex) ? genid2 : genid;
                                                    IndividualImpl itmp = ((IndividualImpl) this.data.getIndividual(uri) != null) ? (IndividualImpl) this.data.getIndividual(uri) : (IndividualImpl) this.data.createIndividual(uri, cls);

                                                    /*
                                                     * Iterator itmp_it2 =
                                                     * itmp_prop.keySet().iterator();
                                                     * while
                                                     * (itmp_it2.hasNext()) {
                                                     * Property pk = (Property)
                                                     * itmp_it2.next(); String
                                                     * pv = (String)
                                                     * itmp_prop.get(pk); pv =
                                                     * pv.substring(pv.indexOf("<")
                                                     * + 1,
                                                     * pv.lastIndexOf(">")); pv
                                                     * =
                                                     * pv.substring(pv.indexOf(">")
                                                     * + 1,
                                                     * pv.lastIndexOf("<")); pv
                                                     * = pv.replaceAll("/",
                                                     * "_");
                                                     * itmp.addProperty(pk, pv);
                                                     * }
                                                     */
                                                    //IndividualImpl indout = this.outputRecursive(itmp, itmp_prop, cls);
                                                    IndividualImpl indout = this.outputRecursive(itmp, props, cls);
                                                    ////System.out.println(indout.getURI());
                                                        /*
                                                     * if (indout != null) {
                                                     * Iterator stmt =
                                                     * indout.listProperties();
                                                     * while (stmt.hasNext()) {
                                                     * this.dataOut.add((Statement)
                                                     * stmt.next()); } }
                                                     * this.dataOut.write(System.out,
                                                     * "RDF/XML-ABBREV");
                                                     */
                                                }
                                            }
                                            StringWriter sw = new StringWriter();
                                            //this.data.write(System.out, "RDF/XML-ABBREV");
                                            this.data.write(sw, "RDF/XML-ABBREV");
                                            JessBridgeCreator jess = new JessBridgeCreator();
                                            try {
                                                OWLModel owlm = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(sw.toString()));
                                                SWRLRuleEngineBridge swrl = jess.create(owlm);
                                                swrl.infer();
                                                //System.out.println(swrl.getNumberOfInferredAxioms());
                                                this.data = owlm.getOntModel();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }

    public ArrayList wsdlBinding(IndividualImpl oper) {
        ArrayList ret = new ArrayList();
        String wsdl = oper.getPropertyValue(this.owls_groundingOper).toString().split("#")[0];
        String addr = "";
        try {
            DocumentBuilderFactory builderf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderf.newDocumentBuilder();
            Document doc = builder.parse(new URL(wsdl).openStream());
            NodeList nds = doc.getChildNodes();
            Node def = null;
            HashMap pfxs = new HashMap();
            String ws_ns = "";
            ArrayList mess = new ArrayList();
            ArrayList type = new ArrayList();
            Node op = null;
            Node in = null;
            Node out = null;
            String out_mess = "";
            HashMap schems = new HashMap();
            for (int i = 0; i < nds.getLength(); i++) {
                if (nds.item(i).getNodeName().contains(":") && nds.item(i).getNodeName().split(":")[1].equals("definitions")) {
                    def = nds.item(i);
                    NamedNodeMap nm = def.getAttributes();
                    for (int j = 0; j < nm.getLength(); j++) {
                        Node def_att = nm.item(j);
                        if (def_att.getNodeName().contains(":")) {
                            pfxs.put(def_att.getNodeValue(), def_att.getNodeName().split(":")[1]);
                        } else if (def_att.getNodeName().equals("targetNamespace")) {
                            ws_ns = def_att.getNodeValue();
                        }
                    }
                }
            }
            NodeList nds_def = def.getChildNodes();
            for (int j = 0; j < nds_def.getLength(); j++) {
                if (nds_def.item(j).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":portType")) {
                    NodeList nds_bin = nds_def.item(j).getChildNodes();
                    for (int k = 0; k < nds_bin.getLength(); k++) {
                        if (nds_bin.item(k).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":operation")) {
                            NamedNodeMap oper_att = nds_bin.item(k).getAttributes();
                            if (oper_att.getNamedItem("name").getNodeValue().equals(oper.getPropertyValue(this.owls_groundingOper).toString().split("#")[1].split("\\^\\^")[0])) {
                                op = nds_bin.item(k);
                            }
                        }
                    }
                } else if (nds_def.item(j).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":message")) {
                    mess.add(nds_def.item(j));
                } else if (nds_def.item(j).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":types")) {
                    NodeList nds_type = nds_def.item(j).getChildNodes();
                    for (int k = 0; k < nds_type.getLength(); k++) {
                        if (nds_type.item(k).getNodeName().equals(pfxs.get("http://www.w3.org/2001/XMLSchema") + ":schema")) {
                            schems.put(nds_type.item(k).getAttributes().getNamedItem("targetNamespace"), nds_type.item(k));
                        }
                    }
                } else if (nds_def.item(j).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":service")) {
                    NodeList nds_serv = nds_def.item(j).getChildNodes();
                    for (int k = 0; k < nds_serv.getLength(); k++) {
                        if (nds_serv.item(k).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":port")) {
                            NodeList nds_port = nds_serv.item(k).getChildNodes();
                            for (int l = 0; l < nds_port.getLength(); l++) {
                                if (nds_port.item(l).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/soap/") + ":address")) {
                                    addr = nds_port.item(l).getAttributes().getNamedItem("location").getNodeValue();
                                }
                            }
                        }
                    }
                }
            }
            NodeList nds_op = op.getChildNodes();
            for (int i = 0; i < nds_op.getLength(); i++) {
                if (nds_op.item(i).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":input")) {
                    for (int j = 0; j < mess.size(); j++) {
                        Node m = (Node) mess.get(j);
                        if (m.getAttributes().getNamedItem("name").getNodeValue().equals(nds_op.item(i).getAttributes().getNamedItem("message").getNodeValue().split(":")[1])) {
                            in = m;
                        }
                    }
                } else if (nds_op.item(i).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":output")) {
                    for (int j = 0; j < mess.size(); j++) {
                        Node m = (Node) mess.get(j);
                        if (m.getAttributes().getNamedItem("name").getNodeValue().equals(nds_op.item(i).getAttributes().getNamedItem("message").getNodeValue().split(":")[1])) {
                            out = m;
                        }
                    }
                }
            }
            //System.out.println(op.getAttributes().getNamedItem("name").getNodeValue());
            ArrayList input_values = this.getPropertyValueOfProcess(oper);
            NodeList nds_in = in.getChildNodes();

            ArrayList com_tmp = new ArrayList();
            for (int j = 0; j < input_values.size(); j++) {
                HashMap input_values_map = (HashMap) input_values.get(j);
                String com_str = "";
                for (int i = 0; i < nds_in.getLength(); i++) {
                    if (nds_in.item(i).getNodeName().equals(pfxs.get("http://schemas.xmlsoap.org/wsdl/") + ":part")) {
                        String part_name = nds_in.item(i).getAttributes().getNamedItem("name").getNodeValue();
                        String part_type = nds_in.item(i).getAttributes().getNamedItem("type").getNodeValue();
                        if (pfxs.get("http://www.w3.org/2001/XMLSchema").equals(part_type.split(":")[0])) {
                            ArrayList p_value_list = (ArrayList) input_values_map.get(part_name);
                            //System.out.println(part_name + "::" + p_value_list.size());
                            if (p_value_list != null) {
                                String[] p_value_list_ar = p_value_list.get(0).toString().split("\\^\\^");
                                String p_value = (p_value_list_ar.length == 2) ? p_value_list_ar[1] : "";
                                com_str += (!com_str.equals("")) ? "<:>" : "";
                                com_str += "<" + part_name + ">" + p_value + "</" + part_name + ">^^" + p_value_list_ar[0];
                            }
                        }
                    }
                }
                com_tmp.add(com_str);
            }

            if (com_tmp.size() > 0) {
                for (int i = 0; i < com_tmp.size(); i++) {
                    HashMap in_p_tmp = new HashMap();
                    String[] prop_arr_str = (String[]) com_tmp.get(i).toString().split("<:>");
                    InputBinding inpb = new InputBinding();
                    String in_mess = "";
                    in_mess += "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                            + " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                            + " xmlns:tns=\"" + ws_ns + "\">"
                            + "<soapenv:Header/>"
                            + "<soapenv:Body>"
                            + "<tns:" + op.getAttributes().getNamedItem("name").getNodeValue() + ">";
                    for (int j = 0; j < prop_arr_str.length; j++) {
                        in_mess += prop_arr_str[j].split("\\^\\^")[0];
                        if (!prop_arr_str[j].split("\\^\\^")[0].equals("none")) {
                            Property p_nm = this.data.getProperty(prop_arr_str[j].split("\\^\\^")[1]);
                            in_p_tmp.put(p_nm, prop_arr_str[j].split("\\^\\^")[0]);
                        }
                        //System.out.println(p_nm.getURI() + "::" + prop_arr_str[j].split("\\^\\^")[0]);
                    }
                    in_mess += "</tns:" + op.getAttributes().getNamedItem("name").getNodeValue() + ">"
                            + "</soapenv:Body>"
                            + "</soapenv:Envelope>";
                    inpb.setMess(addr + "^^" + in_mess);
                    inpb.setProps(in_p_tmp);
                    inpb.setOper("\r\n" + wsdl + "#" + op.getAttributes().getNamedItem("name").getNodeValue());
                    ret.add(inpb);
                }
            } else {
                String in_mess = "";
                InputBinding inpb = new InputBinding();
                in_mess += "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:tns=\"" + ws_ns + "\">"
                        + "<soapenv:Header/>"
                        + "<soapenv:Body>"
                        + "<tns:" + op.getAttributes().getNamedItem("name").getNodeValue() + ">";
                in_mess += "</tns:" + op.getAttributes().getNamedItem("name").getNodeValue() + ">"
                        + "</soapenv:Body>"
                        + "</soapenv:Envelope>";
                inpb.setMess(addr + "^^" + in_mess);
                inpb.setOper("\r\n" + wsdl + "#" + op.getAttributes().getNamedItem("name").getNodeValue());
                ret.add(inpb);
            }
            this.oper_series2 += "," + op.getAttributes().getNamedItem("name").getNodeValue();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }

        return ret;
    }

    public OntModel outputBinding() {
        OntModel ret = null;

        return ret;
    }

    public OntModel classLinking(OntModel mod, IndividualImpl sbj, IndividualImpl obj) {
        OntModel ret = null;

        return ret;
    }

    public ArrayList getPropertyValueOfProcess(IndividualImpl oper) throws Exception {
        ArrayList ret = new ArrayList();
        ArrayList<IndividualImpl> inputs = new ArrayList<IndividualImpl>();
        ArrayList<IndividualImpl> inds = new ArrayList<IndividualImpl>();
        Iterator wg_it = this.owls.listSubjectsWithProperty(this.owls_wsdlOper, oper);
        ResourceImpl wg = (ResourceImpl) wg_it.next();
        IndividualImpl wsdl_g = (IndividualImpl) this.owls.getIndividual(wg.toString());
        Iterator it = wsdl_g.listPropertyValues(this.owls_wsdlinput);
        while (it.hasNext()) {
            IndividualImpl wsdl_i = (IndividualImpl) this.owls.getIndividual(it.next().toString());
            inputs.add(wsdl_i);
            IndividualImpl para = (IndividualImpl) this.owls.getIndividual(wsdl_i.getPropertyValue(this.owls_owlsParam).toString());
            Iterator it2 = para.listPropertyValues(this.owls_paramType);
            while (it2.hasNext()) {
                String propname = it2.next().toString();
                Property pp = this.owls.getProperty(propname);
                //System.out.println(pp.getURI());
                Iterator itd = this.data.listSubjectsWithProperty(pp);
                while (itd.hasNext()) {
                    IndividualImpl v = (IndividualImpl) this.data.getIndividual(itd.next().toString());
                    inds.add(v);
                }
            }
        }
        ArrayList<HashMap> allValuesTemp = new ArrayList<HashMap>();
        for (int j = 0; j < inds.size(); j++) {
            IndividualImpl ind = inds.get(j);
            //System.out.println("=>" + ind.getURI());
            HashMap ret_tmp = new HashMap();
            boolean chk_inputs = true;
            for (int i = 0; i < inputs.size(); i++) {
                IndividualImpl wsdl_i = inputs.get(i);
                String mess_part = wsdl_i.getPropertyValue(this.owls_messpart).toString().split("\\^\\^")[0];
                IndividualImpl para = (IndividualImpl) this.owls.getIndividual(wsdl_i.getPropertyValue(this.owls_owlsParam).toString());
                Iterator it2 = para.listPropertyValues(this.owls_paramType);
                HashMap value_list_tmp = new HashMap();
                ArrayList value_list = new ArrayList();
                boolean chk_inputs2 = false;
                while (it2.hasNext()) {
                    String propname = it2.next().toString();
                    Property pp = this.owls.getProperty(propname);
                    //System.out.println(pp.getURI());
                    Iterator itd = this.data.listObjectsOfProperty(ind, pp);
                    chk_inputs2 = (chk_inputs2 | itd.hasNext());
                    while (itd.hasNext()) {
                        String v = itd.next().toString();
                        //System.out.println("==>" + pp.getURI() + "^^" + v.split("\\^\\^")[0]);
                        if (value_list_tmp.isEmpty()) {
                            value_list_tmp.put(pp.getURI() + "^^" + v.split("\\^\\^")[0], null);
                        } else if (inputs.size() == 1) {
                            HashMap tmp = new HashMap();
                            ArrayList tmpArr = new ArrayList();
                            tmpArr.add(pp.getURI() + "^^" + v.split("\\^\\^")[0]);
                            tmp.put(mess_part, tmpArr);
                            boolean tmpfound = false;
                            for (HashMap s : allValuesTemp) {
                                Object s1 = s.get(mess_part);
                                Object s2 = tmpArr;
                                tmpfound = tmpfound | s1.equals(s2);
                            }
                            if (!tmpfound) {
                                ret.add(tmp);
                                allValuesTemp.add(tmp);
                            }
                        }
                    }
                }
                if (value_list_tmp.isEmpty()) {
                    value_list.add("none^^");
                    ret_tmp.put(mess_part, value_list);
                } else {
                    value_list.addAll(value_list_tmp.keySet());
                    ret_tmp.put(mess_part, value_list);
                }
                chk_inputs = (chk_inputs & chk_inputs2);
            }
            boolean tmpfound = false;
            for (HashMap s : allValuesTemp) {
                for (Object sk : s.keySet()) {
                    ArrayList ar1 = (ArrayList) s.get(sk);
                    ArrayList ar2 = (ArrayList) ret_tmp.get(sk);
                    for (Object s1 : ar1) {
                        for (Object s2 : ar2) {
                            tmpfound = tmpfound | s1.equals(s2);
                        }
                    }
                }
            }
            if (chk_inputs && !tmpfound) {
                ret.add(ret_tmp);
                allValuesTemp.add(ret_tmp);
            }
        }
        return ret;
    }

    public IndividualImpl outputRecursive(IndividualImpl ind, HashMap props, OntClass iClass) throws Exception {
        IndividualImpl ret = null;
        if (ind != null) {
            boolean state = false;
            HashMap restP = new HashMap();
            ArrayList ObjProps = new ArrayList();
            //OntClass iClass = (ind.getRDFType() != null) ? this.data.getOntClass(ind.getRDFType().toString()) : this.data.getOntClass("http://www.w3.org/2002/07/owl#Thing");
            //System.out.println(iClass.getURI());
            /*
             * Iterator it = this.listDeclaredProperties(iClass); while (it !=
             * null && it.hasNext()) { OntPropertyImpl p = (OntPropertyImpl)
             * it.next(); if
             * (p.getRDFType().toString().equals("http://www.w3.org/2002/07/owl#ObjectProperty"))
             * { //System.out.println(p.getURI()); ObjProps.add(p); } }
             */
            if (props != null) {
                Iterator itp = props.keySet().iterator();
                while (itp.hasNext()) {
                    Object obj = itp.next();
                    if (obj.getClass().equals(java.util.ArrayList.class)) {
                        ArrayList prop_arr = (ArrayList) obj;
                        OntProperty obj_p = null;
                        OntProperty dat_p = null;
                        for (int i = 0; i < prop_arr.size(); i++) {
                            if (this.data.getDatatypeProperty((String) prop_arr.get(i)) != null) {
                                dat_p = this.data.getDatatypeProperty((String) prop_arr.get(i));
                            } else if (this.data.getObjectProperty((String) prop_arr.get(i)) != null) {
                                obj_p = this.data.getObjectProperty((String) prop_arr.get(i));
                            }
                        }
                        if (dat_p != null && obj_p != null) {
                            IndividualImpl in_sub = (IndividualImpl) this.data.createIndividual(ind.getURI() + "_" + props.get(obj), this.listDomainClasses(obj_p));
                            in_sub.addProperty(dat_p, (RDFNode) props.get(obj));
                            ind.addProperty(obj_p, in_sub);
                        }
                    } else {
                        Property prop = (Property) obj;
                        boolean propOfCls = false;
                        Iterator it = this.data.listObjectsOfProperty(ret, this.data.getProperty(""));
                        String value = (String) props.get(prop);
                        /*value = value.replaceAll("/", "_");
                         value = value.replaceAll("\\+", "--");
                         value = value.replaceAll("\\*", "s");*/
                        /*
                         * value = value.replaceAll("/", "_"); value =
                         * value.replaceAll("\\+", "--"); value =
                         * value.replaceAll("\\*", "s");
                         */
                        ind.addProperty(prop, value);
                        state = true;
                        //System.out.println(ind.getURI() + "->" + prop.getURI() + "->" + value);
                    }
                }
            }
            if (restP.size() > 0) {
                /*
                 * for (int i = 0; i < ObjProps.size(); i++) { OntPropertyImpl p
                 * = (OntPropertyImpl) ObjProps.get(i); Iterator itop =
                 * this.listRangeClasses(p); while (itop.hasNext()) { String
                 * nextClass = itop.next().toString();
                 * //System.out.println(iClass.getURI() + "-->" + nextClass);
                 * OntClass c = this.data.getOntClass(nextClass); IndividualImpl
                 * new_i = this.outputRecursive((IndividualImpl)
                 * this.data.createIndividual(ind.getURI() + "_" +
                 * c.getLocalName(), c), restP, c); if (new_i != null) {
                 * ind.addProperty(p, new_i); state = true; } } }
                 */
                System.out.println("rest!!!");
            }
            if (state) {
                ret = ind;
            }
        }

        return ret;
    }

    private void modelTransform() {
        StringWriter sw = new StringWriter();
        this.data.write(sw);
        try {
            OWLModel mod = ProtegeOWL.createJenaOWLModelFromReader(new StringReader(sw.toString()));
            this.protege_data = mod;
        } catch (OntologyLoadException ex) {
            //Logger.getLogger(WSExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Iterator listDeclaredProperties(OntClass cls) throws Exception {
        ArrayList ret = new ArrayList();
        if (cls != null) {
            //System.out.println(cls.getURI());
            Iterator it = this.listSuperClasses(cls);
            ArrayList psl = new ArrayList();
            while (it.hasNext()) {
                OntClass c = (OntClass) this.data.getOntClass(it.next().toString());
                //System.out.println(c.getURI());
                Iterator itps = this.data.listSubjectsWithProperty(this.data.getProperty("http://www.w3.org/2000/01/rdf-schema#domain"), c);
                while (itps.hasNext()) {
                    String p_uri = itps.next().toString();
                    OntProperty ps = (OntProperty) this.data.getOntProperty(p_uri);
                    psl.add(ps);
                }
            }
            Iterator itp = this.data.listSubjectsWithProperty(this.data.getProperty("http://www.w3.org/2000/01/rdf-schema#domain"), cls);
            while (itp.hasNext()) {
                OntProperty p = (OntProperty) this.data.getOntProperty(itp.next().toString());
                ret.add(p);
            }
            for (int i = 0; i < ret.size(); i++) {
                for (int j = 0; j < psl.size(); j++) {
                    if (ret.get(i).equals(psl.get(j))) {
                        ret.remove(i);
                    }
                }
            }
            ret.addAll(psl);

        }
        return ret.iterator();
    }

    public boolean hasDeclaredProperty(OntClass cls, Property prop) throws Exception {
        boolean ret = false;
        Iterator it = this.listSuperClasses(cls);
        Iterator itp = this.data.listSubjectsWithProperty(this.data.getProperty("http://www.w3.org/2000/01/rdf-schema#domain"), cls);
        while (itp.hasNext()) {
            Property p = (Property) this.data.getProperty(itp.next().toString());
            if (p.equals(prop)) {
                ret = true;
            }
        }
        while (it.hasNext()) {
            OntClass c = (OntClass) it.next();
            Iterator itps = this.data.listSubjectsWithProperty(this.data.getProperty("http://www.w3.org/2000/01/rdf-schema#domain"), c);
            while (itps.hasNext()) {
                Property ps = (Property) this.data.getProperty(itps.next().toString());
                if (ps.equals(prop)) {
                    ret = true;
                }
            }
        }

        return ret;
    }

    public Iterator listSuperClasses(OntClass cls) throws Exception {
        ArrayList ret = new ArrayList();
        System.out.println(cls.getURI());
        if (cls != null) {
            Iterator it = this.data.listObjectsOfProperty(cls, this.data.getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf"));
            while (it.hasNext()) {
                String rdfs = "http://www.w3.org/";
                String uri = it.next().toString();
                if (!uri.equals(cls.getURI()) && !uri.contains(rdfs)) {
                    OntClass cls_in = this.data.getOntClass(uri);
                    ret.add(cls_in);
                    Iterator it_in = this.listSuperClasses(cls_in);
                    while (it_in.hasNext()) {
                        String uri_in = it_in.next().toString();
                        if (!uri_in.equals(cls_in.getURI()) && !uri_in.contains(rdfs)) {
                            ret.add(this.data.getOntClass(uri_in));
                        }
                    }
                }
            }
        }
        return ret.iterator();
    }

    public OntClass listDomainClasses(Property prop) throws Exception {
        OntClass ret = null;
        Iterator it = this.data.listObjectsOfProperty(prop, this.data.getProperty("http://www.w3.org/2000/01/rdf-schema#domain"));
        if (it.hasNext()) {
            String uri = it.next().toString();
            //System.out.println(uri);
            ret = this.data.getOntClass(uri);
        }
        return ret;
    }

    public Iterator listRangeClasses(Property prop) throws Exception {
        ArrayList ret = new ArrayList();
        Iterator it = this.data.listObjectsOfProperty(prop, this.data.getProperty("http://www.w3.org/2000/01/rdf-schema#range"));
        while (it.hasNext()) {
            String uri = it.next().toString();
            //System.out.println(uri);
            ret.add(this.data.getOntClass(uri));
        }
        return ret.iterator();
    }

    public IndividualImpl getProcessSequenceInd(String p) throws Exception {
        this.process = p;
        IndividualImpl pc = (IndividualImpl) this.owls.getIndividual(this.process);
        IndividualImpl seq = (IndividualImpl) this.owls.getIndividual(pc.getPropertyValue(this.owls_composedOf).toString());
        return seq;
    }

    public OntModel getData() {
        return this.data;
    }

    public void run() {
        int i = Integer.parseInt(Thread.currentThread().getName());
        ResourceImpl seq = this.thred_seq[i];
        try {
            this.execute(seq);
        } catch (Exception ex) {
            Logger.getLogger(WSExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class InputBinding {

    private String cls;
    private String oper;
    private HashMap props;
    private String mess;

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public HashMap getProps() {
        return props;
    }

    public void setProps(HashMap props) {
        this.props = props;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }
}
