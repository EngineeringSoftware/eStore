package org.estore.planner.scan;

import java.util.*;
import org.estore.Estore;
import org.estore.planner.LogicalPlan;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.PathRange;
import org.estore.planner.util.Table;
import org.estore.planner.util.enums.RelationDirection;

public class VarLengthRelationScan extends LogicalPlan implements RelationScan {

    private String variable;
    private String referrerVariable;
    private String referrerLabel;
    private List<NodeProperty> referrerProperties;
    private String refereeVariable;
    private String refereeLabel;
    private List<NodeProperty> refereeProperties;
    private List<String> edgeNames;
    private PathRange pRange;

    public VarLengthRelationScan(
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
                relation.getEdgeNames(),
                relation.getPathRange());
    }

    public VarLengthRelationScan(
            int id,
            Estore estore,
            String referrerVariable,
            String referrerLabel,
            List<NodeProperty> referrerProperties,
            String variable,
            String refereeVariable,
            String refereeLabel,
            List<NodeProperty> refereeProperties,
            List<String> edgeNames,
            PathRange pRange) {
        this.name = "VarLengthRelationScan";
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
        this.pRange = pRange;
    }

    @Override
    public Estore getDataSource() {
        return estore;
    }

    @Override
    public List<String> getEdgeNames() {
        return edgeNames;
    }

    @Override
    public PathRange getPathRange() {
        return pRange;
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
    public List<LogicalPlan> children() {
        return null;
    }

    public Table dfsExecute() {
        List<String> resultVariables =
                Arrays.asList(new String[] {getReferrerVariable(), getRefereeVariable()});
        Table result = new Table(resultVariables);

        // perform dfs for each starting node
        for (Object startNode : getStartingNodes()) {
            Set<Object> visited = new HashSet<>();
            Stack<DFSNode> stack = new Stack<>();
            stack.push(new DFSNode(startNode, 0));

            while (!stack.isEmpty()) {
                DFSNode current = stack.pop();
                Object currentNode = current.currentNode;
                int depth = current.depth;

                if (depth > pRange.getUpperBound()) {
                    continue;
                }

                visited.add(currentNode);

                if (depth >= pRange.getLowerBound()
                        && checkNodeLabel(currentNode, refereeLabel)
                        && checkNodeProperties(currentNode, refereeProperties)) {
                    // found a valid path
                    HashMap<String, Object> row = new HashMap<>();
                    row.put(getReferrerVariable(), startNode);
                    row.put(getRefereeVariable(), currentNode);
                    result.putEntry(row);
                }

                if (depth < pRange.getUpperBound()) {
                    // traverse the neighbors of the current node
                    for (Object neighbor : getNeighbors(currentNode, edgeNames)) {
                        if (neighbor != null && !visited.contains(neighbor)) {
                            stack.push(new DFSNode(neighbor, depth + 1));
                        }
                    }
                }
            }
        }
        return result;
    }

    private static class DFSNode {
        Object currentNode;
        int depth;

        DFSNode(Object currentNode, int depth) {
            this.currentNode = currentNode;
            this.depth = depth;
        }
    }

    private List<Object> getStartingNodes() {
        List<Object> result = new ArrayList<Object>();
        for (Object node :
                estore.getLabelObjectMap().getOrDefault(referrerLabel, new ArrayList<>())) {
            if (node == null) {
                continue;
            }
            if (checkNodeProperties(node, referrerProperties)) {
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public Table execute(Table input) {
        if (referrerLabel != null) {
            return dfsExecute();
        }
        List<String> unionVariables =
                Arrays.asList(new String[] {getReferrerVariable(), getRefereeVariable()});
        Table result = new Table(unionVariables);
        int localId = 0;

        for (int j = pRange.getLowerBound(); j <= pRange.getUpperBound(); j++) {
            if (j == 1) {
                TwoNodeRelationScan rel =
                        new TwoNodeRelationScan(
                                ++localId,
                                estore,
                                referrerVariable,
                                referrerLabel,
                                referrerProperties,
                                variable,
                                refereeVariable,
                                refereeLabel,
                                refereeProperties,
                                edgeNames);
                result = result.union(rel.execute(null), unionVariables);
            } else {
                MultiLengthRelationScan multiRel = new MultiLengthRelationScan(++localId);
                String localReferrerVariable = "_" + (++localId);
                String localRefereeVariable = "";
                for (int k = 0; k < j; k++) {
                    localRefereeVariable = "_" + (++localId);
                    TwoNodeRelationScan rel =
                            new TwoNodeRelationScan(
                                    ++localId,
                                    estore,
                                    k == 0 ? referrerVariable : localReferrerVariable,
                                    k == 0 ? referrerLabel : null,
                                    k == 0 ? referrerProperties : null,
                                    null,
                                    k == (j - 1) ? refereeVariable : localRefereeVariable,
                                    k == (j - 1) ? refereeLabel : null,
                                    k == (j - 1) ? refereeProperties : null,
                                    edgeNames);
                    localReferrerVariable = localRefereeVariable;
                    multiRel.addChild(rel);
                }
                result = result.union(multiRel.execute(null), unionVariables);
            }
        }
        return result;
    }
}
