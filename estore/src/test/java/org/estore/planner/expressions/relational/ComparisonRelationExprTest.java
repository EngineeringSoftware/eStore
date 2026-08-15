package org.estore.planner.expressions.relational;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.Test;

class ComparisonRelationExprTest {

    @Test
    void evaluate_notEquals_comparesOperands() {
        assertTrue(
                new NotEqualsRelationExpr(stubLiteral("a"), stubLiteral("b"))
                        .evaluate(new Table()));
        assertFalse(
                new NotEqualsRelationExpr(stubLiteral("a"), stubLiteral("a"))
                        .evaluate(new Table()));
    }

    @Test
    void evaluate_lessThan_comparesNumericOperands() {
        assertTrue(
                new LessThanRelationExpr(stubLiteral(1L), stubLiteral(2L)).evaluate(new Table()));
        assertFalse(
                new LessThanRelationExpr(stubLiteral(2L), stubLiteral(1L)).evaluate(new Table()));
    }

    @Test
    void evaluate_lessThanOrEquals_comparesNumericOperands() {
        assertTrue(
                new LessThanOrEqualsRelationExpr(stubLiteral(2L), stubLiteral(2L))
                        .evaluate(new Table()));
        assertFalse(
                new LessThanOrEqualsRelationExpr(stubLiteral(3L), stubLiteral(2L))
                        .evaluate(new Table()));
    }

    @Test
    void evaluate_greaterThan_comparesNumericOperands() {
        assertTrue(
                new GreaterThanRelationExpr(stubLiteral(3L), stubLiteral(2L))
                        .evaluate(new Table()));
        assertFalse(
                new GreaterThanRelationExpr(stubLiteral(1L), stubLiteral(2L))
                        .evaluate(new Table()));
    }

    @Test
    void evaluate_greaterThanOrEquals_comparesNumericOperands() {
        assertTrue(
                new GreaterThanOrEqualsRelationExpr(stubLiteral(2L), stubLiteral(2L))
                        .evaluate(new Table()));
        assertFalse(
                new GreaterThanOrEqualsRelationExpr(stubLiteral(1L), stubLiteral(2L))
                        .evaluate(new Table()));
    }

    @Test
    void evaluate_orderedComparisons_returnFalseForNullOrIncompatibleOperands() {
        assertFalse(
                new LessThanRelationExpr(stubLiteral(null), stubLiteral(1L)).evaluate(new Table()));
        assertFalse(
                new GreaterThanRelationExpr(stubLiteral("a"), stubLiteral(1L))
                        .evaluate(new Table()));
    }

    private static LogicalExpr stubLiteral(Object value) {
        return new StubLogicalExpr("n", t -> value);
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
