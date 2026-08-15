package org.estore.planner.delete;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.estore.EstoreEdge;
import org.estore.planner.LogicalPlan;
import org.estore.planner.util.Table;

public class DeleteRelation extends LogicalPlan {

    private String variable;

    public DeleteRelation(int id, String variable) {
        this.name = "DeleteRelation";
        this.id = id;
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        Table result = new Table(new ArrayList<String>(input.keySet()));
        for (int j = 0; j < input.getSize(); j++) {
            HashMap<String, Object> item = input.getAtIndex(j);
            EstoreEdge edge = (EstoreEdge) item.get(variable);
            Object referrerObject = edge.getReferrerObject();
            try {
                if (referrerObject.getClass().isArray()) {
                    int elementIndex = Integer.parseInt(edge.getName());
                    int arrayLength = Array.getLength(referrerObject);
                    if (elementIndex >= 0 && elementIndex < arrayLength) {
                        Array.set(referrerObject, elementIndex, null);
                    }
                } else {
                    Field refereeField = referrerObject.getClass().getDeclaredField(edge.getName());
                    refereeField.setAccessible(true);
                    refereeField.set(referrerObject, null);
                }
                item.put(variable, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.putEntry(item);
        }
        return result;
    }
}
