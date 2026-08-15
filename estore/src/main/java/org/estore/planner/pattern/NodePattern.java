package org.estore.planner.pattern;

import java.util.List;
import org.estore.planner.util.NodeProperty;

public class NodePattern extends Pattern {

    private String variable;
    private String label;
    private List<NodeProperty> properties;

    public NodePattern(int id) {
        this(id, null, null, null);
    }

    public NodePattern(NodePattern nodePattern, String variable) {
        this(nodePattern.getID(), variable, nodePattern.getLabel(), nodePattern.getProperties());
    }

    public NodePattern(NodePattern nodePattern, String variable, String label) {
        this(nodePattern.getID(), variable, label, nodePattern.getProperties());
    }

    public NodePattern(NodePattern nodePattern, List<NodeProperty> properties) {
        this(nodePattern.getID(), nodePattern.getVariable(), nodePattern.getLabel(), properties);
    }

    public NodePattern(int id, String variable, String label, List<NodeProperty> properties) {
        this.name = "NodePattern";
        this.id = id;
        this.label = label;
        this.variable = variable == null ? "_" + this.name + this.id : variable;
        this.properties = properties;
    }

    public String getVariable() {
        return variable;
    }

    public String getLabel() {
        return label;
    }

    public List<NodeProperty> getProperties() {
        return properties;
    }
}
