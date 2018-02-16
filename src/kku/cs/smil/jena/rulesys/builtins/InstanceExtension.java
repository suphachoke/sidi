/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.jena.rulesys.builtins;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class InstanceExtension extends BaseBuiltin {

    public String getName() {
        return "instanceExtension";
    }

    @Override
    public int getArgLength() {
        return 4;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();
        Node n1 = getArg(0, args, context);
        Node n2 = getArg(1, args, context);
        Node n3 = getArg(2, args, context);
        Node n4 = getArg(3, args, context);
        try {
            if (!n3.getURI().contains("http://www.w3.org/")) {
                String strExt = (n1.isLiteral()) ? n1.getLiteralValue().toString() : n1.getLocalName();
                if (strExt.equals("")) {
                    strExt = (n1.isURI()) ? n1.getURI().split("#")[1] : "";
                }
                String str = n3.getLocalName();
                if (str.equals("")) {
                    str = n3.getURI().split("#")[1];
                }
                Node out = Node_URI.createURI(n2.getLiteralValue().toString() + str + strExt);
                return env.bind(n4, out);
            }
        } catch (Exception ex) {
            Logger.getLogger(StringToDouble.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
