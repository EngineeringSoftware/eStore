package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.estore.example.A;
import org.estore.example.B;
import org.estore.example.C;
import org.estore.example.D;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateInstanceTest {

    private Estore estore;

    @BeforeEach
    public void initDatabase() {
        try {
            estore =
                    new Estore(
                            CreateInstanceTest.class.getName(), new EstoreOptions().useDfs(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * @Test
     * public void checkInstanceCreation() throws RemoteException, ESTOREException {
     * estore.add(A.class);
     * Object[] result = estore.query(true, "MATCH (n:`" + (A.class).getName() +
     * "`) RETURN n.field1");
     * assertTrue(result != null && result.length == 1 && ((Integer)
     * result[0]).intValue() == 10);
     * }
     *
     * @RepeatedTest(50)
     * void testNodeAddPropertyESTOREEval() throws Exception {
     * long t1 = System.currentTimeMillis();
     * estore.add(A.class);
     */
    /*
     * estore.add(
     * "CREATE (n:`DummyClass1` {key1 : 1, key2 : '2'}), (m:`DummyClass2` {key1 : '1', key2 : 2})"
     * + " RETURN n, m");
     * assertNotNull(
     * estore.query(
     * true,
     * "MATCH (n:`DummyClass1` {key1 : 1, key2 : '2'}) MATCH (m:`DummyClass2` {key1 : '1',"
     * + " key2 : 2}) RETURN n, m"));
     */
    /*
    * estore.query(true, "MATCH (n:`"+A.class.getName()+"`) RETURN n");
    // * System.out.println((System.currentTimeMillis() - t1));
    * }
    *
    * @Test
    * void createDropNodeLongStringPropertyESTOREEval() throws Exception {
    * String testPropertyKey = "testProperty";
    * String propertyValue = RandomStringUtils.randomAlphanumeric(255);
    *
    * long t1 = System.currentTimeMillis();
    * estore.add("CREATE (n:`marker` {" + testPropertyKey + ":'" + propertyValue +
    * "'}) RETURN n");
    * assertNotNull(
    * estore.query(
    * true, "MATCH (n:`marker` {" + testPropertyKey + ":'" + propertyValue +
    * "'}) RETURN n"));
    // * System.out.println((System.currentTimeMillis() - t1));
    * }
    *
    * @Test
    * void createObjectWithJ() throws Exception {
    * estore.add("create (n: `Val` {value: 30}) return n");
    * Object[] objs = estore.query(true, "match (n: `Val`) return n");
    *
    * assertNotNull(objs);
    * assertEquals(1, objs.length);
    *
    * Object obj = objs[0];
    * assertNotNull(obj);
    * assertEquals(30, estore.getLong(obj, "value"));
    * }
    *
    * @Test
    * void createObjectWithD() throws Exception {
    * estore.add("create (n: `Val` {value: 30.0}) return n");
    * Object[] objs = estore.query(true, "match (n: `Val`) return n");
    *
    * assertNotNull(objs);
    * assertEquals(1, objs.length);
    *
    * Object obj = objs[0];
    * assertNotNull(obj);
    * assertEquals(30.0, estore.getDouble(obj, "value"), 0.01);
    * }
    *
    * @Test
    * void createObjectWithString() throws Exception {
    * estore.add("create (n: `Val` {value: 'something'}) return n");
    * Object[] objs = estore.query(true, "match (n: `Val`) return n");
    *
    * assertNotNull(objs);
    * assertEquals(1, objs.length);
    *
    * Object obj = objs[0];
    * assertNotNull(obj);
    * // TODO: weird that we need those single quotes
    * assertEquals("'something'", estore.getString(obj, "value"));
    * }
    *
    * @Test
    * void createObjectWithManyTypes() throws Exception {
    * estore.add("create (n: `Val` {i: 30, d: 30.0, s: 'something'}) return n");
    * Object[] objs = estore.query(true, "match (n: `Val`) return n");
    *
    * assertNotNull(objs);
    * assertEquals(1, objs.length);
    *
    * Object obj = objs[0];
    * assertNotNull(obj);
    *
    * assertEquals(30, estore.getLong(obj, "i"));
    * assertEquals(30.0, estore.getDouble(obj, "d"), 0.01);
    * assertEquals("'something'", estore.getString(obj, "s"));
    * }
    */

    @Test
    void testObjectGenericMatch() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n) RETURN n");
        assertTrue(result.getSize() == 4);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
        assertTrue(resultContainsInstanceOfClass(result, B.class));
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testObjectSpecificMatch1() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n:`org.estore.example.A`) RETURN n");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
    }

    @Test
    void testObjectSpecificMatch2() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n:`org.estore.example.B`) RETURN n");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testObjectSpecificMatch3() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n:`org.estore.example.C`) RETURN n");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
    }

    @Test
    void testObjectSpecificMatch4() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n:`org.estore.example.D`) RETURN n");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testSingleRelationGenericMatch() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n)-[]->(m) RETURN n");
        assertTrue(result.getSize() == 3);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
        assertTrue(resultContainsInstanceOfClass(result, B.class));
        assertTrue(resultContainsInstanceOfClass(result, C.class));
    }

    @Test
    void testSingleRelationGenericMatch2() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n)-[]->(m) RETURN m");
        assertTrue(result.getSize() == 3);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testSingleRelationSpecificMatch() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n:`org.estore.example.A`)-[]->(m) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testSingleRelationSpecificMatch2() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n:`org.estore.example.B`)-[]->(m) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
    }

    @Test
    void testSingleRelationSpecificMatch3() throws EstoreException {
        estore.insert(A.class);
        Table result =
                estore.query(
                        "MATCH (n:`org.estore.example.A`)-[]->(m:`org.estore.example.B`) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testSingleRelationSpecificMatchProperties1() throws EstoreException {
        estore.insert(A.class);
        Table result =
                estore.query(
                        "MATCH (n:`org.estore.example.A` {field1 : 10})-[r:b]->(m:`org.estore.example.B`) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testMultiRelationGenericMatch() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n)-[]->()-[]->(l) RETURN l");
        assertTrue(result.getSize() == 2);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testMultiRelationGenericMatch2() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n)-[]->()-[]->(l)-[]->() RETURN l");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
    }

    @Test
    void testVarRelationGenericMatch() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n)-[*1..2]->(m) RETURN m");
        assertTrue(result.getSize() == 5);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testVarRelationGenericMatch2() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n)-[*1..3]->(m) RETURN m");
        assertTrue(result.getSize() == 6);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testVarRelationGenericMatch3() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n)-[*2..3]->(m) RETURN m");
        assertTrue(result.getSize() == 3);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testVarRelationSpecificMatch() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n:`org.estore.example.B`)-[*1..2]->(m) RETURN m");
        assertTrue(result.getSize() == 2);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testVarRelationSpecificMatch2() throws EstoreException {
        estore.query("CREATE (m:`org.estore.example.A`)");
        Table result = estore.query("MATCH (n)-[*1..2]->(m:`org.estore.example.C`) RETURN n");
        assertTrue(result.getSize() == 2);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testVarRelationSpecificMatch3() throws EstoreException {
        estore.insert(A.class);
        Table result =
                estore.query(
                        "MATCH (n:`org.estore.example.A`)-[*1..2]->(m:`org.estore.example.C`) RETURN n");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
    }

    @Test
    void testMultiVarRelationGenericMatch() throws EstoreException {
        estore.insert(A.class);
        Table result = estore.query("MATCH (n)-[]->()-[*1..2]->(m) RETURN m");
        assertTrue(result.getSize() == 3);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testMultiVarRelationSpecificMatch() throws EstoreException {
        estore.query("CREATE (m:`org.estore.example.A`)");
        Table result =
                estore.query("MATCH (n:`org.estore.example.B`)-[]->()-[*1..2]->(m) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testRelationGenericMatchEdgeName() throws EstoreException {
        estore.query("CREATE (m:`org.estore.example.A`)");
        Table result = estore.query("MATCH (n)-[:b]->(m) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testVarRelationGenericMatchEdgeName() throws EstoreException {
        estore.query("CREATE (m:`org.estore.example.A`)");
        Table result = estore.query("MATCH (n:`org.estore.example.A`)-[:b*1..2]->(m) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testCreateLabelNode() throws EstoreException {
        Table result = estore.query("CREATE (m:`org.estore.E`) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, "org.estore.E"));
    }

    @Test
    void testCreateTwoNodeRelation() throws EstoreException {
        Table result =
                estore.query("CREATE (n:`org.estore.F`)-[:e]->(m:`org.estore.G`) RETURN m,n");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, "org.estore.F"));
        assertTrue(resultContainsInstanceOfClass(result, "org.estore.G"));
    }

    @Test
    void testCountAggregation() throws EstoreException {
        Table result = estore.query("CREATE (m:`org.estore.H`) RETURN COUNT(m)");
        assertTrue(result.getSize() == 1);
        assertTrue(result.containsKey("COUNT(m)"));
        assertTrue(((Integer) result.get("COUNT(m)").get(0)) == 1);
    }

    /*
     * @Test
     * void testCountAggregation2() throws EstoreException{
     * estore.insert(A.class);
     * Table result = estore.query("MATCH (n)-[*1..3]->(m) RETURN COUNT(m)");
     * result.print();
     * assertTrue(result.getSize() == 1);
     * assertTrue(result.containsKey("COUNT(m)"));
     * assertTrue(((Integer) result.get("COUNT(m)").get(0)) == 1);
     * }
     */

    @Test
    void shouldReportNumberOfNodesCypherNeo4j() {
        long t1 = System.currentTimeMillis();
        estore.query("CREATE (n:`DummyClass`), (m:`DummyClass`)");
        Table result = estore.query("MATCH (n:`DummyClass`) RETURN COUNT(n)");
        // assertTrue(result.getSize() == 1);
        // assertTrue(result.containsKey("COUNT(n)"));
        assertTrue(((Integer) result.get("COUNT(n)").get(0)) == 2);
        //  // System.out.println((System.currentTimeMillis() - t1));
    }

    @Test
    void shouldReportNumberOfNodesInAnEmptyGraphCypherNeo4j() {
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n) RETURN COUNT(n)");
        // assertTrue(result.getSize() == 1);
        // assertTrue(result.containsKey("COUNT(n)"));
        assertTrue(((Integer) result.get("COUNT(n)").get(0)) == 0);
        //  // System.out.println((System.currentTimeMillis() - t1));
    }

    @Test
    void shouldReportNumberOfRelationshipsInAnEmptyGraphCypherNeo4j() {
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n)-[]->(m) RETURN COUNT(n)");
        // assertTrue(result.getSize() == 1);
        // assertTrue(result.containsKey("COUNT(n)"));
        assertTrue(((Integer) result.get("COUNT(n)").get(0)) == 0);
        //  // System.out.println((System.currentTimeMillis() - t1));
    }

    @Test
    void testNodeAddPropertyCypher() {
        estore.query("CREATE (n:`DummyClass1`), (m:`DummyClass2`)");
        Table result = estore.query("MATCH (n:`DummyClass1`) RETURN COUNT(n)");
        Table result2 = estore.query("MATCH (m:`DummyClass2`)  RETURN COUNT(m)");
        assertTrue(((Integer) result.get("COUNT(n)").get(0)) == 1);
        assertTrue(((Integer) result2.get("COUNT(m)").get(0)) == 1);
    }

    @Test
    void testNodeAddPropertyCypher2() throws Exception {
        Table result = estore.query("CREATE (n:`DummyClass3` {name:'Uki', age:30}) RETURN n");
        Object obj = result.get("n").get(0);
        Class objClass = obj.getClass();
        Field f = objClass.getDeclaredField("name");
        Field f1 = objClass.getDeclaredField("age");
        assertEquals((String) f.get(obj), "Uki");
        assertEquals(f1.getLong(obj), 30L);
    }

    @Test
    void testArrayList() throws Exception {
        ArrayList<Long> a = new ArrayList<Long>();
        a.add(10L);
        a.add(20L);
        a.add(30L);
        estore.captureAll(a);
        Table result =
                estore.query(
                        "MATCH (n:`java.util.ArrayList`)-[:elementData]->(p {value:10}) RETURN p");
        // result.print();
        assertEquals(result.get("p").get(0), 10L);
    }

    @Test
    void testLinkedList() throws Exception {
        LinkedList<Long> a = new LinkedList<Long>();
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (long j = 0; j < 10000; j++) {
            a.add(rand.nextLong(1, Long.MAX_VALUE));
        }
        int ind = rand.nextInt(a.size());
        estore.captureAll(a);

        Table result =
                estore.query(
                        "MATCH (n:`java.util.LinkedList$Node`)-[]->(p {value:"
                                + ((long) a.get(ind))
                                + "}) RETURN p");
        assertEquals(a.get(ind), result.get("p").get(0));
    }

    @Test
    void testLinkedList2() throws Exception {
        LinkedList<Long> a = new LinkedList<Long>();
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int j = 0; j < 100000; j++) {
            a.add(rand.nextLong(1, Long.MAX_VALUE));
        }
        estore.captureAll(a);
        int ind = rand.nextInt(a.size());

        // 2..(floor(len + 1 / 2) + 1) is enough since it is double linked list
        Table result =
                estore.query(
                        "MATCH (n:`java.util.LinkedList`)-[*2..50001]->(p {value:"
                                + ((long) a.get(ind))
                                + "}) RETURN p");
        assertEquals(
                a.get(ind), result.get("p").get(0)); // /ERROR Index 0 out of bounds for length 0
    }

    @Test
    void testLinkedList3() throws Exception {
        LinkedList<Long> a = new LinkedList<Long>();
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int j = 0; j < 50; j++) {
            a.add(rand.nextLong(1, Long.MAX_VALUE));
        }
        estore.captureAll(a);
        // excluding the first node
        int ind = rand.nextInt(a.size() - 1) + 1;

        Table result =
                estore.query(
                        "MATCH (n:`java.util.LinkedList`)-[:first]->()-[:next*1..49]->()-[:item]->(p {value:"
                                + ((long) a.get(ind))
                                + "}) RETURN p");
        assertEquals(a.get(ind), result.get("p").get(0));
    }

    @Test
    void testArrayDeque() throws Exception {
        ArrayDeque<Long> a = new ArrayDeque<Long>();
        a.add(10L);
        a.add(20L);
        a.add(30L);
        estore.captureAll(a);
        Table result =
                estore.query(
                        "MATCH (n:`java.util.ArrayDeque`)-[:elements]->(p {value:30}) RETURN p");
        assertEquals(result.get("p").get(0), 30L);
    }

    @Test
    void testArrayListIteration() {
        ArrayList<Long> a = new ArrayList<Long>();
        // a.add(10L);
        // a.add(20L);
        // a.add(30L);
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int j = 0; j < 9528; j++) {
            a.add(rand.nextLong(0, Long.MAX_VALUE));
        }
        long val = a.get(a.size() - 1);
        long t1 = System.nanoTime();
        for (int j = 0; j < a.size(); j++) {
            if (a.get(j) == val) {
                break;
            }
        }
        // System.out.println("Execution time : " + (System.nanoTime() - t1));
    }

    @Test
    void testVector() throws Exception {
        Vector<Long> a = new Vector<Long>();
        a.add(10L);
        a.add(20L);
        a.add(30L);
        estore.captureAll(a);
        Table result =
                estore.query(
                        "MATCH (n:`java.util.Vector`)-[:elementData]->(p {value:20}) RETURN p");
        assertEquals(result.get("p").get(0), 20L);
    }

    @Test
    void testHashMap() throws Exception {
        HashMap<Long, Long> a = new HashMap<Long, Long>();
        a.put(10L, 10L);
        a.put(20L, 20L);
        a.put(30L, 30L);
        estore.captureAll(a);
        Table result =
                estore.query(
                        "MATCH (n:`java.util.HashMap`)-[:table]->()-[:value]->(p {value:20}) RETURN p");
        assertEquals(((Long) result.get("p").get(0)), 20L);
    }

    @Test
    void testConcurrentHashMap() throws Exception {
        ConcurrentHashMap<String, Long> a = new ConcurrentHashMap<String, Long>();
        a.put("TABLE1", 10L);
        a.put("TABLE2", 20L);
        a.put("TABLE3", 30L);
        estore.captureAll(a);
        // estore.printLabelMaps();
        Table result =
                estore.query(
                        "MATCH (n:`java.util.concurrent.ConcurrentHashMap`)-[:table]->(m)-[]->(p {value:"
                                + " 20}) RETURN p");
        assertEquals(((Long) result.get("p").get(0)), 20L);
    }

    @Test
    void testTableUnion() {
        try {
            Table a = new Table(Arrays.asList(new String[] {"m", "n"}));
            Table b = new Table(Arrays.asList(new String[] {"n", "p"}));
            HashMap<String, Object> aData = new HashMap<String, Object>();
            HashMap<String, Object> bData = new HashMap<String, Object>();
            aData.put("m", 10);
            aData.put("n", 20);
            bData.put("n", 10);
            bData.put("p", 30);
            a.putEntry(aData);
            b.putEntry(bData);
            // a.print();
            // b.print();
            a = a.union(b, Arrays.asList(new String[] {"m", "n"}));
            // a.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean resultContainsInstanceOfClass(Table result, Class<?> klass) {
        for (Map.Entry<String, ArrayList<Object>> item : result.entrySet()) {
            for (Object obj : item.getValue()) {
                if (klass.isInstance(obj)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean resultContainsInstanceOfClass(Table result, String className) {
        for (Map.Entry<String, ArrayList<Object>> item : result.entrySet()) {
            for (Object obj : item.getValue()) {
                if (obj.getClass().getName() == className) {
                    return true;
                }
            }
        }
        return false;
    }
}
