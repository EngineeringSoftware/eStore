package org.estore.planner.scan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.estore.Estore;
import org.estore.EstoreEdge;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.util.ClassInfo;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.PathRange;
import org.estore.planner.util.Table;
import org.estore.planner.util.enums.RelationDirection;

public class TwoNodeRelationScan extends LogicalPlan implements RelationScan {

    private Estore estore;
    private String variable;
    private String referrerVariable;
    private String refereeVariable;
    private String referrerLabel;
    private String refereeLabel;
    private List<NodeProperty> referrerProperties;
    private List<NodeProperty> refereeProperties;
    private List<String> edgeNames;

    // private static Map<String, Table> cache = new HashMap<>();
    // public static int cacheHit = 0;

    public TwoNodeRelationScan(
            int id, Estore estore, NodePattern left, RelationPattern relation, NodePattern right) {
        this(
                id,
                estore,
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getVariable()
                        : right.getVariable(),
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getLabel()
                        : right.getLabel(),
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getProperties()
                        : right.getProperties(),
                relation.getVariable(),
                relation.getReferee() == RelationDirection.LEFT
                        ? left.getVariable()
                        : right.getVariable(),
                relation.getReferee() == RelationDirection.LEFT
                        ? left.getLabel()
                        : right.getLabel(),
                relation.getReferee() == RelationDirection.LEFT
                        ? left.getProperties()
                        : right.getProperties(),
                relation.getEdgeNames());
    }

    public TwoNodeRelationScan(
            int id,
            Estore estore,
            String referrerVariable,
            String referrerLabel,
            List<NodeProperty> referrerProperties,
            String variable,
            String refereeVariable,
            String refereeLabel,
            List<NodeProperty> refereeProperties,
            List<String> edgeNames) {
        this.id = id;
        this.estore = estore;
        this.referrerVariable = referrerVariable;
        this.referrerLabel = referrerLabel;
        this.referrerProperties = referrerProperties;
        this.variable = variable;
        this.refereeVariable = refereeVariable;
        this.refereeLabel = refereeLabel;
        this.refereeProperties = refereeProperties;
        this.edgeNames = edgeNames;
        this.name = "TwoNodeRelationScan";
    }

    @Override
    public Estore getDataSource() {
        return estore;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public String getReferrerVariable() {
        return referrerVariable;
    }

    @Override
    public String getRefereeVariable() {
        return refereeVariable;
    }

    @Override
    public String getReferrerLabel() {
        return referrerLabel;
    }

    @Override
    public String getRefereeLabel() {
        return refereeLabel;
    }

    @Override
    public List<String> getEdgeNames() {
        return edgeNames;
    }

    @Override
    public PathRange getPathRange() {
        return null;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    /* disable caching for now
    private String getCacheKey() {
      StringBuilder keyBuilder = new StringBuilder();
      keyBuilder
          .append(referrerLabel)
          .append("|")
          .append(referrerProperties)
          .append("|")
          .append(edgeNames)
          .append("|")
          .append(refereeLabel)
          .append("|")
          .append(refereeProperties)
          .append("|");
      return keyBuilder.toString();
    }

    public static void clearCache() {
      cache.clear();
    }
    */
    @Override
    public Table execute(Table input) {
        /*
        String cacheKey = getCacheKey();
        if (cache.containsKey(cacheKey)) {
          cacheHit++;
          Table cachedResult = new Table(cache.get(cacheKey));
          // Rename the columns based on the specific variable names
          cachedResult.changeColumnName("__referrerVar", getReferrerVariable());
          cachedResult.changeColumnName("__refereeVar", getRefereeVariable());
          cachedResult.changeColumnName("__var", getVariable());
          return cachedResult;
        }*/

        Table result =
                new Table(
                        Arrays.asList(
                                new String[] {
                                    getReferrerVariable(), getRefereeVariable(), getVariable()
                                }));
        // MATCH (n)-[r]->(m)
        if (referrerLabel == null && refereeLabel == null) {
            handlePatternNoLabel(result);
        }
        // MATCH (n:`Label1`)-[r]->(m:`Label2`)
        else if (referrerLabel != null && refereeLabel != null) {
            handlePatternReferrerRefereeLabel(result);
        }
        // MATCH (n:`Label1`)-[r]->(m)
        else if (referrerLabel != null && refereeLabel == null) {
            handlePatternReferrerLabel(result);
        }
        // MATCH (n)-[r]->(m:`Label2`)
        else {
            handlePatternRefereeLabel(result);
        }
        /*
        Table cachingResult = new Table(result);
        cachingResult.changeColumnName(getReferrerVariable(), "__referrerVar");
        cachingResult.changeColumnName(getRefereeVariable(), "__refereeVar");
        cachingResult.changeColumnName(getVariable(), "__var");
        cache.put(cacheKey, cachingResult);
        */
        return result;
    }

    private boolean checkNodeClassNodePropertyMatch(
            ClassInfo cInfo, List<NodeProperty> properties) {
        boolean flag = true;
        if (properties == null) {
            return true;
        }
        for (NodeProperty prop : properties) {
            flag = cInfo.containsPrimitiveFieldWithName(prop.getName());
            // && cInfo.containsPrimitiveFieldWithType(prop.getType().getName());
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    private boolean checkNodeNodePropertyMatch(
            ClassInfo cInfo, Object obj, List<NodeProperty> properties) {
        boolean flag2 = true;
        if (properties == null) {
            return true;
        }
        for (NodeProperty prop : properties) {
            try {
                Object fieldObject =
                        cInfo.getPrimitiveField(prop.getName(), prop.getType().getName(), obj);
                if (fieldObject instanceof Number && prop.getValue() instanceof Number) {
                    flag2 =
                            ((Number) fieldObject).longValue()
                                    == ((Number) prop.getValue()).longValue();
                } else {
                    flag2 = fieldObject.equals(prop.getValue());
                }
                if (!flag2) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return flag2;
    }

    private boolean checkClassNodePropertyMatch(Class<?> obj, List<NodeProperty> properties) {
        boolean flag = false;
        if (properties == null) {
            return true;
        }
        for (NodeProperty prop : properties) {
            if (prop.getName().equals("name")) {
                flag = obj.getName().equals(prop.getValue());
            }
        }
        return flag;
    }

    private boolean checkEdgeMatch(List<String> edgeNames, ClassInfo referrerCinfo) {
        if (edgeNames == null) {
            return true;
        }
        boolean flag = false;
        for (String edgeName : edgeNames) {
            flag |= referrerCinfo.containsReferenceFieldWithName(edgeName, null);
        }
        return flag;
    }

    private void handlePatternNoLabel(Table result) {
        for (String className : estore.getLabelObjectMap().keySet()) {
            ClassInfo referrerCinfo = estore.getLabelClassInfoMap().get(className);
            if (referrerCinfo.getReferenceFieldCount() > 0) {
                if (!checkNodeClassNodePropertyMatch(referrerCinfo, referrerProperties)) {
                    continue;
                }
                if (!checkEdgeMatch(edgeNames, referrerCinfo)) {
                    continue;
                }

                for (Object referrerObject : estore.getLabelObjectMap().get(className)) {
                    if (!checkNodeNodePropertyMatch(
                            referrerCinfo, referrerObject, referrerProperties)) {
                        continue;
                    }
                    for (String refField : referrerCinfo.getReferenceFieldNames(referrerObject)) {
                        try {
                            if (edgeNames != null && !edgeNames.contains(refField)) {
                                continue;
                            }

                            List<Object> refereeObjects =
                                    referrerCinfo.getReferenceField(refField, referrerObject);

                            for (Object refereeObject : refereeObjects) {

                                if (refereeObject == null) {
                                    continue;
                                }

                                if (refereeObject.getClass().equals(Class.class)) {
                                    if (!checkClassNodePropertyMatch(
                                            (Class<?>) refereeObject, refereeProperties)) {
                                        continue;
                                    }
                                } else {
                                    ClassInfo refereeCinfo =
                                            estore.getLabelClassInfoMap()
                                                    .get(refereeObject.getClass().getName());

                                    if (refereeCinfo == null) {
                                        continue;
                                    }

                                    if (!checkNodeClassNodePropertyMatch(
                                            refereeCinfo, refereeProperties)) {
                                        continue;
                                    }

                                    if (!checkNodeNodePropertyMatch(
                                            refereeCinfo, refereeObject, refereeProperties)) {
                                        continue;
                                    }
                                }
                                HashMap<String, Object> temp = new HashMap<String, Object>();
                                temp.put(getReferrerVariable(), referrerObject);
                                temp.put(getRefereeVariable(), refereeObject);
                                temp.put(
                                        getVariable(),
                                        new EstoreEdge(referrerObject, refereeObject, refField));
                                result.putEntry(temp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void handlePatternReferrerRefereeLabel(Table result) {
        ClassInfo referrerCinfo = estore.getLabelClassInfoMap().get(referrerLabel);
        ClassInfo refereeCinfo = estore.getLabelClassInfoMap().get(refereeLabel);

        if (referrerCinfo == null || refereeCinfo == null) {
            return;
        }

        if (!referrerCinfo.containsReferenceFieldWithType(refereeLabel)) {
            return;
        }

        if (!checkNodeClassNodePropertyMatch(referrerCinfo, referrerProperties)
                || !checkNodeClassNodePropertyMatch(refereeCinfo, refereeProperties)) {
            return;
        }

        if (!checkEdgeMatch(edgeNames, referrerCinfo)) {
            return;
        }

        for (Object referrerObject : estore.getLabelObjectMap().get(referrerLabel)) {
            if (!checkNodeNodePropertyMatch(referrerCinfo, referrerObject, referrerProperties)) {
                continue;
            }
            for (Map.Entry<String, String> refField :
                    referrerCinfo.getReferenceFieldTypeMap(referrerObject).entrySet()) {
                String refFieldName = refField.getKey();
                String refFieldType = refField.getValue();

                if (edgeNames != null && !edgeNames.contains(refFieldName)) {
                    continue;
                }
                if (refFieldType.equals(refereeLabel)) {
                    try {
                        List<Object> refereeObjects =
                                referrerCinfo.getReferenceField(refFieldName, referrerObject);
                        for (Object refereeObject : refereeObjects) {
                            if (refereeObject == null) {
                                continue;
                            }

                            if (!checkNodeNodePropertyMatch(
                                    refereeCinfo, refereeObject, refereeProperties)) {
                                continue;
                            }
                            HashMap<String, Object> temp = new HashMap<String, Object>();
                            temp.put(getReferrerVariable(), referrerObject);
                            temp.put(getRefereeVariable(), refereeObject);
                            temp.put(
                                    getVariable(),
                                    new EstoreEdge(referrerObject, refereeObject, refFieldName));
                            result.putEntry(temp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void handlePatternReferrerLabel(Table result) {
        ClassInfo referrerCinfo = estore.getLabelClassInfoMap().get(referrerLabel);

        if (referrerCinfo == null) {
            return;
        }

        if (!checkNodeClassNodePropertyMatch(referrerCinfo, referrerProperties)) {
            return;
        }

        if (!checkEdgeMatch(edgeNames, referrerCinfo)) {
            return;
        }

        for (Object referrerObject : estore.getLabelObjectMap().get(referrerLabel)) {
            if (!checkNodeNodePropertyMatch(referrerCinfo, referrerObject, referrerProperties)) {
                continue;
            }
            for (String refFieldName : referrerCinfo.getReferenceFieldNames(referrerObject)) {

                if (edgeNames != null && !edgeNames.contains(refFieldName)) {
                    continue;
                }
                try {
                    List<Object> refereeObjects =
                            referrerCinfo.getReferenceField(refFieldName, referrerObject);
                    for (Object refereeObject : refereeObjects) {
                        if (refereeObject == null) {
                            continue;
                        }
                        // when refereeObject is an instance of Class, the refereeCinfo will be null
                        if (refereeObject.getClass().equals(Class.class)) {
                            if (!checkClassNodePropertyMatch(
                                    (Class<?>) refereeObject, refereeProperties)) {
                                continue;
                            }
                        } else {
                            ClassInfo refereeCinfo =
                                    estore.getLabelClassInfoMap()
                                            .get(refereeObject.getClass().getName());
                            if (!checkNodeNodePropertyMatch(
                                    refereeCinfo, refereeObject, refereeProperties)) {
                                continue;
                            }
                        }

                        HashMap<String, Object> temp = new HashMap<String, Object>();
                        temp.put(getReferrerVariable(), referrerObject);
                        temp.put(getRefereeVariable(), refereeObject);
                        temp.put(
                                getVariable(),
                                new EstoreEdge(referrerObject, refereeObject, refFieldName));
                        result.putEntry(temp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePatternRefereeLabel(Table result) {
        ClassInfo refereeCinfo = estore.getLabelClassInfoMap().get(refereeLabel);

        if (refereeCinfo == null) {
            return;
        }

        for (Map.Entry<String, ClassInfo> item : estore.getLabelClassInfoMap().entrySet()) {
            String referrerClassName = item.getKey();
            ClassInfo referrerCinfo = item.getValue();

            if (!referrerCinfo.containsReferenceFieldWithType(refereeLabel)) {
                continue;
            }

            if (!checkNodeClassNodePropertyMatch(referrerCinfo, referrerProperties)) {
                return;
            }

            if (!checkEdgeMatch(edgeNames, referrerCinfo)) {
                return;
            }

            for (Object obj : estore.getLabelObjectMap().get(referrerClassName)) {
                if (!checkNodeNodePropertyMatch(referrerCinfo, obj, referrerProperties)) {
                    continue;
                }
                for (Map.Entry<String, String> refField :
                        referrerCinfo.getReferenceFieldTypeMap(obj).entrySet()) {
                    String refFieldName = refField.getKey();
                    String refFieldType = refField.getValue();
                    if (edgeNames != null && !edgeNames.contains(refFieldName)) {
                        continue;
                    }
                    if (refFieldType.equals(refereeLabel)) {
                        try {
                            List<Object> refereeObjects =
                                    referrerCinfo.getReferenceField(refFieldName, obj);
                            for (Object refereeObject : refereeObjects) {
                                if (refereeObject == null) {
                                    continue;
                                }

                                if (!checkNodeNodePropertyMatch(
                                        refereeCinfo, refereeObject, refereeProperties)) {
                                    continue;
                                }
                                HashMap<String, Object> temp = new HashMap<String, Object>();
                                temp.put(getReferrerVariable(), obj);
                                temp.put(getRefereeVariable(), refereeObject);
                                temp.put(
                                        getVariable(),
                                        new EstoreEdge(obj, refereeObject, refFieldName));
                                result.putEntry(temp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
