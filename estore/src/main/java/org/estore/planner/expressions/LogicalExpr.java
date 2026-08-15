package org.estore.planner.expressions;

public interface LogicalExpr<T, V> {
    public T evaluate(V v);

    public String getName();

    public String getRenamedName();

    public void setRenamedName(String renamedName);
}
