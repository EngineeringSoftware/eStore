package org.estore.planner.expressions.relational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.function.Function;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.example.Person;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.function.FunctionInvocationExpr;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Equality is exercised through Cypher {@code =} inside {@code WHERE}, and directly here for
 * operand-evaluation branches in {@link EqualsRelationExpr}.
 */
class EqualsRelationExprTest {
    private Estore db;

    @BeforeEach
    void setUp() {
        db = new Estore(EqualsRelationExprTest.class.getName() + "_" + System.nanoTime());
    }

    @Test
    void queryWherePropertyEqualsLiteralKeepsMatchingRow() throws EstoreException {
        db.captureAll(new Person("Alice", 17));
        Table result =
                db.query("MATCH (p:`org.estore.example.Person`) WHERE p.name = 'Alice' RETURN p");
        assertEquals(1, result.getSize());
    }

    @Test
    void queryWherePropertyEqualsLiteralExcludesNonMatchingRow() throws EstoreException {
        db.captureAll(new Person("Alice", 17));
        Table result =
                db.query("MATCH (p:`org.estore.example.Person`) WHERE p.name = 'Bob' RETURN p");
        assertEquals(0, result.getSize());
    }

    @Test
    void queryWherePropertyNotEqualsLiteralFiltersExpectedRows() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 15));
        Table result =
                db.query("MATCH (p:`org.estore.example.Person`) WHERE p.name <> 'Alice' RETURN p");
        assertEquals(1, result.getSize());
    }

    @Test
    void queryWherePropertyLessThanLiteralFiltersExpectedRows() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 15));
        Table result = db.query("MATCH (p:`org.estore.example.Person`) WHERE p.age < 17 RETURN p");
        assertEquals(1, result.getSize());
    }

    @Test
    void queryWherePropertyLessThanOrEqualsLiteralFiltersExpectedRows() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 15));
        Table result = db.query("MATCH (p:`org.estore.example.Person`) WHERE p.age <= 15 RETURN p");
        assertEquals(1, result.getSize());
    }

    @Test
    void queryWherePropertyGreaterThanLiteralFiltersExpectedRows() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 15));
        Table result = db.query("MATCH (p:`org.estore.example.Person`) WHERE p.age > 15 RETURN p");
        assertEquals(1, result.getSize());
    }

    @Test
    void queryWherePropertyGreaterThanOrEqualsLiteralFiltersExpectedRows() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 15));
        Table result = db.query("MATCH (p:`org.estore.example.Person`) WHERE p.age >= 17 RETURN p");
        assertEquals(1, result.getSize());
    }

    private void capturePeople(Person... people) throws EstoreException {
        for (Person person : people) {
            db.captureAll(person);
        }
    }

    @Test
    void evaluate_functionOperand_comparesFirstCellOfNamedColumn() {
        Table row = new Table();
        StubFunctionInvocationExpr leftFn =
                new StubFunctionInvocationExpr("out", t -> tableWithColumn("out", 7));
        StubFunctionInvocationExpr rightFn =
                new StubFunctionInvocationExpr("out", t -> tableWithColumn("out", 7));
        EqualsRelationExpr eq = new EqualsRelationExpr(leftFn, rightFn);
        assertTrue(eq.evaluate(row));
    }

    @Test
    void evaluate_functionOperand_nullTableYieldsNullOperand() {
        Table row = new Table();
        StubFunctionInvocationExpr leftFn = new StubFunctionInvocationExpr("out", t -> null);
        StubFunctionInvocationExpr rightFn =
                new StubFunctionInvocationExpr("out", t -> tableWithColumn("out", 1));
        EqualsRelationExpr eq = new EqualsRelationExpr(leftFn, rightFn);
        assertFalse(eq.evaluate(row));
    }

    @Test
    void evaluate_functionOperand_missingOrEmptyColumnYieldsNullOperand() {
        Table row = new Table();
        StubFunctionInvocationExpr missingCol =
                new StubFunctionInvocationExpr(
                        "out",
                        t -> {
                            Table tbl = new Table();
                            tbl.put("other", new ArrayList<>());
                            tbl.get("other").add(1);
                            return tbl;
                        });
        StubFunctionInvocationExpr emptyCol =
                new StubFunctionInvocationExpr(
                        "out",
                        t -> {
                            Table tbl = new Table();
                            tbl.put("out", new ArrayList<>());
                            return tbl;
                        });
        assertFalse(new EqualsRelationExpr(missingCol, stubLiteral(1)).evaluate(row));
        assertFalse(new EqualsRelationExpr(emptyCol, stubLiteral(1)).evaluate(row));
    }

    @Test
    void evaluate_nonFunctionOperand_nonTableValueReturnedAsIs() {
        Table row = new Table();
        EqualsRelationExpr eq = new EqualsRelationExpr(stubLiteral("a"), stubLiteral("a"));
        assertTrue(eq.evaluate(row));
        assertFalse(new EqualsRelationExpr(stubLiteral("a"), stubLiteral("b")).evaluate(row));
    }

    @Test
    void evaluate_nonFunctionOperand_tableWithMissingOrEmptyColumnYieldsNull() {
        Table row = new Table();
        StubLogicalExpr exprMissingCol =
                new StubLogicalExpr(
                        "want",
                        t -> {
                            Table tbl = new Table();
                            tbl.put("x", listOf(1));
                            return tbl;
                        });
        StubLogicalExpr exprEmptyCol =
                new StubLogicalExpr(
                        "want",
                        t -> {
                            Table tbl = new Table();
                            tbl.put("want", new ArrayList<>());
                            return tbl;
                        });
        assertFalse(new EqualsRelationExpr(exprMissingCol, stubLiteral(1)).evaluate(row));
        assertFalse(new EqualsRelationExpr(exprEmptyCol, stubLiteral(1)).evaluate(row));
    }

    private static LogicalExpr stubLiteral(Object value) {
        return new StubLogicalExpr("n", t -> value);
    }

    private static Table tableWithColumn(String column, Object cell) {
        Table tbl = new Table();
        tbl.put(column, listOf(cell));
        return tbl;
    }

    private static <T> ArrayList<T> listOf(T x) {
        ArrayList<T> list = new ArrayList<>();
        list.add(x);
        return list;
    }

    private static final class StubFunctionInvocationExpr extends FunctionInvocationExpr {
        private final String fnColumnName;
        private final Function<Table, Table> eval;

        StubFunctionInvocationExpr(String fnColumnName, Function<Table, Table> eval) {
            this.fnColumnName = fnColumnName;
            this.eval = eval;
            this.renamedName = fnColumnName;
        }

        @Override
        public Table evaluate(Table v) {
            return eval.apply(v);
        }

        @Override
        public String getName() {
            return fnColumnName;
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

    private static final class StubLogicalExpr implements LogicalExpr<Object, Table> {
        private final String name;
        private final Function<Table, Object> eval;

        StubLogicalExpr(String name, Function<Table, Object> eval) {
            this.name = name;
            this.eval = eval;
        }

        @Override
        public Object evaluate(Table v) {
            return eval.apply(v);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getRenamedName() {
            return name;
        }

        @Override
        public void setRenamedName(String renamedName) {}
    }
}
