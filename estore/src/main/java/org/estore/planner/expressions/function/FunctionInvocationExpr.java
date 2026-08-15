package org.estore.planner.expressions.function;

import java.util.List;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.util.Table;

public abstract class FunctionInvocationExpr implements LogicalExpr<Table, Table> {
    protected String caller;
    protected String methodName;
    protected List<LogicalExpr> args;
    protected String renamedName;

    @Override
    public abstract Table evaluate(Table v);

    @Override
    public abstract void setRenamedName(String renamedName);

    @Override
    public abstract String getRenamedName();

    @Override
    public abstract String getName();
}
