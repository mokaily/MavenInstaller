package com.example.maveninstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.OperationSystemChecker.isWindows;

public class RunMavenBuildJar {
    public static void runMavenBuildJar(String targetPath) {
        try {
            ProcessBuilder builder;
            String mavenCommand = isWindows() ? "mvn.cmd" : "mvn";

            if (useCustomRepoCheckbox.isSelected()) {
                String customRepo = customRepoPathField.getText().trim();
                builder = new ProcessBuilder( mavenCommand, "clean", "compile", "verify", "install", "-DskipTests","-Dmaven.repo.local=" + customRepo);
//                builder = new ProcessBuilder(mavenCommand, "clean", "package", "-DskipTests", "-Dmaven.repo.local=" + customRepo);
            } else {
                builder = new ProcessBuilder(mavenCommand, "clean", "compile", "verify", "install", "-DskipTests");
//                builder = new ProcessBuilder(mavenCommand, "clean", "package", "-DskipTests");
            }

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
