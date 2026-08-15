package org.estore.planner.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import org.estore.planner.expressions.function.CountFunctionExpr;
import org.estore.planner.expressions.function.CountFunctionSingleExpr;
import org.estore.planner.util.Table;

public class ArithmeticExpr implements LogicalExpr<Table, Table> {
    private final String op;
    private final LogicalExpr left;
    private final LogicalExpr right;
    private String renamedName;

    public ArithmeticExpr(String op, LogicalExpr left, LogicalExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
        this.renamedName = getName();
    }

    @Override
    public String getName() {
        return operandName(left) + op + operandName(right);
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
        String keyName = getName();
        Table result = new Table(Arrays.asList(new String[] {keyName}));
        result.get(keyName).add(apply(valueOf(left, v), valueOf(right, v)));
        return result;
    }

    private Object apply(Object leftValue, Object rightValue) {
        if (!(leftValue instanceof Number) || !(rightValue instanceof Number)) {
            return null;
        }
        Number a = (Number) leftValue;
        Number b = (Number) rightValue;
        long x = a.longValue();
        long y = b.longValue();
        if ("+".equals(op)) {
            return (int) (x + y);
        } else if ("-".equals(op)) {
            return (int) (x - y);
        } else if ("*".equals(op)) {
            return (int) (x * y);
        } else if ("/".equals(op)) {
            if (y == 0) {
                return null;
            }
            return (int) (x / y);
        }
        return null;
    }

    private Object valueOf(LogicalExpr expr, Table v) {
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

    private static String operandName(LogicalExpr expr) {
        if (expr instanceof LiteralExpr) {
            return String.valueOf(((LiteralExpr) expr).evaluate(null));
        }
        return expr.getName();
    }
}
