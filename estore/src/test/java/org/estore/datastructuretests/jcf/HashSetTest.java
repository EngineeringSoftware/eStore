package org.estore.datastructuretests.jcf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashSetTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() throws Exception {
        estore = new Estore(HashSetTest.class.getName(), new EstoreOptions().useUnsafe(false));
        size = 10;
    }

    //    @Disabled
    @Test
    void testHashSetSize() throws EstoreException {
        HashSet<Integer> intSet = new HashSet<Integer>();
        for (int i = 0; i < size; i++) {
            intSet.add(i);
        }
        estore.captureAll(intSet);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH"
                                + " (:`java.util.HashSet`)-[:map]->()-[:table]->()-[:key]->(n)"
                                + " RETURN COUNT(DISTINCT n) as SIZE");
        long t2 = System.currentTimeMillis();
        assertTrue((Integer) result.get("SIZE").get(0) == size);
        // System.out.println(t2 - t1);
    }
}
