package cuccovillo.alessio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

@Mojo(name = "up", defaultPhase = LifecyclePhase.NONE)
public class MigrateMongoUpMojo extends MigrateMongoAbstractMojo {

    @Parameter
    List<String> upParams;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        processMojo("up", upParams);
    }
}
