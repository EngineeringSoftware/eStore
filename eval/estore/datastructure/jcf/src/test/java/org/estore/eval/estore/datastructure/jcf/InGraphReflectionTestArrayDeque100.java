package org.estore.eval.estore.datastructure.jcf;

import org.estore.Estore;
import org.estore.EstoreOptions;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayDeque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InGraphReflectionTestArrayDeque100 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private ArrayDeque<Long> list;

  @BeforeEach
  public void setupData() throws Exception {
    rand = ThreadLocalRandom.current();
    list = new ArrayDeque<Long>();
    for (int j = 0; j < 100; j++) {
      list.add(rand.nextLong(0, Long.MAX_VALUE));
    }
    estore = new Estore("testDb", new EstoreOptions().useUnsafe(false));
    estore.captureAll(list);
  }

  @Test
  public void testFindElement() {
    int ind = rand.nextInt(list.size());
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH (n:`java.util.ArrayDeque`)-[:elements]->(m)-[]->(p {value:"
                + ((long) list.toArray()[ind])
                + "}) RETURN p");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertTrue(((Long) result.get("p").get(0)) == list.toArray()[ind]);
  }
}
