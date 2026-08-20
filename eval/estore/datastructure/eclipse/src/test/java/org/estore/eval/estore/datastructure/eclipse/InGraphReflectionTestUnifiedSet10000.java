package org.estore.eval.estore.datastructure.eclipse;

import org.estore.Estore;
import org.estore.EstoreOptions;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InGraphReflectionTestUnifiedSet10000 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private UnifiedSet<Long> unifiedSet;
  private ArrayList<Long> setData;

  @BeforeEach
  public void setupData() throws Exception {
    rand = ThreadLocalRandom.current();
    setData = new ArrayList<Long>();
    for (int j = 0; j < 10000; j++) {
      setData.add(rand.nextLong(0, Long.MAX_VALUE));
    }
    unifiedSet = new UnifiedSet(setData);
    estore = new Estore("testDb");
    estore.captureAll(unifiedSet);
  }

  @Test
  public void testFindElement() {
    int ind = rand.nextInt(0, setData.size());
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH (n:`org.eclipse.collections.impl.set.mutable.UnifiedSet`)-[:table]->()-[]->(m"
                + " {value:"
                + (long) setData.get(ind)
                + "}) RETURN m");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result.get("m").get(0)),
        setData.get(ind),
        "Mismatch in IngraphReflectionTestUnifiedSet10000 for testFindElement");
  }
}
