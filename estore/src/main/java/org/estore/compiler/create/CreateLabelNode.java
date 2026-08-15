package org.estore.compiler.create;

import org.estore.planner.pattern.NodePattern;

public class CreateLabelNode {
    public static String codegen(String dbname, NodePattern nodePattern) {
        String res = "";

        int id = nodePattern.getID();
        String variable = nodePattern.getVariable();
        variable = variable == null ? "_" + "CreateLabelNode" + id : variable;
        String label = nodePattern.getLabel();

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
        res += dbname + ".addDynamicClass(klass);";
        res += "} catch (Exception e1) {";
        res += "String fullyQualifiedName = \"" + label + "\";";
        res += "fullyQualifiedName = fullyQualifiedName.replace('.', '/');";
        res += "int lastIndexSlash = fullyQualifiedName.lastIndexOf('/');";
        res += "String className = fullyQualifiedName;";
        res += "String packageName = null;";
        res += "if (lastIndexSlash != -1) {";
        res +=
                "className = fullyQualifiedName.substring(lastIndexSlash + 1,"
                        + " fullyQualifiedName.length());";
        res += "packageName = fullyQualifiedName.substring(0, lastIndexSlash);";
        res += "}";
        res += "Class<?> klass = ClassHelper.createClass(";
        res += "className, packageName, new String[0], new Class[0], new Object[0]);";
        res += "try {";
        res += "labelObjects.add(estore.insert(klass));";
        res += dbname + ".addDynamicClass(klass);";
        res += "} catch (Exception e2) {";
        res += "e2.printStackTrace();";
        res += "}";
        res += "}";
        res += "}";
        res += "res.put(\"" + variable + "\", labelObjects);";

        return res;
    }
}
