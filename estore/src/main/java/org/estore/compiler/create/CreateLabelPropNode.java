package org.estore.compiler.create;

import java.util.List;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.util.NodeProperty;

public class CreateLabelPropNode {
    public static String codegen(String dbname, NodePattern nodePattern) {
        String res = "";

        int id = nodePattern.getID();
        String variable = nodePattern.getVariable();
        variable = variable == null ? "_" + "CreateLabelPropNode" + id : variable;
        String label = nodePattern.getLabel();
        List<NodeProperty> properties = nodePattern.getProperties();

        res += "properties = null;";
        if (properties != null) {
            res += "properties = new ArrayList<NodeProperty>();";
            for (NodeProperty prop : properties) {
                Object value = prop.getValue();
                if (value instanceof String) {
                    res +=
                            "properties.add(new NodeProperty(String.class, \""
                                    + value
                                    + "\", \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Long) {
                    res +=
                            "properties.add(new NodeProperty(Long.TYPE, "
                                    + value
                                    + "L, \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Double) {
                    res +=
                            "properties.add(new NodeProperty(Double.TYPE, "
                                    + value
                                    + ", \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Boolean) {
                    res +=
                            "properties.add(new NodeProperty(Boolean.TYPE, "
                                    + value
                                    + ", \""
                                    + prop.getName()
                                    + "\"));\n";
                }
            }
        }

        res += "labelObjects = new ArrayList<Object>();";
        res += "res = new Table(Arrays.asList(new String[] {\"" + variable + "\"}));";
        res += "try {";
        res += "Class<?> klass = Class.forName(\"" + label + "\");";
        res += "labelObjects.add(estore.insert(klass));";
        res += "} catch (Exception e) {";
        res += "try {";
        res +=
                "Class<?> klass = Class.forName(\""
                        + label
                        + "\", true, ClassHelper.getClassLoader());";
        res += "labelObjects.add(estore.insert(klass));";
        res += "} catch (Exception e1) {";
        res += "String fullyQualifiedName = \"" + label + "\";";
        res += "fullyQualifiedName = fullyQualifiedName.replace('.', '/');";
        res += "int lastIndexSlash = fullyQualifiedName.lastIndexOf('/');";
        res += "String className = fullyQualifiedName;";
        res += "String packageName = null;";
        res += "String[] fieldNames = new String[0];";
        res += "Class[] fieldTypes = new Class[0];";
        res += "if (properties != null) {";
        res += "fieldNames = new String[properties.size()];";
        res += "fieldTypes = new Class[properties.size()];";
        res += "for (int i = 0; i < properties.size(); i++) {";
        res += "fieldNames[i] = properties.get(i).getName();";
        res += "fieldTypes[i] = properties.get(i).getType();";
        res += "}";
        res += "}";
        res += "if (lastIndexSlash != -1) {";
        res +=
                "className = fullyQualifiedName.substring(lastIndexSlash + 1,"
                        + " fullyQualifiedName.length());";
        res += "packageName = fullyQualifiedName.substring(0, lastIndexSlash);";
        res += "}";
        res += "Class<?> klass = ClassHelper.createClass(";
        res += "className, packageName, fieldNames, fieldTypes, new Object[0]);";
        res += "try {";
        res += "Object obj = estore.insert(klass);";
        res += "for (NodeProperty property : properties) {";
        res += "Field f = klass.getDeclaredField(property.getName());";
        res += "if (property.getType() == long.class) {";
        res += "f.setLong(obj, ((Long) property.getValue()));";
        res += "} else if (property.getType() == double.class) {";
        res += "f.setDouble(obj, ((Double) property.getValue()));";
        res += "} else if (property.getType() == String.class) {";
        res += "f.set(obj, property.getValue());";
        res += "}";
        res += "}";
        res += "labelObjects.add(obj);";
        res += "} catch (Exception e2) {";
        res += "e2.printStackTrace();";
        res += "}";
        res += "}";
        res += "}";
        res += "res.put(\"" + variable + "\", labelObjects);";
        return res;
    }
}
