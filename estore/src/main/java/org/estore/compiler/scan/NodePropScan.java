package org.estore.compiler.scan;

import java.util.List;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.util.NodeProperty;

/** Code generation for node property scans. */
public final class NodePropScan {

    private NodePropScan() {}

    /**
     * Generates Java source for scanning nodes by property list.
     *
     * @param dbname database name expression
     * @param nodePattern node pattern
     * @return generated code fragment
     */
    public static String codegen(final String dbname, final NodePattern nodePattern) {
        String res = "";
        int id = nodePattern.getID();
        String variable = nodePattern.getVariable();
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
        System.out.println("Not supported yet: NodePropScan");
        System.exit(1);

        return res;
    }
}
