package org.estore.eval.estore.datastructure.jcf;

import org.estore.client.ESTORE;
import org.estore.util.Profile;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayDeque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.estore.client.ESTORE.query;

public class EstoreNeoTestArrayDeque100 {
  private static ThreadLocalRandom rand;
  private ArrayDeque<Long> list;

  @BeforeEach
  public void setupData() throws Exception {
    Thread.sleep(4000);
    ESTORE.inMemory = false;
    ESTORE.init();
    ESTORE.inMemory = false;
    ESTORE.setWhiteList("estore", "eval/estore/datastructure/jcf");
    rand = ThreadLocalRandom.current();
    list = new ArrayDeque<Long>();
    for (int j = 0; j < 100; j++) {
      list.add(rand.nextLong(0, Long.MAX_VALUE));
    }
  }

  @Profile
  @Test
  public void testFindElement() throws Exception {
    int ind = rand.nextInt(list.size());
    long t1 = System.nanoTime();
    Object result[] =
        query(
            this,
            "MATCH (n {$1})-[:elements]->(m)-[*]->(p {value:"
                + ((long) list.toArray()[ind])
                + "}) RETURN p.value",
            list);

    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result[0]), list.toArray()[ind], "Find element failed for EstoreNeoTestArrayDeque100");
  }
}
