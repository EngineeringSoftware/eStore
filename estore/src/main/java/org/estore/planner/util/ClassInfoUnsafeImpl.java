package org.estore.planner.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sun.misc.Unsafe;

public class ClassInfoUnsafeImpl extends ClassInfo {
    protected Unsafe unsafe;
    protected HashMap<String, Long> primitiveFieldMap = new HashMap<String, Long>();
    protected HashMap<String, Long> referenceFieldMap = new HashMap<String, Long>();
    protected Set<String> staticFields;

    public ClassInfoUnsafeImpl(Unsafe unsafe, Class classInstance) {
        primitiveFieldMap = new HashMap<String, Long>();
        referenceFieldMap = new HashMap<String, Long>();
        referenceFieldTypeMap = new HashMap<String, String>();
        primitiveFieldTypeMap = new HashMap<String, String>();
        staticFields = new HashSet<String>();
        this.classInstance = classInstance;
        this.unsafe = unsafe;
    }

    @Override
    public void addPrimitiveField(Field field) {
        String fieldName = field.getName();
        if (Modifier.isStatic(field.getModifiers())) {
            staticFields.add(fieldName);
            primitiveFieldMap.put(fieldName, unsafe.staticFieldOffset(field));
        } else {
            primitiveFieldMap.put(fieldName, unsafe.objectFieldOffset(field));
        }
        primitiveFieldTypeMap.put(fieldName, field.getType().getName());
    }

    @Override
    public void addReferenceField(Field field) {
        String fieldName = field.getName();
        if (Modifier.isStatic(field.getModifiers())) {
            staticFields.add(fieldName);
            referenceFieldMap.put(fieldName, unsafe.staticFieldOffset(field));
        } else {
            referenceFieldMap.put(fieldName, unsafe.objectFieldOffset(field));
        }
        if (field.getType().isArray()) {
            referenceFieldTypeMap.put(fieldName, field.getType().getComponentType().getName());
        } else {
            referenceFieldTypeMap.put(fieldName, field.getType().getName());
        }
    }

    @Override
    public Object getPrimitiveField(String fieldName, String fieldType, Object instance) {
        String classFieldType = primitiveFieldTypeMap.get(fieldName);
        if (classFieldType == null) {
            return null;
        }
        long fieldOffset;

        /* Note: classFieldType is guaranteed to exist since field names and types are
         * checked with an instance's Class instance before this method is invoked
         * on an instance to get its value. The null check is avoided for speedup
         */
        try {
            switch (classFieldType) {
                case "byte":
                    if (fieldType != null && !TypeUtils.isDecimalType(fieldType)) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getByte(instance, fieldOffset);
                case "short":
                    if (fieldType != null && !TypeUtils.isDecimalType(fieldType)) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getShort(instance, fieldOffset);
                case "int":
                    if (fieldType != null && !TypeUtils.isDecimalType(fieldType)) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getInt(instance, fieldOffset);
                case "long":
                    if (fieldType != null && !TypeUtils.isDecimalType(fieldType)) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getLong(instance, fieldOffset);
                case "float":
                    if (fieldType != null && !TypeUtils.isFloatingType(fieldType)) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getFloat(instance, fieldOffset);
                case "double":
                    if (fieldType != null && !TypeUtils.isFloatingType(fieldType)) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getDouble(instance, fieldOffset);
                case "boolean":
                    if (fieldType != null && !fieldType.equals("boolean")) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getBoolean(instance, fieldOffset);
                case "char":
                    if (fieldType != null && !fieldType.equals("char")) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getChar(instance, fieldOffset);
                case "java.lang.String":
                    if (fieldType != null && !fieldType.equals("java.lang.String")) {
                        return null;
                    }
                    fieldOffset = primitiveFieldMap.get(fieldName);
                    return unsafe.getObject(instance, fieldOffset);
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Object> getReferenceField(String fieldName, Object instance) {
        long fieldOffset = referenceFieldMap.get(fieldName);
        if (fieldOffset < 0) {
            return null;
        }
        try {
            Object obj = null;
            List<Object> elements = new ArrayList<Object>();
            if (staticFields.contains(fieldName)) {
                obj = unsafe.getObject(instance.getClass(), fieldOffset);
            } else {
                obj = unsafe.getObject(instance, fieldOffset);
            }
            if (obj == null) {
                return elements;
            }
            Class objClass = obj.getClass();
            if (objClass.isArray()) {
                if (obj instanceof byte[]) {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(byte[].class);
                    int length = ((byte[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getByte(obj, offset));
                    }
                } else if (obj instanceof short[]) {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(short[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(short[].class);
                    int length = ((short[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getShort(obj, offset));
                    }
                } else if (obj instanceof int[]) {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(int[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(int[].class);
                    int length = ((int[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getInt(obj, offset));
                    }
                } else if (obj instanceof long[]) {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(long[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(long[].class);
                    int length = ((long[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getLong(obj, offset));
                    }
                } else if (obj instanceof float[]) {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(float[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(float[].class);
                    int length = ((float[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getFloat(obj, offset));
                    }
                } else if (obj instanceof double[]) {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(double[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(double[].class);
                    int length = ((double[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getDouble(obj, offset));
                    }
                } else if (obj instanceof boolean[]) {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(boolean[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(boolean[].class);
                    int length = ((boolean[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getBoolean(obj, offset));
                    }
                } else if (obj instanceof char[]) {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(char[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(char[].class);
                    int length = ((char[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getChar(obj, offset));
                    }
                } else {
                    int arrayBaseOffset = unsafe.arrayBaseOffset(Object[].class);
                    int arrayIndexScale = unsafe.arrayIndexScale(Object[].class);
                    int length = ((Object[]) obj).length;

                    for (int j = 0; j < length; j++) {
                        int offset = arrayBaseOffset + arrayIndexScale * j;
                        elements.add(unsafe.getObject(obj, offset));
                    }
                }
                return elements;
            } else {
                elements.add(obj);
                return elements;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean containsPrimitiveFieldWithName(String fieldName) {
        return primitiveFieldMap.get(fieldName) != null;
    }

    @Override
    public boolean containsReferenceFieldWithName(String fieldName, Object instance) {
        return referenceFieldMap.get(fieldName) != null;
    }

    @Override
    public Set<String> getReferenceFieldNames(Object instance) {
        return referenceFieldMap.keySet();
    }

    @Override
    public Set<String> getPrimitiveFieldNames() {
        return primitiveFieldMap.keySet();
    }

    @Override
    public int getReferenceFieldCount() {
        return referenceFieldMap.size();
    }
}
