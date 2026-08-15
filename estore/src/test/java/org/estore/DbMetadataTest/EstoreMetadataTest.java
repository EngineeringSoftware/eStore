package org.estore.DbMetadataTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.estore.Estore;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EstoreMetadataTest {
    private Estore estore1, estore2, estore_u1, estore_u2, estore_u3, estore_r1, estore_r2;

    @BeforeEach
    public void setup() throws Exception {
        String unsafeOpt = System.getProperty("useUnsafe");
        Boolean unsafeFlag = (unsafeOpt != null) && (unsafeOpt.equals("true"));
        String profileOpt = System.getProperty("profile");
        Boolean profileFlag = (profileOpt != null) && (profileOpt.equals("true"));

        estore1 =
                new Estore(
                        EstoreMetadataTest.class.getName(),
                        new EstoreOptions().useUnsafe(unsafeFlag).profile(profileFlag));
        estore2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "2",
                        new EstoreOptions().useUnsafe(unsafeFlag).profile(false));
    }

    @Test
    public void testDbName() throws Exception {
        estore1.captureAll(estore2);
        Table res = estore1.query("MATCH (n: `org.estore.Estore`) RETURN n.name");
        assertEquals(EstoreMetadataTest.class.getName() + "2", res.get("n.name").get(0));
    }

    @Test
    public void testDbNameRepeat() throws Exception {
        estore1.captureAll(estore2);
        Table res = null;
        for (int i = 0; i < 5; i++) {
            res = estore1.query("MATCH (n: `org.estore.Estore`) RETURN n.name");
        }
        assertEquals(EstoreMetadataTest.class.getName() + "2", res.get("n.name").get(0));
    }

    @Test
    public void testOptions() throws Exception {
        estore_u1 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Unsafe1",
                        new EstoreOptions().useUnsafe(true));
        estore_u2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Unsafe2",
                        new EstoreOptions().useUnsafe(true));
        estore_u3 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Unsafe3",
                        new EstoreOptions().useUnsafe(true));
        estore_r1 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Reflection1",
                        new EstoreOptions().useUnsafe(false));
        estore_r2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Reflection2",
                        new EstoreOptions().useUnsafe(false));
        estore1.captureAll(estore_u1);
        estore1.captureAll(estore_u2);
        estore1.captureAll(estore_u3);
        estore1.captureAll(estore_r1);
        estore1.captureAll(estore_r2);
        Table res =
                estore1.query(
                        "MATCH (n: `org.estore.Estore`)-[:options]->(m {useUnsafe: true}) RETURN n");
        assertEquals(3, res.getSize());
    }

    @Test
    public void testOptionsRepeat() throws Exception {
        estore_u1 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Unsafe1",
                        new EstoreOptions().useUnsafe(true));
        estore_u2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Unsafe2",
                        new EstoreOptions().useUnsafe(true));
        estore_u3 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Unsafe3",
                        new EstoreOptions().useUnsafe(true));
        estore_r1 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Reflection1",
                        new EstoreOptions().useUnsafe(false));
        estore_r2 =
                new Estore(
                        EstoreMetadataTest.class.getName() + "Reflection2",
                        new EstoreOptions().useUnsafe(false));
        estore1.captureAll(estore_u1);
        estore1.captureAll(estore_u2);
        estore1.captureAll(estore_u3);
        estore1.captureAll(estore_r1);
        estore1.captureAll(estore_r2);
        Table res = null;
        for (int i = 0; i < 5; i++) {
            res =
                    estore1.query(
                            "MATCH (n: `org.estore.Estore`)-[:options]->(m {useUnsafe: true}) RETURN n");
        }
        assertEquals(3, res.getSize());
    }

    @Test
    public void testDynamicClass() throws Exception {
        estore2.query("CREATE (n: `Sample`)");
        estore1.captureAll(estore2);
        // Table res =
        //     estore1.query(
        //         "MATCH (n:"
        //             + "
        // `org.estore.Estore`)-[:dynamicClasses]->(:`java.util.ArrayList`)-[:elementData]->(m"
        //             + " {name: 'Sample'}) RETURN m");
        Table res =
                estore1.query(
                        "MATCH (n:"
                                + " `org.estore.Estore`)-[:dynamicClasses]->()-[:elementData]"
                                + "->(m {name: 'Sample'}) RETURN m");
        assertEquals(1, res.getSize());
    }

    @Test
    public void testDynamicClassRepeat() throws Exception {
        estore2.query("CREATE (n: `Sample`)");
        estore1.captureAll(estore2);
        Table res = null;
        for (int i = 0; i < 5; i++) {
            res =
                    estore1.query(
                            "MATCH (n:"
                                    + " `org.estore.Estore`)-[:dynamicClasses]->()-[:elementData]"
                                    // dynamicClasses is an arraylist of <Class>,
                                    // but Class doesn't have element data
                                    + "->(m {name: 'Sample'}) RETURN m");
        }
        assertEquals(1, res.getSize());
    }
}
