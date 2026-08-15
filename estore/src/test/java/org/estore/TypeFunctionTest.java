package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeFunctionTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(TypeFunctionTest.class.getName(), new EstoreOptions().useUnsafe(true));
    }

    @Test
    void typeReturnsEdgeName() throws Exception {
        Person bob = new Person("Bob", 30);
        Person alice = new Person("Alice", 28, bob);
        db.captureAll(alice);

        Table result =
                db.query(
                        "MATCH (a:`org.estore.example.Person`)-[n]->(b:`org.estore.example.Person`) RETURN type(n)");

        assertEquals(1, result.getSize());
        assertEquals("friend", result.get("TYPE(n)").get(0));
    }
}
