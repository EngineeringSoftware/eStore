package org.estore.datastructuretests.apachecommons;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.list.GrowthList;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GrowthListTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() {
        estore = new Estore(GrowthListTest.class.getName());
        size = 10;
    }

    @Test
    void testGrowthListSize() throws EstoreException {
        GrowthList<Integer> list = new GrowthList<Integer>();

        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        estore.captureAll(list);
        Table result =
                estore.query(
                        "MATCH (:`[Ljava.lang.Object;`)-[n]->() "
                                + "WHERE type(n) <> 'instanceof' "
                                + "RETURN "
                                + "CASE "
                                + "    WHEN COUNT(n) <> 0 THEN toInteger(type(max(n)))+1 "
                                + "    ELSE 0 "
                                + "END as SIZE");
        assertTrue((Integer) result.get("SIZE").get(0) == size);
    }
}
