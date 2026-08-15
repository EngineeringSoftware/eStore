package org.estore.compiler.create;

import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.util.enums.RelationDirection;

public class CreateLabelTwoNodeRelation {
    public static String codegen(
            String dbname, int id, NodePattern left, RelationPattern relation, NodePattern right) {
        String res = "";

        String referrerVariable =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getVariable()
                        : right.getVariable();
        String referrerLabel =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getLabel()
                        : right.getLabel();
        String refereeVariable =
                relation.getReferrer() == RelationDirection.RIGHT
                        ? left.getVariable()
                        : right.getVariable();
        String refereeLabel =
                relation.getReferrer() == RelationDirection.RIGHT
                        ? left.getLabel()
                        : right.getLabel();
        String relationLabel = relation.getEdgeNames().get(0);
        String relationVariable = relation.getVariable();

        res +=
                "res = new Table(Arrays.asList(new String[] {\""
                        + referrerVariable
                        + "\", \""
                        + refereeVariable
                        + "\", \""
                        + relationVariable
                        + "\"}));\n";
        res += "referrerObject2 = null;";
        res += "refereeObject2 = null;";
        res += "refereeClass = null;";
        res += "try {";
        res += "refereeClass = Class.forName(\"" + refereeLabel + "\");";
        res += "refereeObject2 = " + dbname + ".insert(refereeClass);";
        res += "} catch (Exception e) {";
        res += "String fullyQualifiedName =\"" + refereeLabel + "\";";
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
        res +=
                "refereeClass = ClassHelper.createClass(className, packageName, new String[0], new"
                        + " Class[0], new Object[0]);";
        res += "try {";
        res += "refereeObject2 = " + dbname + ".insert(refereeClass);";
        res += "} catch (Exception e2) {";
        res += "e2.printStackTrace();";
        res += "}";
        res += "}";
        res += "try {";
        res += "Class<?> referrerClass = Class.forName(\"" + referrerLabel + "\");";
        res += "referrerObject2 = " + dbname + ".insert(referrerClass);";
        res += "} catch (Exception e) {";
        res += "String fullyQualifiedName =\"" + referrerLabel + "\";";
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
        res +=
                "Class<?> klass = ClassHelper.createClass(className, packageName, new String[] {\""
                        + relationLabel
                        + "\"}, new Class[] {refereeClass}, new Object[] {refereeObject2});";
        res += "try {";
        res += "referrerObject2 = " + dbname + ".insert(klass);";
        res += "} catch (Exception e2) {";
        res += "e2.printStackTrace();";
        res += "}";
        res += "}";
        res += "referrerObjects2 = new ArrayList<Object>();";
        res += "refereeObjects2 = new ArrayList<Object>();";
        res += "referrerObjects2.add(referrerObject2);";
        res += "refereeObjects2.add(refereeObject2);";
        res += "res.put(\"" + referrerVariable + "\", referrerObjects2);";
        res += "res.put(\"" + refereeVariable + "\", refereeObjects2);";

        return res;
    }
}
