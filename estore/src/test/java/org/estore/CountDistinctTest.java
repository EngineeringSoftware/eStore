package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CountDistinctTest {
    private Estore db;

    @BeforeEach
    void setUp() {
        db = new Estore(CountDistinctTest.class.getName());
    }

    @Test
    void countDistinctIsOneWhenCountIsTwo() throws EstoreException {
        // Keanu -> Carrie -> Guy
        // Keanu -> Liam -> Guy
        Person guy = new Person("Guy", 40);
        Person carrie = new Person("Carrie", 30, guy);
        Person liam = new Person("Liam", 30, guy);
        Person keanu = new Person("Keanu", 50, carrie);
        keanu.friend2 = liam;
        db.captureAll(keanu);

        Table all = db.query("MATCH (:`org.estore.example.Person`)-[]->()-[]->(n) RETURN COUNT(n)");
        Table distinct =
                db.query(
                        "MATCH (:`org.estore.example.Person`)-[]->()-[]->(n) RETURN COUNT(DISTINCT n)");

        assertEquals(2, all.get("COUNT(n)").get(0));
        assertEquals(1, distinct.get("COUNT(n)").get(0));
    }
}
