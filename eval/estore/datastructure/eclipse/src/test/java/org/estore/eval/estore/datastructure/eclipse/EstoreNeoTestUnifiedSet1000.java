package org.estore.eval.estore.datastructure.eclipse;

import org.estore.client.ESTORE;
import org.estore.util.Profile;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.estore.client.ESTORE.query;

public class EstoreNeoTestUnifiedSet1000 {
  private static ThreadLocalRandom rand;
  private UnifiedSet<Long> unifiedSet;
  private ArrayList<Long> setData;

  @BeforeEach
  public void setupData() throws Exception {
    Thread.sleep(4000);
    ESTORE.inMemory = false;
    ESTORE.init();
    ESTORE.inMemory = false;
    ESTORE.setWhiteList("estore", "eval/estore/datastructure/eclipse");
    rand = ThreadLocalRandom.current();
    setData = new ArrayList<Long>();
    for (int j = 0; j < 1000; j++) {
      setData.add(rand.nextLong(0, Long.MAX_VALUE));
    }
    unifiedSet = new UnifiedSet(setData);
  }

  @Profile
  @Test
  public void testFindElement() throws Exception {
    int ind = rand.nextInt(0, setData.size());
    long t1 = System.nanoTime();
    Object result[] =
        query(
            this,
            "MATCH (n {$1})-[:table]->()-[]->(m"
                + " {value:"
                + (long) setData.get(ind)
                + "}) RETURN m.value",
            unifiedSet);
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result[0]),
        setData.get(ind),
        "Mismatch in EstoreNeoTestUnifiedSet1000 for testFindElement");
  }
}
