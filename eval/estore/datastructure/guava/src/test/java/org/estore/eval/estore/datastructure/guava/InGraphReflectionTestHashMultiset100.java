package org.estore.eval.estore.datastructure.guava;

import org.estore.Estore;
import org.estore.EstoreOptions;
import java.util.concurrent.ThreadLocalRandom;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.HashMultimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import org.estore.planner.util.Table;

public class InGraphReflectionTestHashMultiset100 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private HashMultiset<Long> multiSet;
  private HashMultimap<Long, Long> multiMap;

  @BeforeEach
  public void setupData() throws Exception {
    rand = ThreadLocalRandom.current();
    ArrayList<Long> values = new ArrayList<Long>();
    multiMap = HashMultimap.create();
    for (int j = 0; j < 100; j++) {
      values.add(rand.nextLong(0, Long.MAX_VALUE));
      multiMap.put(rand.nextLong(0, Long.MAX_VALUE), rand.nextLong(0, Long.MAX_VALUE));
    }
    multiSet = HashMultiset.create(values);
    estore = new Estore("testDb");
    estore.captureAll(multiMap);
  }

  @Test
  public void testFindElement() {
    int ind = rand.nextInt(0, multiSet.size());
    long t1 = System.nanoTime();
    estore.printLabelMaps();
    System.out.println(multiMap.size());
    Table result =
        estore.query("MATCH (n:`com.google.common.collect.HashMultimap`)-[:map]->(m) RETURN m");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    result.print();
    /*assertEquals(
    ((Long) result.get("m").get(0)),
    table.get(ind, ind2),
    "Mismatch in IngraphReflectionTestArrayTable100 for testFindElement");*/
  }
}
