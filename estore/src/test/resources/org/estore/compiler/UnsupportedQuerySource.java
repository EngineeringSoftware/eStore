package org.estore.compiler;

import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.planner.util.Table;

public class UnsupportedQuerySource {

    void runQuery(Estore estore) throws EstoreException {
        String q = "MATCH (n) RETURN n";
        Table result = estore.query(q);
    }
}
