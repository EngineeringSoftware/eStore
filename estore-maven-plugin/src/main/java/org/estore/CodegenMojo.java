package org.estore;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.estore.compiler.QueryCompileException;
import org.estore.compiler.QueryRewriter;

/**
 * Rewrites the queries in every {@code @CompileQuery} method under {@code sourceDir} into Java
 * code, writing each rewritten file next to its source as {@code Transformed<Name>.java}.
 */
@Mojo(name = "codegen", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class CodegenMojo extends AbstractMojo {

    @Parameter(property = "sourceDir", defaultValue = "${project.build.testSourceDirectory}")
    File sourceDir;

    @Parameter(property = "profiling", defaultValue = "true")
    boolean profiling;

    @Override
    public void execute() throws MojoExecutionException {
        if (!sourceDir.isDirectory()) {
            getLog().info("Skipping query rewriting: " + sourceDir + " does not exist");
            return;
        }
        long start = System.currentTimeMillis();
        int rewritten = 0;
        for (Path file : javaFiles()) {
            if (rewriteFile(file)) {
                rewritten++;
            }
        }
        getLog().info("Rewrote @CompileQuery methods in " + rewritten + " file(s)");
        if (profiling) {
            getLog().info("Total time taken: " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    private List<Path> javaFiles() throws MojoExecutionException {
        try (Stream<Path> paths = Files.walk(sourceDir.toPath())) {
            return paths.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList());
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot list Java files under " + sourceDir, e);
        }
    }

    private boolean rewriteFile(Path file) throws MojoExecutionException {
        long start = System.currentTimeMillis();
        Optional<String> rewritten;
        try {
            String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            rewritten = QueryRewriter.rewriteAnnotatedMethods(source);
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot read " + file, e);
        } catch (QueryCompileException e) {
            throw new MojoExecutionException(
                    "Cannot rewrite @CompileQuery queries in " + file + ": " + e.getMessage(), e);
        }
        if (!rewritten.isPresent()) {
            return false;
        }
        Path target = file.resolveSibling(QueryRewriter.TRANSFORMED_PREFIX + file.getFileName());
        try {
            Files.write(target, rewritten.get().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot write " + target, e);
        }
        getLog().info("Transformed " + file.getFileName() + " to " + target.getFileName());
        if (profiling) {
            getLog().info(
                            "Time taken for "
                                    + file.getFileName()
                                    + ": "
                                    + (System.currentTimeMillis() - start)
                                    + " ms");
        }
        return true;
    }
}
