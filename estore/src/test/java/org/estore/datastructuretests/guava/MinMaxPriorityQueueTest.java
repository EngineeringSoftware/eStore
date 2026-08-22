package org.estore.datastructuretests.guava;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.MinMaxPriorityQueue;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MinMaxPriorityQueueTest {
    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() {
        estore = new Estore(MinMaxPriorityQueueTest.class.getName());
        size = 10;
    }

    //    @Disabled
    @Test
    void testMinMaxPriorityQueueSize() throws EstoreException {
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.create();

        for (int i = 0; i < size; i++) {
            queue.add(i);
        }

        estore.captureAll(queue);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH"
                                + " (:`com.google.common.collect.MinMaxPriorityQueue`)-[:`queue`]->(n) "
                                + "RETURN COUNT(DISTINCT n) AS SIZE");
        // result.print();
        // estore.printLabelMaps();
        long t2 = System.currentTimeMillis();
        assertTrue((Integer) result.get("SIZE").get(0) == size);
        // System.out.println("Size: " + result.get("SIZE").get(0));
        // System.out.println(t2 - t1);
    }
}
