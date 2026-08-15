package org.estore.planner.expressions.relational;

import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.function.FunctionInvocationExpr;
import org.estore.planner.util.Table;

public abstract class RelationExpr implements LogicalExpr<Boolean, Table> {
    protected LogicalExpr left;
    protected LogicalExpr right;
    protected String renamedName;

    @Override
    public abstract Boolean evaluate(Table v);

    @Override
    public abstract String getName();

    @Override
    public abstract void setRenamedName(String renamedName);

    @Override
    public abstract String getRenamedName();

    protected Object evaluateOperand(LogicalExpr expr, Table v) {
        if (expr instanceof FunctionInvocationExpr) {
            FunctionInvocationExpr functionExpr = (FunctionInvocationExpr) expr;
            Table functionResultTable = functionExpr.evaluate(v);
            if (functionResultTable == null) {
                return null;
            }
            java.util.ArrayList<Object> col = functionResultTable.get(functionExpr.getName());
            if (col == null || col.isEmpty()) {
                return null;
            }
            return col.get(0);
        }

        try {
            Object value = expr.evaluate(v);
            if (value instanceof Table) {
                Table valueTable = (Table) value;
                String column = expr.getName();
                if (valueTable.get(column) == null || valueTable.get(column).isEmpty()) {
                    return null;
                }
                return valueTable.get(column).get(0);
            }
            return value;
        } catch (ClassCastException ignored) {
            return expr.evaluate(null);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Integer compareOperands(Object leftValue, Object rightValue) {
        if (leftValue == null || rightValue == null) {
            return null;
        }
        if (leftValue instanceof Number && rightValue instanceof Number) {
            return Double.compare(
                    ((Number) leftValue).doubleValue(), ((Number) rightValue).doubleValue());
        }
        if (leftValue.getClass().equals(rightValue.getClass()) && leftValue instanceof Comparable) {
            return ((Comparable) leftValue).compareTo(rightValue);
        }
        return null;
    }
}
