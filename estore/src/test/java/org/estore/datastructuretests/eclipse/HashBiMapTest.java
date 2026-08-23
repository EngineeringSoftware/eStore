package org.estore.datastructuretests.eclipse;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashBiMapTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() {
        estore = new Estore(HashBiMapTest.class.getName());
        size = 10;
    }

    //    @Disabled
    @Test
    void testHashBiMapSize() throws EstoreException {
        HashBiMap<Integer, Integer> biMap = new HashBiMap<Integer, Integer>();

        for (int i = 0; i < size; i++) {
            biMap.put(i, i * 100);
        }

        estore.captureAll(biMap);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH"
                                + " (:`org.eclipse.collections.impl.bimap.mutable.HashBiMap`)-[:delegate]->()-[:table]->(n)"
                                + " RETURN COUNT(n)/2 as SIZE");
        // result.print();
        // estore.printLabelMaps();
        long t2 = System.currentTimeMillis();
        assertTrue((Integer) result.get("SIZE").get(0) == size);
        // System.out.println("Size: " + result.get("SIZE").get(0));
        // System.out.println(t2 - t1);
    }
}
