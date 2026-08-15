package org.estore.planner;

import java.util.ArrayList;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.util.ClassInfo;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.Table;

public abstract class LogicalPlan {
    protected String name;
    protected List<LogicalPlan> children;
    protected int id;
    protected Estore estore;

    public abstract List<LogicalPlan> children();

    public abstract Table execute(Table input);

    public boolean checkNodeLabel(Object node, String expectedLabel) {
        if (expectedLabel == null) {
            return true;
        }
        String className = node.getClass().getName();
        return className.equals(expectedLabel);
    }

    public boolean checkNodeProperties(Object node, List<NodeProperty> properties) {
        String className = node.getClass().getName();
        ClassInfo classInfo = estore.getLabelClassInfoMap().get(className);

        if (properties == null || properties.isEmpty()) {
            return true;
        }
        if (classInfo == null) {
            return false;
        }
        for (NodeProperty property : properties) {
            if (!classInfo.containsPrimitiveFieldWithName(property.getName())) {
                return false;
            }

            Object fieldValue =
                    classInfo.getPrimitiveField(
                            property.getName(), property.getType().getName(), node);
            if (!fieldValue.equals(property.getValue())) {
                return false;
            }
        }
        return true;
    }

    public List<Object> getNeighbors(Object node, List<String> edgeNames) {
        List<Object> neighbors = new ArrayList<>();
        String className = node.getClass().getName();
        ClassInfo classInfo = estore.getLabelClassInfoMap().get(className);

        if (classInfo == null) {
            return neighbors;
        }

        if (edgeNames == null) {
            for (String referenceFieldName : classInfo.getReferenceFieldNames(node)) {
                List<Object> referenceObjects =
                        classInfo.getReferenceField(referenceFieldName, node);
                if (referenceObjects != null) {
                    neighbors.addAll(referenceObjects);
                }
            }
        } else {
            for (String edgeName : edgeNames) {
                if (classInfo.containsReferenceFieldWithName(edgeName, node)) {
                    List<Object> referenceObjects = classInfo.getReferenceField(edgeName, node);
                    if (referenceObjects != null) {
                        neighbors.addAll(referenceObjects);
                    }
                }
            }
        }

        return neighbors;
    }

    public void addChild(LogicalPlan plan) {
        children.add(plan);
    }

    public String getUniqueName() {
        return name + "_" + id;
    }

    public String toString(int indent) {
        String content = "";
        for (int j = indent - 2; j >= 0; j--) {
            content += " ";
        }
        content += "|";
        for (int j = 0; j < indent - 3; j++) {
            content += "-";
        }
        content += this.getName() + "_" + this.getID() + "\n";
        for (int j = 0; j < indent; j++) {}

        if (children != null) {
            for (int j = children.size() - 1; j >= 0; j--) {
                content += children.get(j).toString(indent + 2);
            }
        }
        return content;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }
}
