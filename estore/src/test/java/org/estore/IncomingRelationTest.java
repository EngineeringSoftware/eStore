package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IncomingRelationTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(IncomingRelationTest.class.getName());
        Person bob = new Person("Bob", 30);
        Person alice = new Person("Alice", 28, bob);
        db.captureAll(alice);
    }

    @Test
    void incomingTypedEdgeFindsReferrer() throws Exception {
        Table result =
                db.query(
                        "MATCH (b:`org.estore.example.Person`)<-[:friend]-(a:`org.estore.example.Person`) RETURN a");
        assertEquals(1, result.getSize());
        assertEquals("Alice", ((Person) result.get("a").get(0)).name);
    }

    @Test
    void incomingVarLengthFindsReferrer() throws Exception {
        Table result =
                db.query(
                        "MATCH (a:`org.estore.example.Person`)<-[*1..2]-(b:`org.estore.example.Person`) RETURN b");
        assertEquals(1, result.getSize());
        assertEquals("Alice", ((Person) result.get("b").get(0)).name);
    }
}
