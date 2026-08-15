package org.estore.compiler;

import java.util.ArrayList;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.util.ClassInfo;
import org.estore.planner.util.NodeProperty;

public class Util {
    public static boolean checkNodeClassNodePropertyMatch(
            ClassInfo cInfo, List<NodeProperty> properties) {
        boolean flag = true;
        if (properties == null) {
            return true;
        }
        for (NodeProperty prop : properties) {
            flag = cInfo.containsPrimitiveFieldWithName(prop.getName());
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkEdgeMatch(List<String> edgeNames, ClassInfo referrerCinfo) {
        if (edgeNames == null) {
            return true;
        }
        boolean flag = false;
        for (String edgeName : edgeNames) {
            flag |= referrerCinfo.containsReferenceFieldWithName(edgeName, null);
        }
        return flag;
    }

    public static boolean checkNodeNodePropertyMatch(
            ClassInfo cInfo, Object obj, List<NodeProperty> properties) {
        boolean flag2 = true;
        if (properties == null) {
            return true;
        }
        for (NodeProperty prop : properties) {
            try {
                Object fieldObject =
                        cInfo.getPrimitiveField(prop.getName(), prop.getType().getName(), obj);
                flag2 = fieldObject.equals(prop.getValue());
                if (!flag2) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return flag2;
    }

    public static boolean checkClassNodePropertyMatch(Class<?> obj, List<NodeProperty> properties) {
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

    public static boolean checkNodeLabel(Object node, String expectedLabel) {
        if (expectedLabel == null) {
            return true;
        }
        String className = node.getClass().getName();
        return className.equals(expectedLabel);
    }

    public static boolean checkNodeProperties(
            Object node, List<NodeProperty> properties, Estore estore) {
        String className = node.getClass().getName();
        ClassInfo classInfo = estore.getLabelClassInfoMap().get(className);

        if (properties == null || properties.isEmpty()) {
            return true;
        }
        if (classInfo == null) {
            return false;
        }
        for (NodeProperty property : properties) {
            if (!classInfo.containsPrimitiveFieldWithName(property.getName())) {
                return false;
            }

            Object fieldValue =
                    classInfo.getPrimitiveField(
                            property.getName(), property.getType().getName(), node);
            if (!fieldValue.equals(property.getValue())) {
                return false;
            }
        }
        return true;
    }

    public static List<Object> getStartingNodes(
            String referrerLabel, List<NodeProperty> referrerProperties, Estore estore) {
        List<Object> result = new ArrayList<Object>();
        for (Object node :
                estore.getLabelObjectMap().getOrDefault(referrerLabel, new ArrayList<>())) {
            if (node == null) {
                continue;
            }
            if (checkNodeProperties(node, referrerProperties, estore)) {
                result.add(node);
            }
        }
        return result;
    }

    public static List<Object> getNeighbors(Object node, List<String> edgeNames, Estore estore) {
        List<Object> neighbors = new ArrayList<>();
        String className = node.getClass().getName();
        ClassInfo classInfo = estore.getLabelClassInfoMap().get(className);

        if (classInfo == null) {
            return neighbors;
        }

        if (edgeNames == null) {
            for (String referenceFieldName : classInfo.getReferenceFieldNames(node)) {
                List<Object> referenceObjects =
                        classInfo.getReferenceField(referenceFieldName, node);
                if (referenceObjects != null) {
                    neighbors.addAll(referenceObjects);
                }
            }
        } else {
            for (String edgeName : edgeNames) {
                if (classInfo.containsReferenceFieldWithName(edgeName, node)) {
                    List<Object> referenceObjects = classInfo.getReferenceField(edgeName, node);
                    if (referenceObjects != null) {
                        neighbors.addAll(referenceObjects);
                    }
                }
            }
        }

        return neighbors;
    }
}
