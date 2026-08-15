package org.estore.planner.scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.Table;

public class AllNodeScan extends LogicalPlan implements NodeScan {

    private String variable;
    private Estore estore;

    public AllNodeScan(NodePattern nodePattern, Estore estore) {
        this(nodePattern.getID(), estore, nodePattern.getVariable());
    }

    public AllNodeScan(int id, Estore estore, String variable) {
        this.name = "AllNodeScan";
        this.id = id;
        this.estore = estore;
        this.variable = variable == null ? "_" + this.name + this.id : variable;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        Table result = new Table(Arrays.asList(new String[] {variable}));
        result.put(variable, new ArrayList<Object>(estore.getDataStore().values()));
        return result;
    }

    @Override
    public Estore getDataSource() {
        return estore;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public List<NodeProperty> getProperties() {
        return null;
    }
}
