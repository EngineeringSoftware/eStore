package org.estore.compiler;

/** Thrown when a Cypher query cannot be compiled into Java code. */
public class QueryCompileException extends RuntimeException {

    public QueryCompileException(String message) {
        super(message);
    }

    public QueryCompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
