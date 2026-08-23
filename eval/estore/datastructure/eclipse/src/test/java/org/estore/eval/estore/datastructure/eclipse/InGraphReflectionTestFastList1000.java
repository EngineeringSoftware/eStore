package org.estore.eval.estore.datastructure.eclipse;

import org.estore.Estore;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.estore.EstoreException;

public class InGraphReflectionTestFastList1000 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private FastList<Long> fastList;

  @BeforeEach
  public void setupData() throws EstoreException {
    rand = ThreadLocalRandom.current();
    fastList = new FastList();
    for (int j = 0; j < 1000; j++) {
      fastList.add(rand.nextLong(0, Long.MAX_VALUE));
    }
    estore = new Estore("testDb");
    estore.captureAll(fastList);
  }

  @Test
  public void testFindElement() {
    int ind = rand.nextInt(0, fastList.size());
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH (n:`org.eclipse.collections.impl.list.mutable.FastList`)-[:items]->()-[]->(m"
                + " {value:"
                + (long) fastList.get(ind)
                + "}) RETURN m");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result.get("m").get(0)),
        fastList.get(ind),
        "Mismatch in IngraphReflectionTestFastList1000 for testFindElement");
  }
}
