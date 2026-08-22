package org.estore.eval.estore.metadata.h2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;

public class JDBCMetaDataTest {

  private Connection conn1;

  @BeforeEach
  public void setup() throws SQLException {
    conn1 = DriverManager.getConnection("jdbc:h2:mem:h2TestDb1", "sa", "");
  }

  @Test
  public void testH2DbNameQuery() {
    // placeholder; no api for this query
    System.out.println("Execution Time : 0");
  }

  @Test
  public void testH2TablesQuery() throws SQLException {
    Set<String> tablesSet = new HashSet<>();

    // Create a new table and check that it exists
    Statement stmt = conn1.createStatement();
    stmt.execute("CREATE TABLE IF NOT EXISTS TEST_TABLE (ID INT PRIMARY KEY, NAME VARCHAR(255))");

    long time1 = System.nanoTime();
    DatabaseMetaData meta = conn1.getMetaData();
    ResultSet tables = meta.getTables(null, null, "%", new String[] {"TABLE"});
    System.out.println("Execution Time : " + (System.nanoTime() - time1));

    while (tables.next()) {
      String tableName = tables.getString("TABLE_NAME");
      tablesSet.add(tableName);
    }
    // ROLES is a table that is created by default
    assertTrue(tablesSet.contains("ROLES"), "Table ROLES should exist");
    assertTrue(tablesSet.contains("TEST_TABLE"), "Table TEST_TABLE should exist");
  }

  @Test
  public void testH2UsersQuery() throws SQLException {
    long t1 = System.nanoTime();
    DatabaseMetaData meta = conn1.getMetaData();
    String userName = meta.getUserName();
    System.out.println("Execution Time : " + (System.nanoTime() - t1));

    assertEquals("SA", userName, "User name should be SA");
  }

  @AfterEach
  public void drop() throws SQLException {
    if (conn1 != null) {
      conn1.close();
    }
  }
}
