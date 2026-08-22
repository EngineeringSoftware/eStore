package org.estore.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import org.estore.Estore;
import org.estore.EstoreException;
import org.estore.EstoreOptions;
import org.estore.example.A;
import org.estore.example.B;
import org.estore.example.C;
import org.estore.example.D;
import org.estore.planner.util.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CodeGenTest {

    private Estore estore;

    @BeforeEach
    public void initDatabase() {
        try {
            estore = new Estore(CodeGenTest.class.getName(), new EstoreOptions().useDfs(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testObjectGenericMatch() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n) RETURN n");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 4);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
        assertTrue(resultContainsInstanceOfClass(result, B.class));
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testObjectSpecificMatch1() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n:`org.estore.example.A`) RETURN n");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
    }

    @Test
    void testObjectSpecificMatch2() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n:`org.estore.example.B`) RETURN n");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testObjectSpecificMatch3() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n:`org.estore.example.C`) RETURN n");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
    }

    @Test
    void testObjectSpecificMatch4() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n:`org.estore.example.D`) RETURN n");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testSingleRelationGenericMatch() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n)-[]->(m) RETURN n");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 3);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
        assertTrue(resultContainsInstanceOfClass(result, B.class));
        assertTrue(resultContainsInstanceOfClass(result, C.class));
    }

    @Test
    void testSingleRelationGenericMatch2() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n)-[]->(m) RETURN m");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 3);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
        assertTrue(resultContainsInstanceOfClass(result, C.class));
        assertTrue(resultContainsInstanceOfClass(result, D.class));
    }

    @Test
    void testSingleRelationSpecificMatch() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n:`org.estore.example.A`)-[]->(m) RETURN m");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testSingleRelationSpecificMatch2() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result = estore.query("MATCH (n:`org.estore.example.B`)-[]->(m) RETURN m");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
    }

    @Test
    void testSingleRelationSpecificMatch3() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result =
                estore.query(
                        "MATCH (n:`org.estore.example.A`)-[]->(m:`org.estore.example.B`) RETURN m");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, B.class));
    }

    @Test
    void testSingleRelationSpecificMatchProperties1() throws EstoreException {
        estore.insert(A.class);
        long t1 = System.currentTimeMillis();
        Table result1 =
                estore.query(
                        "MATCH (n:`org.estore.example.A` {field1 : 20})-[r:b]->(m:`org.estore.example.B`) RETURN m");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        t1 = System.currentTimeMillis();
        Table result2 =
                estore.query(
                        "MATCH (n:`org.estore.example.A` {field1 : 10})-[r:b]->(m:`org.estore.example.B`) RETURN m");
        // System.out.println("Time: " + (System.currentTimeMillis() - t1) + " ms");
        assertTrue(result1.getSize() == 0);
        assertTrue(result2.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result2, B.class));
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
        // result.print();
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, C.class));
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
    void testVarRelationSpecificMatch3() throws EstoreException {
        estore.insert(A.class);
        Table result =
                estore.query(
                        "MATCH (n:`org.estore.example.A`)-[*1..2]->(m:`org.estore.example.C`) RETURN n");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, A.class));
    }

    @Test
    void testCreateLabelNode() throws EstoreException {
        Table result = estore.query("CREATE (m:`org.estore.E`) RETURN m");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, "org.estore.E"));
    }

    @Test
    void testNodeAddPropertyCypher2() throws Exception {
        Table result = estore.query("CREATE (n:`DummyClass4` {name:'Uki', age:30}) RETURN n");
        Object obj = result.get("n").get(0);
        Class objClass = obj.getClass();
        Field f = objClass.getDeclaredField("name");
        Field f1 = objClass.getDeclaredField("age");
        assertEquals((String) f.get(obj), "Uki");
        assertEquals(f1.getLong(obj), 30L);
    }

    @Test
    void testCreateTwoNodeRelation() throws EstoreException {
        Table result =
                estore.query("CREATE (n:`org.estore.F2`)-[:e]->(m:`org.estore.G2`) RETURN m,n");
        assertTrue(result.getSize() == 1);
        assertTrue(resultContainsInstanceOfClass(result, "org.estore.F2"));
        assertTrue(resultContainsInstanceOfClass(result, "org.estore.G2"));
    }

    @Test
    void testCountAggregation() throws EstoreException {
        Table result = estore.query("CREATE (m:`org.estore.H`) RETURN COUNT(m)");
        assertTrue(result.getSize() == 1);
        assertTrue(result.containsKey("COUNT(m)"));
        assertTrue(((Integer) result.get("COUNT(m)").get(0)) == 1);
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
