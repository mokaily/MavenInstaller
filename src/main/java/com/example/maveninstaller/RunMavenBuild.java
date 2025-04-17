package com.example.maveninstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.Helpers.OSChecker.isWindows;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class RunMavenBuild {
    public static void runMavenBuild(String targetPath) {
        try {
            ProcessBuilder builder;
            String mavenCommand = isWindows() ? "mvn.cmd" : "mvn";

            if (useCustomRepoCheckbox.isSelected()) {
                String customRepo = customRepoPathField.getText().trim();
                builder = new ProcessBuilder( mavenCommand, "clean", "compile", "verify", "install", "-DskipTests","-Dmaven.repo.local=" + customRepo);
            } else {
                builder = new ProcessBuilder(mavenCommand, "clean", "compile", "verify", "install", "-DskipTests");
            }

            builder.directory(new File(targetPath + "/"));
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                appendToConsole(line + "\n", false);
            }
            process.waitFor();
        } catch (Exception e) {
            appendToConsole("Error running Maven build!\n" + e.getMessage(), false);
        }
    }
}
