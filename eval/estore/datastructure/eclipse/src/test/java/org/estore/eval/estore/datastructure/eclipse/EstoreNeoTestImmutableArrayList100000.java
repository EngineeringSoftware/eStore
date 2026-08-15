package org.estore.eval.estore.datastructure.eclipse;

import org.estore.client.ESTORE;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.list.immutable.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import org.eclipse.collections.api.list.ImmutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.estore.client.ESTORE.query;

public class EstoreNeoTestImmutableArrayList100000 {
  private static ThreadLocalRandom rand;
  private ImmutableList<Long> immutableArrayList;
  private ArrayList<Long> listData;

  @BeforeEach
  public void setupData() throws Exception {
    Thread.sleep(4000);
    ESTORE.inMemory = false;
    ESTORE.init();
    ESTORE.inMemory = false;
    ESTORE.setWhiteList("estore", "eval/estore/datastructure/eclipse");
    rand = ThreadLocalRandom.current();
    listData = new ArrayList<Long>();
    for (int j = 0; j < 100000; j++) {
      listData.add(rand.nextLong(0, Long.MAX_VALUE));
    }
    immutableArrayList = new ImmutableListFactoryImpl().withAll(listData);
  }

  @Test
  public void testFindElement() throws Exception {
    int ind = rand.nextInt(0, immutableArrayList.size());
    long t1 = System.nanoTime();
    Object result[] =
        query(
            this,
            "MATCH"
                + " (n {$1})-[:items]->()-[]->(m"
                + " {value:"
                + (long) immutableArrayList.get(ind)
                + "}) RETURN m.value",
            immutableArrayList);
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result[0]),
        immutableArrayList.get(ind),
        "Mismatch in EstoreNeoTestImmutableArrayList100000 for testFindElement");
  }
}
