package cuccovillo.alessio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Clean the database
 */
@Mojo(name = MigrateMongoCleanMojo.GOAL, defaultPhase = LifecyclePhase.NONE)
public class MigrateMongoCleanMojo extends MigrateMongoAbstractMojo {

    protected static final String GOAL = "clean";

    /**
     * A list of command parameters
     */
    @Parameter(property = "cleanParams")
    List<String> params;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        processMojo(GOAL, params);
    }
}
