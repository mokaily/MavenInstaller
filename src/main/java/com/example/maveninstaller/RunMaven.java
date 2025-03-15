package com.example.maveninstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static com.example.maveninstaller.GitHubCloneUI.*;
import static com.example.maveninstaller.RepositoryUtils.getRepoName;

public class RunMaven {

    public static void runMavenBuild(String targetPath, String repoUrl) {
        String projectDir = targetPath + "/" + getRepoName(repoUrl);

        try {
            // Run Maven to build the project
            ProcessBuilder builder = new ProcessBuilder("mvn", "clean", "install");
            builder.directory(new File(projectDir));
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
