package cuccovillo.alessio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

@Mojo(name = "down", defaultPhase = LifecyclePhase.NONE)
public class MigrateMongoDownMojo extends MigrateMongoAbstractMojo {

    @Parameter
    List<String> downParams;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        processMojo("down", downParams);
    }
}
