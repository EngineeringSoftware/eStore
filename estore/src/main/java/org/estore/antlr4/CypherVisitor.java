// Generated from Cypher.g4 by ANTLR 4.13.1

package org.estore.antlr4;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced by {@link
 * CypherParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return
 *     type.
 */
public interface CypherVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Cypher}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Cypher(CypherParser.OC_CypherContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Statement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Statement(CypherParser.OC_StatementContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Query}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Query(CypherParser.OC_QueryContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RegularQuery}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RegularQuery(CypherParser.OC_RegularQueryContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Union}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Union(CypherParser.OC_UnionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_SingleQuery}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_SingleQuery(CypherParser.OC_SingleQueryContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_SinglePartQuery}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_SinglePartQuery(CypherParser.OC_SinglePartQueryContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_MultiPartQuery}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_MultiPartQuery(CypherParser.OC_MultiPartQueryContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_UpdatingClause}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_UpdatingClause(CypherParser.OC_UpdatingClauseContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ReadingClause}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ReadingClause(CypherParser.OC_ReadingClauseContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Match}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Match(CypherParser.OC_MatchContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Unwind}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Unwind(CypherParser.OC_UnwindContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Merge}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Merge(CypherParser.OC_MergeContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_MergeAction}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_MergeAction(CypherParser.OC_MergeActionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Create}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Create(CypherParser.OC_CreateContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Set}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Set(CypherParser.OC_SetContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_SetItem}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_SetItem(CypherParser.OC_SetItemContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Delete}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Delete(CypherParser.OC_DeleteContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Remove}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Remove(CypherParser.OC_RemoveContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RemoveItem}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RemoveItem(CypherParser.OC_RemoveItemContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_InQueryCall}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_InQueryCall(CypherParser.OC_InQueryCallContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_StandaloneCall}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_StandaloneCall(CypherParser.OC_StandaloneCallContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_YieldItems}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_YieldItems(CypherParser.OC_YieldItemsContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_YieldItem}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_YieldItem(CypherParser.OC_YieldItemContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_With}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_With(CypherParser.OC_WithContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Return}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Return(CypherParser.OC_ReturnContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ProjectionBody}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ProjectionBody(CypherParser.OC_ProjectionBodyContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ProjectionItems}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ProjectionItems(CypherParser.OC_ProjectionItemsContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ProjectionItem}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ProjectionItem(CypherParser.OC_ProjectionItemContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Order}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Order(CypherParser.OC_OrderContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Skip}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Skip(CypherParser.OC_SkipContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Limit}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Limit(CypherParser.OC_LimitContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_SortItem}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_SortItem(CypherParser.OC_SortItemContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Where}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Where(CypherParser.OC_WhereContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Pattern}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Pattern(CypherParser.OC_PatternContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PatternPart}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PatternPart(CypherParser.OC_PatternPartContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_AnonymousPatternPart}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_AnonymousPatternPart(CypherParser.OC_AnonymousPatternPartContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PatternElement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PatternElement(CypherParser.OC_PatternElementContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RelationshipsPattern}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RelationshipsPattern(CypherParser.OC_RelationshipsPatternContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_NodePattern}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_NodePattern(CypherParser.OC_NodePatternContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PatternElementChain}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PatternElementChain(CypherParser.OC_PatternElementChainContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RelationshipPattern}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RelationshipPattern(CypherParser.OC_RelationshipPatternContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RelationshipDetail}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RelationshipDetail(CypherParser.OC_RelationshipDetailContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Properties}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Properties(CypherParser.OC_PropertiesContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RelationshipTypes}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RelationshipTypes(CypherParser.OC_RelationshipTypesContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_NodeLabels}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_NodeLabels(CypherParser.OC_NodeLabelsContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_NodeLabel}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_NodeLabel(CypherParser.OC_NodeLabelContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RangeLiteral}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RangeLiteral(CypherParser.OC_RangeLiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_LabelName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_LabelName(CypherParser.OC_LabelNameContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RelTypeName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RelTypeName(CypherParser.OC_RelTypeNameContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PropertyExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PropertyExpression(CypherParser.OC_PropertyExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Expression(CypherParser.OC_ExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_OrExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_OrExpression(CypherParser.OC_OrExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_XorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_XorExpression(CypherParser.OC_XorExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_AndExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_AndExpression(CypherParser.OC_AndExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_NotExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_NotExpression(CypherParser.OC_NotExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ComparisonExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ComparisonExpression(CypherParser.OC_ComparisonExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PartialComparisonExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PartialComparisonExpression(CypherParser.OC_PartialComparisonExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_StringListNullPredicateExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_StringListNullPredicateExpression(
            CypherParser.OC_StringListNullPredicateExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_StringPredicateExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_StringPredicateExpression(CypherParser.OC_StringPredicateExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ListPredicateExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ListPredicateExpression(CypherParser.OC_ListPredicateExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_NullPredicateExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_NullPredicateExpression(CypherParser.OC_NullPredicateExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_AddOrSubtractExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_AddOrSubtractExpression(CypherParser.OC_AddOrSubtractExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_MultiplyDivideModuloExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_MultiplyDivideModuloExpression(
            CypherParser.OC_MultiplyDivideModuloExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PowerOfExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PowerOfExpression(CypherParser.OC_PowerOfExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_UnaryAddOrSubtractExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_UnaryAddOrSubtractExpression(CypherParser.OC_UnaryAddOrSubtractExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_NonArithmeticOperatorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_NonArithmeticOperatorExpression(
            CypherParser.OC_NonArithmeticOperatorExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ListOperatorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ListOperatorExpression(CypherParser.OC_ListOperatorExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PropertyLookup}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PropertyLookup(CypherParser.OC_PropertyLookupContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Atom}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Atom(CypherParser.OC_AtomContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_CaseExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_CaseExpression(CypherParser.OC_CaseExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_CaseAlternative}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_CaseAlternative(CypherParser.OC_CaseAlternativeContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ListComprehension}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ListComprehension(CypherParser.OC_ListComprehensionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PatternComprehension}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PatternComprehension(CypherParser.OC_PatternComprehensionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Quantifier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Quantifier(CypherParser.OC_QuantifierContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_FilterExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_FilterExpression(CypherParser.OC_FilterExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PatternPredicate}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PatternPredicate(CypherParser.OC_PatternPredicateContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ParenthesizedExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ParenthesizedExpression(CypherParser.OC_ParenthesizedExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_IdInColl}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_IdInColl(CypherParser.OC_IdInCollContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_FunctionInvocation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_FunctionInvocation(CypherParser.OC_FunctionInvocationContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_FunctionName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_FunctionName(CypherParser.OC_FunctionNameContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ExistentialSubquery}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ExistentialSubquery(CypherParser.OC_ExistentialSubqueryContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ExplicitProcedureInvocation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ExplicitProcedureInvocation(CypherParser.OC_ExplicitProcedureInvocationContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ImplicitProcedureInvocation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ImplicitProcedureInvocation(CypherParser.OC_ImplicitProcedureInvocationContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ProcedureResultField}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ProcedureResultField(CypherParser.OC_ProcedureResultFieldContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ProcedureName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ProcedureName(CypherParser.OC_ProcedureNameContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Namespace}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Namespace(CypherParser.OC_NamespaceContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Variable}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Variable(CypherParser.OC_VariableContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Literal}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Literal(CypherParser.OC_LiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_BooleanLiteral}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_BooleanLiteral(CypherParser.OC_BooleanLiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_NumberLiteral}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_NumberLiteral(CypherParser.OC_NumberLiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_IntegerLiteral}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_IntegerLiteral(CypherParser.OC_IntegerLiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_DoubleLiteral}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_DoubleLiteral(CypherParser.OC_DoubleLiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ListLiteral}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ListLiteral(CypherParser.OC_ListLiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_MapLiteral}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_MapLiteral(CypherParser.OC_MapLiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_PropertyKeyName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_PropertyKeyName(CypherParser.OC_PropertyKeyNameContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Parameter}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Parameter(CypherParser.OC_ParameterContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_SchemaName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_SchemaName(CypherParser.OC_SchemaNameContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_ReservedWord}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_ReservedWord(CypherParser.OC_ReservedWordContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_SymbolicName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_SymbolicName(CypherParser.OC_SymbolicNameContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_LeftArrowHead}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_LeftArrowHead(CypherParser.OC_LeftArrowHeadContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_RightArrowHead}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_RightArrowHead(CypherParser.OC_RightArrowHeadContext ctx);

    /**
     * Visit a parse tree produced by {@link CypherParser#oC_Dash}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOC_Dash(CypherParser.OC_DashContext ctx);
}
