package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.Test;

public class CaptureModeTest {

    @Test
    void iterativeDfsCapturesPersonChain() throws Exception {
        Estore db =
                new Estore(
                        CaptureModeTest.class.getName(),
                        new EstoreOptions().useUnsafe(true).useDfs(true));
        Person charlie = new Person("Charlie", 25);
        Person bob = new Person("Bob", 30, charlie);
        Person alice = new Person("Alice", 28, bob);
        db.captureAll(alice);

        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN p");
        assertEquals(3, result.getSize());
    }

    @Test
    void recursiveDfsCapturesPersonChain() throws Exception {
        Estore db =
                new Estore(
                        CaptureModeTest.class.getName(),
                        new EstoreOptions().useUnsafe(true).useDfs(true).useRecursion(true));
        Person charlie = new Person("Charlie", 25);
        Person bob = new Person("Bob", 30, charlie);
        Person alice = new Person("Alice", 28, bob);
        db.captureAll(alice);

        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN p");
        assertEquals(3, result.getSize());
    }

    @Test
    void depthLimitedCaptureStops() throws Exception {
        Estore db =
                new Estore(CaptureModeTest.class.getName(), new EstoreOptions().useUnsafe(true));
        Person charlie = new Person("Charlie", 25);
        Person bob = new Person("Bob", 30, charlie);
        Person alice = new Person("Alice", 28, bob);
        db.captureAll(alice, 2, 0);

        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN p");
        assertEquals(2, result.getSize());
    }
}
