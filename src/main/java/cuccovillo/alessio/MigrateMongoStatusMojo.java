package cuccovillo.alessio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Show the status of the migrations
 */
@Mojo(name = MigrateMongoStatusMojo.GOAL, defaultPhase = LifecyclePhase.NONE)
public class MigrateMongoStatusMojo extends MigrateMongoAbstractMojo {

    protected static final String GOAL = "status";

    /**
     * A list of command parameters
     */
    @Parameter(property = "statusParams")
    List<String> params;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        processMojo(GOAL, params);
    }
}
