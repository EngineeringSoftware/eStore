package org.estore.eval.datastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ArrayTable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import org.estore.Estore;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GuavaTest {
    private Estore estore;
    private static ThreadLocalRandom rand;

    @BeforeEach
    public void setup() throws Exception {
        rand = ThreadLocalRandom.current();
        estore = new Estore(GuavaTest.class.getName(), new EstoreOptions().useUnsafe(false));
    }

    @Test
    public void testArrayTable() throws Exception {
        ArrayList<Long> rows = new ArrayList<Long>();
        ArrayList<Long> columns = new ArrayList<Long>();
        for (long j = 0; j < 10; j++) {
            rows.add(j);
            columns.add(j);
        }
        ArrayTable<Long, Long, Long> table = ArrayTable.create(rows, columns);
        for (long j = 0; j < 10; j++) {
            for (long k = 0; k < 10; k++) {
                table.put(j, k, rand.nextLong(0, Long.MAX_VALUE));
            }
        }
        estore.captureAll(table);

        long ind = rand.nextLong(0, 10);
        long ind2 = rand.nextLong(0, 10);
        long t1 = System.nanoTime();
        Table result =
                estore.query(
                        "MATCH (n:`com.google.common.collect.ArrayTable`)-[:array]->()-[]->(m {value:"
                                + table.get(ind, ind2)
                                + "}) RETURN m");
        // System.out.println("Execution Time : " + (System.nanoTime() - t1));
        assertEquals(
                ((Long) result.get("m").get(0)),
                table.get(ind, ind2),
                "Mismatch in IngraphReflectionTestArrayTable100 for testFindElement");
    }
}
