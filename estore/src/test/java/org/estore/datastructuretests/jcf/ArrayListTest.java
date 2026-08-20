package org.estore.datastructuretests.jcf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArrayListTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() throws Exception {
        estore = new Estore(ArrayListTest.class.getName());
        size = 10;
    }

    @Test
    void testArrayListSize() throws EstoreException {
        ArrayList<Integer> intList = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            intList.add(i);
        }
        estore.captureAll(intList);
        Table result =
                estore.query(
                        "MATCH (:`[Ljava.lang.Object;`)-[n]->() "
                                + "WHERE type(n) <> 'instanceof' RETURN COUNT(DISTINCT n) AS SIZE");
        assertTrue((Integer) result.get("SIZE").get(0) == size);
    }
}
