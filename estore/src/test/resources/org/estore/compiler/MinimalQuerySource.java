package org.estore.compiler;

import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.planner.util.Table;

public class MinimalQuerySource {

    void runQuery(Estore estore) throws EstoreException {
        Table result = estore.query("MATCH (n) RETURN n");
    }
}
