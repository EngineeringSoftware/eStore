package org.estore.planner.expressions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.estore.planner.util.Table;

public class PropertyLookupExpr implements LogicalExpr<Table, Table> {
    private String nodeName;
    private String nodePropertyName;
    private String renamedName;

    public PropertyLookupExpr(String nodeName, String nodePropertyName) {
        this.nodeName = nodeName;
        this.nodePropertyName = nodePropertyName;
        this.renamedName = getName();
    }

    public PropertyLookupExpr(String nodeName, String nodePropertyName, String renamedName) {
        this.nodeName = nodeName;
        this.nodePropertyName = nodePropertyName;
        this.renamedName = renamedName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodePropertyName() {
        return nodePropertyName;
    }

    @Override
    public String getName() {
        return nodeName + "." + nodePropertyName;
    }

    @Override
    public Table evaluate(Table v) {
        List<String> keys = new ArrayList<String>(v.keySet());
        String thisKey = getName();
        keys.add(thisKey);
        Table result = new Table(keys);
        for (int j = 0; j < v.getSize(); j++) {
            HashMap<String, Object> item = v.getAtIndex(j);
            Object obj = item.get(nodeName);
            if (obj == null) {
                item.put(thisKey, null);
            } else {
                try {
                    Field field = obj.getClass().getDeclaredField(nodePropertyName);
                    field.setAccessible(true);
                    Object propertyObject = field.get(obj);
                    item.put(thisKey, propertyObject);
                } catch (Exception e) {
                    item.put(thisKey, null);
                }
            }
            result.putEntry(item);
        }
        return result;
    }

    @Override
    public void setRenamedName(String renamedName) {
        this.renamedName = renamedName;
    }

    @Override
    public String getRenamedName() {
        return renamedName;
    }
}
