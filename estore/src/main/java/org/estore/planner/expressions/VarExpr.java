package org.estore.planner.expressions;

public class VarExpr implements LogicalExpr<String, Void> {
    private String varName;
    private String renamedName;

    public VarExpr(String varName) {
        this.varName = varName;
        this.renamedName = varName;
    }

    public VarExpr(String varName, String renamedName) {
        this.varName = varName;
        this.renamedName = renamedName;
    }

    @Override
    public String evaluate(Void v) {
        return varName;
    }

    @Override
    public String getName() {
        return evaluate(null);
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
