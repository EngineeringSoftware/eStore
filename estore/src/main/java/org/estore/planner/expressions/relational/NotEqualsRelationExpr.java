package org.estore.planner.expressions.relational;

import java.util.Objects;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.util.Table;

public class NotEqualsRelationExpr extends RelationExpr {

    public NotEqualsRelationExpr(LogicalExpr left, LogicalExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean evaluate(Table v) {
        Object leftResult = evaluateOperand(left, v);
        Object rightResult = evaluateOperand(right, v);
        Integer cmp = compareOperands(leftResult, rightResult);
        if (cmp != null) {
            return cmp != 0;
        }
        return !Objects.equals(leftResult, rightResult);
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
