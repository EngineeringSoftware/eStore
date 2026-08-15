package org.estore.planner.util;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class ClassHelper {

    private static final DynamicClassLoader classLoader = new DynamicClassLoader();

    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Static method to create classes dynamically using ASM
     *
     * @param className Name of the class
     * @param packageName Name of the package containing the class (must be null if no package)
     * @param fieldNames String array containing field names
     * @param fieldTypes String array containing field types
     * @return The dynamically created class's class instance
     */
    public static Class<?> createClass(
            String className,
            String packageName,
            String[] fieldNames,
            Class[] fieldTypes,
            Object[] fieldValues) {
        String fullyQualifiedName =
                packageName != null ? (packageName + "/" + className) : className;
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC,
                fullyQualifiedName,
                null,
                "java/lang/Object",
                null);
        // NOTE : Field values are null since assigning field values are only supported for static
        // fields
        // see :
        // https://asm.ow2.io/javadoc/org/objectweb/asm/ClassVisitor.html#visitField(int,java.lang.String,java.lang.String,java.lang.String,java.lang.Object)
        for (int j = 0; j < fieldNames.length; j++) {
            // String fieldType = "L" + fieldTypes[j].replace(".", "/") + ";";
            cw.visitField(
                            Opcodes.ACC_PUBLIC,
                            fieldNames[j],
                            Type.getDescriptor(fieldTypes[j]),
                            null,
                            null)
                    .visitEnd();
        }
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);

        cw.visitEnd();
        fullyQualifiedName = fullyQualifiedName.replace('/', '.');
        return classLoader.defineClass(fullyQualifiedName, cw.toByteArray());
    }

    private static class DynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
