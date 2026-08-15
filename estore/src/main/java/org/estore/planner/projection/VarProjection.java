package org.estore.planner.projection;

import java.util.ArrayList;
import java.util.List;
import org.estore.planner.LogicalPlan;
import org.estore.planner.expressions.ArithmeticExpr;
import org.estore.planner.expressions.CaseExpr;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.PropertyLookupExpr;
import org.estore.planner.expressions.function.AggregateFunctionExpr;
import org.estore.planner.expressions.function.CountDistinctFunctionExpr;
import org.estore.planner.expressions.function.CountFunctionExpr;
import org.estore.planner.expressions.function.CountFunctionSingleExpr;
import org.estore.planner.expressions.function.PropertiesFunctionExpr;
import org.estore.planner.expressions.function.ToIntegerFunctionExpr;
import org.estore.planner.expressions.function.TypeFunctionExpr;
import org.estore.planner.util.Table;

public class VarProjection extends LogicalPlan {
    private List<LogicalExpr> expressions;

    public VarProjection(int id, List<LogicalExpr> expressions) {
        this.id = id;
        this.expressions = expressions;
        this.name = "VarProjection";
    }

    @Override
    public List<LogicalPlan> children() {
        return null;
    }

    @Override
    public Table execute(Table input) {
        Table result = new Table(input);
        List<String> variables = new ArrayList<String>();

        for (LogicalExpr expr : expressions) {
            if (expr instanceof CountFunctionExpr) {
                //        variables.add(((CountFunctionExpr) expr).getName());
                if (expressions.size() == 1) {
                    result =
                            (new CountFunctionSingleExpr((CountFunctionExpr) expr))
                                    .evaluate(result);
                } else {
                    result = ((CountFunctionExpr) expr).evaluate(result);
                }
            } else if (expr instanceof CountDistinctFunctionExpr) {
                result = ((CountDistinctFunctionExpr) expr).evaluate(result);
            } else if (expr instanceof ArithmeticExpr) {
                result = ((ArithmeticExpr) expr).evaluate(result);
            } else if (expr instanceof PropertyLookupExpr) {
                result = ((PropertyLookupExpr) expr).evaluate(result);
            } else if (expr instanceof PropertiesFunctionExpr) {
                result = ((PropertiesFunctionExpr) expr).evaluate(result);
            } else if (expr instanceof TypeFunctionExpr) {
                result = ((TypeFunctionExpr) expr).evaluate(result);
            } else if (expr instanceof ToIntegerFunctionExpr) {
                result = ((ToIntegerFunctionExpr) expr).evaluate(result);
            } else if (expr instanceof AggregateFunctionExpr) {
                result = ((AggregateFunctionExpr) expr).evaluate(result);
            } else if (expr instanceof CaseExpr) {
                result = ((CaseExpr) expr).evaluate(result);
            }
        }

        for (LogicalExpr expr : expressions) {
            if (expr.getName() != expr.getRenamedName()) {
                result.changeColumnName(expr.getName(), expr.getRenamedName());
                variables.add(expr.getRenamedName());
            } else {
                variables.add(expr.getName());
            }
        }

        return result.selectColumns(variables);
    }

    public List<LogicalExpr> getExpressions() {
        return expressions;
    }
}
