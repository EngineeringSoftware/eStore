package org.estore.eval.estore.datastructure.guava;

import org.estore.Estore;
import java.util.concurrent.ThreadLocalRandom;
import com.google.common.collect.ArrayTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import org.estore.planner.util.Table;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InGraphUnsafeTestArrayTable100 {
  private Estore estore;
  private static ThreadLocalRandom rand;
  private ArrayTable<Long, Long, Long> table;

  @BeforeEach
  public void setupData() throws Exception {
    rand = ThreadLocalRandom.current();
    ArrayList<Long> rows = new ArrayList<Long>();
    ArrayList<Long> columns = new ArrayList<Long>();
    for (long j = 0; j < 10; j++) {
      rows.add(j);
      columns.add(j);
    }
    table = ArrayTable.create(rows, columns);
    for (long j = 0; j < 10; j++) {
      for (long k = 0; k < 10; k++) {
        table.put(j, k, rand.nextLong(0, Long.MAX_VALUE));
      }
    }
    estore = new Estore("testDb");
    estore.captureAll(table);
  }

  @Test
  public void testFindElement() {
    long ind = rand.nextLong(0, 10);
    long ind2 = rand.nextLong(0, 10);
    long t1 = System.nanoTime();
    Table result =
        estore.query(
            "MATCH (n:`com.google.common.collect.ArrayTable`)-[:array]->()-[]->()-[]->(m {value:"
                + table.get(ind, ind2)
                + "}) RETURN m");
    System.out.println("Execution Time : " + (System.nanoTime() - t1));
    assertEquals(
        ((Long) result.get("m").get(0)),
        table.get(ind, ind2),
        "Mismatch in IngraphUnsafeTestArrayTable100 for testFindElement");
  }
}
