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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class DataTypeConverter extends BaseBuiltin {

    public String getName() {
        return "dataTypeConverter";
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
        if (n3.isLiteral() && n2.isLiteral()) {
            try {
                String tp = n2.getLiteralValue().toString();
                XSDDatatype dt = XSDDatatype.XSDstring;
                if (tp.equals("double")) {
                    dt = XSDDatatype.XSDdouble;
                } else if (tp.equals("int")) {
                    dt = XSDDatatype.XSDint;
                } else if (tp.equals("float")) {
                    dt = XSDDatatype.XSDfloat;
                } else if (tp.equals("date")) {
                    dt = XSDDatatype.XSDdate;
                }
                Node out = Node_Literal.createLiteral(n3.getLiteralValue().toString(), null, dt);
                return env.bind(n4, out);
            } catch (Exception ex) {
                Logger.getLogger(StringToDouble.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
