package org.estore.compiler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ImplCodeGenTest {

    private static final Path RESOURCES = Paths.get("src/test/resources/org/estore/compiler");

    private void runTransform(Path fixture, Path input, Path output) throws IOException {
        Files.copy(RESOURCES.resolve(fixture), input);
        if (output != null) {
            ImplCodeGen.main(new String[] {input.toString(), output.toString()});
        } else {
            ImplCodeGen.main(new String[] {input.toString()});
        }
    }

    private String readString(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Test
    void transformsMinimalSourceWithCompiledQueryCode(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("MinimalQuerySource.java");
        Path output = tempDir.resolve("TransformedMinimalQuerySource.java");
        runTransform(Paths.get("MinimalQuerySource.java"), input, output);

        String generated = readString(output);

        assertTrue(generated.contains("class TransformedMinimalQuerySource"));
        assertTrue(generated.contains("import java.util.function.Supplier;"));
        assertTrue(generated.contains("private static class DFSNode"));

        assertFalse(generated.contains("estore.query("));
        assertTrue(generated.contains("((Supplier<Table>)"));
        assertTrue(generated.contains("estore.getDataStore().values()"));
        assertTrue(generated.contains("res.put(\"n\""));
    }

    @Test
    void transformsConcatenatedQueryString(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("ConcatQuerySource.java");
        Path output = tempDir.resolve("TransformedConcatQuerySource.java");
        runTransform(Paths.get("ConcatQuerySource.java"), input, output);

        String generated = readString(output);
        assertFalse(generated.contains("estore.query("));
        assertTrue(generated.contains("((Supplier<Table>)"));
    }

    @Test
    void leavesUnsupportedQueryArgumentInPlace(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("UnsupportedQuerySource.java");
        Path output = tempDir.resolve("TransformedUnsupportedQuerySource.java");
        runTransform(Paths.get("UnsupportedQuerySource.java"), input, output);

        String generated = readString(output);
        assertTrue(generated.contains("estore.query(q)"));
        assertTrue(generated.contains("class TransformedUnsupportedQuerySource"));
    }

    @Test
    void leavesNonLiteralConcatenationInPlace(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("MixedConcatQuerySource.java");
        Path output = tempDir.resolve("TransformedMixedConcatQuerySource.java");
        runTransform(Paths.get("MixedConcatQuerySource.java"), input, output);

        String generated = readString(output);
        assertTrue(generated.contains("estore.query(\"MATCH \" + suffix)"));
        assertTrue(generated.contains("class TransformedMixedConcatQuerySource"));
    }

    @Test
    void transformsFileThatAlsoHasNonQueryMethodCalls(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("WithNonQueryCallSource.java");
        Path output = tempDir.resolve("TransformedWithNonQueryCallSource.java");
        runTransform(Paths.get("WithNonQueryCallSource.java"), input, output);

        String generated = readString(output);
        assertTrue(generated.contains("System.out.println"));
        assertFalse(generated.contains("estore.query("));
    }

    @Test
    void usesDefaultOutputPathWhenOnlyInputProvided(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("MinimalQuerySource.java");
        runTransform(Paths.get("MinimalQuerySource.java"), input, null);

        Path output = tempDir.resolve("TransformedMinimalQuerySource.java");
        assertTrue(Files.exists(output));
        assertTrue(readString(output).contains("class TransformedMinimalQuerySource"));
    }
}
