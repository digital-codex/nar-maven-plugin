package dev.codex.java.maven.plugin;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class AbstractNarPlugin extends AbstractMojo {
    private static final String[] DEFAULT_INCLUDES = new String[]{"**/**"};
    private static final String[] DEFAULT_EXCLUDES = new String[]{"**/package.html"};

    @Parameter
    private String[] includes;

    @Parameter
    private String[] excludes;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private String buildDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
    private String finalName;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter
    private final MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    @Component
    private MavenProjectHelper projectHelper;

    @Override
    public void execute() throws MojoExecutionException {
        File narFile = new File(this.buildDirectory, this.finalName + "-native" + ".jar");

        MavenArchiver archiver = new MavenArchiver();
        archiver.setCreatedBy("NAR Maven Plugin", "dev.codex.java.maven", "nar-maven-plugin");
        archiver.setArchiver(new JarArchiver());
        archiver.setOutputFile(narFile);

        try {
            File libraryDirectory = this.getLibraryDirectory();
            if (libraryDirectory.exists()) {
                archiver.getArchiver().addDirectory(libraryDirectory, this.getIncludes(), this.getExcludes());
            }

            archiver.createArchive(this.getSession(), this.getProject(), this.getArchive());
        } catch (DependencyResolutionRequiredException | IOException | ManifestException e) {
            throw new MojoExecutionException(e);
        }

        String[] parts = this.getProject().getArtifactId().split("-");
        StringBuilder anchorName = new StringBuilder();
        for (String part : parts) {
            anchorName.append(Strings.capitalize(part));
        }
        anchorName.append("Anchor");

        try {
            narFile = new ByteBuddy()
                    .subclass(Object.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                    .name(this.getProject().getGroupId() + anchorName)
                    .defineConstructor(Visibility.PRIVATE)
                    .intercept(MethodCall.invoke(Object.class.getConstructor()))
                    .make()
                    .inject(narFile);
        } catch (IOException | NoSuchMethodException e) {
            throw new MojoExecutionException(e);
        }

        projectHelper.attachArtifact(this.getProject(), "jar", "native", narFile);
    }

    public abstract File getLibraryDirectory();

    public String[] getIncludes() {
        if (this.includes != null && this.includes.length > 0) {
            return this.includes;
        }
        return DEFAULT_INCLUDES;
    }

    public String[] getExcludes() {
        if (this.excludes != null && this.excludes.length > 0) {
            return this.excludes;
        }
        return DEFAULT_EXCLUDES;
    }

    public MavenSession getSession() {
        return this.session;
    }

    public MavenProject getProject() {
        return this.project;
    }

    public MavenArchiveConfiguration getArchive() {
        return this.archive;
    }
}