package org.estore.planner.filter;

import java.util.ArrayList;
import java.util.List;
import org.estore.planner.LogicalPlan;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.util.Table;

/**
 * Filters an input {@link Table} to rows where the predicate evaluates to {@link Boolean#TRUE}.
 * Used for Cypher {@code WHERE} after pattern scans (same role as property checks inside {@link
 * org.estore.planner.scan.NodeLabelPropScan}, but for arbitrary boolean expressions).
 */
public class WhereFilter extends LogicalPlan {

    private final LogicalExpr<Boolean, Table> predicate;

    public WhereFilter(int id, LogicalExpr<Boolean, Table> predicate) {
        this.name = "WhereFilter";
        this.id = id;
        this.predicate = predicate;
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        if (input == null) {
            return null;
        }

        Table result = new Table(new ArrayList<String>(input.keySet()));
        for (int j = 0; j < input.getSize(); j++) {
            Table rowTable = input.getAtIndexTable(j);
            Boolean keep = predicate.evaluate(rowTable);
            if (Boolean.TRUE.equals(keep)) {
                result.putEntry(input.getAtIndex(j));
            }
        }

        return result;
    }
}
