package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VarLengthRangeTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(VarLengthRangeTest.class.getName(), new EstoreOptions().useUnsafe(true));
        Person charlie = new Person("Charlie", 25);
        Person bob = new Person("Bob", 30, charlie);
        Person alice = new Person("Alice", 28, bob);
        db.captureAll(alice);
    }

    @Test
    void exactTwoHopsFindsCharlie() throws Exception {
        Table result =
                db.query(
                        "MATCH (a:`org.estore.example.Person`)-[*2]->(b:`org.estore.example.Person`) RETURN b");
        assertEquals(1, result.getSize());
        assertEquals("Charlie", ((Person) result.get("b").get(0)).name);
    }

    @Test
    void twoOrMoreHopsFindsCharlie() throws Exception {
        Table result =
                db.query(
                        "MATCH (a:`org.estore.example.Person`)-[*2..]->(b:`org.estore.example.Person`) RETURN b");
        assertEquals(1, result.getSize());
        assertEquals("Charlie", ((Person) result.get("b").get(0)).name);
    }
}
