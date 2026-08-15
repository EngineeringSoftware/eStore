package org.estore.eval.estore.datastructure.eclipse;

import org.estore.Estore;
import org.estore.EstoreOptions;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.list.immutable.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.planner.util.Table;
import java.util.ArrayList;
import org.eclipse.collections.api.list.ImmutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InGraphReflectionTestImmutableArrayList1000 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private ImmutableList<Long> immutableArrayList;
  private ArrayList<Long> listData;

  @BeforeEach
  public void setupData() throws Exception {
    rand = ThreadLocalRandom.current();
    listData = new ArrayList<Long>();
    for (int j = 0; j < 1000; j++) {
      listData.add(rand.nextLong(0, Long.MAX_VALUE));
    }
    immutableArrayList = new ImmutableListFactoryImpl().withAll(listData);
    estore = new Estore("testDb", new EstoreOptions().useUnsafe(false));
    estore.captureAll(immutableArrayList);
  }

  @Test
  public void testFindElement() {
    int ind = rand.nextInt(0, immutableArrayList.size());
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH"
                + " (n:`org.eclipse.collections.impl.list.immutable.ImmutableArrayList`)-[:items]->()-[]->(m"
                + " {value:"
                + (long) immutableArrayList.get(ind)
                + "}) RETURN m");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result.get("m").get(0)),
        immutableArrayList.get(ind),
        "Mismatch in IngraphReflectionTestImmutableArrayList1000 for testFindElement");
  }
}
