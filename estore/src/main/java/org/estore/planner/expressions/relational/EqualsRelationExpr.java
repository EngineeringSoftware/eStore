package org.estore.planner.expressions.relational;

import java.util.Objects;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.util.Table;

public class EqualsRelationExpr extends RelationExpr {

    public EqualsRelationExpr(LogicalExpr left, LogicalExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean evaluate(Table v) {
        Object leftResult = evaluateOperand(left, v);
        Object rightResult = evaluateOperand(right, v);

        return Objects.equals(leftResult, rightResult);
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
