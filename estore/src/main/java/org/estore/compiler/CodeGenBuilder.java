package org.estore.compiler;

import java.util.ArrayList;
import java.util.List;
import org.estore.Estore;
import org.estore.antlr4.CypherParser;
import org.estore.compiler.create.CreateLabelNode;
import org.estore.compiler.create.CreateLabelPropNode;
import org.estore.compiler.create.CreateLabelTwoNodeRelation;
import org.estore.compiler.create.CreateTwoNodeRelation;
import org.estore.compiler.delete.DeleteRelation;
import org.estore.compiler.projection.VarProjection;
import org.estore.compiler.scan.AllNodeScan;
import org.estore.compiler.scan.NodeLabelPropScan;
import org.estore.compiler.scan.NodeLabelScan;
import org.estore.compiler.scan.NodePropScan;
import org.estore.compiler.scan.TwoNodeRelationScan;
import org.estore.compiler.scan.VarLengthRelationScan;
import org.estore.planner.LogicalPlanBuilder;
import org.estore.planner.expressions.LogicalExpr;
import org.estore.planner.expressions.VarExpr;
import org.estore.planner.pattern.NodePattern;
import org.estore.planner.pattern.PatternElement;

// extends LogicalPlanBuilder to reuse some visitors (e.g., visitOC_Pattern)
public class CodeGenBuilder extends LogicalPlanBuilder {
    private String dbname;

    public CodeGenBuilder(String dbname, Estore estore) {
        super(estore);
        this.dbname = dbname;
    }

    @Override
    public Object visitOC_Cypher(CypherParser.OC_CypherContext ctx) {
        String res = "((Supplier<Table>) () -> {\n";
        res += "Table res = null;\n";
        res += "List<LogicalExpr> expressions = null;\n";
        res += "List<String> variables = null;\n";
        res += "ArrayList<Object> labelObjects = null;\n";
        res += "ClassInfo referrerCinfo = null;\n";
        res += "ClassInfo refereeCinfo = null;\n";
        res += "ClassInfo cInfo = null;\n";
        res += "List<NodeProperty> referrerProperties = null;\n";
        res += "List<NodeProperty> refereeProperties = null;\n";
        res += "List<NodeProperty> properties = null;\n";
        res += "List<String> edgeNames = null;\n";
        res += "ArrayList<Object> referrerObjects2 = null;\n";
        res += "ArrayList<Object> refereeObjects2 = null;\n";
        res += "List<Object> refereeObjects = null;\n";
        res += "HashMap<String, Object> temp = null;\n";
        res += "String refFieldName = null;\n";
        res += "String refFieldType = null;\n";
        res += "Object referrerObject2 = null;\n";
        res += "Object refereeObject2 = null;\n";
        res += "Class<?> refereeClass = null;\n";
        res += "ArrayList<Object> resObjs = null;\n";
        if (ctx.oC_Statement() != null) {
            res += (String) ctx.oC_Statement().accept(this);
        }
        res += "  return res;  }).get()";
        return res;
    }

    @Override
    public Object visitOC_SinglePartQuery(CypherParser.OC_SinglePartQueryContext ctx) {
        String res = "";
        for (CypherParser.OC_ReadingClauseContext rCtx : ctx.oC_ReadingClause()) {
            res += (String) rCtx.accept(this);
        }
        for (CypherParser.OC_UpdatingClauseContext uCtx : ctx.oC_UpdatingClause()) {
            res += (String) uCtx.accept(this);
        }
        if (ctx.oC_Return() != null) {
            res += (String) ctx.oC_Return().accept(this);
        }
        return res;
    }

    @Override
    public Object visitOC_ReadingClause(CypherParser.OC_ReadingClauseContext ctx) {
        String res = "";
        if (ctx.oC_Match() != null) {
            res += (String) ctx.oC_Match().accept(this);
        }
        return res;
    }

    @Override
    public Object visitOC_UpdatingClause(CypherParser.OC_UpdatingClauseContext ctx) {
        String res = "";
        if (ctx.oC_Create() != null) {
            res += (String) ctx.oC_Create().accept(this);
        } else if (ctx.oC_Delete() != null) {
            res += (String) ctx.oC_Delete().accept(this);
        }
        return res;
    }

    @Override
    public Object visitOC_Delete(CypherParser.OC_DeleteContext ctx) {
        String res = "";
        if (ctx.oC_Expression().size() == 1) {
            VarExpr variable = (VarExpr) ctx.oC_Expression(0).accept(this);
            res += DeleteRelation.codegen(dbname, assignId++, variable.evaluate(null));
        }
        return res;
    }

    @Override
    public Object visitOC_Create(CypherParser.OC_CreateContext ctx) {
        String res = "";
        if (ctx.oC_Pattern() != null) {
            List<PatternElement> patterns = (List<PatternElement>) ctx.oC_Pattern().accept(this);
            for (PatternElement pattern : patterns) {
                if (pattern.isNodeOnlyPattern()) {
                    NodePattern nodePattern = pattern.getNodePattern(0);
                    if (nodePattern.getLabel() != null && nodePattern.getProperties() == null) {
                        res += CreateLabelNode.codegen(dbname, nodePattern);
                    } else if (nodePattern.getLabel() != null
                            && nodePattern.getProperties() != null) {
                        res += CreateLabelPropNode.codegen(dbname, nodePattern);
                    } else {
                        System.out.println("Not supported yet");
                        System.exit(1);
                    }
                } else if (pattern.isMultiLengthRelationPattern()) {
                    System.out.println("Not supported yet");
                    System.exit(1);
                } else if (pattern.getRelationPattern(0).isVarLengthRelation()) {
                    System.out.println("Not supported yet");
                    System.exit(1);
                } else {
                    NodePattern nodePattern1 = pattern.getNodePattern(0);
                    NodePattern nodePattern2 = pattern.getNodePattern(1);
                    if (nodePattern1.getLabel() == null && nodePattern2.getLabel() == null) {
                        res +=
                                CreateTwoNodeRelation.codegen(
                                        dbname,
                                        assignId++,
                                        nodePattern1,
                                        pattern.getRelationPattern(0),
                                        nodePattern2);
                    } else {
                        res +=
                                CreateLabelTwoNodeRelation.codegen(
                                        dbname,
                                        assignId++,
                                        nodePattern1,
                                        pattern.getRelationPattern(0),
                                        nodePattern2);
                    }
                }
            }
        }
        return res;
    }

    @Override
    public Object visitOC_ProjectionItems(CypherParser.OC_ProjectionItemsContext ctx) {
        String res = "";
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
            res += VarProjection.codegen(dbname, assignId++, expressions);
        }
        return res;
    }

    @Override
    public Object visitOC_Match(CypherParser.OC_MatchContext ctx) {
        String res = "";
        if (ctx.oC_Pattern() != null) {
            List<PatternElement> patterns = (List<PatternElement>) ctx.oC_Pattern().accept(this);

            for (PatternElement pattern : patterns) {
                if (pattern.isNodeOnlyPattern()) {
                    NodePattern nodePattern = pattern.getNodePattern(0);
                    if (nodePattern.getLabel() == null && nodePattern.getProperties() == null) {
                        res += AllNodeScan.codegen(dbname, nodePattern);
                    } else if (nodePattern.getLabel() != null
                            && nodePattern.getProperties() == null) {
                        res += NodeLabelScan.codegen(dbname, nodePattern);
                    } else if (nodePattern.getLabel() == null
                            && nodePattern.getProperties() != null) {
                        res += NodePropScan.codegen(dbname, nodePattern);
                    } else if (nodePattern.getLabel() != null
                            && nodePattern.getProperties() != null) {
                        res += NodeLabelPropScan.codegen(dbname, nodePattern);
                    }
                } else if (pattern.isMultiLengthRelationPattern()) {
                    // multi-length relation pattern
                    NodePattern nodePattern1 = null;
                    NodePattern nodePattern2 = null;
                    int nodeCount = 0;
                    int relationCount = 0;
                    int numNodes = ((pattern.getElements().size() + 1) / 2);
                    while (nodeCount < (numNodes - 1)) {
                        nodePattern1 = pattern.getNodePattern(nodeCount);
                        nodePattern2 = pattern.getNodePattern(++nodeCount);
                        if (pattern.getRelationPattern(relationCount).isVarLengthRelation()) {
                            // var-length relation pattern
                            res +=
                                    VarLengthRelationScan.codegen(
                                            dbname,
                                            assignId++,
                                            nodePattern1,
                                            pattern.getRelationPattern(relationCount++),
                                            nodePattern2);
                            if (nodeCount > 1) {
                                String joinVar =
                                        VarLengthRelationScan.getReferrerVariable(
                                                nodePattern1,
                                                pattern.getRelationPattern(relationCount - 1),
                                                nodePattern2);
                                res +=
                                        "res = res"
                                                + ".join(res_"
                                                + (assignId - 1)
                                                + ", \""
                                                + joinVar
                                                + "\");\n";
                            } else {
                                res += "res = new Table(res_" + (assignId - 1) + ");\n";
                            }
                        } else {
                            // two node relation pattern
                            res +=
                                    TwoNodeRelationScan.codegen(
                                            dbname,
                                            assignId++,
                                            nodePattern1,
                                            pattern.getRelationPattern(relationCount++),
                                            nodePattern2);
                            if (nodeCount > 1) {
                                String joinVar =
                                        TwoNodeRelationScan.getReferrerVariable(
                                                nodePattern1,
                                                pattern.getRelationPattern(relationCount - 1),
                                                nodePattern2);
                                res +=
                                        "res = res"
                                                + ".join(res_"
                                                + (assignId - 1)
                                                + ", \""
                                                + joinVar
                                                + "\");\n";
                            } else {
                                res += "res = new Table(res_" + (assignId - 1) + ");\n";
                            }
                        }
                    }
                } else if (pattern.getRelationPattern(0).isVarLengthRelation()) {
                    res +=
                            VarLengthRelationScan.codegen(
                                    dbname,
                                    assignId++,
                                    pattern.getNodePattern(0),
                                    pattern.getRelationPattern(0),
                                    pattern.getNodePattern(1));
                    res += "res = new Table(res_" + (assignId - 1) + ");\n";
                } else {
                    // two node relation pattern
                    res +=
                            TwoNodeRelationScan.codegen(
                                    dbname,
                                    assignId++,
                                    pattern.getNodePattern(0),
                                    pattern.getRelationPattern(0),
                                    pattern.getNodePattern(1));
                    res += "res = new Table(res_" + (assignId - 1) + ");\n";
                }
            }
        }
        return res;
    }
}
