package org.estore.eval.estore.datastructure.jcf;

import org.estore.client.ESTORE;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.estore.client.ESTORE.query;

public class EstoreNeoTestArrayList100000 {
  private static ThreadLocalRandom rand;
  private ArrayList<Long> list;

  @BeforeEach
  public void setupData() throws Exception {
    Thread.sleep(4000);
    ESTORE.inMemory = false;
    ESTORE.init();
    ESTORE.inMemory = false;
    ESTORE.clearDatabase = false;
    ESTORE.setWhiteList("estore", "eval/estore/datastructure/jcf", "ArrayList");
    rand = ThreadLocalRandom.current();
    list = new ArrayList<Long>();
    for (int j = 0; j < 100000; j++) {
      list.add(rand.nextLong(0, Long.MAX_VALUE));
    }
  }

  @Test
  public void testFindElement() throws Exception {
    int ind = rand.nextInt(list.size());
    long t1 = System.nanoTime();
    Object result[] =
        query(
            "MATCH (n {$1})-[:elementData]->(m)-[]->(p {value:"
                + ((long) list.get(ind))
                + "}) RETURN p.value",
            list);

    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result[0]), list.get(ind), "Find element failed for EstoreNeoTestArrayList100000");
  }
}
