package org.estore.planner.util;

public class NodeProperty {
    private Class<?> type;
    private Object value;
    private String name;

    public NodeProperty(Class<?> type, Object value, String name) {
        this.type = type;
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public boolean equals(NodeProperty other) {
        return (type.getName().equals(other.type.getName()))
                && (value.equals(other.value))
                && (name.equals(other.name));
    }

    @Override
    public String toString() {
        return name + ":" + type.getSimpleName() + "=" + value;
    }
}
