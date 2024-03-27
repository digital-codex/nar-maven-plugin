package dev.codex.java.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(
        name = "nar",
        defaultPhase = LifecyclePhase.PACKAGE
)
public class NarMojo extends AbstractNarPlugin {
    @Parameter(defaultValue = "${project.build.directory}/library", required = true)
    private File libraryDirectory;

    @Override
    public File getLibraryDirectory() {
        return this.libraryDirectory;
    }
}