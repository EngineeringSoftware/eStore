package org.estore.datastructuretests.fastutil;

import it.unimi.dsi.fastutil.longs.Long2IntAVLTreeMap;
import org.estore.Estore;
import org.estore.planner.util.Table;

public class EstoreAVLTreeMap {

    public static long firstLongKey(Estore estore, Long2IntAVLTreeMap map, boolean defaultMode) {
        if (defaultMode) {
            return map.firstLongKey();
        } else {
            Table result =
                    estore.query(
                            "MATCH (n:`it.unimi.dsi.fastutil.longs.Long2IntAVLTreeMap`)-[:firstEntry]->(m)"
                                    + " return m");
            return Long.parseLong(result.get("m").get(0).toString().split("=>")[0]);
        }
    }

    public static long lastLongKey(Estore estore, Long2IntAVLTreeMap map, boolean defaultMode) {
        if (defaultMode) {
            return map.lastLongKey();
        } else {
            Table result =
                    estore.query(
                            "MATCH (n:`it.unimi.dsi.fastutil.longs.Long2IntAVLTreeMap`)-[:lastEntry]->(m)"
                                    + " return m");
            return Long.parseLong(result.get("m").get(0).toString().split("=>")[0]);
        }
    }
}
