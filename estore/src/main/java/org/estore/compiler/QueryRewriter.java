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
import org.antlr.v4.runtime.tree.ParseTree;
import org.estore.antlr4.CypherLexer;
import org.estore.antlr4.CypherParser;

/**
 * Rewrites {@code db.query("...")} calls in Java source into generated Java code that evaluates the
 * Cypher query directly, and renames the rewritten top-level classes with {@link
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
     * Rewrites every query call inside methods annotated with {@link CompileQuery}.
     *
     * <p>Every query in an annotated method must be compiled, so anything that cannot be is an
     * error rather than being left to the interpreter.
     *
     * @param source Java source of a compilation unit.
     * @return the rewritten source, or empty if the source has no annotated method.
     * @throws QueryCompileException if the source does not parse, an annotated method has no query
     *     call, passes a query that is not a string literal, or contains a query the compiler does
     *     not support.
     */
    public static Optional<String> rewriteAnnotatedMethods(String source) {
        CompilationUnit cu = parseSource(source);
        List<MethodDeclaration> methods =
                cu.findAll(MethodDeclaration.class, md -> md.isAnnotationPresent(ANNOTATION));
        if (methods.isEmpty()) {
            return Optional.empty();
        }
        for (MethodDeclaration md : methods) {
            md.getAnnotationByName(ANNOTATION).ifPresent(Node::remove);
            if (rewriteQueryCalls(md, true) == 0) {
                throw new QueryCompileException(
                        "@CompileQuery method "
                                + md.getNameAsString()
                                + " contains no db.query(\"...\") call to compile");
            }
        }
        return Optional.of(finish(cu));
    }

    /**
     * Rewrites every query call whose argument is a string literal, or a concatenation of string
     * literals. Other query calls are left in place.
     *
     * @param source Java source of a compilation unit.
     * @return the rewritten source.
     * @throws QueryCompileException if the source does not parse or a literal query cannot be
     *     compiled.
     */
    public static String rewriteAllQueries(String source) {
        CompilationUnit cu = parseSource(source);
        rewriteQueryCalls(cu, false);
        return finish(cu);
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
            ParseTree tree = parser.oC_Cypher();
            Object code = new CodeGenBuilder(dbName, null).visit(tree);
            if (!(code instanceof String)) {
                throw new QueryCompileException("code generation produced no Java code");
            }
            return (String) code;
        } catch (RuntimeException e) {
            throw new QueryCompileException(
                    "Cannot compile Cypher query `" + cypher + "`: " + e.getMessage(), e);
        }
    }

    private static CompilationUnit parseSource(String source) {
        try {
            return StaticJavaParser.parse(source);
        } catch (ParseProblemException e) {
            throw new QueryCompileException("Cannot parse Java source: " + e.getMessage(), e);
        }
    }

    private static int rewriteQueryCalls(Node root, boolean strict) {
        int rewritten = 0;
        for (MethodCallExpr call :
                root.findAll(MethodCallExpr.class, c -> c.getNameAsString().equals("query"))) {
            Optional<String> cypher = literalQuery(call);
            if (!cypher.isPresent()) {
                if (strict) {
                    throw new QueryCompileException(
                            describe(call)
                                    + " must have the form db.query(\"...\") with a string"
                                    + " literal (or concatenation of literals) argument");
                }
                continue;
            }
            String dbName = call.getScope().get().toString();
            String code;
            try {
                code = compileCypher(dbName, cypher.get());
            } catch (QueryCompileException e) {
                throw new QueryCompileException(describe(call) + ": " + e.getMessage(), e);
            }
            call.replace(parseGeneratedExpression(call, code));
            rewritten++;
        }
        return rewritten;
    }

    private static Optional<String> literalQuery(MethodCallExpr call) {
        if (!call.getScope().isPresent() || call.getArguments().size() != 1) {
            return Optional.empty();
        }
        return concatenatedLiteral(call.getArgument(0));
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

    private static Expression parseGeneratedExpression(MethodCallExpr call, String code) {
        try {
            return StaticJavaParser.parseExpression(code);
        } catch (ParseProblemException e) {
            throw new QueryCompileException(
                    describe(call) + ": generated Java code does not parse: " + e.getMessage(), e);
        }
    }

    private static String describe(MethodCallExpr call) {
        String line = call.getBegin().map(p -> " at line " + p.line).orElse("");
        return "query call" + line;
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
