package org.estore.eval.estore.ldbc.finbench;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;

public class Neo4jServerTest3 {

  String uri = "bolt://localhost:7687";
  String user = "neo4j";
  String password = "passwd123";

  @Test
  public void testTw1() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        long t1 = System.nanoTime();
        session.run(
            "CREATE (:`Person` {personId: 1, personName:"
                + " 'George'})-[:Own]->(:`Account`"
                + " {accountId: 1020342322, createTime: '26th March', isBlocked: False,"
                + " accountType: 'brokerage account'})");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  @Test
  public void testTw2() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        long t1 = System.nanoTime();
        session.run(
            "CREATE (:`Company` {companyId: 12345,"
                + " companyName: 'Rand'})-[:Own]->(:`Account`"
                + " {accountId: 1213243435, createTime: 'February 5th', isBlocked: False,"
                + " accountType: 'brokerage account'})");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  @Test
  public void testTw3() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        long t1 = System.nanoTime();
        session.run(
            " MATCH (dst:`Account` {accountId:"
                + " 4619004367821865972}), (src:`Account`"
                + " {accountId: 99079191802151398}) CREATE (dst)-[:Transfer]->(src)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  @Test
  public void testTw4() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        long t1 = System.nanoTime();
        session.run(
            " MATCH (dst:`Account` {accountId:"
                + " 4619004367821865972, accountType:'card'}),"
                + " (src:`Account` {accountId:"
                + " 99079191802151398}) CREATE (dst)-[:Withdraw]->(src)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  @Test
  public void testTw8() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        long t1 = System.nanoTime();
        session.run(
            "MATCH (acc:`Account` {accountId:"
                + " 4700350636091245930}), (loan:`Loan`"
                + " {loanId: 4684025087442027461}) CREATE (loan)-[:Deposit]->(acc)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  @Test
  public void testTw9() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        long t1 = System.nanoTime();
        session.run(
            "MATCH (acc:`Account` {accountId:"
                + " 4700350636091245930}), (loan:`Loan`"
                + " {loanId: 4684025087442027461}) CREATE (acc)-[:Repay]->(loan)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  @Test
  public void testTw13() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        long t1 = System.nanoTime();
        session.run(
            "MATCH (p1:`Person` {personId: 2199023255767}),"
                + " (p2:`Person` {personId: 10995116278183})"
                + " CREATE (p1)<-[:Guarantee]-(p2)");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }

  @Test
  public void testTsr1() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        long t1 = System.nanoTime();
        session.run(
            "MATCH (account:`Account` {accountId:"
                + " 4700350636091245930}) RETURN account.createTime, account.isBlocked,"
                + " account.accountType");
        System.out.println("Total Query Time : " + (System.nanoTime() - t1));
      }
    }
  }
}
