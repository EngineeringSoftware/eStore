package org.estore.compiler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.estore.antlr4.CypherLexer;
import org.estore.antlr4.CypherParser;
import org.junit.jupiter.api.Test;

public class CompilerCodegenOutputTest {

    private static final String DB = "estore";

    private String toJava(String cypher, String dbName) {
        CypherLexer lexer = new CypherLexer(CharStreams.fromString(cypher));
        CypherParser parser = new CypherParser(new CommonTokenStream(lexer));
        return (String) new CodeGenBuilder(dbName, null).visit(parser.oC_Cypher());
    }

    @Test
    void compilesSupplierWrapper() {
        String java = toJava("MATCH (n) RETURN n", DB);

        assertTrue(java.contains("((Supplier<Table>)"));
        assertTrue(java.contains("return res"));
    }

    @Test
    void compilesAllNodeScan() {
        String java = toJava("MATCH (n) RETURN n", DB);

        assertTrue(java.contains("estore.getDataStore().values()"));
        assertTrue(java.contains("res.put(\"n\""));
    }

    @Test
    void compilesNodeLabelScan() {
        String java = toJava("MATCH (n:`org.estore.example.A`) RETURN n", DB);

        assertTrue(java.contains("estore.getLabelObjectMap().get(\"org.estore.example.A\")"));
    }

    @Test
    void compilesNodeLabelPropScan() {
        String java = toJava("MATCH (n:`org.estore.example.A` {field1:10}) RETURN n", DB);

        assertTrue(java.contains("referrerProperties = new ArrayList<NodeProperty>()"));
        assertTrue(java.contains("new NodeProperty(Long.TYPE, 10L, \"field1\")"));
    }

    @Test
    void compilesTwoNodeRelationScan() {
        String java = toJava("MATCH (n)-[]->(m) RETURN n", DB);

        assertTrue(java.contains("Util.checkEdgeMatch"));
        assertTrue(java.contains("Util.checkNodeNodePropertyMatch"));
    }

    @Test
    void compilesVarLengthRelationScan() {
        String java = toJava("MATCH (n:`org.estore.example.B`)-[*1..2]->(m) RETURN m", DB);

        assertTrue(java.contains("Util.getStartingNodes"));
        assertTrue(java.contains("Util.getNeighbors"));
    }

    @Test
    void compilesMultiHopJoin() {
        String java = toJava("MATCH (n)-[]->()-[]->(l) RETURN l", DB);

        assertTrue(java.contains(".join(res_"));
    }

    @Test
    void compilesCreateLabelNode() {
        String java = toJava("CREATE (m:`org.estore.E`) RETURN m", DB);

        assertTrue(java.contains("Class.forName(\"org.estore.E\")"));
        assertTrue(java.contains("estore.insert(klass)"));
    }

    @Test
    void compilesCreateLabelPropNode() {
        String java = toJava("CREATE (n:`DummyClass4` {name:'Uki', age:30}) RETURN n", DB);

        assertTrue(java.contains("new NodeProperty(String.class, \"Uki\", \"name\")"));
        assertTrue(java.contains("new NodeProperty(Long.TYPE, 30L, \"age\")"));
    }

    @Test
    void compilesCreateLabelTwoNodeRelation() {
        String java = toJava("CREATE (n:`org.estore.F2`)-[:e]->(m:`org.estore.G2`) RETURN m", DB);

        assertTrue(java.contains("Class.forName(\"org.estore.F2\")"));
        assertTrue(java.contains("Class.forName(\"org.estore.G2\")"));
    }

    @Test
    void compilesCreateTwoNodeRelation() {
        String java = toJava("CREATE (n)-[:e]->(m) RETURN n", DB);

        assertTrue(java.contains("if (res.containsKey"));
    }

    @Test
    void compilesDeleteRelation() {
        String java = toJava("MATCH (n:`org.estore.example.A`)-[r:b]->(m) DELETE r RETURN n", DB);

        assertTrue(java.contains("EstoreEdge edge = (EstoreEdge) item.get(\"r\")"));
        assertTrue(java.contains("refereeField.set(referrerObject, null)"));
    }

    @Test
    void compilesCountProjection() {
        String java = toJava("CREATE (m:`org.estore.H`) RETURN COUNT(m)", DB);

        assertTrue(java.contains("new CountFunctionExpr"));
        assertTrue(java.contains("CountFunctionSingleExpr"));
    }

    @Test
    void compilesMatchAndCreate() {
        String java = toJava("MATCH (n) CREATE (m:`org.estore.E`) RETURN m", DB);

        assertTrue(java.contains("estore.getDataStore().values()"));
        assertTrue(java.contains("Class.forName(\"org.estore.E\")"));
    }

    @Test
    void compilesProjectionAlias() {
        String java = toJava("MATCH (n) RETURN n AS result", DB);

        assertTrue(java.contains("result"));
    }

    @Test
    void compilesVarLengthMultiHopJoin() {
        String java = toJava("MATCH (n:`org.estore.example.B`)-[*1..2]->()-[]->(l) RETURN l", DB);

        assertTrue(java.contains("Util.getNeighbors"));
        assertTrue(java.contains(".join(res_"));
    }
}
