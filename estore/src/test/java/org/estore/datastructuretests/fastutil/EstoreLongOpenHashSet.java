package org.estore.datastructuretests.fastutil;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.estore.Estore;
import org.estore.planner.util.Table;

public abstract class EstoreLongOpenHashSet {

    public static boolean contains(
            Estore estore, LongOpenHashSet set, long k, boolean defaultMode) {
        if (defaultMode) {
            return set.contains(k);
        } else {
            Table result =
                    estore.query(
                            "MATCH (n:`it.unimi.dsi.fastutil.longs.LongOpenHashSet`)-[:key]->(p {value:"
                                    + k
                                    + "}) RETURN p");
            return result.get("p").size() == 1;
        }
    }
}
