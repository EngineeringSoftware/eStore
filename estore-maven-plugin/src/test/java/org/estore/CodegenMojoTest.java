package org.estore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CodegenMojoTest {

    @TempDir Path srcDir;

    private Path writeSource(String className, String body) throws IOException {
        Path pkg = Files.createDirectories(srcDir.resolve("p"));
        String source =
                "package p;\n"
                        + "import org.estore.Estore;\n"
                        + "import org.estore.compiler.CompileQuery;\n"
                        + "public class "
                        + className
                        + " {\n"
                        + "    Estore db;\n"
                        + body
                        + "}\n";
        Path file = pkg.resolve(className + ".java");
        Files.write(file, source.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private static String read(Path file) throws IOException {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private void runMojo(Path sourceDir) throws MojoExecutionException {
        CodegenMojo mojo = new CodegenMojo();
        mojo.sourceDir = sourceDir.toFile();
        mojo.profiling = false;
        mojo.execute();
    }

    @Test
    void writesTransformedFileForAnnotatedQueries() throws Exception {
        writeSource("ATest", "    @CompileQuery void m() { db.query(\"MATCH (n) RETURN n\"); }\n");

        runMojo(srcDir);

        Path transformed = srcDir.resolve("p/TransformedATest.java");
        assertTrue(Files.exists(transformed));
        String code = read(transformed);
        assertTrue(code.contains("class TransformedATest"));
        assertFalse(code.contains("db.query("));
    }

    @Test
    void skipsFilesWithoutAnnotatedMethods() throws Exception {
        Path original =
                writeSource("BTest", "    void m() { db.query(\"MATCH (n) RETURN n\"); }\n");
        String before = read(original);

        runMojo(srcDir);

        assertFalse(Files.exists(srcDir.resolve("p/TransformedBTest.java")));
        assertEquals(before, read(original));
    }

    @Test
    void isIdempotentWhenRunTwice() throws Exception {
        writeSource("CTest", "    @CompileQuery void m() { db.query(\"MATCH (n) RETURN n\"); }\n");

        runMojo(srcDir);
        String first = read(srcDir.resolve("p/TransformedCTest.java"));
        runMojo(srcDir);

        assertEquals(first, read(srcDir.resolve("p/TransformedCTest.java")));
        assertFalse(Files.exists(srcDir.resolve("p/TransformedTransformedCTest.java")));
    }

    @Test
    void failsBuildNamingFileForUnsupportedQuery() throws Exception {
        Path file =
                writeSource(
                        "DTest",
                        "    @CompileQuery void m() {"
                                + " db.query(\"MATCH (n) WHERE n.x = 1 RETURN n\"); }\n");

        MojoExecutionException e =
                assertThrows(MojoExecutionException.class, () -> runMojo(srcDir));

        assertTrue(e.getMessage().contains(file.toString()), e.getMessage());
        assertTrue(e.getMessage().contains("WHERE is not supported"), e.getMessage());
        assertFalse(Files.exists(srcDir.resolve("p/TransformedDTest.java")));
    }

    @Test
    void failsBuildForNonLiteralQuery() throws Exception {
        writeSource("ETest", "    @CompileQuery void m(String q) { db.query(q); }\n");

        MojoExecutionException e =
                assertThrows(MojoExecutionException.class, () -> runMojo(srcDir));

        assertTrue(e.getMessage().contains("string literal"), e.getMessage());
    }

    @Test
    void skipsMissingSourceDirectory() throws Exception {
        Path missing = srcDir.resolve("missing");

        runMojo(missing);

        assertFalse(Files.exists(missing));
    }
}
