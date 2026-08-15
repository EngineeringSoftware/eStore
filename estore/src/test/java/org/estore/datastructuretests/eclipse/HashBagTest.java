package org.estore.datastructuretests.eclipse;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashBagTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() throws Exception {
        estore = new Estore(HashBagTest.class.getName(), new EstoreOptions().useUnsafe(false));
        size = 10;
    }

    //    @Disabled
    @Test
    void testHashBagSize() throws EstoreException {
        HashBag<Integer> bag = new HashBag<Integer>();

        for (int i = 0; i < size; i++) {
            bag.add(i);
        }

        estore.captureAll(bag);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH"
                                + " (:`org.eclipse.collections.impl.bag.mutable.HashBag`)-[:items]->()-[:keys]->(n)"
                                + " RETURN COUNT(DISTINCT n) as SIZE");
        // result.print();
        // estore.printLabelMaps();
        long t2 = System.currentTimeMillis();
        assertTrue((Integer) result.get("SIZE").get(0) == size);
        // System.out.println("Size: " + result.get("SIZE").get(0));
        // System.out.println(t2 - t1);
    }
}
