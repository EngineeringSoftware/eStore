package org.estore;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.estore.antlr4.CypherLexer;
import org.estore.antlr4.CypherParser;
import org.estore.compiler.CodeGenBuilder;

@Mojo(name = "codegen", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class CodegenMojo extends AbstractMojo {

    @Parameter(property = "sourceDir", defaultValue = "${project.build.testSourceDirectory}")
    private File sourceDir;

    @Parameter(property = "profiling", defaultValue = "true")
    private Boolean profiling;

    public void execute() throws MojoExecutionException {
        long t1 = 0;
        if (profiling) {
            t1 = System.currentTimeMillis();
        }
        try {
            List<Path> javaFiles =
                    Files.walk(sourceDir.toPath())
                            .filter(p -> p.toString().endsWith(".java"))
                            .collect(Collectors.toList());
            for (Path fp : javaFiles) {
                File f = fp.toFile();
                processJavaFile(f);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error processing files", e);
        }
        if (profiling) {
            long t2 = System.currentTimeMillis();
            System.out.println("Total Time taken: " + (t2 - t1) + " ms");
        }
    }

    private void processJavaFile(File file) throws MojoExecutionException {
        try (FileInputStream in = new FileInputStream(file)) {
            CompilationUnit cu = StaticJavaParser.parse(in);
            boolean[] modified = {false};
            new AnnotationVisitor().visit(cu, modified);
            if (modified[0]) {
                // if there is a query being transformed
                // add imports, rename the class, and add DFSNode class
                addImports(cu);
                cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                        .filter(
                                c ->
                                        c.getParentNode().isPresent()
                                                && c.getParentNode().get()
                                                        instanceof CompilationUnit)
                        .forEach(
                                c -> {
                                    c.setName("Transformed" + c.getNameAsString());
                                    c.addMember(
                                            StaticJavaParser.parseBodyDeclaration(
                                                    createDFSNodeClass()));
                                });

                String originalFilename = file.getName();
                String newFilename = "Transformed" + originalFilename;
                File newFile = new File(file.getParent(), newFilename);
                Files.write(newFile.toPath(), cu.toString().getBytes());

                getLog().info("Transformed " + originalFilename + " to " + newFilename);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error processing file: " + file, e);
        }
    }

    private class AnnotationVisitor extends VoidVisitorAdapter<boolean[]> {
        @Override
        public void visit(MethodDeclaration md, boolean[] modified) {
            super.visit(md, modified);
            if (md.isAnnotationPresent("CompileQuery")) {
                String mdName = md.getNameAsString();
                long t1 = 0;
                if (profiling) {
                    t1 = System.currentTimeMillis();
                }
                // remove the annotation to avoid re-processing
                md.getAnnotations().removeIf(a -> a.getNameAsString().equals("CompileQuery"));
                new QueryReplacer().visit(md, modified);
                if (profiling) {
                    long t2 = System.currentTimeMillis();
                    System.out.println(
                            "Time taken for method " + mdName + ": " + (t2 - t1) + " ms");
                }
            }
        }
    }

    private class QueryReplacer extends VoidVisitorAdapter<boolean[]> {

        @Override
        public void visit(MethodCallExpr n, boolean[] modified) {
            super.visit(n, modified);
            if (n.getNameAsString().equals("query")) {
                modified[0] = true;

                String dbname = n.getScope().get().toString();

                // get the query string
                String queryString = "";
                Expression expr = n.getArgument(0);
                if (expr.isStringLiteralExpr()) {
                    queryString = expr.asStringLiteralExpr().getValue();
                } else if (expr.isBinaryExpr()) {
                    queryString = resolveStringConcatenation(expr);
                } else {
                    System.out.println("Unsupported query argument type");
                    System.exit(1);
                }

                CypherLexer lexer = new CypherLexer(CharStreams.fromString(queryString));
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                CypherParser parser = new CypherParser(tokenStream);
                ParseTree tree = parser.oC_Cypher();

                CodeGenBuilder builder = new CodeGenBuilder(dbname, null);
                String transformedCode = (String) builder.visit(tree);

                try {
                    n.replace(StaticJavaParser.parseExpression(transformedCode));
                } catch (Exception e) {
                    System.out.println(
                            "Failed to parse transformed code for query: " + queryString);
                    e.printStackTrace();
                }
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
            return left + right;
        } else {
            // TODO: Handle other types of expressions (e.g., variable references)
            // return expr.toString();
            System.out.println("Unsupported expression: " + expr);
            System.exit(1);
            return "";
        }
    }

    private static String createDFSNodeClass() {
        return "private static class DFSNode {"
                + "Object currentNode;"
                + "int depth;"
                + "DFSNode(Object currentNode, int depth) {"
                + "this.currentNode = currentNode;"
                + "this.depth = depth;}}";
    }

    private void addImports(CompilationUnit cu) {
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
}
