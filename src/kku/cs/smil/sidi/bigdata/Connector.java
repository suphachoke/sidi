/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.bigdata;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.atlas.io.InputStreamBuffered;

/**
 *
 * @author Administrator
 */
public class Connector {

    private String host;
    private int port;
    private int streamSize = 1048576;

    public Connector(String h, int p) {
        host = h;
        port = p;
    }

    public void insertData(String[] triples) {
        try {
            Socket sock = new Socket(host, port);

            String rec = "";
            for (int i = 0; i < triples.length; i++) {
                rec += triples[i] + ". ";
            }
            String cont = "update=PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"
                    + "INSERT DATA \r\n"
                    + "{ "
                    + URLEncoder.encode(rec, "UTF-8")
                    + " }";

            String msg = "POST /bigdata/sparql HTTP/1.0\r\n"
                    + "Host: " + host + "\r\n"
                    + "Content-Type: application/x-www-form-urlencoded\r\n"
                    + "Content-Length: " + cont.length() + "\r\n"
                    + "\r\n"
                    + cont;
            PrintWriter os = new PrintWriter(sock.getOutputStream());
            os.println(msg);
            os.flush();
            //System.out.println(msg);
            /*
             * DataInputStream oi = new DataInputStream(sock.getInputStream());
             System.out.println((String)oi.readLine());
             */
            InputStreamReader oi = new InputStreamReader(sock.getInputStream());
            BufferedReader bf = new BufferedReader(oi);
            String line;
            while ((line = bf.readLine()) != null) {
                //System.out.println(line);
            }

            os.close();
            oi.close();
            sock.close();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void insertDataStreaming(String file) {
        try {
            File fl = new File(file);
            InputStreamBuffered isb = new InputStreamBuffered(new FileInputStream(fl));
            byte[] bts = new byte[streamSize];
            long count = 0;
            String buff = "";
            while (isb.read(bts) != -1) {
                count += (long) streamSize;
                long length = (count > fl.length()) ? (long) streamSize - (count - fl.length()) : (long) streamSize;
                String tmp = new String(bts, 0, (int) length);

                String rec = buff + tmp.substring(0, tmp.lastIndexOf("\n"));
                buff = tmp.substring(tmp.lastIndexOf("\n"));

                String cont = "update=PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"
                        + "INSERT DATA \r\n"
                        + "{ "
                        + URLEncoder.encode(rec, "UTF-8")
                        + " }";

                String msg = "POST /bigdata/sparql HTTP/1.0\r\n"
                        + "Host: " + host + "\r\n"
                        + "Content-Type: application/x-www-form-urlencoded\r\n"
                        + "Content-Length: " + cont.length() + "\r\n"
                        + "\r\n"
                        + cont;

                Socket sock = new Socket(host, port);
                PrintWriter os = new PrintWriter(sock.getOutputStream());
                os.println(msg);
                os.flush();
                //System.out.println(msg);
            /*
                 * DataInputStream oi = new DataInputStream(sock.getInputStream());
                 System.out.println((String)oi.readLine());
                 */
                InputStreamReader oi = new InputStreamReader(sock.getInputStream());
                BufferedReader bf = new BufferedReader(oi);
                String line;
                while ((line = bf.readLine()) != null) {
                    System.out.println(line);
                }

                os.close();
                oi.close();
                sock.close();
            }

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteData(String[] inds) {
        try {
            for (int i = 0; i < inds.length; i++) {
                Socket sock = new Socket(host, port);

                String rec = "";
                rec += inds[i] + " ?p" + i + " ?o" + i + ".";
                String cont = "update="
                        + "DELETE WHERE "
                        + "{ "
                        + URLEncoder.encode(rec, "UTF-8")
                        + " }";

                String msg = "POST /bigdata/sparql HTTP/1.0\r\n"
                        + "Host: " + host + "\r\n"
                        + "Content-Type: application/x-www-form-urlencoded\r\n"
                        + "Content-Length: " + cont.length() + "\r\n"
                        + "\r\n"
                        + cont;
                PrintWriter os = new PrintWriter(sock.getOutputStream());
                os.println(msg);
                os.flush();
                //System.out.println(msg);
            /*
                 * DataInputStream oi = new DataInputStream(sock.getInputStream());
                 System.out.println((String)oi.readLine());
                 */
                InputStreamReader oi = new InputStreamReader(sock.getInputStream());
                BufferedReader bf = new BufferedReader(oi);
                String line;
                while ((line = bf.readLine()) != null) {
                    //System.out.println(line);
                }

                os.close();
                oi.close();
                sock.close();
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteDataComplex(String[] inds, String[] props) {
        try {
            for (int j = 0; j < inds.length; j++) {
                Socket sock = new Socket(host, port);
                String rec = "";
                String wh = "";

                rec += "?o" + j + " ?pp" + j + " ?oo" + j + ". ";
                wh += inds[j] + " " + props[j] + " ?o" + j + "." + "?o" + j + " ?pp" + j + " ?oo" + j + ". ";

                String cont = "update="
                        + "DELETE "
                        + "{ "
                        + URLEncoder.encode(rec, "UTF-8")
                        + " } "
                        + "WHERE "
                        + "{ "
                        + URLEncoder.encode(wh, "UTF-8")
                        + " }";

                String msg = "POST /bigdata/sparql HTTP/1.0\r\n"
                        + "Host: " + host + "\r\n"
                        + "Content-Type: application/x-www-form-urlencoded\r\n"
                        + "Content-Length: " + cont.length() + "\r\n"
                        + "\r\n"
                        + cont;
                PrintWriter os = new PrintWriter(sock.getOutputStream());
                os.println(msg);
                os.flush();
                //System.out.println(msg);
            /*
                 * DataInputStream oi = new DataInputStream(sock.getInputStream());
                 System.out.println((String)oi.readLine());
                 */
                InputStreamReader oi = new InputStreamReader(sock.getInputStream());
                BufferedReader bf = new BufferedReader(oi);
                String line;
                while ((line = bf.readLine()) != null) {
                    //System.out.println(line);
                }

                os.close();
                oi.close();
                sock.close();
            }

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connector cc = new Connector("61.19.108.34", 8080);
        cc.insertDataStreaming("C:\\ICD10CM_2.nt");
        /*OntModel mod = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
        mod.read("file:/C:/ICD10CM_2.ttl", "Turtle");
        try {
            FileOutputStream fos = new FileOutputStream(new File("C:/ICD10CM_2.nt"));
            mod.write(fos, "N-Triples");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
