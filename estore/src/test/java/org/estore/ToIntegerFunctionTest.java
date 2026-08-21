package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ToIntegerFunctionTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db = new Estore(ToIntegerFunctionTest.class.getName(), new EstoreOptions().useUnsafe(true));
        db.captureAll(new Person("A", 20));
    }

    @Test
    void toIntegerConvertsStringLiteral() throws Exception {
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN toInteger('9')");
        assertEquals(9, result.get("TOINTEGER(9)").get(0));
    }

    @Test
    void toIntegerConvertsProperty() throws Exception {
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN toInteger(p.age)");
        assertEquals(20, result.get("TOINTEGER(p.age)").get(0));
    }

    @Test
    void toIntegerReturnsNullForNonNumericString() throws Exception {
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN toInteger('nope')");
        assertEquals(null, result.get("TOINTEGER(nope)").get(0));
    }
}
