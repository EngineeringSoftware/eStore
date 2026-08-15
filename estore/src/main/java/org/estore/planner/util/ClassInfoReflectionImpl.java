package org.estore.planner.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ClassInfoReflectionImpl extends ClassInfo {
    protected HashMap<String, Field> primitiveFieldMap = new HashMap<String, Field>();
    protected HashMap<String, Field> referenceFieldMap = new HashMap<String, Field>();

    public ClassInfoReflectionImpl(Class classInstance) {
        this.classInstance = classInstance;
        primitiveFieldMap = new HashMap<String, Field>();
        referenceFieldMap = new HashMap<String, Field>();
        referenceFieldTypeMap = new HashMap<String, String>();
        primitiveFieldTypeMap = new HashMap<String, String>();
    }

    @Override
    public void addPrimitiveField(Field field) {
        String fieldName = field.getName();
        primitiveFieldMap.put(fieldName, field);
        primitiveFieldTypeMap.put(fieldName, field.getType().getName());
    }

    @Override
    public void addReferenceField(Field field) {
        String fieldName = field.getName();
        referenceFieldMap.put(fieldName, field);
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
        Field field;

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
                    field = primitiveFieldMap.get(fieldName);
                    return field.getByte(instance);
                case "short":
                    if (fieldType != null && !TypeUtils.isDecimalType(fieldType)) {
                        return null;
                    }
                    field = primitiveFieldMap.get(fieldName);
                    return field.getShort(instance);
                case "int":
                    if (fieldType != null && !TypeUtils.isDecimalType(fieldType)) {
                        return null;
                    }
                    field = primitiveFieldMap.get(fieldName);
                    return field.getInt(instance);
                case "long":
                    if (fieldType != null && !TypeUtils.isDecimalType(fieldType)) {
                        return null;
                    }
                    field = primitiveFieldMap.get(fieldName);
                    return field.getLong(instance);
                case "float":
                    if (fieldType != null && !TypeUtils.isFloatingType(fieldType)) {
                        return null;
                    }
                    field = primitiveFieldMap.get(fieldName);
                    return field.getFloat(instance);
                case "double":
                    if (fieldType != null && !TypeUtils.isFloatingType(fieldType)) {
                        return null;
                    }
                    field = primitiveFieldMap.get(fieldName);
                    return field.getDouble(instance);
                case "boolean":
                    if (fieldType != null && !fieldType.equals("boolean")) {
                        return null;
                    }
                    field = primitiveFieldMap.get(fieldName);
                    return field.getBoolean(instance);
                case "char":
                    if (fieldType != null && !fieldType.equals("char")) {
                        return null;
                    }
                    field = primitiveFieldMap.get(fieldName);
                    return field.getChar(instance);
                case "java.lang.String":
                    if (fieldType != null && !fieldType.equals("java.lang.String")) {
                        return null;
                    }
                    field = primitiveFieldMap.get(fieldName);
                    return field.get(instance);
                default:
                    return null;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Object> getReferenceField(String fieldName, Object instance) {
        Field field = referenceFieldMap.get(fieldName);
        if (field == null) {
            return null;
        }
        try {
            List<Object> elements = new ArrayList<Object>();
            if (field.getType().isArray()) {
                Object arrayObject = field.get(instance);
                if (arrayObject == null) {
                    return elements;
                }
                int length = Array.getLength(arrayObject);
                for (int j = 0; j < length; j++) {
                    elements.add(Array.get(arrayObject, j));
                }
            } else {
                elements.add(field.get(instance));
            }
            return elements;
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
