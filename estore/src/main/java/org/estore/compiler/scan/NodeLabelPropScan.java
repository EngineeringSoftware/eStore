package org.estore.compiler.scan;

import java.util.List;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.util.NodeProperty;

/** Code generation for node property scans with a label filter. */
public final class NodeLabelPropScan {

    private NodeLabelPropScan() {}

    /**
     * Generates Java source for scanning labeled nodes with property filters.
     *
     * @param dbname database name expression
     * @param nodePattern node pattern
     * @return generated code fragment
     */
    public static String codegen(final String dbname, final NodePattern nodePattern) {
        String res = "";
        int id = nodePattern.getID();
        String variable = nodePattern.getVariable();
        String label = nodePattern.getLabel();
        List<NodeProperty> referrerProperties = nodePattern.getProperties();
        variable = variable == null ? "_" + "NodeLabelPropScan" + id : variable;

        if (referrerProperties != null) {
            res += "referrerProperties = new ArrayList<NodeProperty>();\n";
            for (NodeProperty prop : referrerProperties) {
                Object value = prop.getValue();
                if (value instanceof String) {
                    res +=
                            "referrerProperties.add(new NodeProperty(String.class, \""
                                    + value
                                    + "\", \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Long) {
                    res +=
                            "referrerProperties.add(new NodeProperty(Long.TYPE, "
                                    + value
                                    + "L, \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Double) {
                    res +=
                            "referrerProperties.add(new NodeProperty(Double.TYPE, "
                                    + value
                                    + ", \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Boolean) {
                    res +=
                            "referrerProperties.add(new NodeProperty(Boolean.TYPE, "
                                    + value
                                    + ", \""
                                    + prop.getName()
                                    + "\"));\n";
                }
            }
        }

        res += "resObjs = new ArrayList<Object>();";
        res += "labelObjects = " + dbname + ".getLabelObjectMap().get(\"" + label + "\");";
        res += "cInfo = " + dbname + ".getLabelClassInfoMap().get(\"" + label + "\");";
        res +=
                "Table res_"
                        + id
                        + " = new Table(Arrays.asList(new String[] {\""
                        + variable
                        + "\"}));";

        res += "if (labelObjects != null) {";
        res += "  for (Object obj : labelObjects) {";
        res += "    boolean flag = true;";
        res += "    for (NodeProperty prop : referrerProperties) {";
        res += "      try {";
        res +=
                "        Object fieldObject = cInfo.getPrimitiveField(prop.getName(),"
                        + " prop.getType().getName(), obj);";
        res += "        flag = fieldObject.equals(prop.getValue());";
        res += "        if (!flag) {";
        res += "          continue;";
        res += "        }";
        res += "      } catch (Exception e) {";
        res += "        e.printStackTrace();";
        res += "        continue;";
        res += "      }";
        res += "    }";
        res += "    if (flag) {";
        res += "      resObjs.add(obj);";
        res += "    }";
        res += "  }";
        res += "res_" + id + ".put(\"" + variable + "\", resObjs);";
        res += "if (res != null) {";
        res += "  res_" + id + " = res_" + id + ".cartesianJoin(res);";
        res += "}";
        res += "res = new Table(res_" + id + ");";
        res += "}";

        return res;
    }
}
