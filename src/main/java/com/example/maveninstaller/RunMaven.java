package com.example.maveninstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.OperationSystemChecker.isWindows;

public class RunMaven {
    public static void runMavenBuild(String targetPath) {
        try {
            // Run Maven to build the project mvn clean package -DskipTests
            String mavenCommand = isWindows() ? "mvn.cmd" : "mvn";

            ProcessBuilder builder = new ProcessBuilder(mavenCommand, "clean", "package", "-DskipTests");
            builder.directory(new File(targetPath + "/"));
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                outputConsole.append(line + "\n");
            }
            process.waitFor();
        } catch (Exception e) {
            outputConsole.append("Error running Maven build!\n" + e.getMessage());
        }
    }

}
