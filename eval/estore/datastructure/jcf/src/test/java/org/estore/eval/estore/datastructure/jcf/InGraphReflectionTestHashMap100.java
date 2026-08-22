package org.estore.eval.estore.datastructure.jcf;

import org.estore.Estore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.estore.EstoreException;

public class InGraphReflectionTestHashMap100 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private HashMap<Long, Long> map;

  @BeforeEach
  public void setupData() throws EstoreException {
    rand = ThreadLocalRandom.current();
    map = new HashMap<Long, Long>();
    while (map.size() != 99) {
      map.put(rand.nextLong(0, Long.MAX_VALUE), rand.nextLong(0, Long.MAX_VALUE));
    }
    map.put(rand.nextLong(0, Long.MAX_VALUE), 90L);
    estore = new Estore("testDb");
    estore.captureAll(map);
  }

  @Test
  public void testFindElement() {
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH (n:`java.util.HashMap`)-[:table]->()-[]->()-[:value]->(p {value:90}) RETURN p");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result.get("p").get(0)),
        90L,
        "Mismatch in InGraphReflectionTestHashMap100 in testFindElement");
  }
}
