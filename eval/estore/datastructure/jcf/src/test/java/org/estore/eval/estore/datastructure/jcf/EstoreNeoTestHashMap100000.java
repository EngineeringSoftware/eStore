package org.estore.eval.estore.datastructure.jcf;

import org.estore.client.ESTORE;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.estore.client.ESTORE.query;

public class EstoreNeoTestHashMap100000 {
  private static ThreadLocalRandom rand;
  private HashMap<Long, Long> map;

  @BeforeEach
  public void setupData() throws Exception {
    Thread.sleep(4000);
    ESTORE.inMemory = false;
    ESTORE.init();
    ESTORE.inMemory = false;
    ESTORE.printCSV = true;
    ESTORE.setWhiteList("estore", "eval/estore/datastructure/jcf");
    rand = ThreadLocalRandom.current();
    map = new HashMap<Long, Long>();
    while (map.size() != 99999) {
      map.put(rand.nextLong(0, Long.MAX_VALUE), rand.nextLong(0, Long.MAX_VALUE));
    }
    map.put(rand.nextLong(0, Long.MAX_VALUE), 90L);
  }

  @Test
  public void testFindElement() throws Exception {
    long t1 = System.nanoTime();
    Object result[] =
        query(
            this,
            "MATCH (n {$1})-[:table]->()-[]->()-[:value]->(p {value:90}) RETURN" + " p.value",
            map);

    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(((Long) result[0]), 90L, "Find element failed for EstoreNeoTestHashMap100000");
  }
}
