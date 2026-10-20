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
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;

public class CodegenMojoTest {

    @TempDir Path root;

    private Path srcDir;
    private Path outDir;
    private MavenProject project;
    private String className;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        String method = testInfo.getTestMethod().get().getName();
        className = Character.toUpperCase(method.charAt(0)) + method.substring(1) + "Test";
        srcDir = root.resolve("src");
        outDir = root.resolve("target/generated-test-sources/compiled-queries");
        project = new MavenProject();
    }

    private Path writeSource(String body) throws IOException {
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

    private Path transformedPath() {
        return outDir.resolve("p/Transformed" + className + ".java");
    }

    private static String read(Path file) throws IOException {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private void runMojo() throws MojoExecutionException {
        CodegenMojo mojo = new CodegenMojo();
        mojo.sourceDir = srcDir.toFile();
        mojo.outputDir = outDir.toFile();
        mojo.project = project;
        mojo.profiling = false;
        mojo.execute();
    }

    @Test
    void writesTransformedFileIntoOutputPackage() throws Exception {
        writeSource("    @CompileQuery void m() { db.query(\"MATCH (n) RETURN n\"); }\n");

        runMojo();

        assertTrue(Files.exists(transformedPath()));
        String code = read(transformedPath());
        assertTrue(code.contains("class Transformed" + className));
        assertFalse(code.contains("db.query("));
        assertFalse(Files.exists(srcDir.resolve("p/Transformed" + className + ".java")));
    }

    @Test
    void registersOutputDirAsTestSourceRoot() throws Exception {
        writeSource("    @CompileQuery void m() { db.query(\"MATCH (n) RETURN n\"); }\n");

        runMojo();

        assertTrue(project.getTestCompileSourceRoots().contains(outDir.toString()));
    }

    @Test
    void skipsFilesWithoutAnnotatedMethods() throws Exception {
        Path original = writeSource("    void m() { db.query(\"MATCH (n) RETURN n\"); }\n");
        String before = read(original);

        runMojo();

        assertFalse(Files.exists(transformedPath()));
        assertEquals(before, read(original));
    }

    @Test
    void removesOutputForSourcesThatNoLongerExist() throws Exception {
        Path source =
                writeSource("    @CompileQuery void m() { db.query(\"MATCH (n) RETURN n\"); }\n");
        runMojo();
        assertTrue(Files.exists(transformedPath()));
        Files.delete(source);

        runMojo();

        assertFalse(Files.exists(transformedPath()));
    }

    @Test
    void failsBuildNamingFileForUnsupportedQuery() throws Exception {
        Path file =
                writeSource(
                        "    @CompileQuery void m() {"
                                + " db.query(\"MATCH (n) WHERE n.x = 1 RETURN n\"); }\n");

        MojoExecutionException e = assertThrows(MojoExecutionException.class, this::runMojo);

        assertTrue(e.getMessage().contains(file.toString()), e.getMessage());
        assertTrue(e.getMessage().contains("WHERE is not supported"), e.getMessage());
        assertFalse(Files.exists(transformedPath()));
    }

    @Test
    void refusesOutputDirThatContainsSources() throws Exception {
        Path source =
                writeSource("    @CompileQuery void m() { db.query(\"MATCH (n) RETURN n\"); }\n");
        outDir = root;

        MojoExecutionException e = assertThrows(MojoExecutionException.class, this::runMojo);

        assertTrue(e.getMessage().contains("must be a separate directory"), e.getMessage());
        assertTrue(Files.exists(source));
    }

    @Test
    void skipsMissingSourceDirectory() throws Exception {
        runMojo();

        assertFalse(Files.exists(outDir));
        assertTrue(project.getTestCompileSourceRoots().isEmpty());
    }
}
