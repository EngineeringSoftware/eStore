package org.estore.planner.create;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.estore.Estore;
import org.estore.EstoreEdge;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.util.Table;
import org.estore.planner.util.enums.RelationDirection;

public class CreateTwoNodeRelation extends LogicalPlan {

    private Estore estore;
    private String referrerVariable;
    private String refereeVariable;
    private String relationVariable;
    private String relationLabel;

    public Estore getEstore() {
        return estore;
    }

    public CreateTwoNodeRelation(
            int id, Estore estore, NodePattern left, RelationPattern relation, NodePattern right) {
        this.name = "CreateTwoNodeRelation";
        this.id = id;
        this.estore = estore;
        this.referrerVariable =
                relation.getReferrer() == RelationDirection.LEFT
                        ? left.getVariable()
                        : right.getVariable();
        this.refereeVariable =
                relation.getReferrer() == RelationDirection.RIGHT
                        ? left.getVariable()
                        : right.getVariable();
        this.relationLabel = relation.getEdgeNames().get(0);
        this.relationVariable = relation.getVariable();
    }

    public Estore getDataSource() {
        return estore;
    }

    public String getReferrerVariable() {
        return referrerVariable;
    }

    public String getRefereeVariable() {
        return refereeVariable;
    }

    public String getRelationVariable() {
        return relationVariable;
    }

    public String getRelationLabel() {
        return relationLabel;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        if (input.containsKey(referrerVariable) && input.containsKey(refereeVariable)) {
            ArrayList<String> keys = new ArrayList<String>(input.keySet());
            keys.add(relationVariable);
            Table result = new Table(keys);
            for (int j = 0; j < input.getSize(); j++) {
                HashMap<String, Object> item = input.getAtIndex(j);
                Object referrerObject = item.get(referrerVariable);
                Object refereeObject = item.get(refereeVariable);
                EstoreEdge edge = new EstoreEdge(referrerObject, refereeObject, relationLabel);
                item.put(relationVariable, edge);
                try {
                    Field refField = referrerObject.getClass().getDeclaredField(relationLabel);
                    refField.setAccessible(true);
                    if (refField.getType().isArray()) {
                        Object referrerRefereeObject = refField.get(referrerObject);
                        if (referrerRefereeObject != null) {
                            int initialLength = Array.getLength(referrerRefereeObject);
                            Object newReferrerRefereeObject =
                                    Array.newInstance(refereeObject.getClass(), initialLength + 1);
                            int k = 0;
                            for (; k < initialLength; k++) {
                                Array.set(
                                        newReferrerRefereeObject,
                                        k,
                                        Array.get(referrerRefereeObject, k));
                            }
                            Array.set(newReferrerRefereeObject, k, refereeObject);
                            refField.set(referrerObject, newReferrerRefereeObject);
                        } else {
                        }
                    } else {
                    }
                    result.putEntry(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
        return null;
    }
}
