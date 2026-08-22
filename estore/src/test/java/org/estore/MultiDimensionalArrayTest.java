package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Array;
import java.util.concurrent.ThreadLocalRandom;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiDimensionalArrayTest {

    private Estore db;
    private ThreadLocalRandom rand;

    @BeforeEach
    public void setup() {
        rand = ThreadLocalRandom.current();
        db = new Estore(MultiDimensionalArrayTest.class.getName());
    }

    @Test
    public void testSimple3DMatrix() throws EstoreException {
        String[][][] m = new String[][][] {{{"a", "b"}, {"c", "d"}}, {{"e", "f"}, {"g", "h"}}};
        db.captureAll(m);

        Table result =
                db.query(
                        "MATCH (n:`"
                                + m.getClass().getName()
                                + "`)-[:`1`]->()-[:`0`]->()-[:`0`]->(x) RETURN x");
        assertEquals("e", (String) result.get("x").get(0));
    }

    @Test
    public void testLongMatrix2DVarLength() throws EstoreException {
        Long[][] grid = new Long[10][10];
        long target = rand.nextLong(0, Long.MAX_VALUE);
        int ti = 3;
        int tj = 7;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = (i == ti && j == tj) ? target : rand.nextLong(0, Long.MAX_VALUE);
            }
        }
        db.captureAll(grid);

        Table result =
                db.query(
                        "MATCH (n:`"
                                + grid.getClass().getName()
                                + "`)-[]->()-[]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Long) result.get("m").get(0)).longValue());
    }

    @Test
    public void testLongMatrix2DIndexed() throws EstoreException {
        Long[][] grid = new Long[10][10];
        long target = rand.nextLong(0, Long.MAX_VALUE);
        int ti = 2;
        int tj = 5;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = (i == ti && j == tj) ? target : rand.nextLong(0, Long.MAX_VALUE);
            }
        }
        db.captureAll(grid);

        Table result =
                db.query(
                        "MATCH (n:`"
                                + grid.getClass().getName()
                                + "`)-[]->()-[:`"
                                + tj
                                + "`]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Long) result.get("m").get(0)).longValue());
    }

    @Test
    public void testIntMatrix2DVarLength() throws EstoreException {
        int[][] grid = new int[8][8];
        int target = 4242;
        int ti = 1;
        int tj = 4;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = (i == ti && j == tj) ? target : rand.nextInt(0, 10000);
            }
        }
        db.captureAll(grid);

        Table result =
                db.query(
                        "MATCH (n:`"
                                + grid.getClass().getName()
                                + "`)-[]->()-[]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Integer) result.get("m").get(0)).intValue());
    }

    @Test
    public void testIntMatrix2DIndexed() throws EstoreException {
        int[][] grid = new int[8][8];
        int target = 7777;
        int ti = 0;
        int tj = 6;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = (i == ti && j == tj) ? target : rand.nextInt(0, 10000);
            }
        }
        db.captureAll(grid);

        Table result =
                db.query(
                        "MATCH (n:`"
                                + grid.getClass().getName()
                                + "`)-[]->()-[:`"
                                + tj
                                + "`]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Integer) result.get("m").get(0)).intValue());
    }

    @Test
    public void testObjectMatrix3DVarLength() throws EstoreException {
        Object[][][] cube = new Object[4][4][4];
        long target = rand.nextLong(0, Long.MAX_VALUE);
        int a = 1;
        int b = 2;
        int c = 3;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    cube[i][j][k] =
                            (i == a && j == b && k == c)
                                    ? target
                                    : rand.nextLong(0, Long.MAX_VALUE);
                }
            }
        }
        db.captureAll(cube);

        Table result =
                db.query(
                        "MATCH (n:`"
                                + cube.getClass().getName()
                                + "`)-[]->()-[]->()-[]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Long) result.get("m").get(0)).longValue());
    }

    @Test
    public void testObjectMatrix3DIndexed() throws EstoreException {
        Object[][][] cube = new Object[3][3][3];
        long target = rand.nextLong(0, Long.MAX_VALUE);
        int a = 0;
        int b = 1;
        int c = 2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    cube[i][j][k] =
                            (i == a && j == b && k == c)
                                    ? target
                                    : rand.nextLong(0, Long.MAX_VALUE);
                }
            }
        }
        db.captureAll(cube);

        // Reach plane and row with unlabeled hops, then index into the Long cell.
        Table result =
                db.query(
                        "MATCH (n:`"
                                + cube.getClass().getName()
                                + "`)-[]->()-[]->()-[:`"
                                + c
                                + "`]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Long) result.get("m").get(0)).longValue());
    }

    @Test
    public void testCaptureInsertsAllDims() throws EstoreException {
        Long[][] grid = new Long[2][2];
        grid[0][0] = 1L;
        grid[0][1] = 2L;
        grid[1][0] = 3L;
        grid[1][1] = 4L;
        db.captureAll(grid);

        assertNotNull(db.getLabelObjectMap().get("[[Ljava.lang.Long;"));
        assertNotNull(db.getLabelObjectMap().get("[Ljava.lang.Long;"));
        assertNotNull(db.getLabelObjectMap().get("java.lang.Long"));
        assertTrue(db.getLabelObjectMap().get("[[Ljava.lang.Long;").size() >= 1);
        assertTrue(db.getLabelObjectMap().get("[Ljava.lang.Long;").size() >= 2);
        assertTrue(db.getLabelObjectMap().get("java.lang.Long").size() >= 4);
    }

    @Test
    public void testDeleteArrayIndex() throws EstoreException {
        Long[] arr = new Long[] {10L, 20L, 30L};
        db.captureAll(arr);

        db.query("MATCH ()-[r:`1`]->(m) DELETE r RETURN m");
        assertNull(Array.get(arr, 1));
        assertEquals(10L, ((Long) Array.get(arr, 0)).longValue());
        assertEquals(30L, ((Long) Array.get(arr, 2)).longValue());
    }

    @Test
    public void testArrayTable() throws EstoreException {
        Estore gridStore = new Estore(MultiDimensionalArrayTest.class.getName() + "Unsafe");
        Long[][] grid = new Long[10][10];
        long target = rand.nextLong(0, Long.MAX_VALUE);
        int ti = 4;
        int tj = 6;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = (i == ti && j == tj) ? target : rand.nextLong(0, Long.MAX_VALUE);
            }
        }
        gridStore.captureAll(grid);

        Table result =
                gridStore.query(
                        "MATCH (n:`"
                                + grid.getClass().getName()
                                + "`)-[]->()-[]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Long) result.get("m").get(0)).longValue());
    }

    @Test
    public void testArrayTableDfs() throws EstoreException {
        Estore dfsStore =
                new Estore(
                        MultiDimensionalArrayTest.class.getName() + "Dfs",
                        new EstoreOptions().useDfs(true));
        Long[][] grid = new Long[10][10];
        long target = rand.nextLong(0, Long.MAX_VALUE);
        int ti = 4;
        int tj = 6;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = (i == ti && j == tj) ? target : rand.nextLong(0, Long.MAX_VALUE);
            }
        }
        dfsStore.captureAll(grid);

        Table result =
                dfsStore.query(
                        "MATCH (n:`"
                                + grid.getClass().getName()
                                + "`)-[]->()-[]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Long) result.get("m").get(0)).longValue());
    }

    @Test
    public void testIntMatrix2DDfs() throws EstoreException {
        Estore dfsStore =
                new Estore(
                        MultiDimensionalArrayTest.class.getName() + "IntDfs",
                        new EstoreOptions().useDfs(true));
        int[][] grid = new int[8][8];
        int target = 4242;
        int ti = 1;
        int tj = 4;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = (i == ti && j == tj) ? target : rand.nextInt(0, 10000);
            }
        }
        dfsStore.captureAll(grid);

        Table result =
                dfsStore.query(
                        "MATCH (n:`"
                                + grid.getClass().getName()
                                + "`)-[]->()-[]->(m {value:"
                                + target
                                + "}) RETURN m");
        assertEquals(target, ((Integer) result.get("m").get(0)).intValue());
    }
}
