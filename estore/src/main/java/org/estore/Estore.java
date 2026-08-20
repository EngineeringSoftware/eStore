package org.estore;

import com.jakewharton.fliptables.FlipTable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.estore.antlr4.CypherLexer;
import org.estore.antlr4.CypherParser;
import org.estore.planner.LogicalPlan;
import org.estore.planner.LogicalPlanBuilder;
import org.estore.planner.util.ClassInfo;
import org.estore.planner.util.Table;

public class Estore implements Serializable {
    private String name;
    private HashMap<Long, Object> datastore;
    private LabelMap labelObjectMap;
    private HashMap<String, ClassInfo> labelClassInfoMap;

    /**
     * ClassInfo by runtime class for traversal (so we use Engine's ClassInfo for Engine instances,
     * not Class's).
     */
    private HashMap<String, ClassInfo> runtimeClassInfoMap;

    private EstoreOptions options;
    private int id;
    private ArrayList<Class> dynamicClasses;

    public void printLabelMaps() {
        System.out.println("\nLabel Object Summary:");
        String[][] data = new String[labelObjectMap.size()][2];
        int count = 0;
        for (Map.Entry<String, ArrayList<Object>> item : labelObjectMap.entrySet()) {
            data[count][0] = item.getKey();
            data[count][1] = item.getValue().size() + "";
            count++;
        }
        System.out.println(FlipTable.of(new String[] {"Class", "#Objects"}, data));
    }

    public HashMap<Long, Object> getDataStore() {
        return datastore;
    }

    public LabelMap getLabelObjectMap() {
        return labelObjectMap;
    }

    public HashMap<String, ClassInfo> getLabelClassInfoMap() {
        return labelClassInfoMap;
    }

    public Estore(String name) throws Exception {
        this(name, EstoreOptions.getDefaultOptions());
    }

    public Estore(String name, EstoreOptions options) throws Exception {
        this.name = name;
        this.options = options;
        this.id = 0;
        datastore = new HashMap<Long, Object>();
        labelObjectMap = new LabelMap();
        labelClassInfoMap = new HashMap<String, ClassInfo>();
        runtimeClassInfoMap = new HashMap<String, ClassInfo>();
        dynamicClasses = new ArrayList<Class>();
    }

    public void addDynamicClass(Class<?> klass) {
        if (!dynamicClasses.contains(klass)) {
            dynamicClasses.add(klass);
        }
    }

    public Object insert(Class klass) throws EstoreException {
        try {
            Object obj = klass.getConstructor().newInstance();
            captureAll(obj);
            return obj;
        } catch (Exception e) {
            throw new EstoreException();
        }
    }

    public void insert(Object obj) throws EstoreException {
        if (obj == null) {
            return;
        }

        // We use System.identityHashCode instead of Object.hashCode since
        // the latter can be overriden.
        long objHashCode = estoreHashCode(obj);

        if (datastore.containsKey(objHashCode)) {
            return;
        }

        Class objClass = obj.getClass();
        String className = objClass.getName();
        ClassInfo cInfo;
        if (isClassExcluded(className)) {
            return;
        }
        datastore.put(objHashCode, obj);
        if (labelObjectMap.get(className) == null) {
            cInfo = labelObjectMap.putNew(objClass, new ArrayList<Object>());
            labelClassInfoMap.put(className, cInfo);
        }
        labelObjectMap.get(className).add(obj);
    }

    public void captureAll(Object obj) throws EstoreException {
        if (options.getUseDfs()) {
            if (options.getUseRecursion()) {
                captureAllDfsRec(obj);
            } else {
                captureAllDfs(obj);
            }
        } else {
            captureAllBfs(obj);
        }
    }

    public void captureAllBfs(Object obj) throws EstoreException {
        if (obj == null) {
            throw new EstoreException();
        }

        Queue<Object> queue = new LinkedList<>();
        queue.offer(obj);

        while (!queue.isEmpty()) {
            Object current = queue.poll();
            long objHashCode = estoreHashCode(current);

            if (datastore.containsKey(objHashCode)) {
                continue;
            }

            // When current is a Class (e.g. Engine.class), use Class.class for field lookup so we
            // read
            // Class's fields on the Class instance; using (Class)current would use Engine's fields
            // on
            // Engine.class (a Class instance) and cause IllegalArgumentException.
            Class objClass = (current instanceof Class) ? Class.class : current.getClass();
            String className =
                    (current instanceof Class)
                            ? ((Class<?>) current).getName()
                            : current.getClass().getName();

            if (isClassExcluded(className)) {
                continue;
            }

            datastore.put(objHashCode, current);

            ClassInfo cInfo;
            if (labelObjectMap.get(className) == null) {
                ArrayList<Object> labelList = new ArrayList<>();
                if (current instanceof Class) {
                    if (labelClassInfoMap.get(Class.class.getName()) == null) {
                        ClassInfo classCInfo =
                                labelObjectMap.putNew(Class.class, new ArrayList<>());
                        labelClassInfoMap.put(Class.class.getName(), classCInfo);
                    }
                    cInfo = labelClassInfoMap.get(Class.class.getName());
                    labelObjectMap.put(className, labelList);
                    labelClassInfoMap.put(className, cInfo); /* for query execution by label */
                } else {
                    cInfo = labelObjectMap.putNew(objClass, labelList);
                    labelClassInfoMap.put(className, cInfo);
                }
            } else {
                cInfo = labelClassInfoMap.get(className);
            }
            ClassInfo cInfoForTraversal = getOrCreateClassInfo(objClass);

            if (!objClass.isArray()) {
                for (String fieldName : cInfoForTraversal.getReferenceFieldNames(current)) {
                    try {
                        Field field = getDeclaredFieldIncludingSuper(objClass, fieldName);
                        field.setAccessible(true);
                        Object fieldObject = field.get(current);
                        if (fieldObject != null) {
                            queue.offer(fieldObject);
                        }
                    } catch (Exception e) {
                        if (current instanceof Class)
                            continue; // skip inaccessible Class fields (e.g. genericInfo)
                        e.printStackTrace();
                        throw new EstoreException(e);
                    }
                }
                if (current instanceof Class)
                    offerStaticReferenceFieldsOfClass((Class<?>) current, queue);
            } else {
                if (current instanceof Class) {
                    continue;
                }
                try {
                    int length = Array.getLength(current);
                    if (shouldCaptureElementsIndividually(objClass)) {
                        for (int j = 0; j < length; j++) {
                            Object element = Array.get(current, j);
                            if (element != null) {
                                queue.offer(element);
                            }
                        }
                    } else {
                        for (int j = 0; j < length; j++) {
                            insert(Array.get(current, j));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new EstoreException(e);
                }
            }

            labelObjectMap.get(className).add(current);
        }
    }

    public void captureAllDfs(Object obj) throws EstoreException {
        if (obj == null) {
            throw new EstoreException();
        }

        Stack<Object> stack = new Stack<>();
        stack.push(obj);

        while (!stack.isEmpty()) {
            Object current = stack.pop();
            long objHashCode = estoreHashCode(current);

            if (datastore.containsKey(objHashCode)) {
                continue;
            }

            Class<?> objClass = (current instanceof Class) ? Class.class : current.getClass();
            String className =
                    (current instanceof Class)
                            ? ((Class<?>) current).getName()
                            : current.getClass().getName();

            if (isClassExcluded(className)) {
                continue;
            }

            datastore.put(objHashCode, current);

            ClassInfo cInfo;
            if (labelObjectMap.get(className) == null) {
                ArrayList<Object> labelList = new ArrayList<>();
                if (current instanceof Class) {
                    if (labelClassInfoMap.get(Class.class.getName()) == null) {
                        ClassInfo classCInfo =
                                labelObjectMap.putNew(Class.class, new ArrayList<>());
                        labelClassInfoMap.put(Class.class.getName(), classCInfo);
                    }
                    cInfo = labelClassInfoMap.get(Class.class.getName());
                    labelObjectMap.put(className, labelList);
                    labelClassInfoMap.put(className, cInfo);
                } else {
                    cInfo = labelObjectMap.putNew(objClass, labelList);
                    labelClassInfoMap.put(className, cInfo);
                }
            } else {
                cInfo = labelClassInfoMap.get(className);
            }
            ClassInfo cInfoForTraversal = getOrCreateClassInfo(objClass);

            if (!objClass.isArray()) {
                for (String fieldName : cInfoForTraversal.getReferenceFieldNames(current)) {
                    try {
                        Field field = getDeclaredFieldIncludingSuper(objClass, fieldName);
                        field.setAccessible(true);
                        Object fieldObject = field.get(current);
                        if (fieldObject != null) {
                            stack.push(fieldObject);
                        }
                    } catch (Exception e) {
                        if (current instanceof Class) continue;
                        e.printStackTrace();
                        throw new EstoreException(e);
                    }
                }
                if (current instanceof Class)
                    pushStaticReferenceFieldsOfClass((Class<?>) current, stack);
            } else {
                if (current instanceof Class) {
                    continue;
                }
                try {
                    int length = Array.getLength(current);
                    if (shouldCaptureElementsIndividually(objClass)) {
                        for (int j = 0; j < length; j++) {
                            Object element = Array.get(current, j);
                            if (element != null) {
                                stack.push(element);
                            }
                        }
                    } else {
                        for (int j = 0; j < length; j++) {
                            insert(Array.get(current, j));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new EstoreException(e);
                }
            }

            labelObjectMap.get(className).add(current);
        }
    }

    public void captureAllDfsRec(Object obj) throws EstoreException {
        if (obj == null) {
            return;
        }

        // We use System.identityHashCode instead of Object.hashCode since
        // the latter can be overriden.
        long objHashCode = estoreHashCode(obj);

        if (datastore.containsKey(objHashCode)) {
            return;
        }

        Class objClass;
        String className;
        if (obj instanceof Class) {
            objClass = Class.class;
            className = ((Class<?>) obj).getName();
        } else {
            objClass = obj.getClass();
            className = objClass.getName();
        }
        ClassInfo cInfo;
        if (isClassExcluded(className)) {
            return;
        }
        datastore.put(objHashCode, obj);
        if (labelObjectMap.get(className) == null) {
            ArrayList<Object> labelList = new ArrayList<Object>();
            if (obj instanceof Class) {
                if (labelClassInfoMap.get(Class.class.getName()) == null) {
                    ClassInfo classCInfo =
                            labelObjectMap.putNew(Class.class, new ArrayList<>());
                    labelClassInfoMap.put(Class.class.getName(), classCInfo);
                }
                cInfo = labelClassInfoMap.get(Class.class.getName());
                labelObjectMap.put(className, labelList);
                labelClassInfoMap.put(className, cInfo);
            } else {
                cInfo = labelObjectMap.putNew(objClass, labelList);
                labelClassInfoMap.put(className, cInfo);
            }
        } else {
            cInfo = labelClassInfoMap.get(className);
        }
        ClassInfo cInfoForTraversal = getOrCreateClassInfo(objClass);
        if (!objClass.isArray()) {

            for (String fieldName : cInfoForTraversal.getReferenceFieldNames(obj)) {
                try {
                    Field field = getDeclaredFieldIncludingSuper(objClass, fieldName);
                    field.setAccessible(true);
                    Object fieldObject = field.get(obj);
                    captureAllDfsRec(fieldObject);
                } catch (Exception e) {
                    if (obj instanceof Class) continue;
                    e.printStackTrace();
                    throw new EstoreException(e);
                }
            }
            if (obj instanceof Class) {
                for (Object val : getStaticReferenceFieldsOfClass((Class<?>) obj))
                    captureAllDfsRec(val);
            }
        } else {
            if (obj instanceof Class) {
                return;
            }
            try {
                int length = Array.getLength(obj);
                if (shouldCaptureElementsIndividually(objClass)) {
                    for (int j = 0; j < length; j++) {
                        captureAllDfsRec(Array.get(obj, j));
                    }
                } else {
                    for (int j = 0; j < length; j++) {
                        insert(Array.get(obj, j));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new EstoreException(e);
            }
        }
        labelObjectMap.get(className).add(obj);
    }

    public void captureAll(Object obj, int maxDepth, int currDepth) throws EstoreException {
        if (obj == null) {
            return;
        }

        if (currDepth == maxDepth) {
            return;
        }

        // We use System.identityHashCode instead of Object.hashCode since
        // the latter can be overriden.
        long objHashCode = estoreHashCode(obj);

        if (datastore.containsKey(objHashCode)) {
            return;
        }

        Class objClass;
        String className;
        if (obj instanceof Class) {
            objClass = Class.class;
            className = ((Class<?>) obj).getName();
        } else {
            objClass = obj.getClass();
            className = objClass.getName();
        }
        ClassInfo cInfo;
        if (isClassExcluded(className)) {
            return;
        }
        datastore.put(objHashCode, obj);
        if (labelObjectMap.get(className) == null) {
            ArrayList<Object> labelList = new ArrayList<Object>();
            if (obj instanceof Class) {
                if (labelClassInfoMap.get(Class.class.getName()) == null) {
                    ClassInfo classCInfo =
                            labelObjectMap.putNew(Class.class, new ArrayList<>());
                    labelClassInfoMap.put(Class.class.getName(), classCInfo);
                }
                cInfo = labelClassInfoMap.get(Class.class.getName());
                labelObjectMap.put(className, labelList);
                labelClassInfoMap.put(className, cInfo);
            } else {
                cInfo = labelObjectMap.putNew(objClass, labelList);
                labelClassInfoMap.put(className, cInfo);
            }
        } else {
            cInfo = labelClassInfoMap.get(className);
        }
        ClassInfo cInfoForTraversal = getOrCreateClassInfo(objClass);
        if (!objClass.isArray()) {

            for (String fieldName : cInfoForTraversal.getReferenceFieldNames(obj)) {
                try {
                    Field field = getDeclaredFieldIncludingSuper(objClass, fieldName);
                    field.setAccessible(true);
                    Object fieldObject = field.get(obj);
                    captureAll(fieldObject, maxDepth, currDepth + 1);
                } catch (Exception e) {
                    if (obj instanceof Class) continue;
                    e.printStackTrace();
                    throw new EstoreException(e);
                }
            }
            if (obj instanceof Class) {
                for (Object val : getStaticReferenceFieldsOfClass((Class<?>) obj))
                    captureAll(val, maxDepth, currDepth + 1);
            }
        } else {
            if (obj instanceof Class) {
                return;
            }
            try {
                int length = Array.getLength(obj);
                if (shouldCaptureElementsIndividually(objClass)) {
                    for (int j = 0; j < length; j++) {
                        captureAll(Array.get(obj, j), maxDepth, currDepth + 1);
                    }
                } else {
                    for (int j = 0; j < length; j++) {
                        insert(Array.get(obj, j));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new EstoreException(e);
            }
        }
        labelObjectMap.get(className).add(obj);
    }

    private long estoreHashCode(Object obj) {
        return System.identityHashCode(obj);
    }

    /**
     * Collect static reference field values of the given class (e.g. Engine) to reach singletons
     * like H2 Database.
     */
    private java.util.List<Object> getStaticReferenceFieldsOfClass(Class<?> clazz) {
        java.util.List<Object> out = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (shouldSkipField(f)) continue;
            if (!Modifier.isStatic(f.getModifiers())) continue;
            Class<?> type = f.getType();
            if (type.isPrimitive() || type == String.class) continue;
            try {
                f.setAccessible(true);
                Object val = f.get(null);
                if (val != null) out.add(val);
            } catch (Exception ignored) {
                /* skip inaccessible (e.g. genericInfo) */
            }
        }
        return out;
    }

    private void offerStaticReferenceFieldsOfClass(Class<?> clazz, Queue<Object> queue) {
        for (Object val : getStaticReferenceFieldsOfClass(clazz)) queue.offer(val);
    }

    private void pushStaticReferenceFieldsOfClass(Class<?> clazz, Stack<Object> stack) {
        for (Object val : getStaticReferenceFieldsOfClass(clazz)) stack.push(val);
    }

    /**
     * ClassInfo for traversal only: keyed by runtime class so Engine instance gets Engine's
     * ClassInfo.
     */
    private ClassInfo getOrCreateClassInfo(Class<?> clazz) {
        String key = clazz.getName();
        ClassInfo cInfo = runtimeClassInfoMap.get(key);
        if (cInfo != null) return cInfo;
        cInfo = ClassInfo.getClassInfo(clazz);
        try {
            for (Field f : getDeclaredFieldsIncludingSuper(clazz)) {
                try {
                    f.setAccessible(true);
                } catch (Exception ignored) {
                    continue;
                }
                Class<?> ft = f.getType();
                if (ft.isPrimitive() || ft == String.class) cInfo.addPrimitiveField(f);
                else cInfo.addReferenceField(f);
            }
        } catch (Exception ignored) {
        }
        runtimeClassInfoMap.put(key, cInfo);
        return cInfo;
    }

    /**
     * Declared fields of {@code clazz} and its superclasses (excluding {@link Object}). Inherited
     * static fields are skipped so capture does not follow class-wide caches on parents.
     */
    private static Field[] getDeclaredFieldsIncludingSuper(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<Field>();
        boolean isLeaf = true;
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (shouldSkipField(f)) {
                    continue;
                }
                if (!isLeaf && Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                fields.add(f);
            }
            isLeaf = false;
        }
        return fields.toArray(new Field[0]);
    }

    private static Field getDeclaredFieldIncludingSuper(Class<?> clazz, String name)
            throws NoSuchFieldException {
        boolean isLeaf = true;
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                if (isLeaf || !Modifier.isStatic(f.getModifiers())) {
                    return f;
                }
            } catch (NoSuchFieldException ignored) {
            }
            isLeaf = false;
        }
        throw new NoSuchFieldException(name);
    }

    private static boolean shouldSkipField(Field field) {
        return field.isSynthetic();
    }

    private boolean isClassExcluded(String className) {
        if (className.contains("jdk.")
                || className.contains("java.lang.module")
                || className.contains("java.lang.invoke")
                || className.contains("java.security")
                || className.contains("java.lang.Class")) {
            return true;
        }
        return false;
    }

    private boolean shouldCaptureElementsIndividually(Class<?> arrayClass) {
        if (arrayClass == null || !arrayClass.isArray()) {
            return false;
        }
        Class<?> component = arrayClass.getComponentType();
        return component != null && !component.isPrimitive();
    }

    public Table query(String cQuery) {
        // TwoNodeRelationScan.clearCache();
        long t1 = System.nanoTime();
        CypherLexer lexer = new CypherLexer(CharStreams.fromString(cQuery));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        CypherParser parser = new CypherParser(tokenStream);
        ParseTree tree = parser.oC_Cypher();
        long t2 = System.nanoTime();
        LogicalPlanBuilder builder = new LogicalPlanBuilder(this);
        LogicalPlan plan = ((LogicalPlan) builder.visit(tree));
        long t3 = System.nanoTime();
        Table result = plan.execute(null);
        long t4 = System.nanoTime();

        if (options.getProfileParseTreeGenTime()) {
            System.out.println("Parse Tree Generation Time : " + (t2 - t1));
        }
        if (options.getProfileParseQueryPlanASTBuildTime()) {
            System.out.println("Query Plan AST Building Time : " + (t3 - t2));
        }
        if (options.getProfileQueryExecutionTime()) {
            System.out.println("Query Execution Time : " + (t4 - t3));
        }
        if (options.getProfileTotalQueryTime()) {
            System.out.println("Total Query Time : " + (t4 - t1));
        }
        return result;
    }

    public long getLong(Object obj, String fieldName) throws EstoreException {
        try {
            Field fld = obj.getClass().getDeclaredField(fieldName);
            fld.setAccessible(true);
            return fld.getLong(obj);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new EstoreException();
        }
    }

    public double getDouble(Object obj, String fieldName) throws EstoreException {
        try {
            Field fld = obj.getClass().getDeclaredField(fieldName);
            fld.setAccessible(true);
            return fld.getDouble(obj);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new EstoreException();
        }
    }

    public String getString(Object obj, String fieldName) throws EstoreException {
        try {
            Field fld = obj.getClass().getDeclaredField(fieldName);
            fld.setAccessible(true);
            return (String) fld.get(obj);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new EstoreException();
        }
    }

    public static class LabelMap extends HashMap<String, ArrayList<Object>> {
        public ClassInfo putNew(Class classInstance, ArrayList<Object> value) {
            ClassInfo cInfo = ClassInfo.getClassInfo(classInstance);
            if (classInstance.isArray()) {
                super.put(classInstance.getName(), value);
                return cInfo;
            }
            Field[] fields = getDeclaredFieldsIncludingSuper(classInstance);
            for (int j = 0; j < fields.length; j++) {
                try {
                    fields[j].setAccessible(true);
                } catch (Exception ignored) {
                    continue;
                }
                Class<?> fieldType = fields[j].getType();
                if (fieldType.isPrimitive() || fieldType.equals(String.class)) {
                    cInfo.addPrimitiveField(fields[j]);
                } else {
                    cInfo.addReferenceField(fields[j]);
                }
            }
            super.put(classInstance.getName(), value);
            return cInfo;
        }
    }
}
