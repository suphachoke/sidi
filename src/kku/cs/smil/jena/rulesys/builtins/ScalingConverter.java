/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.jena.rulesys.builtins;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;
import java.util.logging.Level;
import java.util.logging.Logger;
import kku.cs.smil.qudt.QUDT;

/**
 *
 * @author Administrator
 */
public class ScalingConverter extends BaseBuiltin {

    public String getName() {
        return "scalingConverter";
    }

    @Override
    public int getArgLength() {
        return 3;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();
        Node n1 = getArg(0, args, context);
        Node n2 = getArg(1, args, context);
        Node n3 = getArg(2, args, context);
        if (n1.isLiteral() && n2.isLiteral()) {
            try {
                double mul = Double.parseDouble(n1.getLiteralValue().toString());
                Object n2obj = n2.getLiteralValue();
                String n2str = n2obj.toString();
                if (n2obj.getClass().equals(BaseDatatype.TypedValue.class)) {
                    BaseDatatype.TypedValue n2bdt = (BaseDatatype.TypedValue) n2obj;
                    n2str = n2bdt.lexicalValue;
                }
                double src = Double.parseDouble(n2str);

                Node out = Node_Literal.createLiteral(String.valueOf((mul * src)), null, XSDDatatype.XSDdouble);

                return env.bind(n3, out);
            } catch (Exception ex) {
                Logger.getLogger(StringToDouble.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
