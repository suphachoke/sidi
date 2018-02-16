/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author BNK
 */
public class SIDIServer {

    public static void main(String[] args) {
        System.out.println("Server start...");
        while (true) {
            try {
                ServerSocket ss = new ServerSocket(8081);
                Socket s = ss.accept();
                new ConnectionHandler(s);
            } catch (Exception e) {
            }
        }
    }
}

class ConnectionHandler implements Runnable {

    private Socket sock;

    public ConnectionHandler(Socket sock) {
        this.sock = sock;
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            DataInputStream is = new DataInputStream(sock.getInputStream());
            String msg = is.readLine();
            System.out.println("Receive: " + msg);
            String[] msg_arr = msg.split("\\|");

            String ret = "";

            System.out.println(msg_arr[0]);
            if (msg_arr[0].equals("registration")) {
                WSRegistration wr = new WSRegistration();
                wr.submitService(msg_arr[1], msg_arr[2], msg_arr[3], msg_arr[4], msg_arr[5]);
                wr.writeReg();
                ret += "Registration succesfull.";
                System.out.println(ret);
            }

            DataOutputStream os = new DataOutputStream(sock.getOutputStream());
            os.writeBytes(ret + "\r\n");

            is.close();
            os.close();
            sock.close();
        } catch (Exception e) {
        }
    }
}
