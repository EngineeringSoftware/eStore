package org.estore.planner;

import java.util.ArrayList;
import java.util.List;
import org.estore.Estore;
import org.estore.antlr4.CypherBaseVisitor;
import org.estore.antlr4.CypherLexer;
import org.estore.antlr4.CypherParser;
import org.estore.planner.create.CreateLabelMultiNodeRelation;
import org.estore.planner.create.CreateLabelNode;
import org.estore.planner.create.CreateLabelPropNode;
import org.estore.planner.create.CreateLabelTwoNodeRelation;
import org.estore.planner.create.CreateTwoNodeRelation;
import org.estore.planner.delete.DeleteRelation;
import org.estore.planner.expressions.AndBooleanExpr;
import org.estore.planner.expressions.ArithmeticExpr;
import org.estore.planner.expressions.CaseExpr;
import org.estore.planner.expressions.LiteralExpr;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.NotBooleanExpr;
import org.estore.planner.expressions.OrBooleanExpr;
import org.estore.planner.expressions.PropertyLookupExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.expressions.XorBooleanExpr;
import org.estore.planner.expressions.function.AggregateFunctionExpr;
import org.estore.planner.expressions.function.CountDistinctFunctionExpr;
import org.estore.planner.expressions.function.CountFunctionExpr;
import org.estore.planner.expressions.function.PropertiesFunctionExpr;
import org.estore.planner.expressions.function.ToIntegerFunctionExpr;
import org.estore.planner.expressions.function.TypeFunctionExpr;
import org.estore.planner.expressions.relational.EqualsRelationExpr;
import org.estore.planner.expressions.relational.GreaterThanOrEqualsRelationExpr;
import org.estore.planner.expressions.relational.GreaterThanRelationExpr;
import org.estore.planner.expressions.relational.LessThanOrEqualsRelationExpr;
import org.estore.planner.expressions.relational.LessThanRelationExpr;
import org.estore.planner.expressions.relational.NotEqualsRelationExpr;
import org.estore.planner.filter.WhereFilter;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.PatternElement;
import org.estore.planner.pattern.RelationDetail;
import org.estore.planner.pattern.RelationPattern;
import org.estore.planner.projection.VarProjection;
import org.estore.planner.scan.AllNodeScan;
import org.estore.planner.scan.MultiLengthRelationScan;
import org.estore.planner.scan.NodeLabelPropScan;
import org.estore.planner.scan.NodeLabelScan;
import org.estore.planner.scan.NodePropScan;
import org.estore.planner.scan.TwoNodeRelationScan;
import org.estore.planner.scan.VarLengthRelationScan;
import org.estore.planner.util.NodeProperty;
import org.estore.planner.util.PathRange;
import org.estore.planner.util.Table;
import org.estore.planner.util.enums.RelationDirection;

public class LogicalPlanBuilder extends CypherBaseVisitor<Object> {

    private Estore estore;
    protected int assignId;

    public LogicalPlanBuilder(Estore estore) {
        this.estore = estore;
        assignId = 0;
    }

    @Override
    public Object visitOC_Cypher(CypherParser.OC_CypherContext ctx) {
        if (ctx.oC_Statement() != null) {
            return ctx.oC_Statement().accept(this);
        }
        return null;
    }

    @Override
    public Object visitOC_SinglePartQuery(CypherParser.OC_SinglePartQueryContext ctx) {
        GenResult resultPlan = new GenResult(assignId++);
        for (CypherParser.OC_ReadingClauseContext rCtx : ctx.oC_ReadingClause()) {
            List<LogicalPlan> plans = (List<LogicalPlan>) rCtx.accept(this);
            for (LogicalPlan plan : plans) {
                resultPlan.add(plan);
            }
        }
        for (CypherParser.OC_UpdatingClauseContext rCtx : ctx.oC_UpdatingClause()) {
            List<LogicalPlan> plans = (List<LogicalPlan>) rCtx.accept(this);
            for (LogicalPlan plan : plans) {
                resultPlan.add(plan);
            }
        }
        if (ctx.oC_Return() != null) {
            resultPlan.add((LogicalPlan) ctx.oC_Return().accept(this));
        }
        return resultPlan;
    }

    @Override
    public Object visitOC_ProjectionItems(CypherParser.OC_ProjectionItemsContext ctx) {
        VarProjection plan = null;
        List<LogicalExpr> expressions = null;

        if (ctx.oC_ProjectionItem().size() > 0) {
            expressions = new ArrayList<LogicalExpr>();
            for (CypherParser.OC_ProjectionItemContext pCtx : ctx.oC_ProjectionItem()) {
                LogicalExpr expr = (LogicalExpr) pCtx.oC_Expression().accept(this);
                if (pCtx.oC_Variable() != null) {
                    VarExpr renamedName = (VarExpr) pCtx.oC_Variable().accept(this);
                    expr.setRenamedName(renamedName.evaluate(null));
                }
                expressions.add(expr);
            }
            plan = new VarProjection(assignId++, expressions);
        }
        return plan;
    }

    @Override
    public Object visitOC_ReadingClause(CypherParser.OC_ReadingClauseContext ctx) {
        if (ctx.oC_Match() != null) {
            return ctx.oC_Match().accept(this);
        }
        return null;
    }

    @Override
    public Object visitOC_UpdatingClause(CypherParser.OC_UpdatingClauseContext ctx) {
        if (ctx.oC_Create() != null) {
            return ctx.oC_Create().accept(this);
        } else if (ctx.oC_Delete() != null) {
            return ctx.oC_Delete().accept(this);
        }
        return null;
    }

    @Override
    public Object visitOC_Delete(CypherParser.OC_DeleteContext ctx) {
        List<LogicalPlan> plans = null;
        if (ctx.oC_Expression().size() == 1) {
            plans = new ArrayList<LogicalPlan>();
            VarExpr variable = (VarExpr) ctx.oC_Expression(0).accept(this);
            plans.add(new DeleteRelation(assignId++, variable.evaluate(null)));
        }
        return plans;
    }

    @Override
    public Object visitOC_Create(CypherParser.OC_CreateContext ctx) {
        List<LogicalPlan> plans = null;

        if (ctx.oC_Pattern() != null) {
            plans = new ArrayList<LogicalPlan>();
            List<PatternElement> patterns = (List<PatternElement>) ctx.oC_Pattern().accept(this);

            for (int j = 0; j < patterns.size(); j++) {
                PatternElement pattern = patterns.get(j);

                if (pattern.isNodeOnlyPattern()) {
                    NodePattern nodePattern = pattern.getNodePattern(0);
                    if (nodePattern.getLabel() == null && nodePattern.getProperties() == null) {
                        // plan = new CreateGenericNode(nodePattern, estore);
                    } else if (nodePattern.getLabel() != null
                            && nodePattern.getProperties() == null) {
                        plans.add(new CreateLabelNode(nodePattern, estore));
                    } else if (nodePattern.getLabel() == null
                            && nodePattern.getProperties() != null) {
                        // plan = new CreatePropNode(nodePattern, estore);
                    } else {
                        plans.add(new CreateLabelPropNode(nodePattern, estore));
                    }
                } else {
                    LogicalPlan plan = null;
                    if (pattern.isMultiLengthRelationPattern()) {
                        plans.add(new CreateLabelMultiNodeRelation(assignId++, estore, pattern));
                    } else {
                        if (pattern.getNodePattern(0).getLabel() == null
                                && pattern.getNodePattern(1).getLabel() == null) {
                            plans.add(
                                    new CreateTwoNodeRelation(
                                            assignId++,
                                            estore,
                                            pattern.getNodePattern(0),
                                            pattern.getRelationPattern(0),
                                            pattern.getNodePattern(1)));
                        } else {
                            plans.add(
                                    new CreateLabelTwoNodeRelation(
                                            assignId++,
                                            estore,
                                            pattern.getNodePattern(0),
                                            pattern.getRelationPattern(0),
                                            pattern.getNodePattern(1)));
                        }
                    }
                }
            }
        }
        return plans;
    }

    @Override
    public Object visitOC_Match(CypherParser.OC_MatchContext ctx) {
        List<LogicalPlan> plans = null;

        if (ctx.oC_Pattern() != null) {
            plans = new ArrayList<LogicalPlan>();
            List<PatternElement> patterns = (List<PatternElement>) ctx.oC_Pattern().accept(this);

            for (int j = 0; j < patterns.size(); j++) {
                PatternElement pattern = patterns.get(j);

                if (pattern.isNodeOnlyPattern()) {
                    NodePattern nodePattern = pattern.getNodePattern(0);
                    if (nodePattern.getLabel() == null && nodePattern.getProperties() == null) {
                        plans.add(new AllNodeScan(nodePattern, estore));
                    } else if (nodePattern.getLabel() != null
                            && nodePattern.getProperties() == null) {
                        plans.add(new NodeLabelScan(nodePattern, estore));
                    } else if (nodePattern.getLabel() == null
                            && nodePattern.getProperties() != null) {
                        plans.add(new NodePropScan(nodePattern, estore));
                    } else {
                        plans.add(new NodeLabelPropScan(nodePattern, estore));
                    }
                } else {
                    if (pattern.isMultiLengthRelationPattern()) {
                        LogicalPlan plan = new MultiLengthRelationScan(assignId++);
                        NodePattern nodePattern1 = null;
                        NodePattern nodePattern2 = null;
                        RelationPattern relationPattern = null;
                        int nodeCount = 0;
                        int relationCount = 0;
                        int numNodes = ((pattern.getElements().size() + 1) / 2);
                        while (nodeCount < (numNodes - 1)) {
                            nodePattern1 = pattern.getNodePattern(nodeCount);
                            nodePattern2 = pattern.getNodePattern(++nodeCount);
                            if (pattern.getRelationPattern(relationCount).isVarLengthRelation()) {
                                plan.addChild(
                                        new VarLengthRelationScan(
                                                assignId++,
                                                estore,
                                                nodePattern1,
                                                pattern.getRelationPattern(relationCount++),
                                                nodePattern2));
                            } else {
                                plan.addChild(
                                        new TwoNodeRelationScan(
                                                assignId++,
                                                estore,
                                                nodePattern1,
                                                pattern.getRelationPattern(relationCount++),
                                                nodePattern2));
                            }
                        }
                        plans.add(plan);
                    } else {
                        if (pattern.getRelationPattern(0).isVarLengthRelation()) {
                            plans.add(
                                    new VarLengthRelationScan(
                                            assignId++,
                                            estore,
                                            pattern.getNodePattern(0),
                                            pattern.getRelationPattern(0),
                                            pattern.getNodePattern(1)));
                        } else {
                            plans.add(
                                    new TwoNodeRelationScan(
                                            assignId++,
                                            estore,
                                            pattern.getNodePattern(0),
                                            pattern.getRelationPattern(0),
                                            pattern.getNodePattern(1)));
                        }
                    }
                }
            }

            if (ctx.oC_Where() != null) {
                LogicalExpr<Boolean, Table> predicate =
                        (LogicalExpr<Boolean, Table>) ctx.oC_Where().accept(this);
                if (predicate != null) {
                    plans.add(new WhereFilter(assignId++, predicate));
                }
            }
        }
        return plans;
    }

    @Override
    public Object visitOC_Where(CypherParser.OC_WhereContext ctx) {
        return ctx.oC_Expression().accept(this);
    }

    @Override
    public Object visitOC_Expression(CypherParser.OC_ExpressionContext ctx) {
        return ctx.oC_OrExpression().accept(this);
    }

    @Override
    public Object visitOC_OrExpression(CypherParser.OC_OrExpressionContext ctx) {
        LogicalExpr expr = (LogicalExpr) ctx.oC_XorExpression(0).accept(this);
        for (int i = 1; i < ctx.oC_XorExpression().size(); i++) {
            expr = new OrBooleanExpr(expr, (LogicalExpr) ctx.oC_XorExpression(i).accept(this));
        }
        return expr;
    }

    @Override
    public Object visitOC_XorExpression(CypherParser.OC_XorExpressionContext ctx) {
        LogicalExpr expr = (LogicalExpr) ctx.oC_AndExpression(0).accept(this);
        for (int i = 1; i < ctx.oC_AndExpression().size(); i++) {
            expr = new XorBooleanExpr(expr, (LogicalExpr) ctx.oC_AndExpression(i).accept(this));
        }
        return expr;
    }

    @Override
    public Object visitOC_AndExpression(CypherParser.OC_AndExpressionContext ctx) {
        LogicalExpr expr = (LogicalExpr) ctx.oC_NotExpression(0).accept(this);
        for (int i = 1; i < ctx.oC_NotExpression().size(); i++) {
            expr = new AndBooleanExpr(expr, (LogicalExpr) ctx.oC_NotExpression(i).accept(this));
        }
        return expr;
    }

    @Override
    public Object visitOC_NotExpression(CypherParser.OC_NotExpressionContext ctx) {
        LogicalExpr expr = (LogicalExpr) ctx.oC_ComparisonExpression().accept(this);
        for (int i = 0; i < ctx.NOT().size(); i++) {
            expr = new NotBooleanExpr(expr);
        }
        return expr;
    }

    @Override
    public Object visitOC_ComparisonExpression(CypherParser.OC_ComparisonExpressionContext ctx) {
        LogicalExpr left = (LogicalExpr) ctx.oC_StringListNullPredicateExpression().accept(this);
        if (ctx.oC_PartialComparisonExpression().size() == 0) {
            return left;
        }

        LogicalExpr comparisonExpr = null;
        for (CypherParser.OC_PartialComparisonExpressionContext comparison :
                ctx.oC_PartialComparisonExpression()) {
            LogicalExpr right =
                    (LogicalExpr) comparison.oC_StringListNullPredicateExpression().accept(this);
            LogicalExpr current = buildComparisonExpr(left, right, comparison.getText());
            if (current == null) {
                return left;
            }
            comparisonExpr =
                    comparisonExpr == null ? current : new AndBooleanExpr(comparisonExpr, current);
            left = right;
        }
        return comparisonExpr != null ? comparisonExpr : left;
    }

    private LogicalExpr buildComparisonExpr(
            LogicalExpr left, LogicalExpr right, String comparisonText) {
        if (comparisonText.startsWith("=")) {
            return new EqualsRelationExpr(left, right);
        } else if (comparisonText.startsWith("<>")) {
            return new NotEqualsRelationExpr(left, right);
        } else if (comparisonText.startsWith("<=")) {
            return new LessThanOrEqualsRelationExpr(left, right);
        } else if (comparisonText.startsWith(">=")) {
            return new GreaterThanOrEqualsRelationExpr(left, right);
        } else if (comparisonText.startsWith("<")) {
            return new LessThanRelationExpr(left, right);
        } else if (comparisonText.startsWith(">")) {
            return new GreaterThanRelationExpr(left, right);
        }
        return null;
    }

    @Override
    public Object visitOC_StringListNullPredicateExpression(
            CypherParser.OC_StringListNullPredicateExpressionContext ctx) {
        return ctx.oC_AddOrSubtractExpression().accept(this);
    }

    @Override
    public Object visitOC_AddOrSubtractExpression(
            CypherParser.OC_AddOrSubtractExpressionContext ctx) {
        LogicalExpr expr = (LogicalExpr) ctx.oC_MultiplyDivideModuloExpression(0).accept(this);
        int term = 1;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            String text = ctx.getChild(i).getText();
            if ("+".equals(text) || "-".equals(text)) {
                expr =
                        new ArithmeticExpr(
                                text,
                                expr,
                                (LogicalExpr)
                                        ctx.oC_MultiplyDivideModuloExpression(term++).accept(this));
            }
        }
        return expr;
    }

    @Override
    public Object visitOC_MultiplyDivideModuloExpression(
            CypherParser.OC_MultiplyDivideModuloExpressionContext ctx) {
        LogicalExpr expr = (LogicalExpr) ctx.oC_PowerOfExpression(0).accept(this);
        int term = 1;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            String text = ctx.getChild(i).getText();
            if ("*".equals(text) || "/".equals(text)) {
                expr =
                        new ArithmeticExpr(
                                text,
                                expr,
                                (LogicalExpr) ctx.oC_PowerOfExpression(term++).accept(this));
            }
        }
        return expr;
    }

    @Override
    public Object visitOC_PowerOfExpression(CypherParser.OC_PowerOfExpressionContext ctx) {
        return ctx.oC_UnaryAddOrSubtractExpression(0).accept(this);
    }

    @Override
    public Object visitOC_UnaryAddOrSubtractExpression(
            CypherParser.OC_UnaryAddOrSubtractExpressionContext ctx) {
        return ctx.oC_NonArithmeticOperatorExpression().accept(this);
    }

    @Override
    public Object visitOC_NonArithmeticOperatorExpression(
            CypherParser.OC_NonArithmeticOperatorExpressionContext ctx) {
        LogicalExpr expr = null;
        if (ctx.oC_Atom() != null && ctx.oC_PropertyLookup().size() > 0) {
            VarExpr nodeName = (VarExpr) ctx.oC_Atom().accept(this);
            VarExpr nodePropertyName = (VarExpr) ctx.oC_PropertyLookup().get(0).accept(this);
            expr = new PropertyLookupExpr(nodeName.evaluate(null), nodePropertyName.evaluate(null));
        } else if (ctx.oC_Atom() != null) {
            return ctx.oC_Atom().accept(this);
        }
        return expr;
    }

    @Override
    public Object visitOC_Pattern(CypherParser.OC_PatternContext ctx) {
        List<PatternElement> patterns = null;

        if (ctx.oC_PatternPart().size() > 0) {
            patterns = new ArrayList<PatternElement>();

            for (CypherParser.OC_PatternPartContext pCtx : ctx.oC_PatternPart()) {
                patterns.add((PatternElement) pCtx.oC_AnonymousPatternPart().accept(this));
            }
        }
        return patterns;
    }

    @Override
    public Object visitOC_PatternElement(CypherParser.OC_PatternElementContext ctx) {
        PatternElement pattern = null;

        if (ctx.oC_NodePattern() != null) {
            pattern = new PatternElement(assignId++);
            pattern.addElement((NodePattern) ctx.oC_NodePattern().accept(this));
        }

        for (CypherParser.OC_PatternElementChainContext pCtx : ctx.oC_PatternElementChain()) {
            pattern.addElement((RelationPattern) pCtx.oC_RelationshipPattern().accept(this));
            pattern.addElement((NodePattern) pCtx.oC_NodePattern().accept(this));
        }

        return pattern;
    }

    @Override
    public Object visitOC_RelationshipPattern(CypherParser.OC_RelationshipPatternContext ctx) {
        RelationPattern pattern = null;
        RelationDetail relDetail;

        if (ctx.oC_RelationshipDetail() != null) {
            relDetail = (RelationDetail) ctx.oC_RelationshipDetail().accept(this);
        } else {
            relDetail = new RelationDetail(assignId++);
        }

        if (ctx.oC_LeftArrowHead() != null) {
            pattern =
                    new RelationPattern(relDetail, RelationDirection.RIGHT, RelationDirection.LEFT);
        } else if (ctx.oC_RightArrowHead() != null) {
            pattern =
                    new RelationPattern(relDetail, RelationDirection.LEFT, RelationDirection.RIGHT);
        }
        return pattern;
    }

    @Override
    public Object visitOC_RelationshipDetail(CypherParser.OC_RelationshipDetailContext ctx) {
        RelationDetail pattern = new RelationDetail(assignId++);

        if (ctx.oC_Variable() != null) {
            VarExpr variable = (VarExpr) ctx.oC_Variable().accept(this);
            pattern = new RelationDetail(pattern, variable.evaluate(null));
        }

        if (ctx.oC_RangeLiteral() != null) {
            PathRange pRange = (PathRange) ctx.oC_RangeLiteral().accept(this);
            pattern = new RelationDetail(pattern, pRange);
        }

        if (ctx.oC_RelationshipTypes() != null) {
            List<String> edgeNames = (List<String>) ctx.oC_RelationshipTypes().accept(this);
            pattern = new RelationDetail(pattern, edgeNames);
        }

        return pattern;
    }

    @Override
    public Object visitOC_RelationshipTypes(CypherParser.OC_RelationshipTypesContext ctx) {
        List<String> edgeNames = null;

        if (ctx.oC_RelTypeName().size() > 0) {
            edgeNames = new ArrayList<String>();
            for (CypherParser.OC_RelTypeNameContext rCtx : ctx.oC_RelTypeName()) {
                String edgeName = ((VarExpr) rCtx.accept(this)).evaluate(null);
                edgeNames.add(edgeName);
            }
        }

        return edgeNames;
    }

    @Override
    public Object visitOC_RangeLiteral(CypherParser.OC_RangeLiteralContext ctx) {
        PathRange pRange = null;
        int low, high;

        switch (ctx.oC_IntegerLiteral().size()) {
            case 0:
                pRange = new PathRange(1, 1);
                break;
            case 1:
                low =
                        ((Long)
                                        (((LiteralExpr) ctx.oC_IntegerLiteral(0).accept(this))
                                                .evaluate(null)))
                                .intValue();
                if (ctx.getTokens(CypherLexer.T__11).size() > 0) {
                    pRange = new PathRange(low);
                } else {
                    pRange = new PathRange(low, low);
                }
                break;
            case 2:
                low =
                        ((Long)
                                        (((LiteralExpr) ctx.oC_IntegerLiteral(0).accept(this))
                                                .evaluate(null)))
                                .intValue();
                high =
                        ((Long)
                                        (((LiteralExpr) ctx.oC_IntegerLiteral(1).accept(this))
                                                .evaluate(null)))
                                .intValue();
                pRange = new PathRange(low, high);
                break;
        }

        return pRange;
    }

    @Override
    public Object visitOC_NodePattern(CypherParser.OC_NodePatternContext ctx) {
        NodePattern pattern = new NodePattern(assignId++);

        if (ctx.oC_Variable() != null) {
            VarExpr variable = (VarExpr) ctx.oC_Variable().accept(this);
            pattern = new NodePattern(pattern, variable.evaluate(null));
        }
        if (ctx.oC_NodeLabels() != null) {
            VarExpr label = (VarExpr) ctx.oC_NodeLabels().accept(this);
            pattern = new NodePattern(pattern, pattern.getVariable(), label.evaluate(null));
        }
        if (ctx.oC_Properties() != null) {
            List<NodeProperty> properties = (List<NodeProperty>) ctx.oC_Properties().accept(this);
            pattern = new NodePattern(pattern, properties);
        }
        return pattern;
    }

    @Override
    public Object visitOC_MapLiteral(CypherParser.OC_MapLiteralContext ctx) {
        if (ctx.oC_PropertyKeyName().size() > 0) {
            ArrayList<NodeProperty> properties = new ArrayList<NodeProperty>();
            for (int j = 0; j < ctx.oC_PropertyKeyName().size(); j++) {
                VarExpr propertyName = (VarExpr) ctx.oC_PropertyKeyName(j).accept(this);
                LogicalExpr propertyValue = (LogicalExpr) ctx.oC_Expression(j).accept(this);
                if (propertyValue instanceof LiteralExpr) {
                    properties.add(
                            new NodeProperty(
                                    ((LiteralExpr) propertyValue).getType(),
                                    ((LiteralExpr) propertyValue).evaluate(null),
                                    propertyName.evaluate(null)));
                }
            }
            return properties;
        }
        return null;
    }

    @Override
    public Object visitOC_Atom(CypherParser.OC_AtomContext ctx) {
        if (ctx.COUNT() != null) {
            return new CountFunctionExpr();
        }
        return visitChildren(ctx);
    }

    @Override
    public Object visitOC_FunctionInvocation(CypherParser.OC_FunctionInvocationContext ctx) {
        LogicalExpr expr = null;
        List<LogicalExpr> args = null;

        if (ctx.oC_Expression().size() > 0) {
            args = new ArrayList<LogicalExpr>();
            for (CypherParser.OC_ExpressionContext pCtx : ctx.oC_Expression()) {
                args.add((LogicalExpr) pCtx.accept(this));
            }
        }

        if (ctx.oC_FunctionName() != null) {
            String functionName = ((VarExpr) ctx.oC_FunctionName().accept(this)).evaluate(null);
            if (functionName.equalsIgnoreCase("COUNT")) {
                // Two variants of COUNT
                // 1. COUNT(*)
                // 2. COUNT(expr)
                if (ctx.DISTINCT() != null) {
                    if (args != null) {
                        expr = new CountDistinctFunctionExpr(args.get(0));
                    } else {
                        expr = new CountDistinctFunctionExpr();
                    }
                } else {
                    if (args != null) {
                        expr = new CountFunctionExpr(args.get(0));
                    } else {
                        expr = new CountFunctionExpr();
                    }
                }
            } else if (functionName.equalsIgnoreCase("PROPERTIES")) {
                expr = new PropertiesFunctionExpr(estore, args.get(0));
            } else if (functionName.equalsIgnoreCase("TYPE")) {
                expr = new TypeFunctionExpr(args.get(0));
            } else if (functionName.equalsIgnoreCase("TOINTEGER")
                    || functionName.equalsIgnoreCase("TOINT")) {
                expr = new ToIntegerFunctionExpr(args.get(0));
            } else if (functionName.equalsIgnoreCase("MIN")
                    || functionName.equalsIgnoreCase("MAX")
                    || functionName.equalsIgnoreCase("SUM")
                    || functionName.equalsIgnoreCase("AVG")) {
                expr = new AggregateFunctionExpr(functionName.toUpperCase(), args.get(0));
            }
        }
        return expr;
    }

    @Override
    public Object visitOC_CaseExpression(CypherParser.OC_CaseExpressionContext ctx) {
        LogicalExpr subject = null;
        int elseCount = ctx.ELSE() != null ? 1 : 0;
        if (ctx.oC_Expression().size() > elseCount) {
            subject = (LogicalExpr) ctx.oC_Expression(0).accept(this);
        }

        List<LogicalExpr> whenExprs = new ArrayList<LogicalExpr>();
        List<LogicalExpr> thenExprs = new ArrayList<LogicalExpr>();
        for (CypherParser.OC_CaseAlternativeContext alt : ctx.oC_CaseAlternative()) {
            whenExprs.add((LogicalExpr) alt.oC_Expression(0).accept(this));
            thenExprs.add((LogicalExpr) alt.oC_Expression(1).accept(this));
        }

        LogicalExpr elseExpr = null;
        if (ctx.ELSE() != null) {
            elseExpr = (LogicalExpr) ctx.oC_Expression(ctx.oC_Expression().size() - 1).accept(this);
        }
        return new CaseExpr(subject, whenExprs, thenExprs, elseExpr);
    }

    @Override
    public Object visitOC_SymbolicName(CypherParser.OC_SymbolicNameContext ctx) {
        if (ctx.UnescapedSymbolicName() != null) {
            return new VarExpr(ctx.UnescapedSymbolicName().getSymbol().getText());
        } else if (ctx.EscapedSymbolicName() != null) {
            String text = ctx.EscapedSymbolicName().getSymbol().getText();
            return new VarExpr(text.substring(1, text.length() - 1));
        } else if (ctx.HexLetter() != null) {
            return new VarExpr(ctx.HexLetter().getSymbol().getText());
        } else if (ctx.COUNT() != null) {
            return new VarExpr("COUNT");
        }
        return null;
    }

    @Override
    public Object visitOC_Literal(CypherParser.OC_LiteralContext ctx) {
        if (ctx.StringLiteral() != null) {
            String original = ctx.StringLiteral().getSymbol().getText();
            String value = original.substring(1, original.length() - 1);
            return new LiteralExpr(String.class, value);
        } else if (ctx.oC_BooleanLiteral() != null) {
            return ctx.oC_BooleanLiteral().accept(this);
        } else if (ctx.oC_NumberLiteral() != null) {
            return ctx.oC_NumberLiteral().accept(this);
        }
        return null;
    }

    @Override
    public Object visitOC_IntegerLiteral(CypherParser.OC_IntegerLiteralContext ctx) {
        if (ctx.DecimalInteger() != null) {
            return new LiteralExpr(
                    Long.TYPE, Long.parseLong(ctx.DecimalInteger().getSymbol().getText()));
        }
        return null;
    }

    @Override
    public Object visitOC_DoubleLiteral(CypherParser.OC_DoubleLiteralContext ctx) {
        if (ctx.RegularDecimalReal() != null) {
            return new LiteralExpr(
                    Double.TYPE,
                    Double.parseDouble(ctx.RegularDecimalReal().getSymbol().getText()));
        }
        if (ctx.ExponentDecimalReal() != null) {
            return new LiteralExpr(
                    Double.TYPE,
                    Double.parseDouble(ctx.ExponentDecimalReal().getSymbol().getText()));
        }
        return null;
    }

    @Override
    public Object visitOC_BooleanLiteral(CypherParser.OC_BooleanLiteralContext ctx) {
        if (ctx.TRUE() != null) {
            return new LiteralExpr(Boolean.TYPE, true);
        } else if (ctx.FALSE() != null) {
            return new LiteralExpr(Boolean.TYPE, false);
        }
        return null;
    }
}
