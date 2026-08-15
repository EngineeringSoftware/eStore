package org.estore.planner.expressions;

import org.estore.planner.util.Table;

public class NotBooleanExpr implements LogicalExpr<Boolean, Table> {
    private final LogicalExpr expr;
    private String renamedName;

    public NotBooleanExpr(LogicalExpr expr) {
        this.expr = expr;
    }

    @Override
    public Boolean evaluate(Table v) {
        return !Boolean.TRUE.equals(expr.evaluate(v));
    }

    @Override
    public String getName() {
        return renamedName;
    }

    @Override
    public String getRenamedName() {
        return renamedName;
    }

    @Override
    public void setRenamedName(String renamedName) {
        this.renamedName = renamedName;
    }
}
