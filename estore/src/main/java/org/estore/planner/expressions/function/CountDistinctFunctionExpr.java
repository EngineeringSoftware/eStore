package org.estore.planner.expressions.function;

import java.util.Arrays;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.util.Table;

public class CountDistinctFunctionExpr extends FunctionInvocationExpr {
    private LogicalExpr arg;

    public CountDistinctFunctionExpr() {
        this.caller = null;
        this.methodName = "COUNT";
        this.arg = null;
        this.renamedName = getName();
    }

    public CountDistinctFunctionExpr(LogicalExpr arg) {
        this.caller = null;
        this.methodName = "COUNT";
        this.arg = arg;
        this.renamedName = getName();
    }

    @Override
    public String getName() {
        if (arg != null) {
            if (arg instanceof VarExpr) {

                return methodName + "(" + ((VarExpr) arg).evaluate(null) + ")";
            }
        }
        return methodName + "(*)";
    }

    @Override
    public Table evaluate(Table v) {
        if (arg instanceof VarExpr) {
            String keyName = getName();
            Table result = new Table(Arrays.asList(new String[] {keyName}));
            String variable = ((VarExpr) arg).evaluate(null);

            result.get(keyName).add(v.getDistinctVariable(variable).size());
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
