package org.estore.compiler.scan;

import org.estore.planner.pattern.NodePattern;

/** Code generation for label-based node scans. */
public final class NodeLabelScan {

    private NodeLabelScan() {}

    /**
     * Generates Java source for scanning nodes by label.
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
        variable = variable == null ? "_" + "AllNodeScan" + id : variable;

        res += "labelObjects = " + dbname + ".getLabelObjectMap().get(\"" + label + "\");\n";
        res += "res = new Table(Arrays.asList(new String[] {\"" + variable + "\"}));\n";
        res +=
                "if (labelObjects != null) {\n"
                        + "  res.put(\""
                        + variable
                        + "\", labelObjects);\n"
                        + "}\n";

        return res;
    }
}
