package org.estore.eval.estore.metadata.h2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.estore.Estore;
import org.estore.EstoreOptions;
import org.estore.planner.util.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InGraphReflectionMetaDataTest {

  private Connection conn1;
  private Estore estore1;

  @BeforeEach
  public void setup() throws Exception {
    estore1 = new Estore("estoreTestDb1", new EstoreOptions().useUnsafe(false));
    conn1 = DriverManager.getConnection("jdbc:h2:mem:h2TestDb1", "sa", "");
  }

  @Test
  public void testH2DbNameQuery() throws Exception {
    // insert H2 Engine into estore
    Class t = Class.forName("org.h2.engine.Engine");
    estore1.captureAll(t);

    long time1 = System.nanoTime();
    Table res = estore1.query("MATCH (n: `org.h2.engine.Database`) RETURN n.databaseName");
    System.out.println("Execution Time : " + (System.nanoTime() - time1));

    Set<String> names = new HashSet<>();
    for (Object name : res.get("n.databaseName")) {
      names.add((String) name);
    }
    assertEquals(2, res.getSize());
    assertTrue(names.contains("mem:h2TestDb1"));
    assertTrue(names.contains("mem:h2TestDb2"));
    assertTrue(true);
  }

  @Test
  public void testH2TablesQuery() throws Exception {
    // Create new tables
    Statement stmt = conn1.createStatement();
    stmt.execute("CREATE TABLE IF NOT EXISTS TEST_TABLE1 (ID INT PRIMARY KEY, NAME VARCHAR(255))");
    stmt.execute("CREATE TABLE IF NOT EXISTS TEST_TABLE2 (ID INT PRIMARY KEY, NAME VARCHAR(255))");

    // insert H2 Engine into estore
    Class t = Class.forName("org.h2.engine.Engine");
    estore1.captureAll(t);

    long time1 = System.nanoTime();
    Table res1 =
        estore1.query(
            "MATCH (n:"
                + " `org.h2.engine.Database`)-[:mainSchema]->()-[:tablesAndViews]->()-[:table]->()-[]->()-[:key]->(k)"
                + " RETURN k");
    System.out.println("Execution Time : " + (System.nanoTime() - time1));

    Set<String> names = new HashSet<>();
    for (Object name : res1.get("k")) {
      names.add((String) name);
    }
    estore1.captureAll(names);
    // assertTrue(names.contains("TEST_TABLE1"));
    // assertTrue(names.contains("TEST_TABLE2"));
  }

  @Test
  public void testH2UsersQuery() throws Exception {
    // create new user
    Statement stmt = conn1.createStatement();
    stmt.execute("CREATE USER IF NOT EXISTS USER1 PASSWORD 'password1'");

    // insert H2 Engine into estore
    Class t = Class.forName("org.h2.engine.Engine");
    // estore1.captureAll(t);

    long time1 = System.nanoTime();
    Table res =
        estore1.query(
            "MATCH (n:"
                + " `org.h2.engine.Database`)-[:usersAndRoles]->()-[:table]->()-[]->()-[:key]->(k)"
                + " RETURN k");
    System.out.println("Execution Time : " + (System.nanoTime() - time1));

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
    if (conn1 != null) {
      conn1.close();
    }
  }
}
