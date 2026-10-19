package org.estore;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.estore.compiler.QueryCompileException;
import org.estore.compiler.QueryRewriter;

/**
 * Rewrites the queries in every {@code @CompileQuery} method under {@code sourceDir} into Java
 * code. Each rewritten file is written to the same package under {@code outputDir} as {@code
 * Transformed<Name>.java}, and {@code outputDir} is added as a test source root.
 */
@Mojo(name = "codegen", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class CodegenMojo extends AbstractMojo {

    @Parameter(property = "sourceDir", defaultValue = "${project.build.testSourceDirectory}")
    File sourceDir;

    @Parameter(
            property = "outputDir",
            defaultValue = "${project.build.directory}/generated-test-sources/compiled-queries")
    File outputDir;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "profiling", defaultValue = "true")
    boolean profiling;

    @Override
    public void execute() throws MojoExecutionException {
        if (!sourceDir.isDirectory()) {
            getLog().info("Skipping query rewriting: " + sourceDir + " does not exist");
            return;
        }
        long start = System.currentTimeMillis();
        deleteOutputDir();
        int rewritten = 0;
        for (Path file : javaFiles()) {
            if (rewriteFile(file)) {
                rewritten++;
            }
        }
        project.addTestCompileSourceRoot(outputDir.getPath());
        getLog().info(
                        "Rewrote @CompileQuery methods in "
                                + rewritten
                                + " file(s) into "
                                + outputDir);
        if (profiling) {
            getLog().info("Total time taken: " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    private void deleteOutputDir() throws MojoExecutionException {
        Path dir = outputDir.toPath();
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path p : paths.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                Files.delete(p);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot clear stale output in " + outputDir, e);
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
        Path packageDir =
                outputDir.toPath().resolve(sourceDir.toPath().relativize(file.getParent()));
        Path target = packageDir.resolve(QueryRewriter.TRANSFORMED_PREFIX + file.getFileName());
        try {
            Files.createDirectories(packageDir);
            Files.write(target, rewritten.get().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot write " + target, e);
        }
        getLog().info("Transformed " + file.getFileName() + " to " + target.getFileName());
        return true;
    }
}
