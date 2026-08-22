package org.estore.eval.estore.datastructure.jcf;

import org.estore.Estore;
import org.estore.EstoreOptions;
import java.util.concurrent.ThreadLocalRandom;
import java.util.LinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InGraphReflectionTestLinkedList1000 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private LinkedList<Long> list;

  @BeforeEach
  public void setupData() throws Exception {
    rand = ThreadLocalRandom.current();
    list = new LinkedList<Long>();
    for (int j = 0; j < 1000; j++) {
      list.add(rand.nextLong(0, Long.MAX_VALUE));
    }
    estore = new Estore("testDb");
    estore.captureAll(list);
  }

  @Test
  public void testFindElement() {
    int ind = rand.nextInt(list.size());
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH (n:`java.util.LinkedList`)-[:*1.."
                + list.size()
                + "]->(m)-[:item]->(p {value:"
                + ((long) list.get(ind))
                + "}) RETURN p");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertTrue(((Long) result.get("p").get(0)) == list.get(ind));
  }
}
