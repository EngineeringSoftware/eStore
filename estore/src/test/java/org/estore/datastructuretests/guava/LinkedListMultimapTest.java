package org.estore.datastructuretests.guava;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.LinkedListMultimap;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LinkedListMultimapTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() throws Exception {
        estore =
                new Estore(LinkedListMultimapTest.class.getName());
        size = 10;
    }

    //    @Disabled
    @Test
    void testLinkedListMultimapSize() throws EstoreException {
        LinkedListMultimap<Integer, Integer> LLmap = LinkedListMultimap.create();

        for (int i = 0; i < size; i++) {
            LLmap.put(i, i);
        }

        estore.captureAll(LLmap);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH"
                                + " (:`com.google.common.collect.LinkedListMultimap`)-[:head]->"
                                + "(:`com.google.common.collect.LinkedListMultimap$Node`)"
                                + "-[:next*0..9]->(r) "
                                + "RETURN COUNT(DISTINCT r) as SIZE");
        // result.print();
        // estore.printLabelMaps();
        long t2 = System.currentTimeMillis();
        assertTrue((Integer) result.get("SIZE").get(0) == size);
        // System.out.println("Size: " + result.get("SIZE").get(0));
        // System.out.println(t2 - t1);
    }
}
