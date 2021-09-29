package cuccovillo.alessio;

import cuccovillo.alessio.exception.SkipMojoException;
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

    @Parameter
    String executablePath;

    @Parameter
    String executableExtension;

    @Parameter(defaultValue = "false")
    boolean skip;

    @Parameter(defaultValue = ".")
    String configPath;

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
            executableName = Paths.get(executableName, executableExtension).toString();
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
                getLog().error(err.nextLine());
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
