package org.estore.planner.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhereBooleanLogicTest {
    private Estore db;

    @BeforeEach
    void setUp() {
        db = new Estore(WhereBooleanLogicTest.class.getName() + "_" + System.nanoTime());
    }

    @Test
    void queryWhereAnd_requiresBothPredicatesTrue() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 15));
        Table result =
                db.query(
                        "MATCH (p:`org.estore.example.Person`) WHERE p.age > 16 AND p.name = 'Alice' RETURN p");
        assertEquals(1, result.getSize());
    }

    @Test
    void queryWhereOr_keepsRowsWhenEitherPredicateIsTrue() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 15), new Person("Carl", 20));
        Table result =
                db.query(
                        "MATCH (p:`org.estore.example.Person`) WHERE p.name = 'Alice' OR p.age > 19 RETURN p");
        assertEquals(2, result.getSize());
    }

    @Test
    void queryWhereXor_keepsRowsWhenExactlyOnePredicateIsTrue() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 17), new Person("Alice", 15));
        Table result =
                db.query(
                        "MATCH (p:`org.estore.example.Person`) WHERE p.name = 'Alice' XOR p.age = 17 RETURN p");
        assertEquals(2, result.getSize());
    }

    @Test
    void queryWhereNot_invertsPredicateTruthValue() throws EstoreException {
        capturePeople(new Person("Alice", 17), new Person("Bob", 15));
        Table result =
                db.query(
                        "MATCH (p:`org.estore.example.Person`) WHERE NOT p.name = 'Alice' RETURN p");
        assertEquals(1, result.getSize());
    }

    private void capturePeople(Person... people) throws EstoreException {
        for (Person person : people) {
            db.captureAll(person);
        }
    }
}
