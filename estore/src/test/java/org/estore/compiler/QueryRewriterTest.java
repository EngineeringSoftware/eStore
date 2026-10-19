package org.estore.compiler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class QueryRewriterTest {

    private static String source(String... methods) {
        return "package p;\n"
                + "import org.estore.Estore;\n"
                + "import org.estore.compiler.CompileQuery;\n"
                + "public class Q {\n"
                + "    Estore db;\n"
                + String.join("\n", methods)
                + "}\n";
    }

    private static String rewriteAnnotated(String source) {
        Optional<String> rewritten = QueryRewriter.rewriteAnnotatedMethods(source);
        assertTrue(rewritten.isPresent());
        return rewritten.get();
    }

    private static String annotatedMethodBody(String body) {
        return "    @CompileQuery\n    void m() {\n        " + body + "\n    }\n";
    }

    @Test
    void rewritesOnlyAnnotatedMethods() {
        String rewritten =
                rewriteAnnotated(
                        source(
                                annotatedMethodBody("db.query(\"MATCH (n) RETURN n\");"),
                                "    void plain() { db.query(\"MATCH (m) RETURN m\"); }\n"));

        assertTrue(rewritten.contains("class TransformedQ"));
        assertTrue(rewritten.contains("((Supplier<Table>)"));
        assertTrue(rewritten.contains("db.query(\"MATCH (m) RETURN m\")"));
        assertFalse(rewritten.contains("db.query(\"MATCH (n) RETURN n\")"));
        assertFalse(rewritten.contains("@CompileQuery"));
    }

    @Test
    void returnsEmptyWithoutAnnotatedMethods() {
        String source = source("    void plain() { db.query(\"MATCH (n) RETURN n\"); }\n");

        assertFalse(QueryRewriter.rewriteAnnotatedMethods(source).isPresent());
    }

    @Test
    void joinsConcatenatedLiterals() {
        String rewritten =
                rewriteAnnotated(
                        source(
                                annotatedMethodBody(
                                        "db.query(\"MATCH (n:`org.estore.example.A`) \""
                                                + " + \"RETURN n\");")));

        assertTrue(rewritten.contains("getLabelObjectMap().get(\"org.estore.example.A\")"));
    }

    @Test
    void usesQueryScopeAsDatabaseName() {
        String rewritten =
                rewriteAnnotated(
                        source(
                                "    Estore other;\n",
                                annotatedMethodBody("other.query(\"MATCH (n) RETURN n\");")));

        assertTrue(rewritten.contains("other.getDataStore()"));
        assertFalse(rewritten.contains("db.getDataStore()"));
    }

    private static String rewriteError(String source) {
        return assertThrows(
                        QueryCompileException.class,
                        () -> QueryRewriter.rewriteAnnotatedMethods(source))
                .getMessage();
    }

    @Test
    void rejectsAnnotatedMethodWithoutQuery() {
        assertTrue(rewriteError(source(annotatedMethodBody("int x = 1;"))).contains("method m"));
    }

    @Test
    void rejectsNonLiteralQueries() {
        String variable = "String q = \"MATCH (n) RETURN n\"; db.query(q);";
        String mixed = "String s = \"n\"; db.query(\"MATCH (n) RETURN \" + s);";

        assertTrue(rewriteError(source(annotatedMethodBody(variable))).contains("string literal"));
        assertTrue(rewriteError(source(annotatedMethodBody(mixed))).contains("string literal"));
    }

    @Test
    void reportsLineAndQueryForUnsupportedCypher() {
        String message =
                rewriteError(
                        source(
                                annotatedMethodBody(
                                        "db.query(\"MATCH (n) WHERE n.field1 = 1 RETURN n\");")));

        assertTrue(message.contains("line 8"), message);
        assertTrue(message.contains("MATCH (n) WHERE n.field1 = 1 RETURN n"), message);
        assertTrue(message.contains("WHERE is not supported"), message);
    }

    @Test
    void rejectsCypherSyntaxError() {
        QueryCompileException e =
                assertThrows(
                        QueryCompileException.class,
                        () -> QueryRewriter.compileCypher("db", "MATCH (n RETURN n"));

        assertTrue(e.getMessage().contains("syntax error"), e.getMessage());
    }

    @Test
    void rejectsUnparseableJavaSource() {
        assertTrue(rewriteError("class {").contains("Cannot parse Java source"));
    }

    @Test
    void codegenRejectsUnsupportedConstructs() {
        String[][] cases = {
            {"MATCH (n) WHERE n.field1 = 5 RETURN n", "WHERE"},
            {"OPTIONAL MATCH (n) RETURN n", "OPTIONAL MATCH"},
            {"MATCH (n) RETURN DISTINCT n", "DISTINCT"},
            {"MATCH (n) RETURN n ORDER BY n.field1", "ORDER BY"},
            {"MATCH (n) RETURN n SKIP 1", "SKIP"},
            {"MATCH (n) RETURN n LIMIT 1", "LIMIT"},
            {"MATCH (n) WITH n RETURN n", "WITH"},
            {"MATCH (n) RETURN n UNION MATCH (m) RETURN m", "UNION"},
            {"UNWIND [1, 2] AS x RETURN x", "reading clauses other than MATCH"},
            {"MATCH (n) SET n.field1 = 1 RETURN n", "updating clauses"},
            {"MATCH (n)-[r]->(m) DELETE r, n RETURN n", "DELETE of more than one"},
            {"MATCH (n {field1:10}) RETURN n", "MATCH on node properties without a label"},
            {"MATCH (n)-[*1..2]->(m) RETURN m", "variable-length relationships from a node"},
            {"CREATE (n) RETURN n", "CREATE of a node without a label"},
            {"CREATE (a:`A`)-[:e]->(b:`B`)-[:e]->(c:`C`) RETURN a", "more than one relationship"},
            {"CREATE (a:`A`)-[:e*1..2]->(b:`B`) RETURN a", "variable-length"},
        };
        assertAll(
                Arrays.stream(cases)
                        .map(
                                c ->
                                        () -> {
                                            QueryCompileException e =
                                                    assertThrows(
                                                            QueryCompileException.class,
                                                            () ->
                                                                    QueryRewriter.compileCypher(
                                                                            "db", c[0]),
                                                            c[0]);
                                            assertTrue(
                                                    e.getMessage().contains(c[1]),
                                                    c[0] + " -> " + e.getMessage());
                                        }));
    }
}
