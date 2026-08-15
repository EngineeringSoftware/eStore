package org.estore.planner.expressions;

import org.estore.planner.util.Table;

public class AndBooleanExpr implements LogicalExpr<Boolean, Table> {
    private final LogicalExpr left;
    private final LogicalExpr right;
    private String renamedName;

    public AndBooleanExpr(LogicalExpr left, LogicalExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean evaluate(Table v) {
        return Boolean.TRUE.equals(left.evaluate(v)) && Boolean.TRUE.equals(right.evaluate(v));
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
