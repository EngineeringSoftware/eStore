package org.estore.datastructuretests.apachecommons;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DualHashBidiMapTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() throws Exception {
        estore =
                new Estore(
                        DualHashBidiMapTest.class.getName(), new EstoreOptions().useUnsafe(false));
        size = 10;
    }

    //    @Disabled
    @Test
    void testDualHashBidiMapSize() throws EstoreException {
        DualHashBidiMap<Integer, Integer> biMap = new DualHashBidiMap<Integer, Integer>();

        for (int i = 0; i < size; i++) {
            biMap.put(i, i);
        }

        estore.captureAll(biMap);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH"
                                + " (:`org.apache.commons.collections4.bidimap.DualHashBidiMap`)"
                                + "-[:normalMap]->()-[:table]->()-[:value]->(n)"
                                + " RETURN COUNT(DISTINCT n) as SIZE");
        // result.print();
        // estore.printLabelMaps();
        long t2 = System.currentTimeMillis();
        assertTrue((Integer) result.get("SIZE").get(0) == size);
        // System.out.println("Size: " + result.get("SIZE").get(0));
        // System.out.println(t2 - t1);
    }
}
