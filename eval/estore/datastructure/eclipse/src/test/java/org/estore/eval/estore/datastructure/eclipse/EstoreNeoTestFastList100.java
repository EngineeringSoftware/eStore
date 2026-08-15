package org.estore.eval.estore.datastructure.eclipse;

import org.estore.client.ESTORE;
import org.estore.util.Profile;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.estore.client.ESTORE.query;

public class EstoreNeoTestFastList100 {
  private static ThreadLocalRandom rand;
  private FastList<Long> fastList;

  @BeforeEach
  public void setupData() throws Exception {
    Thread.sleep(4000);
    ESTORE.inMemory = false;
    ESTORE.init();
    ESTORE.inMemory = false;
    ESTORE.setWhiteList("estore", "eval/estore/datastructure/eclipse");
    rand = ThreadLocalRandom.current();
    fastList = new FastList();
    for (int j = 0; j < 100; j++) {
      fastList.add(rand.nextLong(0, Long.MAX_VALUE));
    }
  }

  @Profile
  @Test
  public void testFindElement() throws Exception {
    int ind = rand.nextInt(0, fastList.size());
    long t1 = System.nanoTime();
    Object result[] =
        query(
            "MATCH (n {$1})-[:items]->()-[]->(m"
                + " {value:"
                + (long) fastList.get(ind)
                + "}) RETURN m.value",
            fastList);
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result[0]),
        fastList.get(ind),
        "Mismatch in EstoreNeoTestFastList100 for testFindElement");
  }
}
