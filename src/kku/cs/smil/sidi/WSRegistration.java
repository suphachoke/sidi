/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.sidi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Administrator
 */
public class WSRegistration {

    private Document doc;
    private File file;
    private Element root_nd;

    public WSRegistration() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            file = new File("C:\\sidi\\wsregistry.xml");
            if (file.isFile()) {
                doc = builder.parse(new FileInputStream(file));
                root_nd = doc.getDocumentElement();
            } else {
                doc = builder.newDocument();
                root_nd = doc.createElement("registry");
                doc.appendChild(root_nd);
            }
        } catch (Exception ex) {
            Logger.getLogger(WSRegistration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void submitService(String uri, String sname, String onto, String owls, String wsdl) {
        NodeList nds = root_nd.getChildNodes();
        Node provider_nd = null;
        for (int i = 0; i < nds.getLength(); i++) {
            if (nds.item(i).getNodeName().equals("provider") && nds.item(i).getAttributes().getNamedItem("uri").getNodeValue().equals(uri)) {
                provider_nd = nds.item(i);
            }
        }
        if (provider_nd == null) {
            provider_nd = doc.createElement("provider");
            Attr puri = doc.createAttribute("uri");
            puri.setNodeValue(uri);
            provider_nd.getAttributes().setNamedItem(puri);
            root_nd.appendChild(provider_nd);
        }
        NodeList service_nds = provider_nd.getChildNodes();
        Node service_nd = null;
        for (int j = 0; j < service_nds.getLength(); j++) {
            if (service_nds.item(j).getNodeName().equals("service") && service_nds.item(j).getAttributes().getNamedItem("name").getNodeValue().equals(sname)) {
                service_nd = service_nds.item(j);
            }
        }
        if (service_nd == null) {
            service_nd = doc.createElement("service");
            Attr snm = doc.createAttribute("name");
            snm.setNodeValue(sname);
            service_nd.getAttributes().setNamedItem(snm);
            provider_nd.appendChild(service_nd);
        }
        NodeList data_nds = service_nd.getChildNodes();
        Node onto_nd = null;
        Node owls_nd = null;
        Node wsdl_nd = null;
        for (int k = 0; k < data_nds.getLength(); k++) {
            if (data_nds.item(k).getNodeName().equals("onto")) {
                onto_nd = data_nds.item(k);
            } else if (data_nds.item(k).getNodeName().equals("owls")) {
                owls_nd = data_nds.item(k);
            } else if (data_nds.item(k).getNodeName().equals("wsdl")) {
                wsdl_nd = data_nds.item(k);
            }
        }
        if (onto_nd == null) {
            onto_nd = doc.createElement("onto");
            onto_nd.appendChild(doc.createCDATASection(""));
            service_nd.appendChild(onto_nd);
        }
        if (owls_nd == null) {
            owls_nd = doc.createElement("owls");
            owls_nd.appendChild(doc.createCDATASection(""));
            service_nd.appendChild(owls_nd);
        }
        if (wsdl_nd == null) {
            wsdl_nd = doc.createElement("wsdl");
            wsdl_nd.appendChild(doc.createCDATASection(""));
            service_nd.appendChild(wsdl_nd);
        }
        onto_nd.getChildNodes().item(0).setNodeValue(onto);
        owls_nd.getChildNodes().item(0).setNodeValue(owls);
        wsdl_nd.getChildNodes().item(0).setNodeValue(wsdl);
    }

    public void writeReg() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            XMLSerializer serial = new XMLSerializer(fos, null);
            serial.serialize(doc.getDocumentElement());
            fos.close();
        } catch (Exception ex) {
            Logger.getLogger(WSRegistration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        WSRegistration w = new WSRegistration();
        w.submitService("localhost", "bunthugnonline", "http://localhost/dbservice/ontology/bunthungonline.owlaa", "http://localhost/dbservice/ontology/bunthungonline-sws.owlaa", "http://localhost/dbservice/wsdl/bunthungonline.wsdlaa");
        w.writeReg();
    }
}
