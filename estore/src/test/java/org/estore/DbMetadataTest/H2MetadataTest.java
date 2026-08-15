package org.estore.DbMetadataTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import org.estore.Estore;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class H2MetadataTest {
    private Connection h2Conn1;
    private Estore estore1;

    @BeforeEach
    public void setup() throws Exception {
        String unsafeOpt = System.getProperty("useUnsafe");
        Boolean unsafeFlag = (unsafeOpt != null) && (unsafeOpt.equals("true"));
        String profileOpt = System.getProperty("profile");
        Boolean profileFlag = (profileOpt != null) && (profileOpt.equals("true"));

        estore1 =
                new Estore(
                        H2MetadataTest.class.getName(),
                        new EstoreOptions().useUnsafe(unsafeFlag).profile(profileFlag));
        Class.forName("org.h2.Driver");
        h2Conn1 = DriverManager.getConnection("jdbc:h2:mem:h2TestDb1", "sa", "");
    }

    @Test
    public void testConnection() throws Exception {
        assertNotNull(h2Conn1, "Connection should not be null");
    }

    @Test
    public void testSimpleQueryExecution() throws Exception {
        // test the connection works by executing a simple query
        try (Statement stmt = h2Conn1.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT 1");
            assertTrue(rs.next(), "Result set should not be empty");
            assertEquals(1, rs.getInt(1), "The query should return 1");
        }
    }

    @Test
    public void testCatalogsESTORE() throws Exception {
        // insert H2 Engine into estore
        Class t1 = Class.forName("org.h2.engine.Engine");
        estore1.captureAll(t1);
        Table res = estore1.query("MATCH (n: `org.h2.engine.Database`) RETURN n.databaseShortName");

        Set<String> catalogs = new HashSet<>();
        for (Object name : res.get("n.databaseShortName")) {
            catalogs.add((String) name);
        }

        assertEquals(1, res.getSize());
        assertTrue(catalogs.contains("H2TESTDB1"));
    }

    @Test
    public void testCatalogsESTORERepeat() throws Exception {
        // insert H2 Engine into estore
        Class t1 = Class.forName("org.h2.engine.Engine");
        estore1.captureAll(t1);
        Table res = null;
        for (int i = 0; i < 5; i++) {
            res = estore1.query("MATCH (n: `org.h2.engine.Database`) RETURN n.databaseShortName");
        }

        Set<String> catalogs = new HashSet<>();
        for (Object name : res.get("n.databaseShortName")) {
            catalogs.add((String) name);
        }

        assertEquals(1, res.getSize());
        assertTrue(catalogs.contains("H2TESTDB1"));
    }

    @Test
    public void testCatalogsJDBC() throws Exception {
        long t1 = System.nanoTime();
        DatabaseMetaData meta1 = h2Conn1.getMetaData();
        ResultSet res1 = meta1.getCatalogs();
        // System.out.println("Total Query Time : " + (System.nanoTime() - t1));

        Set<String> catalogs = new HashSet<>();
        while (res1.next()) {
            String catalogName = res1.getString("TABLE_CAT");
            catalogs.add(catalogName);
        }

        assertEquals(1, catalogs.size());
        assertTrue(catalogs.contains("H2TESTDB1"));
    }

    @Test
    public void testCatalogsJDBCRepeat() throws Exception {
        ResultSet res1 = null;
        for (int i = 0; i < 5; i++) {
            long t1 = System.nanoTime();
            DatabaseMetaData meta1 = h2Conn1.getMetaData();
            res1 = meta1.getCatalogs();
            // System.out.println("Total Query Time : " + (System.nanoTime() - t1));
        }

        Set<String> catalogs = new HashSet<>();
        while (res1.next()) {
            String catalogName = res1.getString("TABLE_CAT");
            catalogs.add(catalogName);
        }

        assertEquals(1, catalogs.size());
        assertTrue(catalogs.contains("H2TESTDB1"));
    }

    @Test
    public void testSchemasESTORE() throws Exception {
        // insert H2 Engine into estore
        Class t1 = Class.forName("org.h2.engine.Engine");
        estore1.captureAll(t1);
        // Table res =
        // estore1.query(
        // "MATCH (db: `org.h2.engine.Database`)-[:schemas]->(m:"
        // + " `java.util.concurrent.ConcurrentHashMap`)-[:table]->(n:"
        // + " `java.util.concurrent.ConcurrentHashMap$Node`)-[:key]->(k) RETURN k");

        Table res =
                estore1.query(
                        "MATCH (db: `org.h2.engine.Database`)-[:schemas]->()"
                                + "-[:table]->()"
                                + "-[:key]->(k) RETURN k");

        Set<String> schemas = new HashSet<>();
        for (Object name : res.get("k")) {
            schemas.add((String) name);
        }

        assertEquals(2, schemas.size());
        assertTrue(schemas.contains("PUBLIC"));
        assertTrue(schemas.contains("INFORMATION_SCHEMA"));
    }

    @Test
    public void testSchemasESTORERepeat() throws Exception {
        // insert H2 Engine into estore
        Class t1 = Class.forName("org.h2.engine.Engine");
        estore1.captureAll(t1);
        Table res = null;
        for (int i = 0; i < 5; i++) {
            res =
                    estore1.query(
                            "MATCH (db: `org.h2.engine.Database`)-[:schemas]->(m:"
                                    + " `java.util.concurrent.ConcurrentHashMap`)-[:table]->(n:"
                                    + " `java.util.concurrent.ConcurrentHashMap$Node`)-[:key]->(k) RETURN k");
        }
        // Table res = estore1.query(
        // "MATCH (db: `org.h2.engine.Database`)-[:schemas]->()"
        // + "-[:table]->()"
        // + "-[:key]->(k) RETURN k");

        Set<String> schemas = new HashSet<>();
        for (Object name : res.get("k")) {
            schemas.add((String) name);
        }

        assertEquals(2, schemas.size());
        assertTrue(schemas.contains("PUBLIC"));
        assertTrue(schemas.contains("INFORMATION_SCHEMA"));
    }

    @Test
    public void testSchemasJDBC() throws Exception {
        long t1 = System.nanoTime();
        DatabaseMetaData meta1 = h2Conn1.getMetaData();
        ResultSet res1 = meta1.getSchemas();
        // System.out.println("Total Query Time : " + (System.nanoTime() - t1));

        Set<String> schemas = new HashSet<>();
        while (res1.next()) {
            String schemaName = res1.getString("TABLE_SCHEM");
            schemas.add(schemaName);
        }

        assertEquals(2, schemas.size());
        assertTrue(schemas.contains("PUBLIC"));
        assertTrue(schemas.contains("INFORMATION_SCHEMA"));
    }

    @Test
    public void testSchemasJDBCRepeat() throws Exception {
        ResultSet res1 = null;
        for (int i = 0; i < 5; i++) {
            long t1 = System.nanoTime();
            DatabaseMetaData meta1 = h2Conn1.getMetaData();
            res1 = meta1.getSchemas();
            // System.out.println("Total Query Time : " + (System.nanoTime() - t1));
        }

        Set<String> schemas = new HashSet<>();
        while (res1.next()) {
            String schemaName = res1.getString("TABLE_SCHEM");
            schemas.add(schemaName);
        }

        assertEquals(2, schemas.size());
        assertTrue(schemas.contains("PUBLIC"));
        assertTrue(schemas.contains("INFORMATION_SCHEMA"));
    }

    @Test
    public void testTablesESTORE() throws Exception {
        // Create new tables
        Statement stmt = h2Conn1.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE1 (ID INT PRIMARY KEY, NAME VARCHAR(255))");
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE2 (ID INT PRIMARY KEY, NAME VARCHAR(255))");

        // insert H2 Engine into estore
        Class t1 = Class.forName("org.h2.engine.Engine");
        estore1.captureAll(t1);
        // Table res1 = estore1.query(
        // "MATCH (db: `org.h2.engine.Database`)-[:mainSchema]->(s:"
        // + " `org.h2.schema.Schema`)-[:tablesAndViews]->(m:"
        // + " `java.util.concurrent.ConcurrentHashMap`)-[:table]->(n:"
        // + " `java.util.concurrent.ConcurrentHashMap$Node`)-[:key]->(k) RETURN k");

        Table res1 =
                estore1.query(
                        "MATCH (db: `org.h2.engine.Database`)-[:mainSchema]->()"
                                + "-[:tablesAndViews]->()"
                                + "-[:table]->()"
                                + "-[:key]->(k) RETURN k");

        Set<String> names = new HashSet<>();
        for (Object name : res1.get("k")) {
            names.add((String) name);
        }
        assertTrue(names.contains("TEST_TABLE1"));
        assertTrue(names.contains("TEST_TABLE2"));
    }

    @Test
    public void testTablesESTORERepeat() throws Exception {
        // Create new tables
        Statement stmt = h2Conn1.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE1 (ID INT PRIMARY KEY, NAME VARCHAR(255))");
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE2 (ID INT PRIMARY KEY, NAME VARCHAR(255))");

        // insert H2 Engine into estore
        Class t1 = Class.forName("org.h2.engine.Engine");
        estore1.captureAll(t1);

        Table res1 = null;
        for (int i = 0; i < 5; i++) {
            res1 =
                    estore1.query(
                            "MATCH (db: `org.h2.engine.Database`)-[:mainSchema]->(s:"
                                    + " `org.h2.schema.Schema`)-[:tablesAndViews]->(m:"
                                    + " `java.util.concurrent.ConcurrentHashMap`)-[:table]->(n:"
                                    + " `java.util.concurrent.ConcurrentHashMap$Node`)-[:key]->(k) RETURN k");
        }

        Set<String> names = new HashSet<>();
        for (Object name : res1.get("k")) {
            names.add((String) name);
        }
        assertTrue(names.contains("TEST_TABLE1"));
        assertTrue(names.contains("TEST_TABLE2"));
    }

    @Test
    public void testTablesJDBC() throws Exception {
        // Create new tables
        Statement stmt = h2Conn1.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE1 (ID INT PRIMARY KEY, NAME VARCHAR(255))");
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE2 (ID INT PRIMARY KEY, NAME VARCHAR(255))");

        long t1 = System.nanoTime();
        DatabaseMetaData meta1 = h2Conn1.getMetaData();
        ResultSet res1 = meta1.getTables(null, "PUBLIC", "%", new String[] {"TABLE"});
        // System.out.println("Total Query Time : " + (System.nanoTime() - t1));

        Set<String> tables = new HashSet<>();
        while (res1.next()) {
            String tableName = res1.getString("TABLE_NAME");
            tables.add(tableName);
        }

        assertTrue(tables.contains("TEST_TABLE1"));
        assertTrue(tables.contains("TEST_TABLE2"));
    }

    @Test
    public void testTablesJDBCRepeat() throws Exception {
        // Create new tables
        Statement stmt = h2Conn1.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE1 (ID INT PRIMARY KEY, NAME VARCHAR(255))");
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS TEST_TABLE2 (ID INT PRIMARY KEY, NAME VARCHAR(255))");

        ResultSet res1 = null;
        for (int i = 0; i < 5; i++) {
            long t1 = System.nanoTime();
            DatabaseMetaData meta1 = h2Conn1.getMetaData();
            res1 = meta1.getTables(null, "PUBLIC", "%", new String[] {"TABLE"});
            // System.out.println("Total Query Time : " + (System.nanoTime() - t1));
        }

        Set<String> tables = new HashSet<>();
        while (res1.next()) {
            String tableName = res1.getString("TABLE_NAME");
            tables.add(tableName);
        }

        assertTrue(tables.contains("TEST_TABLE1"));
        assertTrue(tables.contains("TEST_TABLE2"));
    }

    @Test
    public void testDbNameESTORE() throws Exception {
        // insert H2 Engine into estore
        Class t1 = Class.forName("org.h2.engine.Engine");
        estore1.captureAll(t1);

        Table res = estore1.query("MATCH (n: `org.h2.engine.Database`) RETURN n.databaseName");

        Set<String> names = new HashSet<>();
        for (Object name : res.get("n.databaseName")) {
            names.add((String) name);
        }
        assertEquals(1, res.getSize());
        assertTrue(names.contains("mem:h2TestDb1"));
    }

    @Test
    public void testUsersESTORE() throws Exception {
        // create new user
        Statement stmt = h2Conn1.createStatement();
        stmt.execute("CREATE USER IF NOT EXISTS USER1 PASSWORD 'password1'");

        // insert H2 Engine into estore
        Class t1 = Class.forName("org.h2.engine.Engine");
        estore1.captureAll(t1);

        Table res =
                estore1.query(
                        "MATCH (n: `org.h2.engine.Database`)-[:usersAndRoles]->()-[:table]->(k1)-[:key]->(k)"
                                + " RETURN k");

        Set<String> users = new HashSet<>();
        for (Object name : res.get("k")) {
            users.add((String) name);
        }

        assertTrue(users.contains("USER1"));
        // built-in users
        assertTrue(users.contains("PUBLIC"));
        assertTrue(users.contains("SA"));
    }

    @AfterEach
    public void drop() throws Exception {
        if (h2Conn1 != null) {
            h2Conn1.close();
        }
    }
}
