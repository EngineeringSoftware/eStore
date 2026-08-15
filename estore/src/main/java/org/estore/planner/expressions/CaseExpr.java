package org.estore.planner.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.estore.planner.expressions.function.CountFunctionExpr;
import org.estore.planner.expressions.function.CountFunctionSingleExpr;
import org.estore.planner.util.Table;

public class CaseExpr implements LogicalExpr<Table, Table> {
    private final LogicalExpr subject;
    private final List<LogicalExpr> whenExprs;
    private final List<LogicalExpr> thenExprs;
    private final LogicalExpr elseExpr;
    private String renamedName;

    public CaseExpr(
            LogicalExpr subject,
            List<LogicalExpr> whenExprs,
            List<LogicalExpr> thenExprs,
            LogicalExpr elseExpr) {
        this.subject = subject;
        this.whenExprs = whenExprs;
        this.thenExprs = thenExprs;
        this.elseExpr = elseExpr;
        this.renamedName = getName();
    }

    @Override
    public String getName() {
        return "CASE";
    }

    @Override
    public String getRenamedName() {
        return renamedName;
    }

    @Override
    public void setRenamedName(String renamedName) {
        this.renamedName = renamedName;
    }

    @Override
    public Table evaluate(Table v) {
        Object chosen = valueOf(elseExpr, v);
        for (int i = 0; i < whenExprs.size(); i++) {
            if (matches(whenExprs.get(i), v)) {
                chosen = valueOf(thenExprs.get(i), v);
                break;
            }
        }
        String keyName = getName();
        Table result = new Table(Arrays.asList(new String[] {keyName}));
        result.get(keyName).add(chosen);
        return result;
    }

    private boolean matches(LogicalExpr whenExpr, Table v) {
        Object whenValue = valueOf(whenExpr, v);
        if (subject == null) {
            return Boolean.TRUE.equals(whenValue);
        }
        return Objects.equals(valueOf(subject, v), whenValue);
    }

    private Object valueOf(LogicalExpr expr, Table v) {
        if (expr == null) {
            return null;
        }
        LogicalExpr toEval = expr;
        if (expr instanceof CountFunctionExpr && !(expr instanceof CountFunctionSingleExpr)) {
            toEval = new CountFunctionSingleExpr((CountFunctionExpr) expr);
        }
        try {
            Object value = toEval.evaluate(v);
            if (value instanceof Table) {
                Table valueTable = (Table) value;
                ArrayList<Object> col = valueTable.get(toEval.getName());
                if (col == null || col.isEmpty()) {
                    return null;
                }
                return col.get(0);
            }
            return value;
        } catch (ClassCastException ignored) {
            return toEval.evaluate(null);
        }
    }
}
