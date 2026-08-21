package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertiesFunctionTest {
    private Estore db;

    @BeforeEach
    void setUp() throws Exception {
        db =
                new Estore(
                        PropertiesFunctionTest.class.getName(),
                        new EstoreOptions().useUnsafe(true));
        db.captureAll(new Person("A", 20));
    }

    @Test
    void propertiesReturnsPrimitiveFields() {
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN properties(p)");
        HashMap<?, ?> props = (HashMap<?, ?>) result.get("PROPERTIES(p)").get(0);
        assertEquals("A", props.get("name"));
        assertEquals(20, props.get("age"));
    }
}
