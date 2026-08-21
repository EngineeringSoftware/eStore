package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NodePropScanTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(NodePropScanTest.class.getName(), new EstoreOptions().useUnsafe(true));
        Person bob = new Person("Bob", 30);
        Person alice = new Person("Alice", 28, bob);
        db.captureAll(alice);
    }

    @Test
    void unlabeledPropertyMatchFindsPersonByName() {
        Table result = db.query("MATCH (n {name:'Alice'}) RETURN n");
        assertEquals(1, result.getSize());
        assertEquals("Alice", ((Person) result.get("n").get(0)).name);
    }

    @Test
    void unlabeledPropertyMatchExcludesWrongName() {
        Table result = db.query("MATCH (n {name:'Nobody'}) RETURN n");
        assertEquals(0, result.getSize());
    }
}
