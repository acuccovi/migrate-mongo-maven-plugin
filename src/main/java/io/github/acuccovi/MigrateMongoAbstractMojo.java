package io.github.acuccovi;

import io.github.acuccovi.exception.SkipMojoException;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class MigrateMongoAbstractMojo extends AbstractMojo {

    /**
     * The path where migrate-mongo is installed.<br>
     * Useful when the plugin is not able to find it by itself<br>
     *<br>
     * This is an optional information
     */
    @Parameter
    String executablePath;

    /**
     * The extension of the migrate-mongo executable.<br>
     * Useful when the plugin is not able to find it by itself<br>
     *<br>
     * This information is optional
     */
    @Parameter
    String executableExtension;

    /**
     * A boolean value used to skip the execution of the plugin<br>
     * Useful when the execution is bound to a phase<br>
     *<br>
     * This information is optional, defaults to false
     */
    @Parameter(defaultValue = "false")
    boolean skip;

    /**
     * The path where the plugin will search the migration configuration file<br>
     *<br>
     * This information is optional, defaults to current working directory
     */
    @Parameter(defaultValue = ".")
    String configPath;

    /**
     * The name of the migration configuration file, if the default file name is not used<br>
     *<br>
     * This information is optional, defaults to migrate-mongo default value
     */
    @Parameter
    String configFile;

    List<String> checkList(List<String> cliParams) {

        List<String> result;
        if (cliParams == null) {
            result = new ArrayList<>();
        } else {
            result = cliParams;
        }
        return result;
    }

    void processMojo(String goal, List<String> cliParams) throws MojoExecutionException, MojoFailureException {

        try {
            checkIfShouldSkip();

            cliParams = checkList(cliParams);
            Process process = startProcess(goal, cliParams);
            printStandardOutput(process);
            printStandardError(process);
            checkExitCode(process);
        } catch (SkipMojoException ignore) {
            getLog().info("Skipping migrate-mongo:" + goal + " execution");
        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            getLog().info(sw.toString());
            throw new MojoExecutionException("Fail to run mojo", ex);
        }
    }

    void checkIfShouldSkip() throws SkipMojoException {

        if (skip) {
            throw new SkipMojoException();
        }
    }

    Process startProcess(String goal, List<String> cliParams) throws IOException {

        ProcessBuilder processBuilder = getProcessBuilder(goal, cliParams);
        return processBuilder.start();
    }

    ProcessBuilder getProcessBuilder(String goal, List<String> cliParams) {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(configPath));
        List<String> commands = getCommands(goal, cliParams);
        processBuilder.command(commands);
        return processBuilder;
    }

    List<String> getCommands(String goal, List<String> cliParams) {

        String executableName = "migrate-mongo";
        if (StringUtils.isNotBlank(executablePath)) {
            executableName = Paths.get(executablePath, executableName).toString();
        }
        if (StringUtils.isNotBlank(executableExtension)) {
            executableName += executableExtension;
        }
        List<String> commands = new ArrayList<>();
        commands.add(executableName);
        commands.add(goal);
        commands.addAll(cliParams);
        if (StringUtils.isNotBlank(configFile)) {
            commands.add("-f");
            commands.add(configFile);
        }
        dumpCommandLine(commands);
        return commands;
    }

    void printStandardOutput(Process process) {

        try (Scanner in = new Scanner(process.getInputStream())) {
            while (in.hasNextLine()) {
                getLog().info(in.nextLine());
            }
        }
    }

    void printStandardError(Process process) {

        try (Scanner err = new Scanner(process.getErrorStream())) {
            while (err.hasNextLine()) {
                if (process.exitValue() != 0) {
                    getLog().error(err.nextLine());
                } else {
                    getLog().warn(err.nextLine());
                }
            }
        }
    }

    void checkExitCode(Process process) throws MojoFailureException {

        if (process.exitValue() != 0) {
            throw new MojoFailureException("Fail to run mojo. Exit code is: " + process.exitValue());
        }
    }

    void dumpCommandLine(List<String> commands) {
        StringBuilder sb = new StringBuilder();
        commands.forEach(command -> sb.append(command).append(StringUtils.SPACE));
        getLog().info("Executing: " + StringUtils.trim(sb.toString()));
    }
}
