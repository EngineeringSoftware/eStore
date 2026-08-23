package org.estore.eval.estore.datastructure.eclipse;

import org.estore.Estore;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.estore.EstoreException;

public class InGraphReflectionTestUnifiedMap1000 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private UnifiedMap<Long, Long> unifiedMap;
  private ArrayList<Long> setData;

  @BeforeEach
  public void setupData() throws EstoreException {
    rand = ThreadLocalRandom.current();
    setData = new ArrayList<Long>();
    unifiedMap = new UnifiedMap();
    while (unifiedMap.size() != 1000) {
      long randValue = rand.nextLong(0, Long.MAX_VALUE);
      unifiedMap.put(rand.nextLong(0, Long.MAX_VALUE), randValue);
      setData.add(randValue);
    }
    estore = new Estore("testDb");
    estore.captureAll(unifiedMap);
  }

  @Test
  public void testFindElement() {
    int ind = rand.nextInt(0, setData.size());
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH (n:`org.eclipse.collections.impl.map.mutable.UnifiedMap`)-[:table]->()-[]->(m"
                + " {value:"
                + (long) setData.get(ind)
                + "}) RETURN m");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result.get("m").get(0)),
        setData.get(ind),
        "Mismatch in IngraphReflectionTestUnifiedMap100 for testFindElement");
  }
}
