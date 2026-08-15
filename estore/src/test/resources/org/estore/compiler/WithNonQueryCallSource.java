package org.estore.compiler;

import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.planner.util.Table;

public class WithNonQueryCallSource {

    void runQuery(Estore estore) throws EstoreException {
        System.out.println("warmup");
        Table result = estore.query("MATCH (n) RETURN n");
    }
}
