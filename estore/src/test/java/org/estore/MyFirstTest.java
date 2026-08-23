package org.estore;

import static org.junit.jupiter.api.Assertions.*;

import org.estore.example.Person;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyFirstTest {

    private Estore db;

    @BeforeEach
    void setup() {
        db = new Estore(MyFirstTest.class.getName());
    }

    @Test
    void testFindAllPeople() throws EstoreException {
        // Create people: Alice -> Bob -> Charlie
        Person charlie = new Person("Charlie", 25);
        Person bob = new Person("Bob", 30, charlie);
        Person alice = new Person("Alice", 28, bob);

        // Insert into database (captures whole graph)
        db.captureAll(alice);

        // Query: Find all Person nodes
        Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN p");

        // System.out.println("");

        // Print each person
        // System.out.println("\n=== People in the graph ===");
        for (Object person : result.get("p")) {
            Person p = (Person) person;
            // System.out.println("Name: " + p.name + ", Age: " + p.age);
        }

        Table friends =
                db.query(
                        "MATCH (a:`org.estore.example.Person`)-[:friend]->(b:`org.estore.example.Person`) RETURN a, b");

        // System.out.println("");

        // System.out.println("\n=== Friendships ===");
        for (int i = 0; i < friends.getSize(); i++) {
            Person a = (Person) friends.get("a").get(i);
            Person b = (Person) friends.get("b").get(i);
            // System.out.println(a.name + " -> " + b.name);
        }

        // System.out.println("");
        // System.out.println(alice.name + "'s friend's friend is " + alice.friend.friend.name);

        // System.out.println("");
        //  // System.out.println(bob.name + "'s friend's friend is " + bob.friend.friend.name);
        // System.out.println("");

        // Check: Should find 3 people
        assertEquals(3, result.getSize());
        // System.out.println("Found " + result.getSize() + " people!");

        // System.out.println("");
    }
}
