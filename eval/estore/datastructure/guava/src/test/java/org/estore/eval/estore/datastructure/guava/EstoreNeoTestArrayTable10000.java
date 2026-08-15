package org.estore.eval.estore.datastructure.guava;

import org.estore.client.ESTORE;
import org.estore.util.Profile;
import java.util.concurrent.ThreadLocalRandom;
import com.google.common.collect.ArrayTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.estore.client.ESTORE.query;

public class EstoreNeoTestArrayTable10000 {
  private static ThreadLocalRandom rand;
  private ArrayTable<Long, Long, Long> table;

  @BeforeEach
  public void setupData() throws Exception {
    Thread.sleep(4000);
    ESTORE.inMemory = false;
    ESTORE.init();
    ESTORE.inMemory = false;
    ESTORE.setWhiteList("estore", "eval/estore/datastructure/guava");

    rand = ThreadLocalRandom.current();
    ArrayList<Long> rows = new ArrayList<Long>();
    ArrayList<Long> columns = new ArrayList<Long>();
    for (long j = 0; j < 100; j++) {
      rows.add(j);
      columns.add(j);
    }
    table = ArrayTable.create(rows, columns);
    for (long j = 0; j < 100; j++) {
      for (long k = 0; k < 100; k++) {
        table.put(j, k, rand.nextLong(0, Long.MAX_VALUE));
      }
    }
  }

  @Profile
  @Test
  public void testFindElement() throws Exception {
    long ind = rand.nextLong(0, 100);
    long ind2 = rand.nextLong(0, 100);
    long t1 = System.nanoTime();
    Object result[] =
        query(
            "MATCH (n {$1})-[:array]->()-[]->()-[]->(m {value:"
                + table.get(ind, ind2)
                + "}) RETURN m.value",
            table);
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result[0]),
        table.get(ind, ind2),
        "Mismatch in EstoreNeoTestArrayTable10000 for testFindElement");
  }
}
