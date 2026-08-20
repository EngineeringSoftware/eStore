package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AggregateFunctionTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(AggregateFunctionTest.class.getName());
        Person c = new Person("C", 30);
        Person b = new Person("B", 40, c);
        Person a = new Person("A", 20, b);
        db.captureAll(a);
    }

    @Test
    void maxReturnsLargestAge() throws Exception {
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN max(p.age)");
        assertEquals(40, result.get("MAX(p.age)").get(0));
    }

    @Test
    void minReturnsSmallestAge() throws Exception {
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN min(p.age)");
        assertEquals(20, result.get("MIN(p.age)").get(0));
    }

    @Test
    void sumReturnsTotalAge() throws Exception {
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN sum(p.age)");
        assertEquals(90, result.get("SUM(p.age)").get(0));
    }

    @Test
    void avgReturnsMeanAge() throws Exception {
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN avg(p.age)");
        assertEquals(30.0, result.get("AVG(p.age)").get(0));
    }
}
