package org.estore.datastructuretests.guava;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.HashBiMap;
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
        HashBiMap<Integer, Integer> biMap = HashBiMap.create();

        for (int i = 0; i < size; i++) {
            biMap.put(i, i);
        }

        estore.captureAll(biMap);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH ()-[*]->()-[n:value]->() " + "RETURN COUNT(DISTINCT n) as SIZE");
        // result.print();
        // estore.printLabelMaps();
        long t2 = System.currentTimeMillis();
        assertTrue((Integer) result.get("SIZE").get(0) == size);
        // System.out.println("Size: " + result.get("SIZE").get(0));
        // System.out.println(t2 - t1);
    }
}
