package org.estore.planner.expressions.function;

import java.util.Arrays;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.util.Table;

public class CountFunctionSingleExpr extends CountFunctionExpr {
    public CountFunctionSingleExpr() {
        super();
    }

    public CountFunctionSingleExpr(LogicalExpr arg) {
        super(arg);
    }

    public CountFunctionSingleExpr(CountFunctionExpr expr) {
        super(expr.getArg());
    }

    @Override
    public Table evaluate(Table v) {
        if (arg instanceof VarExpr) {
            String keyName = getName();
            Table result = new Table(Arrays.asList(new String[] {keyName}));
            String variable = ((VarExpr) arg).evaluate(null);

            result.get(keyName).add(v.get(variable).size());
            return result;
        }
        return null;
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
