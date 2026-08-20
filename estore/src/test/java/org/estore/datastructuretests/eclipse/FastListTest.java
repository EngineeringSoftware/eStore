package org.estore.datastructuretests.eclipse;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FastListTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() throws Exception {
        estore = new Estore(FastListTest.class.getName());
        size = 10;
    }

    @Test
    void testFastListSize() throws EstoreException {
        FastList<Integer> list = new FastList<Integer>();

        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        estore.captureAll(list);
        Table result =
                estore.query(
                        "MATCH"
                                + " (:`[Ljava.lang.Object;`)-[n]->()"
                                + " WHERE type(n) <> 'instanceof' RETURN COUNT(DISTINCT n) AS SIZE");
        assertTrue((Integer) result.get("SIZE").get(0) == size);
    }
}
