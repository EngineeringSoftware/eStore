package org.estore.planner.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import sun.misc.Unsafe;

public abstract class ClassInfo {
    protected HashMap<String, String> referenceFieldTypeMap;
    /* We need this to handle inter-matchability between byte, short, int, long
     * since we can not infer type for them from Cypher query string
     */
    protected HashMap<String, String> primitiveFieldTypeMap;
    public Class classInstance;

    public static ClassInfo getClassInfo(boolean useUnsafe, Unsafe unsafe, Class classInstance) {
        if (classInstance.isArray()) {
            return new ArrayClassInfoReflectionImpl(classInstance);
        }
        if (useUnsafe) {
            return new ClassInfoUnsafeImpl(unsafe, classInstance);
        } else {
            return new ClassInfoReflectionImpl(classInstance);
        }
    }

    public abstract void addPrimitiveField(Field field);

    public abstract void addReferenceField(Field field);

    public abstract Object getPrimitiveField(String fieldName, String fieldType, Object instance);

    public abstract List<Object> getReferenceField(String fieldName, Object instance);

    public abstract boolean containsPrimitiveFieldWithName(String fieldName);

    public abstract boolean containsReferenceFieldWithName(String fieldName, Object instance);

    public boolean containsReferenceFieldWithType(String type) {
        return referenceFieldTypeMap.containsValue(type);
    }

    public boolean containsPrimitiveFieldWithType(String type) {
        return primitiveFieldTypeMap.containsValue(type);
    }

    public HashMap<String, String> getReferenceFieldTypeMap(Object instance) {
        return referenceFieldTypeMap;
    }

    public abstract Set<String> getReferenceFieldNames(Object instance);

    public abstract Set<String> getPrimitiveFieldNames();

    public abstract int getReferenceFieldCount();
}
