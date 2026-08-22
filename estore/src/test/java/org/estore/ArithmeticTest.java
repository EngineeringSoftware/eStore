package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArithmeticTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(ArithmeticTest.class.getName());
    }

    @Test
    void fourOperationsAndCountDivide() throws Exception {
        db.query("CREATE (n:`ArithNode`)");
        db.query("CREATE (n:`ArithNode`)");

        Table add = db.query("MATCH (n:`ArithNode`) RETURN 1+2 AS n");
        Table sub = db.query("MATCH (n:`ArithNode`) RETURN 6-2 AS n");
        Table mul = db.query("MATCH (n:`ArithNode`) RETURN 3*4 AS n");
        Table div = db.query("MATCH (n:`ArithNode`) RETURN 10/2 AS n");
        Table countDiv = db.query("MATCH (n:`ArithNode`) RETURN COUNT(n)/2 AS n");

        assertEquals(3, add.get("n").get(0));
        assertEquals(4, sub.get("n").get(0));
        assertEquals(12, mul.get("n").get(0));
        assertEquals(5, div.get("n").get(0));
        assertEquals(1, countDiv.get("n").get(0));
    }
}
