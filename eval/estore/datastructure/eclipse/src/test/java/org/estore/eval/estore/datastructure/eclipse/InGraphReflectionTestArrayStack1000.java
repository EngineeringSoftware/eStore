package org.estore.eval.estore.datastructure.eclipse;

import org.estore.Estore;
import org.estore.EstoreOptions;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InGraphReflectionTestArrayStack1000 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private ArrayStack<Long> arrayStack;
  private ArrayList<Long> stackData;

  @BeforeEach
  public void setupData() throws Exception {
    rand = ThreadLocalRandom.current();
    arrayStack = new ArrayStack();
    stackData = new ArrayList<Long>();
    for (int j = 0; j < 1000; j++) {
      long randValue = rand.nextLong(0, Long.MAX_VALUE);
      stackData.add(randValue);
      arrayStack.push(randValue);
    }
    estore = new Estore("testDb");
    estore.captureAll(arrayStack);
  }

  @Test
  public void testFindElement() {
    int ind = rand.nextInt(0, stackData.size());
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH"
                + " (n:`org.eclipse.collections.impl.stack.mutable.ArrayStack`)-[:delegate]->()-[:items]->()-[]->(m"
                + " {value:"
                + (long) stackData.get(ind)
                + "}) RETURN m");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result.get("m").get(0)),
        stackData.get(ind),
        "Mismatch in IngraphReflectionTestArrayStack1000 for testFindElement");
  }
}
