package org.estore.datastructuretests.apachecommons;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PatriciaTrieTest {

    private Estore estore;
    private int size;

    @BeforeEach
    public void initDatabase() throws Exception {
        estore = new Estore(PatriciaTrieTest.class.getName(), new EstoreOptions().useUnsafe(false));
        size = 5;
    }

    //    @Disabled
    @Test
    void testPatriciaTrieSize() throws EstoreException {
        PatriciaTrie<Integer> trie = new PatriciaTrie<Integer>();

        trie.put("LEET", 10);
        trie.put("BEET", 20);
        trie.put("BEAT", 30);
        trie.put("SEAT", 40);
        trie.put("FEAT", 50);

        estore.captureAll(trie);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH"
                                + " (:`org.apache.commons.collections4.trie.AbstractPatriciaTrie$TrieEntry`)"
                                + "-[:predecessor|parent*0..5]->()-[:value]->(n) "
                                + "RETURN COUNT(DISTINCT n) as SIZE");
        // result.print();
        // estore.printLabelMaps();
        long t2 = System.currentTimeMillis();
        assertTrue((Integer) result.get("SIZE").get(0) == size);
        // System.out.println("Size: " + result.get("SIZE").get(0));
        // System.out.println(t2 - t1);
    }
}
