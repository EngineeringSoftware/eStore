package org.estore.planner.expressions;

import org.estore.planner.util.Table;

public class XorBooleanExpr implements LogicalExpr<Boolean, Table> {
    private final LogicalExpr left;
    private final LogicalExpr right;
    private String renamedName;

    public XorBooleanExpr(LogicalExpr left, LogicalExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean evaluate(Table v) {
        boolean leftValue = Boolean.TRUE.equals(left.evaluate(v));
        boolean rightValue = Boolean.TRUE.equals(right.evaluate(v));
        return leftValue ^ rightValue;
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
