package org.estore.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Command-line entry point that rewrites every literal query call in one Java file. Usage: {@code
 * ImplCodeGen [input.java [output.java]]}.
 */
public class ImplCodeGen {
    private static final String DEFAULT_INPUT_FILE_PATH =
            "src/test/java/org/estore/compiler/CodeGenTest.java";

    private static String getDefaultOutputFilePath(String inputFilePath) {
        File inputFile = new File(inputFilePath);
        String parentDir = inputFile.getParent();
        String fileName = inputFile.getName();
        return Paths.get(parentDir, QueryRewriter.TRANSFORMED_PREFIX + fileName).toString();
    }

    public static void main(String[] args) throws IOException {
        String inputFilePath = args.length > 0 ? args[0] : DEFAULT_INPUT_FILE_PATH;
        String outputFilePath = args.length > 1 ? args[1] : getDefaultOutputFilePath(inputFilePath);
        String source =
                new String(Files.readAllBytes(Paths.get(inputFilePath)), StandardCharsets.UTF_8);
        String rewritten = QueryRewriter.rewriteAllQueries(source);
        Files.write(Paths.get(outputFilePath), rewritten.getBytes(StandardCharsets.UTF_8));
    }
}
