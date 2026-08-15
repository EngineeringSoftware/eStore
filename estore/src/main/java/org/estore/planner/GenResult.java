package org.estore.planner;

import java.util.ArrayList;
import org.estore.planner.util.Table;

public class GenResult extends LogicalPlan {

    public GenResult(int id) {
        this.id = id;
        children = new ArrayList<LogicalPlan>();
        this.name = "GenResult";
    }

    public void add(LogicalPlan plan) {
        children.add(plan);
    }

    @Override
    public java.util.List<LogicalPlan> children() {
        return children;
    }

    @Override
    public Table execute(Table input) {
        Table result = null;
        for (LogicalPlan plan : children) {
            result = plan.execute(result);
        }
        return result;
    }
}
