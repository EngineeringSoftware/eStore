package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnonymousRelationTest {
    private Estore db;

    @BeforeEach
    void setUp() {
        db = new Estore(AnonymousRelationTest.class.getName());
    }

    @Test
    void bareArrowMatchesEmptyBrackets() throws EstoreException {
        Person bob = new Person("Bob", 30);
        Person alice = new Person("Alice", 28, bob);
        db.captureAll(alice);

        Table brackets =
                db.query(
                        "MATCH (a:`org.estore.example.Person`)-[]->(b:`org.estore.example.Person`) RETURN b");
        Table arrow =
                db.query(
                        "MATCH (a:`org.estore.example.Person`)-->(b:`org.estore.example.Person`) RETURN b");

        assertEquals(1, brackets.getSize());
        assertEquals(1, arrow.getSize());
        assertEquals(brackets.get("b").get(0), arrow.get("b").get(0));
    }
}
