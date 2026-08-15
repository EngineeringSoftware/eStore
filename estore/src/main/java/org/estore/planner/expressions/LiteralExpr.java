package org.estore.planner.expressions;

public class LiteralExpr implements LogicalExpr<Object, Void> {
    private Class<?> type;
    private Object value;
    private String renamedName;

    public LiteralExpr(Class<?> type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public Object evaluate(Void v) {
        return value;
    }

    @Override
    public String getName() {
        return renamedName;
    }

    @Override
    public String getRenamedName() {
        return renamedName;
    }

    @Override
    public void setRenamedName(String renamedName) {
        this.renamedName = renamedName;
    }
}
