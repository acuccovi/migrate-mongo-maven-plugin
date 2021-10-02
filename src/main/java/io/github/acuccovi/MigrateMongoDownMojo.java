package io.github.acuccovi;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Rollback the migrations
 */
@Mojo(name = MigrateMongoDownMojo.GOAL, defaultPhase = LifecyclePhase.NONE)
public class MigrateMongoDownMojo extends MigrateMongoAbstractMojo {

    static final String GOAL = "down";

    /**
     * A list of command parameters
     */
    @Parameter
    List<String> downParams;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        processMojo(GOAL, downParams);
    }
}
