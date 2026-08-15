package org.estore.planner.util;

public final class TypeUtils {

    private TypeUtils() {}

    public static boolean isDecimalType(String fieldType) {
        switch (fieldType) {
            case "byte":
            case "short":
            case "int":
            case "long":
                return true;
            default:
                return false;
        }
    }

    public static boolean isFloatingType(String fieldType) {
        switch (fieldType) {
            case "float":
            case "double":
                return true;
            default:
                return false;
        }
    }
}
