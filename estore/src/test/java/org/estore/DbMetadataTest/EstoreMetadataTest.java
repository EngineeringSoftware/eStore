package org.estore.DbMetadataTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EstoreMetadataTest {
    private Estore estore1, estore2, estore_d1, estore_d2, estore_d3, estore_b1, estore_b2;

    @BeforeEach
    public void setup() {
        String profileOpt = System.getProperty("profile");
        Boolean profileFlag = (profileOpt != null) && (profileOpt.equals("true"));

        estore1 =
                new Estore(
                        EstoreMetadataTest.class.getName(),
                        new EstoreOptions().profile(profileFlag));
        estore2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "2",
                        new EstoreOptions().profile(false));
    }

    @Test
    public void testDbName() throws EstoreException {
        estore1.captureAll(estore2);
        Table res = estore1.query("MATCH (n: `org.estore.Estore`) RETURN n.name");
        assertEquals(EstoreMetadataTest.class.getName() + "2", res.get("n.name").get(0));
    }

    @Test
    public void testDbNameRepeat() throws EstoreException {
        estore1.captureAll(estore2);
        Table res = null;
        for (int i = 0; i < 5; i++) {
            res = estore1.query("MATCH (n: `org.estore.Estore`) RETURN n.name");
        }
        assertEquals(EstoreMetadataTest.class.getName() + "2", res.get("n.name").get(0));
    }

    @Test
    public void testOptions() throws EstoreException {
        estore_d1 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Dfs1",
                        new EstoreOptions().useDfs(true));
        estore_d2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Dfs2",
                        new EstoreOptions().useDfs(true));
        estore_d3 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Dfs3",
                        new EstoreOptions().useDfs(true));
        estore_b1 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Bfs1",
                        new EstoreOptions().useDfs(false));
        estore_b2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Bfs2",
                        new EstoreOptions().useDfs(false));
        estore1.captureAll(estore_d1);
        estore1.captureAll(estore_d2);
        estore1.captureAll(estore_d3);
        estore1.captureAll(estore_b1);
        estore1.captureAll(estore_b2);
        Table res =
                estore1.query(
                        "MATCH (n: `org.estore.Estore`)-[:options]->(m {useDfs: true}) RETURN n");
        assertEquals(3, res.getSize());
    }

    @Test
    public void testOptionsRepeat() throws EstoreException {
        estore_d1 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Dfs1",
                        new EstoreOptions().useDfs(true));
        estore_d2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Dfs2",
                        new EstoreOptions().useDfs(true));
        estore_d3 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Dfs3",
                        new EstoreOptions().useDfs(true));
        estore_b1 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Bfs1",
                        new EstoreOptions().useDfs(false));
        estore_b2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Bfs2",
                        new EstoreOptions().useDfs(false));
        estore1.captureAll(estore_d1);
        estore1.captureAll(estore_d2);
        estore1.captureAll(estore_d3);
        estore1.captureAll(estore_b1);
        estore1.captureAll(estore_b2);
        Table res = null;
        for (int i = 0; i < 5; i++) {
            res =
                    estore1.query(
                            "MATCH (n: `org.estore.Estore`)-[:options]->(m {useDfs: true}) RETURN n");
        }
        assertEquals(3, res.getSize());
    }

    @Test
    public void testDynamicClass() throws EstoreException {
        estore2.query("CREATE (n: `Sample`)");
        estore1.captureAll(estore2);
        Table res =
                estore1.query(
                        "MATCH (n:"
                                + " `org.estore.Estore`)-[:dynamicClasses]->()-[:elementData]"
                                + "->(m {name: 'Sample'}) RETURN m");
        assertEquals(1, res.getSize());
    }

    @Test
    public void testDynamicClassRepeat() throws EstoreException {
        estore2.query("CREATE (n: `Sample`)");
        estore1.captureAll(estore2);
        Table res = null;
        for (int i = 0; i < 5; i++) {
            res =
                    estore1.query(
                            "MATCH (n:"
                                    + " `org.estore.Estore`)-[:dynamicClasses]->()-[:elementData]"
                                    + "->(m {name: 'Sample'}) RETURN m");
        }
        assertEquals(1, res.getSize());
    }
}
