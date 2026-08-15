package org.estore.eval.estore.ldbc.snb;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;

public class Neo4jServerTest10 {
  String uri = "bolt://localhost:7687";
  String user = "neo4j";
  String password = "passwd123";

  @Test
  public void testNodeCount() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        Result result = session.run("MATCH (n) RETURN COUNT(n)");
        while (result.hasNext()) {
          org.neo4j.driver.Record record = result.next();

          System.out.println("Nodes " + record.get("COUNT(n)"));
        }
      }
    }
  }

  @Test
  public void testRelationCount() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        Result result = session.run("MATCH (n)-[r]->(m) RETURN COUNT(n)");
        while (result.hasNext()) {
          org.neo4j.driver.Record record = result.next();

          System.out.println("Relations " + record.get("COUNT(n)"));
        }
      }
    }
  }

  /*
  @Test
  public void testInteractiveDeleteQuery2() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (m:`Person`"
                + " {id:6597069780295})-[likes:LIKES]->(:`Post`"
                + " {id:7146825580576}) DELETE likes RETURN COUNT(m)");
        System.out.println("Execution Time : " + (System.nanoTime() - t1));
      }
    }
    }*/

  @Test
  public void testInteractiveDeleteQuery2() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (m:`Person`"
                + " {id:32985348866137})-[likes:LIKES]->(:`Post`"
                + " {id:8246353327119}) DELETE likes RETURN m");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveDeleteQuery3() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (m:`Person`"
                + " {id:6597069815834})-[likes:LIKES]->(:`Comment`"
                + " {id:8246337208337}) DELETE likes RETURN COUNT(m)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveDeleteQuery5() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (m:`Forum`"
                + " {id:1099511627777})-[hasMember:HAS_MEMBER]->(:`Person`"
                + " {id:6597069780295}) DELETE hasMember RETURN COUNT(m)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveShortQuery1() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (n:`Person`"
                + " {id:32985348833679})-[:IS_LOCATED_IN]->(p:`Place`)"
                + " RETURN n.firstName AS firstName, n.lastName AS lastName, n.birthday AS"
                + " birthday, n.locationIP AS locationIP, n.browserUsed AS browserUsed, p.id AS"
                + " cityId, n.gender AS gender, n.creationDate AS creationDate");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveShortQuery5() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (m:`Comment`"
                + " {id:7146825883053})-[:HAS_CREATOR]->(p:`Person`)"
                + " RETURN p.id AS personId, p.firstName AS firstName, p.lastName AS lastName");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveUpdateQuery2() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (person:`Person` {id:6597069815834}),"
                + " (post:`Post` {id:6047313953017})  CREATE"
                + " (person)-[:LIKES]->(post)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveUpdateQuery3() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (person:`Person` {id:6597069780295}),"
                + " (comment:`Comment` {id:3848290697802})  CREATE"
                + " (person)-[:LIKES]->(comment)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveUpdateQuery5() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (f:`Forum` {id:1099511627777}),"
                + " (p:`Person` {id:8796093058058})  CREATE"
                + " (f)-[:HAS_MEMBER]->(p)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  //
  @Test
  public void testInteractiveUpdateQuery8() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {
        long t1 = System.nanoTime();
        session.run(
            "MATCH (p1:`Person` {id:2199023256684}),"
                + " (p2:`Person` {id:17592186114273})  CREATE"
                + " (p1)-[:KNOWS]->(p2)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }
}
