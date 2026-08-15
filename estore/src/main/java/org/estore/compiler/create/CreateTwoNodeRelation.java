package org.estore.compiler.create;

import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.util.enums.RelationDirection;

public class CreateTwoNodeRelation {
    public static String codegen(
            String dbname, int id, NodePattern left, RelationPattern relation, NodePattern right) {
        String res = "";

        String referrerVariable =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getVariable()
                        : right.getVariable();
        String refereeVariable =
                relation.getReferrer() == RelationDirection.RIGHT
                        ? left.getVariable()
                        : right.getVariable();

        String relationLabel = relation.getEdgeNames().get(0);
        String relationVariable = relation.getVariable();

        res +=
                "if (res.containsKey(\""
                        + referrerVariable
                        + "\") && res.containsKey(\""
                        + refereeVariable
                        + "\")) {";
        res += "ArrayList<String> keys = new ArrayList<String>(res.keySet());";
        res += "keys.add(\"" + relationVariable + "\");";
        res += "Table res2 = new Table(keys);";
        res += "for (int j = 0; j < res.getSize(); j++) {";
        res += "HashMap<String, Object> item = res.getAtIndex(j);";
        res += "Object referrerObject = item.get(\"" + referrerVariable + "\");";
        res += "Object refereeObject = item.get(\"" + refereeVariable + "\");";
        res +=
                "EstoreEdge edge = new EstoreEdge(referrerObject, refereeObject, \""
                        + relationLabel
                        + "\");";
        res += "item.put(\"" + relationVariable + "\", edge);";
        res += "try {";
        res +=
                "Field refField = referrerObject.getClass().getDeclaredField(\""
                        + relationLabel
                        + "\");";
        res += "refField.setAccessible(true);";
        res += "if (refField.getType().isArray()) {";
        res += "Object referrerRefereeObject = refField.get(referrerObject);";
        res += "if (referrerRefereeObject != null) {";
        res += "int initialLength = Array.getLength(referrerRefereeObject);";
        res +=
                "Object newReferrerRefereeObject = Array.newInstance(refereeObject.getClass(),"
                        + " initialLength + 1);";
        res += "int k = 0;";
        res += "for (; k < initialLength; k++) {";
        res += "Array.set(newReferrerRefereeObject, k, Array.get(referrerRefereeObject, k));";
        res += "}";
        res += "Array.set(newReferrerRefereeObject, k, refereeObject);";
        res += "refField.set(referrerObject, newReferrerRefereeObject);";
        res += "}";
        res += "}";
        res += "res2.putEntry(item);";
        res += "} catch (Exception e) {";
        res += "e.printStackTrace();";
        res += "}";
        res += "}";
        res += "res = new Table(res2);";
        res += "}";

        return res;
    }
}
