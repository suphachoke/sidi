/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kku.cs.smil.jena.rulesys.builtins;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.GraphUtil;
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
public class Count extends BaseBuiltin {

    public String getName() {
        return "count";
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
        try {
            ExtendedIterator<Node> it = GraphUtil.listObjects(context.getGraph().getRawGraph(), n1, n2);
            int count = 0;
            while (it.hasNext()) {
                it.next();
                count++;
            }
            Node out = Node_Literal.createLiteral(String.valueOf(count), null, XSDDatatype.XSDdouble);
            return env.bind(n3, out);
        } catch (Exception ex) {
            Logger.getLogger(StringToDouble.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
