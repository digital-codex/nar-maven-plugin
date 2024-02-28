package dev.codex.java.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(
        name = "nar",
        defaultPhase = LifecyclePhase.PACKAGE
)
public class NarMojo extends AbstractNarPlugin {
}