package org.estore.compiler;

import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.planner.util.Table;

public class MixedConcatQuerySource {

    void runQuery(Estore estore) throws EstoreException {
        String suffix = "(n) RETURN n";
        Table result = estore.query("MATCH " + suffix);
    }
}
