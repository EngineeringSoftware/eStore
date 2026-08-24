package org.estore.planner.create;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.util.ClassHelper;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.Table;

public class CreateLabelPropNode extends LogicalPlan {

    private Estore estore;
    private String label;
    private String variable;
    private List<NodeProperty> properties;

    public CreateLabelPropNode(NodePattern nodePattern, Estore estore) {
        this(
                nodePattern.getID(),
                estore,
                nodePattern.getVariable(),
                nodePattern.getLabel(),
                nodePattern.getProperties());
    }

    public CreateLabelPropNode(
            int id, Estore estore, String variable, String label, List<NodeProperty> properties) {
        this.name = "CreateLabelPropNode";
        this.id = id;
        this.estore = estore;
        this.label = label;
        this.properties = properties;
        this.variable = variable == null ? "_" + this.name + this.id : variable;
    }

    public Estore getDataSource() {
        return estore;
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

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        ArrayList<Object> labelObjects = new ArrayList<Object>();
        Table result = new Table(Arrays.asList(new String[] {variable}));
        try {
            Class<?> klass = Class.forName(label);
            labelObjects.add(estore.insert(klass));
        } catch (Exception e) {
            try {
                Class<?> klass = Class.forName(label, true, ClassHelper.getClassLoader());
                labelObjects.add(estore.insert(klass));
            } catch (Exception e1) {
                String fullyQualifiedName = label;
                fullyQualifiedName = fullyQualifiedName.replace('.', '/');
                int lastIndexSlash = fullyQualifiedName.lastIndexOf('/');
                String className = fullyQualifiedName;
                String packageName = null;
                String[] fieldNames = new String[0];
                Class[] fieldTypes = new Class[0];
                if (properties != null) {
                    fieldNames = new String[properties.size()];
                    fieldTypes = new Class[properties.size()];
                    for (int j = 0; j < properties.size(); j++) {
                        fieldNames[j] = properties.get(j).getName();
                        fieldTypes[j] = properties.get(j).getType();
                    }
                }
                if (lastIndexSlash != -1) {
                    className =
                            fullyQualifiedName.substring(
                                    lastIndexSlash + 1, fullyQualifiedName.length());
                    packageName = fullyQualifiedName.substring(0, lastIndexSlash);
                }
                Class<?> klass =
                        ClassHelper.createClass(
                                className, packageName, fieldNames, fieldTypes, new Object[0]);
                try {
                    Object obj = estore.insert(klass);
                    for (NodeProperty property : properties) {
                        Field f = klass.getDeclaredField(property.getName());
                        if (property.getType() == long.class) {
                            f.setLong(obj, ((Long) property.getValue()));
                        } else if (property.getType() == double.class) {
                            f.setDouble(obj, ((Double) property.getValue()));
                        } else if (property.getType() == String.class) {
                            f.set(obj, property.getValue());
                        }
                    }
                    labelObjects.add(obj);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        result.put(variable, labelObjects);
        return result;
    }
}
