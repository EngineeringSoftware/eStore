package org.estore.planner.pattern;

import java.util.List;
import org.estore.planner.util.PathRange;

public class RelationDetail extends Pattern {

    private String variable;
    private PathRange pRange;
    private List<String> edgeNames;

    public RelationDetail(int id) {
        this(id, null, null, null);
    }

    public RelationDetail(RelationDetail relNode, String variable) {
        this(relNode.getID(), variable, relNode.getEdgeNames(), relNode.getPathRange());
    }

    public RelationDetail(RelationDetail relNode, List<String> edgeNames) {
        this(relNode.getID(), relNode.getVariable(), edgeNames, relNode.getPathRange());
    }

    public RelationDetail(RelationDetail relNode, PathRange pRange) {
        this(relNode.getID(), relNode.getVariable(), relNode.getEdgeNames(), pRange);
    }

    public RelationDetail(int id, String variable, List<String> edgeNames) {
        this(id, variable, edgeNames, null);
    }

    public RelationDetail(int id, String variable, List<String> edgeNames, PathRange pRange) {
        this.id = id;
        this.variable = variable;
        this.edgeNames = edgeNames;
        this.pRange = pRange;
        this.name = "RelationDetail";
    }

    public List<String> getEdgeNames() {
        return edgeNames;
    }

    public String getVariable() {
        return variable;
    }

    public PathRange getPathRange() {
        return pRange;
    }
}
