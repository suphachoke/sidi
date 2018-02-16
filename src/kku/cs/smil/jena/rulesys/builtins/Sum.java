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
public class Sum extends BaseBuiltin {

    public String getName() {
        return "Sum";
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
            ExtendedIterator<Node> it = GraphUtil.listObjects(context.getGraph().getRawGraph(), n1, n2);
            double sum = 0;
            while (it.hasNext()) {
                Node nd = it.next();
                ExtendedIterator<Node> it2 = GraphUtil.listObjects(context.getGraph().getRawGraph(), nd, n3);
                if (it2.hasNext()) {
                    sum += Double.valueOf(it2.next().getLiteralValue().toString());
                }
            }
            Node out = Node_Literal.createLiteral(String.valueOf(sum), null, XSDDatatype.XSDdouble);
            return env.bind(n4, out);
        } catch (Exception ex) {
            Logger.getLogger(StringToDouble.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
