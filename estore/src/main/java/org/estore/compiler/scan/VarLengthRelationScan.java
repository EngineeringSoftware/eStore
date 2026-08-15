package org.estore.compiler.scan;

import java.util.List;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.PathRange;
import org.estore.planner.util.enums.RelationDirection;

/** Code generation for variable-length relation scans (path ranges). */
public final class VarLengthRelationScan {

    private VarLengthRelationScan() {}

    /**
     * Returns the variable name for the relation referrer side.
     *
     * @param left left node pattern
     * @param relation relation pattern
     * @param right right node pattern
     * @return referrer variable name
     */
    public static String getReferrerVariable(
            final NodePattern left, final RelationPattern relation, final NodePattern right) {
        return relation.getReferrer() == RelationDirection.LEFT
                ? left.getVariable()
                : right.getVariable();
    }

    /**
     * Generates Java source for a variable-length relation scan.
     *
     * @param dbname database name expression
     * @param id result table id suffix
     * @param left left node pattern
     * @param relation relation pattern
     * @param right right node pattern
     * @return generated code fragment
     */
    public static String codegen(
            final String dbname,
            final int id,
            final NodePattern left,
            final RelationPattern relation,
            final NodePattern right) {
        String res = "";

        String referrerVariable =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getVariable()
                        : right.getVariable();
        String referrerLabel =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getLabel()
                        : right.getLabel();
        List<NodeProperty> referrerProperties =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getProperties()
                        : right.getProperties();
        String refereeVariable =
                relation.getReferee() == RelationDirection.LEFT
                        ? left.getVariable()
                        : right.getVariable();
        String refereeLabel =
                relation.getReferee() == RelationDirection.LEFT
                        ? left.getLabel()
                        : right.getLabel();
        List<NodeProperty> refereeProperties =
                relation.getReferee() == RelationDirection.LEFT
                        ? left.getProperties()
                        : right.getProperties();
        List<String> edgeNames = relation.getEdgeNames();
        PathRange pRange = relation.getPathRange();

        res += "referrerProperties = null;\n";
        res += "refereeProperties = null;\n";
        res += "edgeNames = null;\n";
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
        if (refereeProperties != null) {
            res += "refereeProperties = new ArrayList<NodeProperty>();\n";
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
        if (edgeNames != null) {
            res += "edgeNames = new ArrayList<String>();\n";
            for (String edgeName : edgeNames) {
                res += "edgeNames.add(\"" + edgeName + "\");\n";
            }
        }

        if (referrerLabel != null) {
            res +=
                    dfs(
                            dbname,
                            id,
                            referrerVariable,
                            referrerLabel,
                            refereeVariable,
                            refereeLabel,
                            pRange);
        } else {
            // Non-DFS case not implemented yet
            System.out.println("Non-DFS case VarLengthRelation not supported yet");
            System.exit(1);
        }

        return res;
    }

    /**
     * Generates DFS-based scan code for labeled variable-length paths.
     *
     * @param dbname database name expression
     * @param id result table id suffix
     * @param referrerVariable referrer variable name
     * @param referrerLabel referrer label or null
     * @param refereeVariable referee variable name
     * @param refereeLabel referee label or null
     * @param pRange path hop range
     * @return generated code fragment
     */
    public static String dfs(
            final String dbname,
            final int id,
            final String referrerVariable,
            final String referrerLabel,
            final String refereeVariable,
            final String refereeLabel,
            final PathRange pRange) {
        String res = "";

        res +=
                "Table res_"
                        + id
                        + " = new Table(Arrays.asList(new String[] {\""
                        + referrerVariable
                        + "\", \""
                        + refereeVariable
                        + "\"}));\n";
        res +=
                "for (Object startNode : Util.getStartingNodes(\""
                        + referrerLabel
                        + "\", referrerProperties, "
                        + dbname
                        + ")){";
        res += "Set<Object> visited = new HashSet<>();";
        res += "Stack<DFSNode> stack = new Stack<>();";
        res += "stack.push(new DFSNode(startNode, 0));";
        res += "while (!stack.isEmpty()) {";
        res += "DFSNode current = stack.pop();";
        res += "Object currentNode = current.currentNode;";
        res += "int depth = current.depth;";
        res += "if (depth > " + pRange.getUpperBound() + ") {";
        res += "continue;}";
        res += "visited.add(currentNode);";

        res += "if (depth >= " + pRange.getLowerBound();
        if (refereeLabel != null) {
            res += "&& Util.checkNodeLabel(currentNode, \"" + refereeLabel + "\")";
        } else {
            res += "&& Util.checkNodeLabel(currentNode, null)";
        }
        res += "&& Util.checkNodeProperties(currentNode, refereeProperties, " + dbname + ")) {";
        res += "HashMap<String, Object> row = new HashMap<>();";
        res += "row.put(\"" + referrerVariable + "\", startNode);";
        res += "row.put(\"" + refereeVariable + "\", currentNode);";
        res += "res_" + id + ".putEntry(row);";
        res += "}";

        res += "if (depth < " + pRange.getUpperBound() + ") {";
        res +=
                "for (Object neighbor : Util.getNeighbors(currentNode, edgeNames, "
                        + dbname
                        + ")) {";
        res += "if (neighbor != null && !visited.contains(neighbor)) {";
        res += "stack.push(new DFSNode(neighbor, depth + 1));";
        res += "}}}}}";

        return res;
    }
}
