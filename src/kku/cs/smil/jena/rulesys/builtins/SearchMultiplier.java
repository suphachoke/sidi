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
import kku.cs.smil.qudt.QUDT;

/**
 *
 * @author Administrator
 */
public class SearchMultiplier extends BaseBuiltin {

    public String getName() {
        return "searchMultiplier";
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
                String src = n1.getLiteralValue().toString();
                String tgt = n2.getLiteralValue().toString();

                QUDT qudt = new QUDT();
                double srcM = Double.parseDouble(qudt.searchMultiplier(src));
                double tgtM = Double.parseDouble(qudt.searchMultiplier(tgt));
                double mul = srcM / tgtM;
                Node out = Node_Literal.createLiteral(String.valueOf(mul), null, XSDDatatype.XSDdouble);

                return env.bind(n3, out);
            } catch (Exception ex) {
                Logger.getLogger(StringToDouble.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
