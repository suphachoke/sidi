/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 *
 * @author Administrator
 */
public class DiscoveryEvaluation {

    private FileInputStream dis_ref;
    private String casename;
    private FileOutputStream dis_ref_out;

    public static void main(String[] args) throws Exception {
        DiscoveryEvaluation dise = new DiscoveryEvaluation("case0-2");
        System.out.println(dise.evaluate(false));
    }

    public DiscoveryEvaluation(String cs) throws Exception {
        this.casename = cs;
        this.dis_ref = new FileInputStream(new File("C:\\evaluation\\discovery\\" + casename + ".txt"));
    }

    public String evaluate(boolean withComposite) throws Exception {
        String ret = "";
        StringBuffer sb = new StringBuffer();
        byte[] bts = new byte[1024];
        while (this.dis_ref.read(bts) != -1) {
            sb.append(new String(bts));
        }
        String str = sb.toString();
        String[] request_in = str.split(">")[1].split("\n");
        String[] request_out = str.split(">")[2].split("\n");
        String[] ref_match = str.split(">")[3].split("\n");

        String in = ">[Request input]\r\n";
        ArrayList requestI = new ArrayList();
        for (int i = 1; i < request_in.length; i++) {
            in += ((i + 1) == request_in.length) ? request_in[i] + "\r\n" : request_in[i];
            requestI.add(request_in[i].trim());
        }
        String out = ">[Request output]\r\n";
        ArrayList requestO = new ArrayList();
        for (int i = 1; i < request_out.length; i++) {
            out += ((i + 1) == request_out.length) ? request_out[i] + "\r\n" : request_out[i];
            requestO.add(request_out[i].trim());
        }
        OntModel mod = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        try {
            mod.read("http://202.28.94.50/owl-s/1.1/mod/Profile.owl");
            if (!withComposite) {
                mod.read(new FileInputStream(new File("C:\\evaluation\\composition\\" + this.casename.split("-")[0] + "_composition-profile-ap.owl")), null);
            } else {
                mod.read(new FileInputStream(new File("C:\\evaluation\\composition\\" + this.casename.split("-")[0] + "_composition-profile.owl")), null);
            }
            long startTime = System.currentTimeMillis();
            WSDiscovery wsd = new WSDiscovery();
            wsd.setCompositionMatch(withComposite);
            ArrayList res = wsd.matchInput(mod, requestI);
            res = wsd.matchOutput(mod, requestO, res);
            //res = wsd.inputSort(res);
            res = wsd.outputSort(res);
            //res = wsd.pscoreSort(res);
            //res = wsd.rscoreSort(res);
            long endTime = System.currentTimeMillis();
            Date dtd = new Date();
            ret += ">[Discovery match] " + dtd.toLocaleString() + "\r\n";
            ArrayList dis_match_arr = new ArrayList();
            for (int i = 0; i < res.size(); i++) {
                MatchingFilter m = (MatchingFilter) res.get(i);
                ret += m.getProcessURI() + ":[" + m.getInputFilter() + "," + m.getOutputFilter() + "," + m.getpScore() + "," + m.getrScore() + "]\r\n";
                dis_match_arr.add(m.getProcessURI() + ":[" + m.getInputFilter() + "," + m.getOutputFilter() + "," + m.getpScore() + "," + m.getrScore() + "]");

            }
            Date dte = new Date();
            ret += ">[Evaluation] " + dte.toLocaleString() + "\r\n";
            ret += "Processes found :" + res.size() + "\r\n";
            ret += "Total elapsed time in WS Excution is :" + (endTime - startTime) + "millisec.\r\n";
            ret += "Match reference :" + (ref_match.length - 1) + "\r\n";
            ret += "Match discovery :" + dis_match_arr.size() + "\r\n";
            int matchCount = 0;
            String ref = ">[Reference match]\r\n";
            for (int i = 1; i < ref_match.length; i++) {
                ref += ref_match[i] + "\r\n";
                for (int j = 0; j < dis_match_arr.size(); j++) {
                    if (ref_match[i].trim().equals(dis_match_arr.get(j).toString().trim())) {
                        matchCount++;
                    }
                }
            }
            ret += "Match relevant :" + matchCount + "\r\n";
            ret += "Precision :" + ((double) matchCount / (double) (ref_match.length - 1)) + "\r\n";
            ret += "Recall :" + ((double) matchCount / (double) dis_match_arr.size()) + "\r\n";

            ret = in + out + ref + ret;
            this.dis_ref_out = new FileOutputStream(new File("C:\\evaluation\\discovery\\" + casename + ".txt"));
            this.dis_ref_out.write(ret.getBytes());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        return ret;
    }
}
