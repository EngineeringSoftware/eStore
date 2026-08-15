package org.estore.planner.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayClassInfoReflectionImpl extends ClassInfo {
    private static final String ELEMENT_FIELD = "$elem";

    public ArrayClassInfoReflectionImpl(Class<?> arrayClass) {
        this.classInstance = arrayClass;
        referenceFieldTypeMap = new HashMap<>();
        primitiveFieldTypeMap = new HashMap<>();
        Class<?> component = arrayClass == null ? null : arrayClass.getComponentType();
        if (component != null) {
            referenceFieldTypeMap.put(ELEMENT_FIELD, component.getName());
        }
    }

    @Override
    public void addPrimitiveField(Field field) {}

    @Override
    public void addReferenceField(Field field) {}

    @Override
    public Object getPrimitiveField(String fieldName, String fieldType, Object instance) {
        return null;
    }

    @Override
    public List<Object> getReferenceField(String fieldName, Object instance) {
        if (instance == null || !instance.getClass().isArray()) {
            return new ArrayList<>();
        }
        if (isIndexEdgeName(fieldName)) {
            if (isValidIndexEdge(fieldName, instance)) {
                List<Object> out = new ArrayList<>();
                out.add(Array.get(instance, Integer.parseInt(fieldName)));
                return out;
            }
            return new ArrayList<>();
        }
        if (ELEMENT_FIELD.equals(fieldName) || fieldName == null) {
            List<Object> elements = new ArrayList<>();
            int length = Array.getLength(instance);
            for (int i = 0; i < length; i++) {
                elements.add(Array.get(instance, i));
            }
            return elements;
        }
        return new ArrayList<>();
    }

    @Override
    public boolean containsPrimitiveFieldWithName(String fieldName) {
        return false;
    }

    @Override
    public boolean containsReferenceFieldWithName(String fieldName, Object instance) {
        if (isIndexEdgeName(fieldName)) {
            if (instance != null) {
                return isValidIndexEdge(fieldName, instance);
            }
            return true;
        }
        return ELEMENT_FIELD.equals(fieldName);
    }

    @Override
    public boolean containsReferenceFieldWithType(String type) {
        return referenceFieldTypeMap.containsValue(type);
    }

    @Override
    public Set<String> getReferenceFieldNames(Object instance) {
        if (instance == null || !instance.getClass().isArray()) {
            return Collections.emptySet();
        }
        int length = Array.getLength(instance);
        Set<String> names = new HashSet<>();
        for (int i = 0; i < length; i++) {
            names.add(String.valueOf(i));
        }
        return names;
    }

    @Override
    public Set<String> getPrimitiveFieldNames() {
        return Collections.emptySet();
    }

    @Override
    public int getReferenceFieldCount() {
        return 1;
    }

    private static boolean isIndexEdgeName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isDigit(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidIndexEdge(String name, Object array) {
        if (!isIndexEdgeName(name) || array == null || !array.getClass().isArray()) {
            return false;
        }
        int index = Integer.parseInt(name);
        return index >= 0 && index < Array.getLength(array);
    }
}
