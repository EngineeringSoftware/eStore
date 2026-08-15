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

public class NodePropScan extends LogicalPlan implements NodeScan {

    private Estore estore;
    private String variable;
    private List<NodeProperty> properties;

    public NodePropScan(NodePattern nodePattern, Estore estore) {
        this(nodePattern.getID(), estore, nodePattern.getVariable(), nodePattern.getProperties());
    }

    public NodePropScan(int id, Estore estore, String variable, List<NodeProperty> properties) {
        this.name = "NodePropScan";
        this.id = id;
        this.estore = estore;
        this.properties = properties;
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
        return null;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        ArrayList<Object> result = new ArrayList<Object>();
        Table resultTable = new Table(Arrays.asList(new String[] {variable}));

        OUTERLOOP:
        for (String className : estore.getLabelObjectMap().keySet()) {
            ClassInfo cInfo = estore.getLabelClassInfoMap().get(className);
            boolean flag = true;
            for (NodeProperty prop : properties) {
                flag = cInfo.containsPrimitiveFieldWithName(prop.getName());
                if (!flag) {
                    continue OUTERLOOP;
                }
            }
            for (Object obj : estore.getLabelObjectMap().get(className)) {
                boolean flag2 = true;
                for (NodeProperty prop : properties) {
                    try {

                        Object fieldObject =
                                cInfo.getPrimitiveField(
                                        prop.getName(), prop.getType().getName(), obj);
                        flag2 = fieldObject.equals(prop.getValue());
                        if (!flag2) {
                            continue;
                        }
                    } catch (Exception e) {
                        continue OUTERLOOP;
                    }
                }
                if (flag2) {
                    result.add(obj);
                }
            }
        }

        resultTable.put(variable, result);
        return resultTable;
    }
}
