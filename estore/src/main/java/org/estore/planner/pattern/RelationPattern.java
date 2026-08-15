package org.estore.planner.pattern;

import java.util.List;
import org.estore.planner.util.PathRange;
import org.estore.planner.util.enums.RelationDirection;

public class RelationPattern extends Pattern {

    private String variable;
    private RelationDirection referrer;
    private RelationDirection referee;
    private List<String> edgeNames;
    private PathRange pRange;

    public RelationPattern(
            RelationDetail relationDetail, RelationDirection referrer, RelationDirection referee) {
        this(
                relationDetail.getID(),
                relationDetail.getVariable(),
                relationDetail.getEdgeNames(),
                referrer,
                referee,
                relationDetail.getPathRange());
    }

    public RelationPattern(
            int id,
            String variable,
            List<String> edgeNames,
            RelationDirection referrer,
            RelationDirection referee) {
        this(id, variable, edgeNames, referrer, referee, null);
    }

    public RelationPattern(
            int id,
            String variable,
            List<String> edgeNames,
            RelationDirection referrer,
            RelationDirection referee,
            PathRange pRange) {
        this.id = id;
        this.name = "RelationPattern";
        this.variable = variable == null ? "_" + this.name + this.id : variable;
        this.edgeNames = edgeNames;
        this.referrer = referrer;
        this.referee = referee;
        this.pRange = pRange;
    }

    public boolean isVarLengthRelation() {
        return pRange != null;
    }

    public List<String> getEdgeNames() {
        return edgeNames;
    }

    public PathRange getPathRange() {
        return pRange;
    }

    public RelationDirection getReferrer() {
        return referrer;
    }

    public RelationDirection getReferee() {
        return referee;
    }

    public String getVariable() {
        return variable;
    }
}
