package org.estore.planner.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.Test;

class WhereFilterTest {

    @Test
    void execute_nullInput_returnsNull() {
        WhereFilter filter = new WhereFilter(1, stub(t -> true));
        assertNull(filter.execute(null));
    }

    @Test
    void execute_zeroRowTable_returnsEmptyTableWithSameColumns() {
        Table input = new Table(Arrays.asList("a", "b"));
        WhereFilter filter = new WhereFilter(1, stub(t -> true));
        Table out = filter.execute(input);
        assertEquals(0, out.getSize());
        assertEquals(new HashSet<>(input.keySet()), new HashSet<>(out.keySet()));
    }

    @Test
    void execute_predicateAlwaysTrue_keepsAllRows() {
        Table input = tableWithColumn("n", 1, 2, 3);
        WhereFilter filter = new WhereFilter(1, stub(t -> true));
        Table out = filter.execute(input);
        assertEquals(3, out.getSize());
        assertEquals(1, out.getAtIndex(0).get("n"));
        assertEquals(3, out.getAtIndex(2).get("n"));
    }

    @Test
    void execute_predicateFalse_dropsAllRows() {
        Table input = tableWithColumn("n", 1, 2);
        WhereFilter filter = new WhereFilter(1, stub(t -> false));
        Table out = filter.execute(input);
        assertEquals(0, out.getSize());
    }

    @Test
    void execute_predicateNull_dropsAllRows() {
        Table input = tableWithColumn("n", 1, 2);
        WhereFilter filter = new WhereFilter(1, stub(t -> null));
        Table out = filter.execute(input);
        assertEquals(0, out.getSize());
    }

    @Test
    void execute_mixedPredicateResults_keepsOnlyTrueRows() {
        Table input = tableWithColumn("k", 0, 1, 2);
        WhereFilter filter =
                new WhereFilter(
                        1,
                        stub(
                                t -> {
                                    int k = (Integer) t.get("k").get(0);
                                    switch (k) {
                                        case 0:
                                            return Boolean.TRUE;
                                        case 1:
                                            return Boolean.FALSE;
                                        default:
                                            return null;
                                    }
                                }));
        Table out = filter.execute(input);
        assertEquals(1, out.getSize());
        assertEquals(0, out.getAtIndex(0).get("k"));
    }

    @Test
    void execute_contentBased_keepsRowsMatchingPredicate() {
        Table input = tableWithColumn("n", 1, 2, 3, 4);
        WhereFilter filter =
                new WhereFilter(
                        1,
                        stub(
                                t -> {
                                    int n = (Integer) t.get("n").get(0);
                                    return n % 2 == 0;
                                }));
        Table out = filter.execute(input);
        assertEquals(2, out.getSize());
        assertEquals(2, out.getAtIndex(0).get("n"));
        assertEquals(4, out.getAtIndex(1).get("n"));
    }

    @Test
    void constructor_setsNameAndId_childrenIsNull() {
        LogicalExpr<Boolean, Table> pred = stub(t -> true);
        WhereFilter filter = new WhereFilter(42, pred);
        assertEquals("WhereFilter", filter.getName());
        assertEquals(42, filter.getID());
        assertNull(filter.children());
    }

    private static LogicalExpr<Boolean, Table> stub(Function<Table, Boolean> eval) {
        return new StubBooleanExpr(eval);
    }

    private static Table tableWithColumn(String column, int... values) {
        Table t = new Table();
        ArrayList<Object> col = new ArrayList<>();
        for (int v : values) {
            col.add(v);
        }
        t.put(column, col);
        return t;
    }

    private static final class StubBooleanExpr implements LogicalExpr<Boolean, Table> {
        private final Function<Table, Boolean> eval;

        StubBooleanExpr(Function<Table, Boolean> eval) {
            this.eval = eval;
        }

        @Override
        public Boolean evaluate(Table v) {
            return eval.apply(v);
        }

        @Override
        public String getName() {
            return "stub";
        }

        @Override
        public String getRenamedName() {
            return "stub";
        }

        @Override
        public void setRenamedName(String renamedName) {}
    }
}
