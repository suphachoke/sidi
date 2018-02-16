/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi.bigdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 *
 * @author BNK
 */
public class LoopingSPARQL {

    private String host;
    private int port;

    public LoopingSPARQL() {
    }

    public LoopingSPARQL(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void exec(String query, int start, int limit, int max) {
        query = query.replace("$$limit", String.valueOf(limit));
        int round = 1;
        for (int i = start; i < max; i += limit) {
            String loopQ = query.replace("$$offset", String.valueOf(i));

            System.out.println("Round: " + round + "; Offset: " + i + "; Limit: " + limit);
            if (i == start) {
                System.out.println(loopQ);
            }
            try {
                Socket sock = new Socket(host, port);

                String cont = "update="
                        + URLEncoder.encode(loopQ, "UTF-8");

                String msg = "POST /bigdata/sparql HTTP/1.0\r\n"
                        + "Host: " + host + "\r\n"
                        + "Content-Type: application/x-www-form-urlencoded\r\n"
                        + "Content-Length: " + cont.length() + "\r\n"
                        + "\r\n"
                        + cont;
                PrintWriter os = new PrintWriter(sock.getOutputStream());
                os.println(msg);
                os.flush();

                InputStreamReader oi = new InputStreamReader(sock.getInputStream());
                BufferedReader bf = new BufferedReader(oi);
                String line;
                int lineCount = 0;
                while ((line = bf.readLine()) != null && lineCount < 5) {
                    System.out.println(line);
                    lineCount++;
                }

                os.close();
                oi.close();
                sock.close();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            round++;
        }
    }

    public void execDelete(String query, int limit, int max) {
        query = query.replace("$$limit", String.valueOf(limit));
        int round = 1;
        for (int i = 0; i < max; i += limit) {

            System.out.println("Round: " + round + "; Limit: " + limit);
            if (i == 0) {
                System.out.println(query);
            }
            try {
                Socket sock = new Socket(host, port);

                String cont = "update="
                        + URLEncoder.encode(query, "UTF-8");

                String msg = "POST /bigdata/sparql HTTP/1.0\r\n"
                        + "Host: " + host + "\r\n"
                        + "Content-Type: application/x-www-form-urlencoded\r\n"
                        + "Content-Length: " + cont.length() + "\r\n"
                        + "\r\n"
                        + cont;
                PrintWriter os = new PrintWriter(sock.getOutputStream());
                os.println(msg);
                os.flush();

                InputStreamReader oi = new InputStreamReader(sock.getInputStream());
                BufferedReader bf = new BufferedReader(oi);
                String line;
                int lineCount = 0;
                while ((line = bf.readLine()) != null && lineCount < 5) {
                    System.out.println(line);
                    lineCount++;
                }

                os.close();
                oi.close();
                sock.close();
            } catch (UnknownHostException ex) {
                ex.printStackTrace(System.out);
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }

            round++;
        }
    }

    //args[0]=action(insert or delete), args[1]=host, args[2]=port, args[3]=offset, args[4]=limit, args[5]=maximum, args[6]=file
    public static void main(String[] args) {
        String filepath = args[6].trim();
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader buff = new BufferedReader(new FileReader(new File(filepath)));
            String line;
            while ((line = buff.readLine()) != null) {
                sb.append(line).append("\n");
            }
            System.out.println(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.out);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        long startT = System.currentTimeMillis();
        LoopingSPARQL lp = new LoopingSPARQL(args[1], Integer.parseInt(args[2]));
        if (args[0].equals("insert")) {
            String q = sb.toString();
            lp.exec(q, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
        } else if (args[0].equals("delete")) {
            String q2 = sb.toString();
            lp.execDelete(q2, Integer.parseInt(args[4]), Integer.parseInt(args[5]));
        }
        long endT = System.currentTimeMillis();
        System.out.println("Total elapsed time " + (endT - startT) + " msec.");
    }
}
