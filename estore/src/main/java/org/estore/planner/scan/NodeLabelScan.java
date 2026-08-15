package org.estore.planner.scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.Table;

public class NodeLabelScan extends LogicalPlan implements NodeScan {

    private Estore estore;
    private String label;
    private String variable;

    public NodeLabelScan(NodePattern nodePattern, Estore estore) {
        this(nodePattern.getID(), estore, nodePattern.getVariable(), nodePattern.getLabel());
    }

    public NodeLabelScan(int id, Estore estore, String variable, String label) {
        this.name = "NodeLabelScan";
        this.id = id;
        this.estore = estore;
        this.label = label;
        this.variable = variable == null ? "_" + this.name + this.id : variable;
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
        return label;
    }

    @Override
    public List<NodeProperty> getProperties() {
        return null;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        ArrayList<Object> labelObjects = estore.getLabelObjectMap().get(label);
        Table result = new Table(Arrays.asList(new String[] {variable}));
        if (labelObjects == null) {
            return result;
        }
        result.put(variable, labelObjects);
        return result;
    }
}
