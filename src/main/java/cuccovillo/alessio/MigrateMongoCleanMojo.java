    package cuccovillo.alessio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.NONE)
public class MigrateMongoCleanMojo extends MigrateMongoAbstractMojo {

    @Parameter
    CLIOptions cleanOptions;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        processMojo("up", cleanOptions);
    }
}
