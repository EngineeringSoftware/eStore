package org.estore.planner.expressions.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.estore.EstoreEdge;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.util.Table;

public class TypeFunctionExpr extends FunctionInvocationExpr {
    protected LogicalExpr arg;

    public TypeFunctionExpr(LogicalExpr arg) {
        this.caller = null;
        this.methodName = "TYPE";
        this.arg = arg;
        this.renamedName = getName();
    }

    @Override
    public String getName() {
        if (arg instanceof VarExpr) {
            return methodName + "(" + ((VarExpr) arg).evaluate(null) + ")";
        }
        if (arg != null && arg.getName() != null) {
            return methodName + "(" + arg.getName() + ")";
        }
        return methodName;
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
                HashMap<String, Object> currentMap = v.getAtIndex(j);
                Object obj = currentMap.get(variable);
                if (obj instanceof EstoreEdge) {
                    currentMap.put(keyName, ((EstoreEdge) obj).getName());
                } else {
                    currentMap.put(keyName, null);
                }
                result.putEntry(currentMap);
            }
            return result;
        }
        if (arg instanceof FunctionInvocationExpr) {
            Table source = ((FunctionInvocationExpr) arg).evaluate(v);
            if (source == null || arg.getName() == null || source.get(arg.getName()) == null) {
                return null;
            }
            String keyName = getName();
            Table result = new Table(Arrays.asList(new String[] {keyName}));
            for (Object obj : source.get(arg.getName())) {
                if (obj instanceof EstoreEdge) {
                    result.get(keyName).add(((EstoreEdge) obj).getName());
                } else {
                    result.get(keyName).add(null);
                }
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
