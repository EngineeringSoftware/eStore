package org.estore.eval.estore.datastructure.eclipse;

import org.estore.client.ESTORE;
import org.estore.util.Profile;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.estore.client.ESTORE.query;

public class EstoreNeoTestArrayStack10000 {
  private static ThreadLocalRandom rand;
  private ArrayStack<Long> arrayStack;
  private ArrayList<Long> stackData;

  @BeforeEach
  public void setupData() throws Exception {
    Thread.sleep(4000);
    ESTORE.inMemory = false;
    ESTORE.init();
    ESTORE.inMemory = false;
    ESTORE.setWhiteList("estore", "eval/estore/datastructure/eclipse");
    rand = ThreadLocalRandom.current();
    arrayStack = new ArrayStack();
    stackData = new ArrayList<Long>();
    for (int j = 0; j < 10000; j++) {
      long randValue = rand.nextLong(0, Long.MAX_VALUE);
      stackData.add(randValue);
      arrayStack.push(randValue);
    }
  }

  @Profile
  @Test
  public void testFindElement() throws Exception {
    int ind = rand.nextInt(0, stackData.size());
    long t1 = System.nanoTime();
    Object result[] =
        query(
            "MATCH"
                + " (n {$1})-[:delegate]->()-[:items]->()-[]->(m"
                + " {value:"
                + (long) stackData.get(ind)
                + "}) RETURN m.value",
            arrayStack);
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result[0]),
        stackData.get(ind),
        "Mismatch in EstoreNeoTestArrayStack10000 for testFindElement");
  }
}
