// Generated from Cypher.g4 by ANTLR 4.13.1

package org.estore.antlr4;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link CypherVisitor}, which can be extended to
 * create a visitor which only needs to handle a subset of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return
 *     type.
 */
@SuppressWarnings("CheckReturnValue")
public class CypherBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements CypherVisitor<T> {
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Cypher(CypherParser.OC_CypherContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Statement(CypherParser.OC_StatementContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Query(CypherParser.OC_QueryContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RegularQuery(CypherParser.OC_RegularQueryContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Union(CypherParser.OC_UnionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_SingleQuery(CypherParser.OC_SingleQueryContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_SinglePartQuery(CypherParser.OC_SinglePartQueryContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_MultiPartQuery(CypherParser.OC_MultiPartQueryContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_UpdatingClause(CypherParser.OC_UpdatingClauseContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ReadingClause(CypherParser.OC_ReadingClauseContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Match(CypherParser.OC_MatchContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Unwind(CypherParser.OC_UnwindContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Merge(CypherParser.OC_MergeContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_MergeAction(CypherParser.OC_MergeActionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Create(CypherParser.OC_CreateContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Set(CypherParser.OC_SetContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_SetItem(CypherParser.OC_SetItemContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Delete(CypherParser.OC_DeleteContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Remove(CypherParser.OC_RemoveContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RemoveItem(CypherParser.OC_RemoveItemContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_InQueryCall(CypherParser.OC_InQueryCallContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_StandaloneCall(CypherParser.OC_StandaloneCallContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_YieldItems(CypherParser.OC_YieldItemsContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_YieldItem(CypherParser.OC_YieldItemContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_With(CypherParser.OC_WithContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Return(CypherParser.OC_ReturnContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ProjectionBody(CypherParser.OC_ProjectionBodyContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ProjectionItems(CypherParser.OC_ProjectionItemsContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ProjectionItem(CypherParser.OC_ProjectionItemContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Order(CypherParser.OC_OrderContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Skip(CypherParser.OC_SkipContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Limit(CypherParser.OC_LimitContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_SortItem(CypherParser.OC_SortItemContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Where(CypherParser.OC_WhereContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Pattern(CypherParser.OC_PatternContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PatternPart(CypherParser.OC_PatternPartContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_AnonymousPatternPart(CypherParser.OC_AnonymousPatternPartContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PatternElement(CypherParser.OC_PatternElementContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RelationshipsPattern(CypherParser.OC_RelationshipsPatternContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_NodePattern(CypherParser.OC_NodePatternContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PatternElementChain(CypherParser.OC_PatternElementChainContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RelationshipPattern(CypherParser.OC_RelationshipPatternContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RelationshipDetail(CypherParser.OC_RelationshipDetailContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Properties(CypherParser.OC_PropertiesContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RelationshipTypes(CypherParser.OC_RelationshipTypesContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_NodeLabels(CypherParser.OC_NodeLabelsContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_NodeLabel(CypherParser.OC_NodeLabelContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RangeLiteral(CypherParser.OC_RangeLiteralContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_LabelName(CypherParser.OC_LabelNameContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RelTypeName(CypherParser.OC_RelTypeNameContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PropertyExpression(CypherParser.OC_PropertyExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Expression(CypherParser.OC_ExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_OrExpression(CypherParser.OC_OrExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_XorExpression(CypherParser.OC_XorExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_AndExpression(CypherParser.OC_AndExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_NotExpression(CypherParser.OC_NotExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ComparisonExpression(CypherParser.OC_ComparisonExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PartialComparisonExpression(
            CypherParser.OC_PartialComparisonExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_StringListNullPredicateExpression(
            CypherParser.OC_StringListNullPredicateExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_StringPredicateExpression(
            CypherParser.OC_StringPredicateExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ListPredicateExpression(CypherParser.OC_ListPredicateExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_NullPredicateExpression(CypherParser.OC_NullPredicateExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_AddOrSubtractExpression(CypherParser.OC_AddOrSubtractExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_MultiplyDivideModuloExpression(
            CypherParser.OC_MultiplyDivideModuloExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PowerOfExpression(CypherParser.OC_PowerOfExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_UnaryAddOrSubtractExpression(
            CypherParser.OC_UnaryAddOrSubtractExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_NonArithmeticOperatorExpression(
            CypherParser.OC_NonArithmeticOperatorExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ListOperatorExpression(CypherParser.OC_ListOperatorExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PropertyLookup(CypherParser.OC_PropertyLookupContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Atom(CypherParser.OC_AtomContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_CaseExpression(CypherParser.OC_CaseExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_CaseAlternative(CypherParser.OC_CaseAlternativeContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ListComprehension(CypherParser.OC_ListComprehensionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PatternComprehension(CypherParser.OC_PatternComprehensionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Quantifier(CypherParser.OC_QuantifierContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_FilterExpression(CypherParser.OC_FilterExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PatternPredicate(CypherParser.OC_PatternPredicateContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ParenthesizedExpression(CypherParser.OC_ParenthesizedExpressionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_IdInColl(CypherParser.OC_IdInCollContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_FunctionInvocation(CypherParser.OC_FunctionInvocationContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_FunctionName(CypherParser.OC_FunctionNameContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ExistentialSubquery(CypherParser.OC_ExistentialSubqueryContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ExplicitProcedureInvocation(
            CypherParser.OC_ExplicitProcedureInvocationContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ImplicitProcedureInvocation(
            CypherParser.OC_ImplicitProcedureInvocationContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ProcedureResultField(CypherParser.OC_ProcedureResultFieldContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ProcedureName(CypherParser.OC_ProcedureNameContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Namespace(CypherParser.OC_NamespaceContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Variable(CypherParser.OC_VariableContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Literal(CypherParser.OC_LiteralContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_BooleanLiteral(CypherParser.OC_BooleanLiteralContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_NumberLiteral(CypherParser.OC_NumberLiteralContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_IntegerLiteral(CypherParser.OC_IntegerLiteralContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_DoubleLiteral(CypherParser.OC_DoubleLiteralContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ListLiteral(CypherParser.OC_ListLiteralContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_MapLiteral(CypherParser.OC_MapLiteralContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_PropertyKeyName(CypherParser.OC_PropertyKeyNameContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Parameter(CypherParser.OC_ParameterContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_SchemaName(CypherParser.OC_SchemaNameContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_ReservedWord(CypherParser.OC_ReservedWordContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_SymbolicName(CypherParser.OC_SymbolicNameContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_LeftArrowHead(CypherParser.OC_LeftArrowHeadContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_RightArrowHead(CypherParser.OC_RightArrowHeadContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitOC_Dash(CypherParser.OC_DashContext ctx) {
        return visitChildren(ctx);
    }
}
