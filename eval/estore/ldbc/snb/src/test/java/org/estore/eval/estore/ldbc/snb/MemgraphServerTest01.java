package org.estore.eval.estore.ldbc.snb;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;

public class MemgraphServerTest01 {

  String uri = "bolt://localhost:7687";
  String user = "";
  String password = "";

  @Test
  public void testNodeCount() {
    try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
      // Create a session
      try (Session session = driver.session()) {

        Result result = session.run("MATCH (n) RETURN COUNT(n)");
        System.out.println(session);
        System.out.println(result.keys());
        while (result.hasNext()) {
          org.neo4j.driver.Record record = result.next();

          System.out.println(record);
        }
      }
    }
  }
}
