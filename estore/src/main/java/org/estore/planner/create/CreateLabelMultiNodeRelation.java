package org.estore.planner.create;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.estore.Estore;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.PatternElement;
import org.estore.planner.util.Table;

public class CreateLabelMultiNodeRelation extends LogicalPlan {

    private Estore estore;
    private HashMap<String, ArrayList<String>> referrerRefereeMap;

    public CreateLabelMultiNodeRelation(int id, Estore estore, PatternElement pattern) {
        this.name = "CreateLabelMultiNodeRelation";
        this.id = id;
        this.estore = estore;
        referrerRefereeMap = new HashMap<String, ArrayList<String>>();
        /*
        NodePattern node1 = null;
        NodePattern node2 = null;
        RelationPattern relation = null;
        int nodeCount = 0;
        int relationCount = 0;
        int numNodes = ((pattern.getElements().size() + 1) / 2);
        while (nodeCount < (numNodes - 1)) {
            node1 = pattern.getNodePattern(nodeCount);
            node2 = pattern.getNodePattern(++nodeCount);
            relation = pattern.getRelationPattern(relationCount);
            if(relation.getReferrer() == RelationDirection.LEFT){
                if(referrerRefereeMap.get(node1.getLabel()) == null){
                    referrerRefereeMap.put(node1.getLabel(), new ArrayList<String>());

                }
                referrerRefereeMap.get(node1.getLabel()).add(node2.getLabel());
            }
            }*/
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        throw new UnsupportedOperationException("CreateLabelMultiNodeRelation is not implemented");
    }
}
