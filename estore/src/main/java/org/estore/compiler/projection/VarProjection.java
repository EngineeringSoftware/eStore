package org.estore.compiler.projection;

import java.util.List;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.PropertyLookupExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.expressions.function.CountFunctionExpr;

public class VarProjection {
    public static String codegen(String dbname, int id, List<LogicalExpr> expressions) {
        String res = "";

        res += "res = new Table(res);";
        res += "expressions = new ArrayList<LogicalExpr>();";
        res += "variables = new ArrayList<String>();";

        for (LogicalExpr expr : expressions) {
            if (expr instanceof VarExpr) {
                res +=
                        "expressions.add(new VarExpr(\""
                                + ((VarExpr) expr).evaluate(null)
                                + "\", \""
                                + ((VarExpr) expr).getRenamedName()
                                + "\"));\n";
            } else if (expr instanceof CountFunctionExpr) {
                LogicalExpr arg = ((CountFunctionExpr) expr).getArg();
                if (arg != null) {
                    if (arg instanceof VarExpr) {
                        res +=
                                "expressions.add(new CountFunctionExpr(new VarExpr(\""
                                        + ((VarExpr) arg).evaluate(null)
                                        + "\")));";
                    }
                } else {
                    res += "expressions.add(new CountFunctionExpr());";
                }
            } else if (expr instanceof PropertyLookupExpr) {
                res +=
                        "expressions.add(new PropertyLookupExpr(\""
                                + ((PropertyLookupExpr) expr).getNodeName()
                                + "\", \""
                                + ((PropertyLookupExpr) expr).getNodePropertyName()
                                + "\", \""
                                + ((PropertyLookupExpr) expr).getRenamedName()
                                + "\"));";
            }
        }

        res += "for (LogicalExpr expr : expressions) {";
        res += "  if (expr instanceof CountFunctionExpr) {";
        res += "    if (expressions.size() == 1) {";
        res += "      res = (new CountFunctionSingleExpr((CountFunctionExpr) expr)).evaluate(res);";
        res += "    } else {";
        res += "      res = ((CountFunctionExpr) expr).evaluate(res);";
        res += "    }";
        res += "  } else if (expr instanceof PropertyLookupExpr) {";
        res += "    res = ((PropertyLookupExpr) expr).evaluate(res);";
        res += "  }";
        res += "}";

        res += "for (LogicalExpr expr : expressions) {";
        res += "  if (expr.getName() != expr.getRenamedName()) {";
        res += "    res.changeColumnName(expr.getName(), expr.getRenamedName());";
        res += "    variables.add(expr.getRenamedName());";
        res += "  } else {";
        res += "    variables.add(expr.getName());";
        res += "  }}";
        res += "res = res.selectColumns(variables);";

        return res;
    }
}
