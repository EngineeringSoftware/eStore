package org.estore.planner.expressions.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.util.Table;

public class CountFunctionExpr extends FunctionInvocationExpr {
    protected LogicalExpr arg;

    public CountFunctionExpr() {
        this.caller = null;
        this.methodName = "COUNT";
        this.arg = null;
        this.renamedName = getName();
    }

    public CountFunctionExpr(LogicalExpr arg) {
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

    public LogicalExpr getArg() {
        return arg;
    }

    @Override
    public Table evaluate(Table v) {
        if (arg instanceof VarExpr) {
            List<String> keys = new ArrayList<String>(v.keySet());
            String keyName = getName();
            keys.add(keyName);
            Table result = new Table(keys);
            String variable = ((VarExpr) arg).evaluate(null);

            for (int j = 0; j < v.getSize(); j++) {
                int count = 1;
                HashMap<String, Object> currentMap = v.getAtIndex(j);
                for (int k = 0; k < v.getSize(); k++) {
                    if (j == k) {
                        continue;
                    }
                    if (currentMap.get(variable).equals(v.getAtIndex(k).get(variable))) {
                        count++;
                    }
                }
                currentMap.put(keyName, count);
                result.putEntry(currentMap);
            }
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
