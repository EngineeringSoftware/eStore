package org.estore.planner.expressions.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.estore.planner.expressions.LiteralExpr;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.PropertyLookupExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.util.Table;

public class ToIntegerFunctionExpr extends FunctionInvocationExpr {
    protected LogicalExpr arg;

    public ToIntegerFunctionExpr(LogicalExpr arg) {
        this.caller = null;
        this.methodName = "TOINTEGER";
        this.arg = arg;
        this.renamedName = getName();
    }

    @Override
    public String getName() {
        if (arg instanceof VarExpr) {
            return methodName + "(" + ((VarExpr) arg).evaluate(null) + ")";
        }
        if (arg instanceof LiteralExpr) {
            return methodName + "(" + ((LiteralExpr) arg).evaluate(null) + ")";
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
        List<Object> values = new ArrayList<Object>();
        if (arg instanceof LiteralExpr) {
            values.add(((LiteralExpr) arg).evaluate(null));
        } else if (arg instanceof VarExpr) {
            String column = ((VarExpr) arg).evaluate(null);
            if (v != null && v.get(column) != null) {
                values.addAll(v.get(column));
            }
        } else if (arg instanceof PropertyLookupExpr) {
            Table source = ((PropertyLookupExpr) arg).evaluate(v);
            if (source != null && source.get(arg.getName()) != null) {
                values.addAll(source.get(arg.getName()));
            }
        } else if (arg instanceof FunctionInvocationExpr) {
            Table source = ((FunctionInvocationExpr) arg).evaluate(v);
            if (source != null && arg.getName() != null && source.get(arg.getName()) != null) {
                values.addAll(source.get(arg.getName()));
            }
        }

        String keyName = getName();
        Table result = new Table(Arrays.asList(new String[] {keyName}));
        for (Object value : values) {
            result.get(keyName).add(toInteger(value));
        }
        return result;
    }

    private Object toInteger(Object value) {
        if (value instanceof Integer) {
            return value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
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
