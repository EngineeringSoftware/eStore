package org.estore.planner.scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.util.ClassInfo;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.Table;

public class NodeLabelPropScan extends LogicalPlan implements NodeScan {

    private Estore estore;
    private String variable;
    private String label;
    private List<NodeProperty> properties;

    public NodeLabelPropScan(NodePattern nodePattern, Estore estore) {
        this(
                nodePattern.getID(),
                estore,
                nodePattern.getVariable(),
                nodePattern.getLabel(),
                nodePattern.getProperties());
    }

    public NodeLabelPropScan(
            int id, Estore estore, String variable, String label, List<NodeProperty> properties) {
        this.name = "NodeLabelPropScan";
        this.id = id;
        this.estore = estore;
        this.properties = properties;
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
    public List<NodeProperty> getProperties() {
        return properties;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        ArrayList<Object> result = new ArrayList<Object>();
        ArrayList<Object> labelObjects = estore.getLabelObjectMap().get(label);
        ClassInfo cInfo = estore.getLabelClassInfoMap().get(label);
        Table resultTable = new Table(Arrays.asList(new String[] {variable}));

        if (labelObjects == null) {
            return resultTable;
        }

        OUTERLOOP:
        for (Object obj : labelObjects) {
            boolean flag = true;
            for (NodeProperty prop : properties) {
                try {
                    Object fieldObject =
                            cInfo.getPrimitiveField(prop.getName(), prop.getType().getName(), obj);
                    flag = fieldObject.equals(prop.getValue());
                    if (!flag) {
                        continue OUTERLOOP;
                    }
                } catch (Exception e) {
                    continue OUTERLOOP;
                }
            }
            if (flag) {
                result.add(obj);
            }
        }
        resultTable.put(variable, result);
        if (input != null) {
            resultTable = resultTable.cartesianJoin(input);
        }
        return resultTable;
    }
}
