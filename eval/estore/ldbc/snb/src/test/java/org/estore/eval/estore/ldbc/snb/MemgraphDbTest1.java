package org.estore.eval.estore.ldbc.snb;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;

public class MemgraphDbTest1 {

  String uri = "bolt://localhost:7687";
  String user = "neo4j";
  String password = "neo4j";
  private static HashMap<String, Long> endToEndTimeMs;

  @BeforeAll
  public static void setupEndToEndTimeCalc() {
    endToEndTimeMs = new HashMap<String, Long>();
  }

  @AfterAll
  public static void printEndToEndTime() {
    for (Map.Entry<String, Long> item : endToEndTimeMs.entrySet()) {
      System.out.println(item.getKey() + " : " + item.getValue());
    }
  }

  @Test
  public void exampleTest() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("", ""))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        Result result =
            session.run(
                "LOAD CSV FROM"
                    + " \"/home/audi/ldbc_data_sets/snb/social_network-csv_composite-longdateformatter-sf1/dynamic/person_0_0.csv\""
                    + " WITH HEADER AS row RETURN row");
        while (result.hasNext()) {
          org.neo4j.driver.Record record = result.next();

          System.out.println("Row " + record.get("row"));
        }
        endToEndTimeMs.put("interactive-delete-2", (System.currentTimeMillis() - t1));
      }
    }
  }

  @Test
  public void testInteractiveDeleteQuery2() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (m:`Person`"
                + " {id:6597069777240})-[likes:LIKES]->(:`Post`"
                + " {id:1374389534822}) DELETE likes RETURN COUNT(m)");
        endToEndTimeMs.put("interactive-delete-2", (System.currentTimeMillis() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveDeleteQuery3() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (m:`Person`"
                + " {id:32985348833579})-[likes:LIKES]->(:`Comment`"
                + " {id:2061584302097}) DELETE likes RETURN COUNT(m)");
        endToEndTimeMs.put("interactive-delete-3", (System.currentTimeMillis() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveDeleteQuery5() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (m:`Forum`"
                + " {id:1786706395137})-[hasMember:HAS_MEMBER]->(:`Person`"
                + " {id:6597069777240}) DELETE hasMember RETURN COUNT(m)");
        endToEndTimeMs.put("interactive-delete-5", (System.currentTimeMillis() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveShortQuery1() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (n:`Person`"
                + " {id:32985348833679})-[:IS_LOCATED_IN]->(p:`Place`)"
                + " RETURN n.firstName AS firstName, n.lastName AS lastName, n.birthday AS"
                + " birthday, n.locationIP AS locationIP, n.browserUsed AS browserUsed, p.id AS"
                + " cityId, n.gender AS gender, n.creationDate AS creationDate");
        endToEndTimeMs.put("interactive-short-1", (System.currentTimeMillis() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveShortQuery5() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (m:`Comment`"
                + " {id:1511828523046})-[:HAS_CREATOR]->(p:`Person`)"
                + " RETURN p.id AS personId, p.firstName AS firstName, p.lastName AS lastName");
        endToEndTimeMs.put("interactive-short-5", (System.currentTimeMillis() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveUpdateQuery2() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (person:`Person` {id:6597069777240}),"
                + " (post:`Post` {id:549755815810})  CREATE"
                + " (person)-[:LIKES]->(post)");
        endToEndTimeMs.put("interactive-update-2", (System.currentTimeMillis() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveUpdateQuery3() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (person:`Person` {id:10995116284808}),"
                + " (comment:`Comment` {id:824633722450})  CREATE"
                + " (person)-[:LIKES]->(comment)");
        endToEndTimeMs.put("interactive-update-3", (System.currentTimeMillis() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveUpdateQuery5() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (f:`Forum` {id:1374389534723}),"
                + " (p:`Person` {id:32985348838375})  CREATE"
                + " (f)-[:HAS_MEMBER]->(p)");
        endToEndTimeMs.put("interactive-update-5", (System.currentTimeMillis() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveUpdateQuery8() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.currentTimeMillis();
        session.run(
            "MATCH (p1:`Person` {id:2199023256684}),"
                + " (p2:`Person` {id:21990232560132})  CREATE"
                + " (p1)-[:KNOWS]->(p2)");
        endToEndTimeMs.put("interactive-update-8", (System.currentTimeMillis() - t1));
      }
    }
  }
}
