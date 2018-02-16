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
 * @author BNK
 */
public class NameSimilarity extends BaseBuiltin {

    public String getName() {
        return "nameSimilarity";
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
                double score = 0.0;
                semantics.NameSimilarity nsim = new semantics.NameSimilarity(src, tgt);
                nsim.findingAbbreviations();

                if (nsim.getSrcName().toLowerCase().equals(nsim.getTgtName().toLowerCase())) {
                    score = 1.0;
                    System.out.println("[" + src + ":" + tgt + "]");
                    System.out.println(score);
                } else {
                    double a = (double) nsim.getSrcName().split(" ").length;
                    double b = (double) nsim.getTgtName().split(" ").length;
                    a += (a == 0) ? 1 : 0;
                    b += (b == 0) ? 1 : 0;
                    double chk = (a < b) ? (a / b) : (b / a);

                    if (chk > 0.85) {
                        //System.out.println("[" + src + ":" + tgt + "]");
                        System.out.println(nsim.getSrcName() + ":" + nsim.getTgtName());
                        nsim.wordsMatching();
                        System.out.println(nsim.getSrcWords().size() + ":" + nsim.getTgtWords().size() + ":" + nsim.getMppdWords().size());
                        System.out.println(nsim.getCoefficient());
                        System.out.println(nsim.getSimilarityScore());
                        score = nsim.getSimilarityScore();
                    }
                }

                XSDDatatype dt = XSDDatatype.XSDdouble;
                Node out = Node_Literal.createLiteral(String.valueOf(score), null, dt);
                return env.bind(n3, out);
            } catch (Exception ex) {
                Logger.getLogger(StringToDouble.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
