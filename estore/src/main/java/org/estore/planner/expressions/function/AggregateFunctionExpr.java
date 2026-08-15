package org.estore.planner.expressions.function;

import java.util.Arrays;
import java.util.List;
import org.estore.EstoreEdge;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.PropertyLookupExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.util.Table;

public class AggregateFunctionExpr extends FunctionInvocationExpr {
    protected LogicalExpr arg;

    public AggregateFunctionExpr(String methodName, LogicalExpr arg) {
        this.caller = null;
        this.methodName = methodName;
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
        Table source = v;
        String column = null;
        if (arg instanceof VarExpr) {
            column = ((VarExpr) arg).evaluate(null);
        } else if (arg instanceof PropertyLookupExpr || arg instanceof FunctionInvocationExpr) {
            source = (Table) arg.evaluate(v);
            column = arg.getName();
        }
        if (column == null || source == null || source.get(column) == null) {
            return null;
        }

        String keyName = getName();
        Table result = new Table(Arrays.asList(new String[] {keyName}));
        result.get(keyName).add(aggregate(source.get(column)));
        return result;
    }

    private Object aggregate(List<Object> values) {
        if ("MIN".equals(methodName) || "MAX".equals(methodName)) {
            Object chosen = null;
            for (Object value : values) {
                if (value == null) {
                    continue;
                }
                if (chosen == null || better(value, chosen)) {
                    chosen = value;
                }
            }
            return chosen;
        }

        double sum = 0;
        int n = 0;
        boolean allIntegers = true;
        for (Object value : values) {
            if (!(value instanceof Number)) {
                continue;
            }
            if (!(value instanceof Integer)) {
                allIntegers = false;
            }
            sum += ((Number) value).doubleValue();
            n++;
        }
        if (n == 0) {
            return null;
        }
        if ("SUM".equals(methodName)) {
            if (allIntegers) {
                return (int) sum;
            }
            return sum;
        }
        return sum / n;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean better(Object value, Object chosen) {
        int cmp;
        if (value instanceof Number && chosen instanceof Number) {
            cmp = Double.compare(((Number) value).doubleValue(), ((Number) chosen).doubleValue());
        } else if (value instanceof EstoreEdge && chosen instanceof EstoreEdge) {
            cmp = ((EstoreEdge) value).getName().compareTo(((EstoreEdge) chosen).getName());
        } else if (value.getClass().equals(chosen.getClass()) && value instanceof Comparable) {
            cmp = ((Comparable) value).compareTo(chosen);
        } else {
            return false;
        }
        if ("MAX".equals(methodName)) {
            return cmp > 0;
        }
        return cmp < 0;
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
