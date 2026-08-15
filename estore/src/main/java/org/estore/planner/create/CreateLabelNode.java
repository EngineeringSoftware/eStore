package org.estore.planner.create;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.util.ClassHelper;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.Table;

public class CreateLabelNode extends LogicalPlan {

    private Estore estore;
    private String label;
    private String variable;

    public CreateLabelNode(NodePattern nodePattern, Estore estore) {
        this(nodePattern.getID(), estore, nodePattern.getVariable(), nodePattern.getLabel());
    }

    public CreateLabelNode(int id, Estore estore, String variable, String label) {
        this.name = "CreateLabelNode";
        this.id = id;
        this.estore = estore;
        this.label = label;
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
        return null;
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
                estore.addDynamicClass(klass);
            } catch (Exception e1) {
                String fullyQualifiedName = label;
                fullyQualifiedName = fullyQualifiedName.replace('.', '/');
                int lastIndexSlash = fullyQualifiedName.lastIndexOf('/');
                String className = fullyQualifiedName;
                String packageName = null;
                if (lastIndexSlash != -1) {
                    className =
                            fullyQualifiedName.substring(
                                    lastIndexSlash + 1, fullyQualifiedName.length());
                    packageName = fullyQualifiedName.substring(0, lastIndexSlash);
                }
                Class<?> klass =
                        ClassHelper.createClass(
                                className, packageName, new String[0], new Class[0], new Object[0]);
                try {
                    labelObjects.add(estore.insert(klass));
                    estore.addDynamicClass(klass);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        result.put(variable, labelObjects);
        return result;
    }
}
