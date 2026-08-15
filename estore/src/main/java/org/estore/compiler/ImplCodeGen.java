package org.estore.compiler;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.estore.antlr4.CypherLexer;
import org.estore.antlr4.CypherParser;
import org.estore.planner.LogicalPlan;
import org.estore.planner.LogicalPlanBuilder;

public class ImplCodeGen {
    private static final String DEFAULT_INPUT_FILE_PATH =
            "src/test/java/org/estore/compiler/CodeGenTest.java";

    private static final JavaParser jparser = new JavaParser();

    private static String createDFSNodeClass() {
        return "private static class DFSNode {"
                + "Object currentNode;"
                + "int depth;"
                + "DFSNode(Object currentNode, int depth) {"
                + "this.currentNode = currentNode;"
                + "this.depth = depth;}}";
    }

    private static class QueryReplacer extends VoidVisitorAdapter<Void> {

        private static final JavaParser jparser = new JavaParser();

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (n.getNameAsString().equals("query")) {
                // TODO: check the class of caller
                // get the database name
                String dbname = n.getScope().get().toString();

                // get the query string
                String queryString;
                Expression expr = n.getArgument(0);
                if (expr.isStringLiteralExpr()) {
                    queryString = expr.asStringLiteralExpr().getValue();
                } else if (expr.isBinaryExpr()) {
                    queryString = resolveStringConcatenation(expr);
                    if (queryString == null) {
                        return;
                    }
                    // System.out.println("resolved query string: " + queryString);
                } else {
                    // System.out.println("Unsupported query argument type");
                    return;
                }

                CypherLexer lexer = new CypherLexer(CharStreams.fromString(queryString));
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                CypherParser parser = new CypherParser(tokenStream);
                ParseTree tree = parser.oC_Cypher();

                LogicalPlanBuilder lbuilder = new LogicalPlanBuilder(null);
                LogicalPlan plan = (LogicalPlan) lbuilder.visit(tree);
                // System.out.println("logical plan: \n" + plan.toString(0));

                CodeGenBuilder builder = new CodeGenBuilder(dbname, null);
                String transformedCode = (String) builder.visit(tree);

                try {
                    n.replace(jparser.parseExpression(transformedCode).getResult().get());
                } catch (Exception e) {
                    System.out.println(
                            "Failed to parse transformed code for query: " + queryString);
                    e.printStackTrace();
                }
            }
        }

        private String resolveStringConcatenation(Expression expr) {
            if (expr.isStringLiteralExpr()) {
                return expr.asStringLiteralExpr().getValue();
            } else if (expr.isBinaryExpr()) {
                BinaryExpr binaryExpr = expr.asBinaryExpr();
                String left = resolveStringConcatenation(binaryExpr.getLeft());
                String right = resolveStringConcatenation(binaryExpr.getRight());
                if (left == null || right == null) {
                    return null;
                }
                return left + right;
            } else {
                // TODO: Handle other types of expressions (e.g., variable references)
                return null;
            }
        }
    }

    private static void addImports(CompilationUnit cu) {
        cu.addImport("java.util.function.Supplier");
        cu.addImport("java.util.Arrays");
        cu.addImport("java.util.List");
        cu.addImport("java.util.ArrayList");
        cu.addImport("java.util.HashMap");
        cu.addImport("java.util.HashSet");
        cu.addImport("java.util.Map");
        cu.addImport("java.util.Set");
        cu.addImport("java.util.Stack");
        cu.addImport("java.lang.reflect.Array");
        cu.addImport("java.lang.reflect.Field");
        cu.addImport("org.estore.compiler.Util");
        cu.addImport("org.estore.EstoreEdge");
        cu.addImport("org.estore.planner.expressions.LogicalExpr");
        cu.addImport("org.estore.planner.expressions.PropertyLookupExpr");
        cu.addImport("org.estore.planner.expressions.function.CountFunctionExpr");
        cu.addImport("org.estore.planner.expressions.function.CountFunctionSingleExpr");
        cu.addImport("org.estore.planner.expressions.VarExpr");
        cu.addImport("org.estore.planner.util.ClassInfo");
        cu.addImport("org.estore.planner.util.NodeProperty");
        cu.addImport("org.estore.planner.util.ClassHelper");
    }

    private static String getDefaultOutputFilePath(String inputFilePath) {
        File inputFile = new File(inputFilePath);
        String parentDir = inputFile.getParent();
        String fileName = inputFile.getName();
        return Paths.get(parentDir, "Transformed" + fileName).toString();
    }

    public static void main(String[] args) throws Exception {
        String inputFilePath = args.length > 0 ? args[0] : DEFAULT_INPUT_FILE_PATH;
        String outputFilePath = args.length > 1 ? args[1] : getDefaultOutputFilePath(inputFilePath);
        CompilationUnit cu = StaticJavaParser.parse(Files.newInputStream(Paths.get(inputFilePath)));

        addImports(cu);
        cu.accept(new QueryReplacer(), null);

        // change the class name of the generated file
        cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                .filter(
                        c ->
                                c.getParentNode().isPresent()
                                        && c.getParentNode().get() instanceof CompilationUnit)
                .forEach(
                        c -> {
                            c.setName("Transformed" + c.getNameAsString());
                            c.addMember(
                                    jparser.parseBodyDeclaration(createDFSNodeClass())
                                            .getResult()
                                            .get());
                        });

        Files.write(Paths.get(outputFilePath), cu.toString().getBytes());
    }
}
