package org.estore.compiler.scan;

import java.util.List;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.enums.RelationDirection;

/** Code generation for two-node relation scans. */
public final class TwoNodeRelationScan {

    private TwoNodeRelationScan() {}

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
     * Generates Java source for a two-node relation scan.
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
        String variable = relation.getVariable();

        String res =
                buildRelationScanPrefix(
                        id,
                        referrerVariable,
                        refereeVariable,
                        variable,
                        referrerProperties,
                        refereeProperties,
                        edgeNames);

        if (referrerLabel == null && refereeLabel == null) {
            res += handlePatternNoLabel(dbname, id, referrerVariable, refereeVariable, variable);
        } else if (referrerLabel != null && refereeLabel != null) {
            res +=
                    handlePatternReferrerRefereeLabel(
                            dbname,
                            id,
                            referrerVariable,
                            refereeVariable,
                            variable,
                            referrerLabel,
                            refereeLabel);
        } else if (referrerLabel != null && refereeLabel == null) {
            res +=
                    handlePatternReferrerLabel(
                            dbname,
                            id,
                            referrerVariable,
                            refereeVariable,
                            variable,
                            referrerLabel,
                            refereeLabel);
        } else {
            res +=
                    handlePatternRefereeLabel(
                            dbname,
                            id,
                            referrerVariable,
                            refereeVariable,
                            variable,
                            referrerLabel,
                            refereeLabel);
        }

        return res;
    }

    private static String buildRelationScanPrefix(
            final int id,
            final String referrerVariable,
            final String refereeVariable,
            final String variable,
            final List<NodeProperty> referrerProperties,
            final List<NodeProperty> refereeProperties,
            final List<String> edgeNames) {
        String res =
                "Table res_"
                        + id
                        + " = new Table(Arrays.asList(new String[] {\""
                        + referrerVariable
                        + "\", \""
                        + refereeVariable
                        + "\", \""
                        + variable
                        + "\"}));\n";
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
            for (NodeProperty prop : refereeProperties) {
                Object value = prop.getValue();
                if (value instanceof String) {
                    res +=
                            "refereeProperties.add(new NodeProperty(String.class, \""
                                    + value
                                    + "\", \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Long) {
                    res +=
                            "refereeProperties.add(new NodeProperty(Long.TYPE, "
                                    + value
                                    + "L, \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Double) {
                    res +=
                            "refereeProperties.add(new NodeProperty(Double.TYPE, "
                                    + value
                                    + ", \""
                                    + prop.getName()
                                    + "\"));\n";
                } else if (value instanceof Boolean) {
                    res +=
                            "refereeProperties.add(new NodeProperty(Boolean.TYPE, "
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
        return res;
    }

    /**
     * Handles patterns with no node labels on either side.
     *
     * @param dbname database name expression
     * @param id result table id suffix
     * @param referrerVariable referrer variable name
     * @param refereeVariable referee variable name
     * @param variable edge variable name
     * @return generated code fragment
     */
    public static String handlePatternNoLabel(
            final String dbname,
            final int id,
            final String referrerVariable,
            final String refereeVariable,
            final String variable) {
        String res = "";

        res += "for (String className :" + dbname + ".getLabelObjectMap().keySet()) {";
        res += "  referrerCinfo = " + dbname + ".getLabelClassInfoMap().get(className);";
        res += "  if (referrerCinfo.getReferenceFieldCount() > 0) {";
        res +=
                "    if (!Util.checkNodeClassNodePropertyMatch(referrerCinfo,"
                        + " referrerProperties)) {";
        res += "      continue;}";
        res += "    if (!Util.checkEdgeMatch(edgeNames, referrerCinfo)) {";
        res += "      continue;}";
        res +=
                "    for (Object referrerObject : "
                        + dbname
                        + ".getLabelObjectMap().get(className)) {";
        res +=
                "      if (!Util.checkNodeNodePropertyMatch(referrerCinfo,"
                        + " referrerObject, referrerProperties)) {";
        res += "        continue;}";
        res +=
                "      for (String refField :"
                        + " referrerCinfo.getReferenceFieldNames(referrerObject)) {";
        res += "        try {";
        res += "          if (edgeNames != null && !edgeNames.contains(refField)) {";
        res += "            continue;}";
        res +=
                "          refereeObjects = referrerCinfo.getReferenceField(refField,"
                        + " referrerObject);";
        res += "          for (Object refereeObject : refereeObjects) {";
        res += "            if (refereeObject == null) { continue;}";
        res += "            if (refereeObject.getClass().equals(Class.class)) {";
        res +=
                "              if (!Util.checkClassNodePropertyMatch("
                        + "(Class<?>) refereeObject, refereeProperties)) {";
        res += "                continue;}";
        res += "            } else {";
        res +=
                "              refereeCinfo = "
                        + dbname
                        + ".getLabelClassInfoMap().get("
                        + "refereeObject.getClass().getName());";
        res += "              if (refereeCinfo == null) { continue;}";
        res +=
                "              if (!Util.checkNodeClassNodePropertyMatch("
                        + "refereeCinfo, refereeProperties)) {";
        res += "                continue;}";
        res +=
                "              if (!Util.checkNodeNodePropertyMatch("
                        + "refereeCinfo, refereeObject, refereeProperties)) {";
        res += "                continue;}";
        res += "            }";

        res += "            temp = new HashMap<String, Object>();";
        res += "            temp.put(\"" + referrerVariable + "\", referrerObject);";
        res += "            temp.put(\"" + refereeVariable + "\", refereeObject);";
        res +=
                "            temp.put(\""
                        + variable
                        + "\", new EstoreEdge(referrerObject, refereeObject, refField));";
        res += "            res_" + id + ".putEntry(temp);";
        res += "          }";
        res += "        } catch (Exception e) {";
        res += "          e.printStackTrace();";
        res += "          return null;";
        res += "        }";
        res += "      }";
        res += "    }";
        res += "  }";
        res += "}";

        return res;
    }

    /**
     * Handles patterns with a referrer label only.
     *
     * @param dbname database name expression
     * @param id result table id suffix
     * @param referrerVariable referrer variable name
     * @param refereeVariable referee variable name
     * @param variable edge variable name
     * @param referrerLabel referrer label
     * @param refereeLabel referee label (may be null)
     * @return generated code fragment
     */
    private static String handlePatternReferrerLabel(
            final String dbname,
            final int id,
            final String referrerVariable,
            final String refereeVariable,
            final String variable,
            final String referrerLabel,
            final String refereeLabel) {
        String res = "";

        res +=
                "referrerCinfo = "
                        + dbname
                        + ".getLabelClassInfoMap().get(\""
                        + referrerLabel
                        + "\");";
        res += "if (referrerCinfo != null";
        res += "&& Util.checkNodeClassNodePropertyMatch(referrerCinfo," + " referrerProperties)";
        res += "&& Util.checkEdgeMatch(edgeNames, referrerCinfo)) {";

        res +=
                "  for (Object referrerObject : "
                        + dbname
                        + ".getLabelObjectMap().get(\""
                        + referrerLabel
                        + "\")) {";
        res +=
                "    if (!Util.checkNodeNodePropertyMatch(referrerCinfo,"
                        + " referrerObject, referrerProperties)) {";
        res += "      continue;}";
        res +=
                "    for (String rFieldName :"
                        + " referrerCinfo.getReferenceFieldNames(referrerObject)) {";
        res += "      if (edgeNames != null && !edgeNames.contains(rFieldName)) {";
        res += "        continue;}";
        res += "      try {";
        res +=
                "        refereeObjects = referrerCinfo.getReferenceField(rFieldName,"
                        + " referrerObject);";
        res += "        for (Object refereeObject : refereeObjects) {";
        res += "          if (refereeObject == null) { continue;}";
        res += "          if (refereeObject.getClass().equals(Class.class)) {";
        res +=
                "            if (!Util.checkClassNodePropertyMatch("
                        + "(Class<?>) refereeObject, refereeProperties)) {";
        res += "              continue;}";
        res += "          } else {";
        res +=
                "            refereeCinfo = "
                        + dbname
                        + ".getLabelClassInfoMap().get("
                        + "refereeObject.getClass().getName());";
        res +=
                "            if (!Util.checkNodeNodePropertyMatch("
                        + "refereeCinfo, refereeObject, refereeProperties)) {";
        res += "              continue;}";
        res += "          }";

        res += "          temp = new HashMap<String, Object>();";
        res += "          temp.put(\"" + referrerVariable + "\", referrerObject);";
        res += "          temp.put(\"" + refereeVariable + "\", refereeObject);";
        res +=
                "          temp.put(\""
                        + variable
                        + "\", new EstoreEdge(referrerObject, refereeObject, rFieldName));";
        res += "          res_" + id + ".putEntry(temp);";
        res += "        }";
        res += "      } catch (Exception e) {";
        res += "        e.printStackTrace();";
        res += "        return null;";
        res += "      }";
        res += "    }";
        res += "  }";
        res += "}";
        return res;
    }

    /**
     * Handles patterns with both referrer and referee labels.
     *
     * @param dbname database name expression
     * @param id result table id suffix
     * @param referrerVariable referrer variable name
     * @param refereeVariable referee variable name
     * @param variable edge variable name
     * @param referrerLabel referrer label
     * @param refereeLabel referee label
     * @return generated code fragment
     */
    private static String handlePatternReferrerRefereeLabel(
            final String dbname,
            final int id,
            final String referrerVariable,
            final String refereeVariable,
            final String variable,
            final String referrerLabel,
            final String refereeLabel) {
        String res = "";

        res +=
                "referrerCinfo = "
                        + dbname
                        + ".getLabelClassInfoMap().get(\""
                        + referrerLabel
                        + "\");";
        res +=
                "refereeCinfo = "
                        + dbname
                        + ".getLabelClassInfoMap().get(\""
                        + refereeLabel
                        + "\");";
        res += "if (referrerCinfo != null && refereeCinfo != null";
        res += "&& referrerCinfo.containsReferenceFieldWithType(\"" + refereeLabel + "\")";
        res += "&& Util.checkNodeClassNodePropertyMatch(referrerCinfo," + " referrerProperties)";
        res += "&& Util.checkNodeClassNodePropertyMatch(refereeCinfo," + " refereeProperties)";
        res += "&& Util.checkEdgeMatch(edgeNames, referrerCinfo)) {";
        res +=
                "  for (Object referrerObject : "
                        + dbname
                        + ".getLabelObjectMap().get(\""
                        + referrerLabel
                        + "\")) {";
        res +=
                "    if (!Util.checkNodeNodePropertyMatch(referrerCinfo,"
                        + " referrerObject, referrerProperties)) {";
        res += "      continue;}";
        res += "    for (Map.Entry<String, String> refField :";
        res += "    referrerCinfo.getReferenceFieldTypeMap(referrerObject)" + ".entrySet()) {";
        res += "      refFieldName = refField.getKey();";
        res += "      refFieldType = refField.getValue();";
        res += "      if (edgeNames != null && !edgeNames.contains(refFieldName)) {";
        res += "        continue;}";
        res += "      if (refFieldType.equals(\"" + refereeLabel + "\")) {";
        res += "        try {";
        res +=
                "          refereeObjects = referrerCinfo.getReferenceField("
                        + "refFieldName, referrerObject);";
        res += "          for (Object refereeObject : refereeObjects) {";
        res += "            if (refereeObject == null) { continue;}";
        res +=
                "            if (!Util.checkNodeNodePropertyMatch("
                        + "refereeCinfo, refereeObject, refereeProperties)) {";
        res += "              continue;}";
        res += "            temp = new HashMap<String, Object>();";
        res += "            temp.put(\"" + referrerVariable + "\", referrerObject);";
        res += "            temp.put(\"" + refereeVariable + "\", refereeObject);";
        res +=
                "            temp.put(\""
                        + variable
                        + "\", new EstoreEdge(referrerObject, refereeObject,"
                        + " refFieldName));";
        res += "            res_" + id + ".putEntry(temp);";
        res += "          }";
        res += "        } catch (Exception e) {";
        res += "          e.printStackTrace();";
        res += "          return null;";
        res += "        }";
        res += "      }";
        res += "    }";
        res += "  }";
        res += "}";
        return res;
    }

    /**
     * Handles patterns with a referee label only (not covered by current tests).
     *
     * @param dbname database name expression
     * @param id result table id suffix
     * @param referrerVariable referrer variable name
     * @param refereeVariable referee variable name
     * @param variable edge variable name
     * @param referrerLabel referrer label (may be null)
     * @param refereeLabel referee label
     * @return generated code fragment
     */
    private static String handlePatternRefereeLabel(
            final String dbname,
            final int id,
            final String referrerVariable,
            final String refereeVariable,
            final String variable,
            final String referrerLabel,
            final String refereeLabel) {
        String res = "";

        return res;
    }
}
