package org.estore.datastructuretests.fastutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unimi.dsi.fastutil.longs.Long2IntAVLTreeMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.concurrent.ThreadLocalRandom;
import org.estore.Estore;
import org.estore.EstoreOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FastutilTest {
    private Estore estore;
    private static ThreadLocalRandom rand;
    private LongArrayList list;
    private LongOpenHashSet set;
    private Long2IntAVLTreeMap map;

    @BeforeEach
    public void initDatabase() throws Exception {
        estore = new Estore(FastutilTest.class.getName(), new EstoreOptions().useUnsafe(false));
    }

    @Test
    public void testArrayListContains() throws Exception {
        rand = ThreadLocalRandom.current();
        list = new LongArrayList();
        for (int j = 0; j < 100; j++) {
            list.add(rand.nextLong(0, Long.MAX_VALUE));
        }
        estore.captureAll(list);
        Long k = list.getLong(rand.nextInt(list.size()));
        boolean var1 = EstoreLongArrayList.contains(estore, list, k, true);
        boolean var2 = EstoreLongArrayList.contains(estore, list, k, false);
        assertTrue(var2);
        assertEquals(var1, var2);
    }

    @Test
    public void testHashSetContains() throws Exception {
        set = new LongOpenHashSet();
        for (int j = 0; j < 100; j++) {
            set.add(j);
        }
        estore.captureAll(set);
        long k = 90;
        boolean var1 = EstoreLongOpenHashSet.contains(estore, set, k, true);
        boolean var2 = EstoreLongOpenHashSet.contains(estore, set, k, false);
        assertTrue(var2);
        assertEquals(var1, var2);
    }

    @Test
    public void testAVLTreeMapKeys() throws Exception {
        map = new Long2IntAVLTreeMap();
        for (int j = 1; j <= 100; j++) {
            map.put(j, j);
        }
        estore.captureAll(map);
        long firstKey = EstoreAVLTreeMap.firstLongKey(estore, map, true);
        long firstKey2 = EstoreAVLTreeMap.firstLongKey(estore, map, false);
        long lastKey = EstoreAVLTreeMap.lastLongKey(estore, map, true);
        long lastKey2 = EstoreAVLTreeMap.lastLongKey(estore, map, false);
        assertEquals(firstKey, 1);
        assertEquals(lastKey, 100);
        assertEquals(firstKey, firstKey2);
        assertEquals(lastKey, lastKey2);
    }
}
