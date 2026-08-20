package org.estore.eval.datastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import org.estore.Estore;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JCFTest {
    private Estore estore;
    private static ThreadLocalRandom rand;

    @BeforeEach
    public void setup() throws Exception {
        rand = ThreadLocalRandom.current();
        estore = new Estore(JCFTest.class.getName());
    }

    @Test
    public void testArrayList() throws Exception {
        ArrayList<Long> list = new ArrayList<Long>();
        int ind = rand.nextInt(0, 99);
        for (int j = 0; j < 100; j++) {
            if (j == ind) {
                list.add(90L);
            } else {
                list.add(rand.nextLong(0, Long.MAX_VALUE));
            }
        }
        estore.captureAll(list);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (n:`java.util.ArrayList`)-[:elementData]->(p {value: 90}) RETURN p");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertTrue(((Long) result.get("p").get(0)) == list.get(ind));
    }

    @Test
    public void testArrayDeque() throws Exception {
        ArrayDeque<Long> list = new ArrayDeque<Long>();
        int ind = rand.nextInt(0, 99);
        for (int j = 0; j < 100; j++) {
            if (j == ind) {
                list.add(87L);
            } else {
                list.add(rand.nextLong(0, Long.MAX_VALUE));
            }
        }
        estore.captureAll(list);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (n:`java.util.ArrayDeque`)-[:elements]->(p {value: 87}) RETURN p");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertTrue(((Long) result.get("p").get(0)) == list.toArray()[ind]);
    }

    @Test
    public void testLinkedList() throws Exception {
        LinkedList<Long> list = new LinkedList<Long>();
        int ind = rand.nextInt(0, 99);
        for (int j = 0; j < 100; j++) {
            if (j == ind) {
                list.add(999L);
            } else {
                list.add(rand.nextLong(0, Long.MAX_VALUE));
            }
        }
        estore.captureAll(list);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (n:`java.util.LinkedList`)-[*1..99]->(m)-[:item]->(p {value: 999}) RETURN p");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertTrue(((Long) result.get("p").get(0)) == list.get(ind));
    }

    @Test
    public void testVector() throws Exception {
        Vector<Long> list = new Vector<Long>();
        int ind = rand.nextInt(0, 99);
        for (int j = 0; j < 100; j++) {
            if (j == ind) {
                list.add(999L);
            } else {
                list.add(rand.nextLong(0, Long.MAX_VALUE));
            }
        }
        estore.captureAll(list);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (n:`java.util.Vector`)-[:elementData]->(p {value: 999}) RETURN p");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertTrue(((Long) result.get("p").get(0)) == list.get(ind));
    }

    @Test
    public void testHashMap() throws Exception {
        HashMap<Long, Long> map = new HashMap<Long, Long>();
        while (map.size() != 99) {
            map.put(rand.nextLong(0, Long.MAX_VALUE), rand.nextLong(0, Long.MAX_VALUE));
        }
        map.put(rand.nextLong(0, Long.MAX_VALUE), 90L);
        estore.captureAll(map);

        long t1 = System.nanoTime();
        /*
         NOTE: The `table` field of `HashMap` may store some of the values inserted
         into the HashMap as LinkedLists. We write the query to treat all entries in the
         `table` field as head of LinkedLists'
         NOTE: Codegen does not work for this query for now.
        */
        Table result =
                estore.query(
                        "MATCH (n:`java.util.HashMap`)-[:table]->()-[*1..99]->(p {value:90})  RETURN p");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        // result.print();
        assertEquals(((Long) result.get("p").get(0)), 90L);
    }
}
