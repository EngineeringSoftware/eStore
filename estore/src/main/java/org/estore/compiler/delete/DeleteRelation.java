package org.estore.compiler.delete;

public class DeleteRelation {
    public static String codegen(String dbname, int id, String variable) {
        String res = "";

        res += "Table res_" + id + " = new Table(new ArrayList<String>(res.keySet()));";
        res += "for (int j = 0; j < res.getSize(); j++) {";
        res += "HashMap<String, Object> item = res.getAtIndex(j);";
        res += "EstoreEdge edge = (EstoreEdge) item.get(\"" + variable + "\");";
        res += "Object referrerObject = edge.getReferrerObject();";
        res += "try {";
        res += "if (referrerObject.getClass().isArray()) {";
        res += "int elementIndex = Integer.parseInt(edge.getName());";
        res += "int arrayLength = Array.getLength(referrerObject);";
        res += "if (elementIndex >= 0 && elementIndex < arrayLength) {";
        res += "Array.set(referrerObject, elementIndex, null);";
        res += "}";
        res += "} else {";
        res += "Field refereeField = referrerObject.getClass().getDeclaredField(edge.getName());";
        res += "refereeField.setAccessible(true);";
        res += "refereeField.set(referrerObject, null);";
        res += "}";
        res += "item.put(\"" + variable + "\", null);";
        res += "} catch (Exception e) {";
        res += "e.printStackTrace();";
        res += "}";
        res += "res_" + id + ".putEntry(item);";
        res += "}";
        res += "res = new Table(res_" + id + ");";

        return res;
    }
}
