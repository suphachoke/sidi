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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class DataFormatConverter extends BaseBuiltin {

    public String getName() {
        return "dataFormatConverter";
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
        if (n1.isLiteral() && n2.isLiteral() && n3.isLiteral()) {
            try {
                Object n3obj = n3.getLiteralValue();
                String n3str = n3obj.toString();
                if (n3obj.getClass().equals(BaseDatatype.TypedValue.class)) {
                    BaseDatatype.TypedValue n3bdt = (BaseDatatype.TypedValue) n3obj;
                    n3str = n3bdt.lexicalValue;
                }
                Date dt = new SimpleDateFormat(n1.getLiteralValue().toString(), Locale.ENGLISH).parse(n3str);
                String newdt = new SimpleDateFormat(n2.getLiteralValue().toString(), Locale.ENGLISH).format(dt);
                Node out = Node_Literal.createLiteral(newdt, null, XSDDatatype.XSDstring);
                return env.bind(n4, out);
            } catch (ParseException ex) {
                Logger.getLogger(DateFormat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
