package org.estore.planner.expressions.relational;

import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.util.Table;

public class GreaterThanOrEqualsRelationExpr extends RelationExpr {

    public GreaterThanOrEqualsRelationExpr(LogicalExpr left, LogicalExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean evaluate(Table v) {
        Object leftResult = evaluateOperand(left, v);
        Object rightResult = evaluateOperand(right, v);
        Integer comparison = compareOperands(leftResult, rightResult);
        return comparison != null && comparison >= 0;
    }

    @Override
    public String getName() {
        return renamedName;
    }

    @Override
    public void setRenamedName(String renamedName) {
        this.renamedName = renamedName;
    }

    @Override
    public String getRenamedName() {
        return renamedName;
    }
}
