package org.estore.compiler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method whose {@code db.query("...")} calls are compiled to Java by the codegen
 * plugin. Only methods in the test sources are processed; the annotation has no effect elsewhere.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface CompileQuery {}
