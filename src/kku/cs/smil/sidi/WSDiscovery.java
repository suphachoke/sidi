/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 *
 * @author Administrator
 */
public class WSDiscovery {

    private boolean compositionState = true;

    public static void main(String[] args) {
        ArrayList requestI = new ArrayList();
        requestI.add("http://202.28.94.50/ontologies/healthcare.owl#citizen_id");
        requestI.add("http://202.28.94.50/ontologies/healthcare.owl#organization_code");
        ArrayList requestO = new ArrayList();
        requestO.add("http://202.28.94.50/ontologies/healthcare.owl#patient_code");
        requestO.add("http://202.28.94.50/ontologies/healthcare.owl#physician_code");
        requestO.add("http://202.28.94.50/ontologies/healthcare.owl#organization_code");
        OntModel mod = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        try {
            mod.read("http://202.28.94.50/owl-s/1.1/mod/Profile.owl");
            mod.read(new FileInputStream(new File("C:\\composition-profile.owl")), null);
            long startTime = System.currentTimeMillis();
            WSDiscovery wsd = new WSDiscovery();
            wsd.setCompositionMatch(false);
            ArrayList res = wsd.matchInput(mod, requestI);
            res = wsd.matchOutput(mod, requestO, res);
            res = wsd.inputSort(res);
            //res = wsd.outputSort(res);
            //res = wsd.pscoreSort(res);
            //res = wsd.rscoreSort(res);
            long endTime = System.currentTimeMillis();
            System.out.println(res.size() + " processes found");
            for (int i = 0; i < res.size(); i++) {
                MatchingFilter m = (MatchingFilter) res.get(i);
                System.out.println(m.getProcessURI() + ":[" + m.getInputFilter() + "," + m.getOutputFilter() + "," + m.getpScore() + "," + m.getrScore() + "]");
            }
            System.out.println("Total elapsed time in WS Excution is :" + (endTime - startTime) + "millisec.");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public ArrayList matchInput(OntModel mod, ArrayList R) {
        ArrayList ret = new ArrayList();
        for (int i = R.size(); i >= 0; i--) {
            int combination = (this.getFactorial(R.size()) / (this.getFactorial(R.size() - i) * this.getFactorial(i)));
            for (int j = 0; j < combination; j++) {//Combination
                String params = "";
                for (int k = 0; k < i; k++) {
                    int pos = ((j + k) < R.size()) ? (j + k) : ((j + k) - R.size());
                    params += (i == 0) ? "" : ". ?x profile:hasInput ?z" + pos + ". ?z" + pos + " process:parameterType <" + R.get(pos) + ">";
                }

                String queryStr = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
                        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"
                        + "PREFIX dom: <http://202.28.94.50/ontologies/healthcare.owl#>\r\n"
                        + "PREFIX profile: <http://202.28.94.50/owl-s/1.1/mod/Profile.owl#>\r\n"
                        + "PREFIX process: <http://202.28.94.50/owl-s/1.1/mod/Process.owl#>\r\n"
                        + "SELECT ?x ?y \r\n"
                        + "WHERE {\r\n"
                        + "?x profile:has_process ?y" + params + "\r\n"
                        + "}";
                //System.out.println(queryStr);
                Query query = QueryFactory.create(queryStr);
                QueryExecution exe = SparqlDLExecutionFactory.create(query, mod);
                ResultSet res = exe.execSelect();
                while (res.hasNext()) {
                    ResultBinding rb = (ResultBinding) res.next();
                    boolean found = false;
                    for (int a = 0; a < ret.size(); a++) {
                        MatchingFilter m = (MatchingFilter) ret.get(a);
                        if (m.getProcessURI().equals(rb.get("y").toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        //System.out.println(rb.get("x"));
                        IndividualImpl in = (IndividualImpl) mod.getIndividual(rb.get("x").toString());
                        Iterator itin = in.listPropertyValues(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#hasInput"));
                        int insize = 0;
                        while (itin.hasNext()) {
                            insize += 1;
                            itin.next();
                        }
                        String inputFilter = "Fail";
                        if (i == 0) {
                            inputFilter = "Exact";
                        } else if (i == R.size() && i == insize) {
                            inputFilter = "Exact";
                        } else if (i == R.size() && i < insize) {
                            inputFilter = "Subsume";
                        } else if (i < R.size() && i == insize) {
                            inputFilter = "Plugin";
                        } else if (i < R.size() && i < insize) {
                            inputFilter = "Sibling";
                        }
                        //System.out.println(inputFilter);
                        MatchingFilter m = new MatchingFilter(rb.get("y").toString());
                        m.setInputFilter(inputFilter);
                        ret.add(m);
                    }
                }
            }
        }

        return ret;
    }

    public ArrayList matchOutput(OntModel mod, ArrayList R, ArrayList inputResults) {
        ArrayList ret = inputResults;
        for (int i = R.size(); i >= 0; i--) {
            int combination = (this.getFactorial(R.size()) / (this.getFactorial(R.size() - i) * this.getFactorial(i)));
            for (int j = 0; j < combination; j++) {//Combination
                String params = "";
                for (int k = 0; k < i; k++) {
                    int pos = ((j + k) < R.size()) ? (j + k) : ((j + k) - R.size());
                    params += (i == 0) ? "" : ". ?x profile:hasOutput ?z" + pos + ". ?z" + pos + " process:parameterType <" + R.get(pos) + ">";
                }

                String queryStr = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
                        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"
                        + "PREFIX dom: <http://202.28.94.50/ontologies/healthcare.owl#>\r\n"
                        + "PREFIX profile: <http://202.28.94.50/owl-s/1.1/mod/Profile.owl#>\r\n"
                        + "PREFIX process: <http://202.28.94.50/owl-s/1.1/mod/Process.owl#>\r\n"
                        + "SELECT ?x ?y \r\n"
                        + "WHERE {\r\n"
                        + "?x profile:has_process ?y" + params + "\r\n"
                        + "}";
                //System.out.println(queryStr);
                Query query = QueryFactory.create(queryStr);
                QueryExecution exe = SparqlDLExecutionFactory.create(query, mod);
                ResultSet res = exe.execSelect();
                while (res.hasNext()) {
                    ResultBinding rb = (ResultBinding) res.next();
                    for (int a = 0; a < ret.size(); a++) {
                        MatchingFilter m = (MatchingFilter) ret.get(a);
                        if (m.getProcessURI().equals(rb.get("y").toString()) && m.getOutputFilter().equals("")) {
                            //System.out.println(rb.get("x"));
                            IndividualImpl in = (IndividualImpl) mod.getIndividual(rb.get("x").toString());
                            Iterator itout = in.listPropertyValues(mod.getProperty("http://202.28.94.50/owl-s/1.1/mod/Profile.owl#hasOutput"));
                            int outsize = 0;
                            while (itout.hasNext()) {
                                outsize += 1;
                                itout.next();
                            }
                            String outputFilter = "Fail";
                            if (i == 0) {
                                outputFilter = "Exact";
                            } else if (i == R.size() && i == outsize) {
                                outputFilter = "Exact";
                            } else if (i == R.size() && i < outsize) {
                                outputFilter = "Subsume";
                            } else if (i < R.size() && i == outsize) {
                                outputFilter = "Plugin";
                            } else if (i < R.size() && i < outsize) {
                                outputFilter = "Sibling";
                            }
                            double pScore = (double) i / (double) R.size();
                            double rScore = (double) i / (double) outsize;
                            m.setOutputFilter(outputFilter);
                            m.setpScore(pScore);
                            m.setrScore(rScore);
                            //System.out.println(outputFilter + ":" + pScore + ":" + rScore);
                        }
                    }
                }
            }
        }
        return ret;
    }

    private int getFactorial(int n) {
        int fact = 1;
        for (int i = n; i > 1; i--) {
            fact = fact * i;
        }
        return fact;
    }

    public ArrayList inputSort(ArrayList matchSet) {
        ArrayList ret = matchSet;
        boolean swap = false;
        do {
            swap = false;
            for (int i = 0; i < ret.size() - 1; i++) {
                MatchingFilter m0 = (MatchingFilter) ret.get(i);
                int s0 = (m0.getInputFilter().equals("Exact")) ? 5 : (m0.getInputFilter().equals("Plugin")) ? 4 : (m0.getInputFilter().equals("Subsume")) ? 3 : (m0.getInputFilter().equals("Sibling")) ? 2 : (m0.getInputFilter().equals("Fail")) ? 1 : 0;
                MatchingFilter m1 = (MatchingFilter) ret.get(i + 1);
                int s1 = (m1.getInputFilter().equals("Exact")) ? 5 : (m1.getInputFilter().equals("Plugin")) ? 4 : (m1.getInputFilter().equals("Subsume")) ? 3 : (m1.getInputFilter().equals("Sibling")) ? 2 : (m1.getInputFilter().equals("Fail")) ? 1 : 0;
                if (s1 > s0) {
                    ret.set(i, m1);
                    ret.set(i + 1, m0);
                    swap = true;
                }
            }
        } while (swap);
        return ret;
    }

    public ArrayList outputSort(ArrayList matchSet) {
        ArrayList ret = matchSet;
        boolean swap = false;
        do {
            swap = false;
            for (int i = 0; i < ret.size() - 1; i++) {
                MatchingFilter m0 = (MatchingFilter) ret.get(i);
                int s0 = (m0.getOutputFilter().equals("Exact")) ? 5 : (m0.getOutputFilter().equals("Subsume")) ? 4 : (m0.getOutputFilter().equals("Plugin")) ? 3 : (m0.getInputFilter().equals("Sibling")) ? 2 : (m0.getInputFilter().equals("Fail")) ? 1 : 0;
                MatchingFilter m1 = (MatchingFilter) ret.get(i + 1);
                int s1 = (m1.getOutputFilter().equals("Exact")) ? 5 : (m1.getOutputFilter().equals("Subsume")) ? 4 : (m1.getOutputFilter().equals("Plugin")) ? 3 : (m1.getInputFilter().equals("Sibling")) ? 2 : (m1.getInputFilter().equals("Fail")) ? 1 : 0;
                if (s1 > s0) {
                    ret.set(i, m1);
                    ret.set(i + 1, m0);
                    swap = true;
                }
            }
        } while (swap);
        return ret;
    }

    public ArrayList pscoreSort(ArrayList matchSet) {
        ArrayList ret = matchSet;
        boolean swap = false;
        do {
            swap = false;
            for (int i = 0; i < ret.size() - 1; i++) {
                MatchingFilter m0 = (MatchingFilter) ret.get(i);
                double s0 = m0.getpScore();
                MatchingFilter m1 = (MatchingFilter) ret.get(i + 1);
                double s1 = m1.getpScore();
                if (s1 > s0) {
                    ret.set(i, m1);
                    ret.set(i + 1, m0);
                    swap = true;
                }
            }
        } while (swap);
        return ret;
    }

    public ArrayList rscoreSort(ArrayList matchSet) {
        ArrayList ret = matchSet;
        boolean swap = false;
        do {
            swap = false;
            for (int i = 0; i < ret.size() - 1; i++) {
                MatchingFilter m0 = (MatchingFilter) ret.get(i);
                double s0 = m0.getrScore();
                MatchingFilter m1 = (MatchingFilter) ret.get(i + 1);
                double s1 = m1.getrScore();
                if (s1 > s0) {
                    ret.set(i, m1);
                    ret.set(i + 1, m0);
                    swap = true;
                }
            }
        } while (swap);
        return ret;
    }

    public void setCompositionMatch(boolean state) {
        this.compositionState = state;
    }
}
