package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CaseExpressionTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(CaseExpressionTest.class.getName());
        db.captureAll(new Person("A", 20));
    }

    @Test
    void caseReturnsElseWhenPredicateIsFalse() throws Exception {
        Table result =
                db.query(
                        "MATCH (p:`org.estore.example.Person`) RETURN CASE WHEN p.age > 25 THEN 1 ELSE 0 END");
        assertEquals(0L, result.get("CASE").get(0));
    }

    @Test
    void caseReturnsThenWhenPredicateIsTrue() throws Exception {
        Table result =
                db.query(
                        "MATCH (p:`org.estore.example.Person`) RETURN CASE WHEN p.age > 15 THEN 1 ELSE 0 END");
        assertEquals(1L, result.get("CASE").get(0));
    }

    @Test
    void caseMatchesSubject() throws Exception {
        Table result =
                db.query(
                        "MATCH (p:`org.estore.example.Person`) RETURN CASE p.name WHEN 'A' THEN 1 ELSE 0 END");
        assertEquals(1L, result.get("CASE").get(0));
    }

    @Test
    void caseWithoutElseIsNullWhenNoWhenMatches() throws Exception {
        Table result =
                db.query(
                        "MATCH (p:`org.estore.example.Person`) RETURN CASE WHEN p.age > 99 THEN 1 END");
        assertEquals(null, result.get("CASE").get(0));
    }
}
