package org.estore.compiler.scan;

import org.estore.planner.pattern.NodePattern;

/** Code generation for all-node scans. */
public final class AllNodeScan {

    private AllNodeScan() {}

    /**
     * Generates Java source for scanning all nodes.
     *
     * @param dbname database name expression
     * @param nodePattern node pattern
     * @return generated code fragment
     */
    public static String codegen(final String dbname, final NodePattern nodePattern) {
        String res = "";

        int id = nodePattern.getID();
        String variable = nodePattern.getVariable();
        variable = variable == null ? "_" + "AllNodeScan" + id : variable;

        res += "res = new Table(Arrays.asList(new String[] {\"" + variable + "\"}));\n";
        res +=
                "res.put(\""
                        + variable
                        + "\", new ArrayList<Object>("
                        + dbname
                        + ".getDataStore().values()));\n";

        return res;
    }
}
