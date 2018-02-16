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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BNK
 */
public class TestComposition {

    public static void main(String[] args) throws Exception {
        try {
            TestComposition tc = new TestComposition();
            tc.testCompose("case0");
            tc.testEvaluate("case0");
        } catch (Exception ex) {
            Logger.getLogger(CompositionEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testCompose(String cn) throws Exception {
        String caseName = cn;
        FileInputStream fis = new FileInputStream(new File("C:\\evaluation\\composition\\" + caseName + ".txt"));
        byte[] tmp = new byte[1024];
        StringBuffer sb = new StringBuffer();
        while (fis.read(tmp) != -1) {
            sb.append(new String(tmp));
        }
        String str = sb.toString();
        String[] ws_tmp = str.split(">")[1].split("\n");
        String[] ws = new String[ws_tmp.length - 1];
        FileOutputStream fos_txt = new FileOutputStream(new File("C:\\evaluation\\composition\\" + caseName + ".txt"));
        fos_txt.write(">[ws]\r\n".getBytes());
        int numws = 0;
        for (int i = 1; i < ws_tmp.length; i++) {
            ws[i - 1] = ws_tmp[i].trim();
            if ((i + 1) < ws_tmp.length && !ws_tmp[i].equals("")) {
                fos_txt.write((ws_tmp[i] + "\r\n").getBytes());
                numws++;
            } else if (!ws_tmp[i].equals("")) {
                fos_txt.write((ws_tmp[i]).getBytes());
                numws++;
            }
        }
        Date dt = new Date();
        String dts = dt.toLocaleString();
        fos_txt.write("\r\n".getBytes());
        fos_txt.write((">[Composition Results] " + dts + "\r\n").getBytes());
        fos_txt.write(("Number of Web services AtomicProcess :" + numws + "\r\n").getBytes());
        try {
            WSComposition mn = new WSComposition("http://202.28.94.50/ontologies/" + caseName + "_composition.owl#",
                    "http://202.28.94.50/ontologies/" + caseName + "_composition-profile.owl#");

            long start1 = System.currentTimeMillis();
            mn.executeRules("", null);
            long end1 = System.currentTimeMillis();
            System.out.println("Total elapsed time in WS Composition - Inference Rules is :" + (end1 - start1) + " millisec.");
            fos_txt.write(("Total elapsed time in WS Composition - Inference Rules is :" + (end1 - start1) + " millisec.").getBytes());
            fos_txt.write("\r\n".getBytes());
            long start2 = System.currentTimeMillis();
            OntModel mi = ModelFactory.createOntologyModel();
            mi.read(new FileInputStream(new File("C:\\tmp-comp.owl")), null);
            Iterator it = mi.listIndividuals(mi.getOntClass("http://202.28.94.50/owl-s/1.1/mod/Process.owl#Sequence"));
            int numOfComp = mn.CompositeRecursive(1, new ArrayList(), new ArrayList(), null, mi, 0);
            System.out.println("Number of Composition :" + numOfComp);
            fos_txt.write(("Number of Composition :" + numOfComp).getBytes());
            fos_txt.write("\r\n".getBytes());
            long end2 = System.currentTimeMillis();
            System.out.println("Total elapsed time in WS Composition - Composite Processes Generation is :" + (end2 - start2) + " millisec.");
            fos_txt.write(("Total elapsed time in WS Composition - Composite Processes Generation is :" + (end2 - start2) + " millisec.").getBytes());
            fos_txt.write("\r\n".getBytes());
            FileOutputStream fos = new FileOutputStream(new File("C:\\evaluation\\composition\\" + caseName + "_composition.owl"));
            mn.getModCP().write(fos, "RDF/XML-ABBREV");
            FileOutputStream fos2 = new FileOutputStream(new File("C:\\evaluation\\composition\\" + caseName + "_composition-profile.owl"));
            mn.getModCPProfile().write(fos2, "RDF/XML-ABBREV");
            FileOutputStream fos3 = new FileOutputStream(new File("C:\\evaluation\\composition\\" + caseName + "_composition-profile-ap.owl"));
            mn.getModCPProfileAP().write(fos3, "RDF/XML-ABBREV");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        fis.close();
        fos_txt.close();
    }

    public void testEvaluate(String cn) throws Exception {
        CompositionEvaluation eval = new CompositionEvaluation(cn + "_composition", cn);
        String res = eval.evaluate();
        FileInputStream fis = new FileInputStream(new File("C:\\evaluation\\composition\\" + cn + ".txt"));
        String str = "";
        byte[] bts = new byte[1024];
        while ((fis.read(bts)) != -1) {
            str += new String(bts);
        }
        str = ">" + str.split(">")[1] + ">" + str.split(">")[2];
        FileOutputStream fos = new FileOutputStream(new File("C:\\evaluation\\composition\\" + cn + ".txt"));
        fos.write(str.trim().getBytes());
        fos.write(("\r\n").getBytes());
        fos.write(res.getBytes());
    }
}
