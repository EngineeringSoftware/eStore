package org.estore;

import static org.junit.jupiter.api.Assertions.*;

import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleQueryTest {

    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(SimpleQueryTest.class.getName());
    }

    @Test
    void createsAndFindsSingleNode() {
        Table created = db.query("CREATE (n:`SimpleNode`) RETURN n");
        assertEquals(1, created.getSize());
        assertTrue(created.containsKey("n"));

        Table count = db.query("MATCH (n:`SimpleNode`) RETURN COUNT(n)");
        assertEquals(1, count.getSize());
        assertEquals(1, count.get("COUNT(n)").get(0));
    }
}
