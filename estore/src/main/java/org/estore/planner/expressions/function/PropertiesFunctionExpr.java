package org.estore.planner.expressions.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.estore.Estore;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.util.ClassInfo;
import org.estore.planner.util.Table;

public class PropertiesFunctionExpr extends FunctionInvocationExpr {
    protected LogicalExpr arg;
    private Estore estore;

    public PropertiesFunctionExpr(Estore estore, LogicalExpr arg) {
        this.estore = estore;
        this.caller = null;
        this.methodName = "PROPERTIES";
        this.arg = arg;
        this.renamedName = getName();
    }

    @Override
    public String getName() {
        if (arg != null) {
            if (arg instanceof VarExpr) {

                return methodName + "(" + ((VarExpr) arg).evaluate(null) + ")";
            }
        }
        // NOTE : Should throw here as atleast node, relation expected as
        //       arguement
        return null;
    }

    public LogicalExpr getArg() {
        return arg;
    }

    @Override
    public Table evaluate(Table v) {
        if (arg instanceof VarExpr) {
            List<String> keys = new ArrayList<String>(v.keySet());
            String keyName = getName();
            keys.add(keyName);
            Table result = new Table(keys);
            String variable = ((VarExpr) arg).evaluate(null);
            for (int j = 0; j < v.getSize(); j++) {
                int count = 1;
                HashMap<String, Object> currentMap = v.getAtIndex(j);
                Object obj = currentMap.get(variable);
                ClassInfo objClassInfo =
                        estore.getLabelClassInfoMap().get(obj.getClass().getName());
                Set<String> propNames = objClassInfo.getPrimitiveFieldNames();
                HashMap<String, Object> propertyMap = null;

                if (propNames.size() == 0) {
                    currentMap.put(keyName, null);
                } else {
                    propertyMap = new HashMap<String, Object>();
                    for (String propName : propNames) {
                        Object fieldValue =
                                objClassInfo.getPrimitiveField(
                                        propName, null, currentMap.get(variable));
                        propertyMap.put(propName, fieldValue);
                    }
                    currentMap.put(keyName, propertyMap);
                }
                result.putEntry(currentMap);
            }
            return result;
        }
        return null;
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
