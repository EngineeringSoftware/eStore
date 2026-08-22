package org.estore.eval.estore.datastructure.jcf;

import org.estore.Estore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.estore.EstoreException;

public class InGraphReflectionTestArrayList10000 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private ArrayList<Long> list;

  @BeforeEach
  public void setupData() throws EstoreException {
    rand = ThreadLocalRandom.current();
    list = new ArrayList<Long>();
    for (int j = 0; j < 10000; j++) {
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
            "MATCH (n:`java.util.ArrayList`)-[:elementData]->(m)-[]->(p {value:"
                + ((long) list.get(ind))
                + "}) RETURN p");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertTrue(((Long) result.get("p").get(0)) == list.get(ind));
  }
}
