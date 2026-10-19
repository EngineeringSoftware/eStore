package org.estore.compiler;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.estore.antlr4.CypherLexer;
import org.estore.antlr4.CypherParser;

/**
 * Rewrites {@code db.query("...")} calls in {@link CompileQuery} methods into generated Java code
 * that evaluates the Cypher query directly, and renames the rewritten top-level classes with {@link
 * #TRANSFORMED_PREFIX}.
 */
public final class QueryRewriter {

    public static final String TRANSFORMED_PREFIX = "Transformed";

    private static final String ANNOTATION = "CompileQuery";

    private static final String[] IMPORTS = {
        "java.util.function.Supplier",
        "java.util.Arrays",
        "java.util.List",
        "java.util.ArrayList",
        "java.util.HashMap",
        "java.util.HashSet",
        "java.util.Map",
        "java.util.Set",
        "java.util.Stack",
        "java.lang.reflect.Array",
        "java.lang.reflect.Field",
        "org.estore.compiler.Util",
        "org.estore.EstoreEdge",
        "org.estore.planner.expressions.LogicalExpr",
        "org.estore.planner.expressions.PropertyLookupExpr",
        "org.estore.planner.expressions.function.CountFunctionExpr",
        "org.estore.planner.expressions.function.CountFunctionSingleExpr",
        "org.estore.planner.expressions.VarExpr",
        "org.estore.planner.util.ClassInfo",
        "org.estore.planner.util.NodeProperty",
        "org.estore.planner.util.ClassHelper",
    };

    private static final String DFS_NODE_CLASS =
            "private static class DFSNode {"
                    + "Object currentNode;"
                    + "int depth;"
                    + "DFSNode(Object currentNode, int depth) {"
                    + "this.currentNode = currentNode;"
                    + "this.depth = depth;}}";

    private QueryRewriter() {}

    /**
     * Rewrites every {@code db.query("...")} call inside methods annotated with {@link
     * CompileQuery}. Any query that cannot be compiled is an error rather than being left to the
     * interpreter.
     *
     * @param source Java source of a compilation unit.
     * @return the rewritten source, or empty if the source has no annotated method.
     * @throws QueryCompileException if the source does not parse, an annotated method has no query
     *     call, passes a query that is not a string literal, or contains a query the compiler does
     *     not support.
     */
    public static Optional<String> rewriteAnnotatedMethods(String source) {
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(source);
        } catch (ParseProblemException e) {
            throw new QueryCompileException("Cannot parse Java source: " + e.getMessage(), e);
        }
        List<MethodDeclaration> methods =
                cu.findAll(MethodDeclaration.class, md -> md.isAnnotationPresent(ANNOTATION));
        if (methods.isEmpty()) {
            return Optional.empty();
        }
        for (MethodDeclaration md : methods) {
            md.getAnnotationByName(ANNOTATION).ifPresent(Node::remove);
            List<MethodCallExpr> calls =
                    md.findAll(MethodCallExpr.class, c -> c.getNameAsString().equals("query"));
            if (calls.isEmpty()) {
                throw new QueryCompileException(
                        "@CompileQuery method "
                                + md.getNameAsString()
                                + " contains no db.query(\"...\") call to compile");
            }
            calls.forEach(QueryRewriter::rewriteQueryCall);
        }
        return Optional.of(finish(cu));
    }

    /**
     * Compiles one Cypher query into a Java expression of type {@code Table}.
     *
     * @param dbName the Java expression that refers to the {@code Estore} instance.
     * @param cypher the Cypher query.
     * @return the generated Java expression.
     * @throws QueryCompileException if the query has a syntax error or uses a construct the
     *     compiler does not support.
     */
    public static String compileCypher(String dbName, String cypher) {
        try {
            CypherLexer lexer = new CypherLexer(CharStreams.fromString(cypher));
            CypherParser parser = new CypherParser(new CommonTokenStream(lexer));
            lexer.removeErrorListeners();
            lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
            parser.removeErrorListeners();
            parser.addErrorListener(ThrowingErrorListener.INSTANCE);
            return (String) new CodeGenBuilder(dbName, null).visit(parser.oC_Cypher());
        } catch (RuntimeException e) {
            throw new QueryCompileException(
                    "Cannot compile Cypher query `" + cypher + "`: " + e.getMessage(), e);
        }
    }

    private static void rewriteQueryCall(MethodCallExpr call) {
        String where = "query call at line " + call.getBegin().map(p -> p.line).orElse(-1);
        Optional<String> cypher =
                call.getScope().isPresent() && call.getArguments().size() == 1
                        ? concatenatedLiteral(call.getArgument(0))
                        : Optional.empty();
        if (!cypher.isPresent()) {
            throw new QueryCompileException(
                    where
                            + " must have the form db.query(\"...\") with a string literal"
                            + " (or concatenation of literals) argument");
        }
        try {
            String code = compileCypher(call.getScope().get().toString(), cypher.get());
            call.replace(StaticJavaParser.parseExpression(code));
        } catch (QueryCompileException | ParseProblemException e) {
            throw new QueryCompileException(where + ": " + e.getMessage(), e);
        }
    }

    private static Optional<String> concatenatedLiteral(Expression expr) {
        if (expr.isStringLiteralExpr()) {
            return Optional.of(expr.asStringLiteralExpr().asString());
        }
        if (expr.isBinaryExpr() && expr.asBinaryExpr().getOperator() == BinaryExpr.Operator.PLUS) {
            Optional<String> left = concatenatedLiteral(expr.asBinaryExpr().getLeft());
            Optional<String> right = concatenatedLiteral(expr.asBinaryExpr().getRight());
            if (left.isPresent() && right.isPresent()) {
                return Optional.of(left.get() + right.get());
            }
        }
        return Optional.empty();
    }

    private static String finish(CompilationUnit cu) {
        for (String imp : IMPORTS) {
            cu.addImport(imp);
        }
        for (ClassOrInterfaceDeclaration c : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            if (c.getParentNode().filter(p -> p instanceof CompilationUnit).isPresent()) {
                c.setName(TRANSFORMED_PREFIX + c.getNameAsString());
                c.addMember(StaticJavaParser.parseBodyDeclaration(DFS_NODE_CLASS));
            }
        }
        return cu.toString();
    }

    private static final class ThrowingErrorListener extends BaseErrorListener {
        static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            throw new QueryCompileException(
                    "syntax error at " + line + ":" + charPositionInLine + ": " + msg);
        }
    }
}
