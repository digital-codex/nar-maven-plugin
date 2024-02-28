package dev.codex.java.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public abstract class AbstractNarPlugin extends AbstractMojo {
    @Parameter
    private List<String> includes;

    @Parameter
    private List<String> excludes;

    @Override
    public void execute() {
        getLog().info("Hello, World");
    }
}