package org.estore.eval.estore.datastructure.jcf;

import org.estore.Estore;
import java.util.Random;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InGraphUnsafeTestArrayList1000 {
  private Estore estore;
  private Random rand;
  private ArrayList<Long> list;

  @BeforeEach
  public void setupData() throws Exception {
    rand = new Random();
    list = new ArrayList<Long>();
    for (int j = 0; j < 1000; j++) {
      list.add(rand.nextLong());
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
    assertEquals(
        ((Long) result.get("p").get(0)),
        list.get(ind),
        "Mismatch in expected value in InGraphUnsafeTestArrayList1000");
  }
}
