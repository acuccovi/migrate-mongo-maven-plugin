package cuccovillo.alessio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Execute the pending migrations
 */
@Mojo(name = MigrateMongoUpMojo.GOAL, defaultPhase = LifecyclePhase.NONE)
public class MigrateMongoUpMojo extends MigrateMongoAbstractMojo {

    protected static final String GOAL = "up";

    /**
     * A list of command parameters
     */
    @Parameter(property = "upParams")
    List<String> params;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        processMojo(GOAL, params);
    }
}
