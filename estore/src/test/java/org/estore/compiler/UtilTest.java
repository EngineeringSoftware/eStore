package org.estore.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.estore.Estore;
import org.estore.EstoreOptions;
import org.estore.example.A;
import org.estore.example.B;
import org.estore.planner.util.ClassInfo;
import org.estore.planner.util.NodeProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UtilTest {

    private Estore estore;
    private Object a;
    private ClassInfo aInfo;

    @BeforeEach
    void setUp() throws Exception {
        estore = new Estore(UtilTest.class.getName());
        a = estore.insert(A.class);
        aInfo = estore.getLabelClassInfoMap().get(A.class.getName());
    }

    @Test
    void checksNodeClassPropertyNames() {
        assertTrue(Util.checkNodeClassNodePropertyMatch(aInfo, null));
        assertTrue(
                Util.checkNodeClassNodePropertyMatch(
                        aInfo, Arrays.asList(longProperty("field1", 10L))));
        assertFalse(
                Util.checkNodeClassNodePropertyMatch(
                        aInfo, Arrays.asList(longProperty("missing", 10L))));
    }

    @Test
    void checksEdgeNames() {
        assertTrue(Util.checkEdgeMatch(null, aInfo));
        assertTrue(Util.checkEdgeMatch(Arrays.asList("b"), aInfo));
        assertFalse(Util.checkEdgeMatch(Arrays.asList("missing"), aInfo));
    }

    @Test
    void checksNodePropertyValues() {
        assertTrue(Util.checkNodeNodePropertyMatch(aInfo, a, null));
        assertTrue(
                Util.checkNodeNodePropertyMatch(
                        aInfo, a, Arrays.asList(longProperty("field1", 10L))));
        assertFalse(
                Util.checkNodeNodePropertyMatch(
                        aInfo, a, Arrays.asList(longProperty("field1", 20L))));
        assertFalse(
                Util.checkNodeNodePropertyMatch(
                        aInfo, a, Arrays.asList(longProperty("missing", 10L))));
    }

    @Test
    void checksClassNameProperty() {
        assertTrue(Util.checkClassNodePropertyMatch(A.class, null));
        assertTrue(
                Util.checkClassNodePropertyMatch(
                        A.class, Arrays.asList(stringProperty("name", A.class.getName()))));
        assertFalse(
                Util.checkClassNodePropertyMatch(
                        A.class, Arrays.asList(stringProperty("name", B.class.getName()))));
        assertFalse(
                Util.checkClassNodePropertyMatch(
                        A.class, Arrays.asList(stringProperty("missing", A.class.getName()))));
    }

    @Test
    void checksNodeLabels() {
        assertTrue(Util.checkNodeLabel(a, null));
        assertTrue(Util.checkNodeLabel(a, A.class.getName()));
        assertFalse(Util.checkNodeLabel(a, B.class.getName()));
    }

    @Test
    void checksNodePropertiesFromEstoreMetadata() {
        assertTrue(Util.checkNodeProperties(a, null, estore));
        assertTrue(Util.checkNodeProperties(a, Collections.emptyList(), estore));
        assertTrue(Util.checkNodeProperties(a, Arrays.asList(longProperty("field1", 10L)), estore));
        assertFalse(
                Util.checkNodeProperties(a, Arrays.asList(longProperty("field1", 20L)), estore));
        assertFalse(
                Util.checkNodeProperties(a, Arrays.asList(longProperty("missing", 10L)), estore));
        assertFalse(
                Util.checkNodeProperties(
                        new UntrackedNode(), Arrays.asList(longProperty("field1", 10L)), estore));
    }

    @Test
    void getsStartingNodesByLabelAndProperties() {
        estore.getLabelObjectMap().get(A.class.getName()).add(null);

        assertEquals(1, Util.getStartingNodes(A.class.getName(), null, estore).size());
        assertEquals(
                1,
                Util.getStartingNodes(
                                A.class.getName(),
                                Arrays.asList(longProperty("field1", 10L)),
                                estore)
                        .size());
        assertEquals(
                0,
                Util.getStartingNodes(
                                A.class.getName(),
                                Arrays.asList(longProperty("field1", 20L)),
                                estore)
                        .size());
        assertEquals(0, Util.getStartingNodes("missing.Label", null, estore).size());
    }

    @Test
    void getsNeighborsFromAllOrNamedEdges() throws Exception {
        estore.insert(new NullReferenceNode());

        List<Object> allNeighbors = Util.getNeighbors(a, null, estore);
        List<Object> namedNeighbors = Util.getNeighbors(a, Arrays.asList("b"), estore);
        List<Object> missingNeighbors = Util.getNeighbors(a, Arrays.asList("missing"), estore);
        List<Object> unknownNeighbors = Util.getNeighbors(new UntrackedNode(), null, estore);
        List<Object> nullNeighbors = Util.getNeighbors(new NullReferenceNode(), null, estore);

        assertEquals(1, allNeighbors.size());
        assertTrue(allNeighbors.get(0) instanceof B);
        assertEquals(1, namedNeighbors.size());
        assertTrue(namedNeighbors.get(0) instanceof B);
        assertEquals(0, missingNeighbors.size());
        assertEquals(0, unknownNeighbors.size());
        assertEquals(1, nullNeighbors.size());
    }

    private NodeProperty longProperty(String name, long value) {
        return new NodeProperty(Long.TYPE, value, name);
    }

    private NodeProperty stringProperty(String name, String value) {
        return new NodeProperty(String.class, value, name);
    }

    private static class UntrackedNode {
        long field1 = 10L;
    }

    private static class NullReferenceNode {
        Object child;
    }
}
