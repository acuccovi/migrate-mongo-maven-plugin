package cuccovillo.alessio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

@Mojo(name = MigrateMongoDownMojo.GOAL, defaultPhase = LifecyclePhase.NONE)
public class MigrateMongoDownMojo extends MigrateMongoAbstractMojo {

    protected static final String GOAL = "down";

    @Parameter(property = "downParams")
    List<String> params;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        processMojo(GOAL, params);
    }
}
