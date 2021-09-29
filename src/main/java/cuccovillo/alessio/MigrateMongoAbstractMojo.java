package cuccovillo.alessio;

import cuccovillo.alessio.exception.SkipMojoException;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Usage:
 *            &lt;plugin&gt;
 *                 &lt;groupId&gt;cuccovillo.alessio&lt;/groupId&gt;
 *                 &lt;artifactId&gt;migrate-mongo-maven-plugin&lt;/artifactId&gt;
 *                 &lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
 *                 &lt;configuration&gt;
 *                     &lt;skip&gt;false&lt;/skip&gt;
 *                     &lt;configPath&gt;/path/to/config_file.js&lt;/configPath&gt;
 *                     &lt;configFile&gt;config_file.js&lt;/configFile&gt;
 *                     &lt;statusOptions&gt;
 *                         &lt;option&gt;-option1&lt;/option&gt;
 *                         &lt;option&gt;-option2&lt;/option&gt;
 *                         &lt;option&gt;-option2 value&lt;/option&gt;
 *                     &lt;/statusOptions&gt;
 *                     &lt;cleanOptions&gt;
 *                         &lt;option&gt;-option1&lt;/option&gt;
 *                         &lt;option&gt;-option2&lt;/option&gt;
 *                         &lt;option&gt;-option2 value&lt;/option&gt;
 *                     &lt;/cleanOptions&gt;
 *                     &lt;upOptions&gt;
 *                         &lt;option&gt;-option1&lt;/option&gt;
 *                         &lt;option&gt;-option2&lt;/option&gt;
 *                         &lt;option&gt;-option2 value&lt;/option&gt;
 *                     &lt;/upOptions&gt;
 *                     &lt;downOptions&gt;
 *                         &lt;option&gt;-option1&lt;/option&gt;
 *                         &lt;option&gt;-option2&lt;/option&gt;
 *                         &lt;option&gt;-option2 value&lt;/option&gt;
 *                     &lt;/downOptions&gt;
 *                 &lt;/configuration&gt;
 *             &lt;/plugin&gt;
 */
public abstract class MigrateMongoAbstractMojo extends AbstractMojo {

    final Log log = getLog();

    @Parameter(property = "skip", defaultValue = "false")
    boolean skip;

    @Parameter(property = "configPath", defaultValue = ".")
    String configPath;

    @Parameter(property = "configFile")
    String configFile;

    CLIOptions checkList(CLIOptions cliOptions) {

        CLIOptions result;
        if (cliOptions == null) {
            result = new CLIOptions();
        } else {
            result = cliOptions;
        }
        return result;
    }

    void processMojo(String goal, CLIOptions cliOptions) throws MojoExecutionException, MojoFailureException {

        try {
            checkIfShouldSkip();

            cliOptions = checkList(cliOptions);
            Process process = startProcess(goal, cliOptions);
            printStandardOutput(process);
            printStandardError(process);
            checkExitCode(process);
        } catch (SkipMojoException ignore) {
            log.info("Skipping migrate-mongo:" + goal + " execution");
        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            log.info(sw.toString());
            throw new MojoExecutionException("Fail to run mojo", ex);
        }
    }

    void checkIfShouldSkip() throws SkipMojoException {

        if (skip) {
            throw new SkipMojoException();
        }
    }

    Process startProcess(String goal, CLIOptions cliOptions) throws IOException {

        ProcessBuilder processBuilder = getProcessBuilder(goal, cliOptions);
        return processBuilder.start();
    }

    ProcessBuilder getProcessBuilder(String goal, CLIOptions cliOptions) {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(configPath));
        List<String> commands = getCommands(goal, cliOptions);
        processBuilder.command(commands);
        return processBuilder;
    }

    List<String> getCommands(String goal, CLIOptions cliOptions) {

        List<String> commands = new ArrayList<>();
        commands.add("migrate-mongo");
        commands.add(goal);
        if (StringUtils.isNotBlank(configFile)) {
            commands.add("-f");
            commands.add(configFile);
        }
        commands.addAll(cliOptions.getOptions());
        return commands;
    }

    void printStandardOutput(Process process) {

        try (Scanner in = new Scanner(process.getInputStream())) {
            while (in.hasNextLine()) {
                log.info(in.nextLine());
            }
        }
    }

    void printStandardError(Process process) {

        try (Scanner err = new Scanner(process.getErrorStream())) {
            while (err.hasNextLine()) {
                log.error(err.nextLine());
            }
        }
    }

    void checkExitCode(Process process) throws MojoFailureException {

        if (process.exitValue() != 0) {
            throw new MojoFailureException("Fail to run mojo. Exit code is: " + process.exitValue());
        }
    }
}
