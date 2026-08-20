package org.estore.eval.datastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.estore.Estore;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EclipseTest {
    private Estore estore;
    private static ThreadLocalRandom rand;

    @BeforeEach
    public void setup() throws Exception {
        estore = new Estore(EclipseTest.class.getName());
        rand = ThreadLocalRandom.current();
    }

    @Test
    public void testUnifiedSet() throws Exception {
        ArrayList<Long> setData = new ArrayList<Long>();
        int ind = rand.nextInt(0, 99);
        for (int j = 0; j < 100; j++) {
            if (j == ind) {
                setData.add(90L);
            } else {
                setData.add((long) j);
            }
        }
        UnifiedSet<Long> unifiedSet = new UnifiedSet(setData);
        estore.captureAll(unifiedSet);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (n:`org.eclipse.collections.impl.set.mutable.UnifiedSet`)-[:table]->(m {value:"
                                + " 90}) RETURN m");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertEquals(
                ((Long) result.get("m").get(0)),
                setData.get(ind),
                "Mismatch in IngraphReflectionTestUnifiedSet100 for testFindElement");
    }

    @Test
    public void testUnifiedMap() throws Exception {
        ArrayList<Long> setData = new ArrayList<Long>();
        UnifiedMap<Long, Long> unifiedMap = new UnifiedMap();
        int ind = rand.nextInt(0, 99);
        long key = 0L;
        while (unifiedMap.size() != 100) {
            long randValue = rand.nextLong(0, Long.MAX_VALUE);
            if (unifiedMap.size() == ind) {
                randValue = 999L;
            }
            unifiedMap.put(key++, randValue);
            setData.add(randValue);
        }
        estore.captureAll(unifiedMap);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (n:`org.eclipse.collections.impl.map.mutable.UnifiedMap`)-[:table]->(m {value:"
                                + " 999}) RETURN m");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertEquals(
                ((Long) result.get("m").get(0)),
                setData.get(ind),
                "Mismatch in IngraphReflectionTestUnifiedMap100 for testFindElement");
    }

    @Test
    public void testFastList() throws Exception {
        FastList<Long> fastList = new FastList();
        int ind = rand.nextInt(0, 99);
        for (int j = 0; j < 100; j++) {
            if (j == ind) {
                fastList.add(999L);
            } else {
                fastList.add(rand.nextLong(0, Long.MAX_VALUE));
            }
        }
        estore.captureAll(fastList);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (n:`org.eclipse.collections.impl.list.mutable.FastList`)-[:items]->(m {value:"
                                + " 999}) RETURN m");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertEquals(
                ((Long) result.get("m").get(0)),
                fastList.get(ind),
                "Mismatch in IngraphReflectionTestFastList100 for testFindElement");
    }

    @Test
    public void testArrayStack() throws Exception {
        ArrayStack<Long> arrayStack = new ArrayStack();
        ArrayList<Long> stackData = new ArrayList<Long>();
        int ind = rand.nextInt(0, 99);
        for (int j = 0; j < 100; j++) {
            long randValue = rand.nextLong(0, Long.MAX_VALUE);
            if (j == ind) {
                randValue = 999L;
            }
            stackData.add(randValue);
            arrayStack.push(randValue);
        }
        estore.captureAll(arrayStack);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH"
                                + " (n:`org.eclipse.collections.impl.stack.mutable.ArrayStack`)-[:delegate]->()-[:items]->(m"
                                + " {value: 999}) RETURN m");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertEquals(((Long) result.get("m").get(0)), stackData.get(ind));
    }

    @Test
    public void testImmutableArrayList() throws Exception {
        ArrayList<Long> listData = new ArrayList<Long>();
        int ind = rand.nextInt(0, 99);
        for (int j = 0; j < 100; j++) {
            if (j == ind) {
                listData.add(90L);
            } else {
                listData.add(rand.nextLong(0, Long.MAX_VALUE));
            }
        }
        ImmutableList<Long> immutableArrayList = new ImmutableListFactoryImpl().withAll(listData);
        estore.captureAll(immutableArrayList);

        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH"
                                + " (n:`org.eclipse.collections.impl.list.immutable.ImmutableArrayList`)-[:items]->(m"
                                + " {value: 90}) RETURN m");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertEquals(
                ((Long) result.get("m").get(0)),
                immutableArrayList.get(ind),
                "Mismatch in IngraphReflectionTestImmutableArrayList100 for testFindElement");
    }
}
