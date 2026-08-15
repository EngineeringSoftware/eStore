package org.estore.datastructuretests.fastutil;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.estore.Estore;
import org.estore.planner.util.Table;

public abstract class EstoreLongArrayList {

    public static boolean contains(Estore estore, LongArrayList list, long k, boolean defaultMode) {
        if (defaultMode) {
            return list.contains(k);
        } else {
            Table result =
                    estore.query(
                            "MATCH (n:`it.unimi.dsi.fastutil.longs.LongArrayList`)-[:a]->(p {value:"
                                    + k
                                    + "}) RETURN p");
            return result.get("p").size() != 0;
        }
    }
}
