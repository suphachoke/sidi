/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.jena.rulesys.builtins;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Concatenate extends BaseBuiltin {

    public String getName() {
        return "concatenate";
    }

    @Override
    public int getArgLength() {
        return 5;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();
        Node n1 = getArg(0, args, context);
        Node n2 = getArg(1, args, context);
        Node n3 = getArg(2, args, context);
        Node n4 = getArg(3, args, context);
        Node n5 = getArg(4, args, context);
        try {
            String str1 = n3.getLocalName();
            if (str1.equals("")) {
                str1 = n3.getURI().split("#")[1];
            }
            String str2 = n4.getLocalName();
            if (str2.equals("")) {
                str2 = n4.getURI().split("#")[1];
            }
            Node out = Node.createURI(n2.getLiteralValue().toString() + str1 + n1.getLiteralValue().toString() + str2);
            return env.bind(n5, out);
        } catch (Exception ex) {
            Logger.getLogger(StringToDouble.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
