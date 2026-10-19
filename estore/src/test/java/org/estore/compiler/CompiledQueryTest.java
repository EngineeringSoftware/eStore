package org.estore.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.example.A;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Checks that each query shape the compiler supports returns the same result when compiled as when
 * interpreted. With {@code -Pcompile-queries}, the codegen plugin generates {@code
 * TransformedCompiledQueryTest}, where every {@code estore.query(...)} call in a {@link
 * CompileQuery} method runs as generated Java code; {@link #interpret} is not annotated, so it
 * always runs the interpreter.
 */
public class CompiledQueryTest {

    private Estore estore;
    private Estore reference;

    @BeforeEach
    public void setUp() throws EstoreException {
        EstoreOptions options = new EstoreOptions().profile(false);
        estore = new Estore(CompiledQueryTest.class.getName(), options);
        estore.insert(A.class);
        reference = new Estore(CompiledQueryTest.class.getName() + ".reference", options);
        reference.insert(A.class);
    }

    private static Table interpret(Estore db, String cypher) {
        return db.query(cypher);
    }

    private static Map<Map<String, Object>, Long> rowCounts(Table table) {
        Map<Map<String, Object>, Long> counts = new HashMap<>();
        for (int i = 0; i < table.getSize(); i++) {
            counts.merge(table.getAtIndex(i), 1L, Long::sum);
        }
        return counts;
    }

    private void assertSameAsInterpreted(String cypher, Table compiled) {
        Table interpreted = interpret(estore, cypher);
        assertTrue(interpreted.getSize() > 0, "interpreter returned no rows for " + cypher);
        assertEquals(interpreted.keySet(), compiled.keySet(), cypher);
        assertEquals(rowCounts(interpreted), rowCounts(compiled), cypher);
    }

    private void assertSameGraphAsReference() {
        for (String cypher :
                new String[] {"MATCH (n) RETURN COUNT(n)", "MATCH (n)-[]->(m) RETURN COUNT(n)"}) {
            assertEquals(
                    rowCounts(interpret(reference, cypher)),
                    rowCounts(interpret(estore, cypher)),
                    cypher);
        }
    }

    @CompileQuery
    @Test
    void allNodeScan() {
        assertSameAsInterpreted("MATCH (n) RETURN n", estore.query("MATCH (n) RETURN n"));
    }

    @CompileQuery
    @Test
    void nodeLabelScan() {
        assertSameAsInterpreted(
                "MATCH (n:`org.estore.example.B`) RETURN n",
                estore.query("MATCH (n:`org.estore.example.B`) RETURN n"));
    }

    @CompileQuery
    @Test
    void nodeLabelPropScan() {
        assertSameAsInterpreted(
                "MATCH (n:`org.estore.example.A` {field1:10}) RETURN n",
                estore.query("MATCH (n:`org.estore.example.A` {field1:10}) RETURN n"));
    }

    @CompileQuery
    @Test
    void twoNodeRelationScan() {
        assertSameAsInterpreted(
                "MATCH (n)-[]->(m) RETURN n, m", estore.query("MATCH (n)-[]->(m) RETURN n, m"));
    }

    @CompileQuery
    @Test
    void multiHopJoin() {
        assertSameAsInterpreted(
                "MATCH (n:`org.estore.example.A`)-[]->()-[]->(l) RETURN n, l",
                estore.query("MATCH (n:`org.estore.example.A`)-[]->()-[]->(l) RETURN n, l"));
    }

    @CompileQuery
    @Test
    void varLengthRelationScan() {
        assertSameAsInterpreted(
                "MATCH (n:`org.estore.example.A`)-[*1..2]->(m) RETURN m",
                estore.query("MATCH (n:`org.estore.example.A`)-[*1..2]->(m) RETURN m"));
    }

    @CompileQuery
    @Test
    void varLengthMultiHopJoin() {
        assertSameAsInterpreted(
                "MATCH (n:`org.estore.example.B`)-[*1..2]->()-[]->(l) RETURN l",
                estore.query("MATCH (n:`org.estore.example.B`)-[*1..2]->()-[]->(l) RETURN l"));
    }

    @CompileQuery
    @Test
    void propertyProjectionWithAlias() {
        assertSameAsInterpreted(
                "MATCH (n) RETURN n.field1 AS f, n.field2 AS s",
                estore.query("MATCH (n) RETURN n.field1 AS f, n.field2 AS s"));
    }

    @CompileQuery
    @Test
    void countProjection() {
        assertSameAsInterpreted(
                "MATCH (n)-[]->(m) RETURN COUNT(m)",
                estore.query("MATCH (n)-[]->(m) RETURN COUNT(m)"));
    }

    @CompileQuery
    @Test
    void createLabelNode() {
        Table compiled = estore.query("CREATE (n:`org.estore.example.D`) RETURN COUNT(n)");
        Table interpreted =
                interpret(reference, "CREATE (n:`org.estore.example.D`) RETURN COUNT(n)");

        assertEquals(rowCounts(interpreted), rowCounts(compiled));
        assertSameGraphAsReference();
    }

    @CompileQuery
    @Test
    void deleteRelation() {
        Table compiled =
                estore.query(
                        "MATCH (n:`org.estore.example.A`)-[r:b]->(m) DELETE r RETURN COUNT(n)");
        Table interpreted =
                interpret(
                        reference,
                        "MATCH (n:`org.estore.example.A`)-[r:b]->(m) DELETE r RETURN COUNT(n)");

        assertEquals(rowCounts(interpreted), rowCounts(compiled));
        assertSameGraphAsReference();
    }

    @CompileQuery
    @Test
    void matchThenCreateRelation() {
        Table compiled =
                estore.query(
                        "MATCH (c:`org.estore.example.C` {field1:10}),"
                                + " (d:`org.estore.example.D` {field1:10})"
                                + " CREATE (c)-[r:d]->(d) RETURN COUNT(r)");
        Table interpreted =
                interpret(
                        reference,
                        "MATCH (c:`org.estore.example.C` {field1:10}),"
                                + " (d:`org.estore.example.D` {field1:10})"
                                + " CREATE (c)-[r:d]->(d) RETURN COUNT(r)");

        assertEquals(rowCounts(interpreted), rowCounts(compiled));
        assertSameGraphAsReference();
    }
}
