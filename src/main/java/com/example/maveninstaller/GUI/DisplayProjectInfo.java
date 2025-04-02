package com.example.maveninstaller.GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.repoUrlField;
import static com.example.maveninstaller.GUI.GitMavenCloneUI.targetPathField;
import static com.example.maveninstaller.RepositoryUtils.getRepoName;

public class DisplayProjectInfo {
    public static void displayProjectInfo() {
        String projectPath = targetPathField.getText() + "/" + getRepoName(repoUrlField.getText());

        StringBuilder projectInfo = new StringBuilder("\nProject Information:\n");
        projectInfo.append(repoUrlField.getText());

        // Display the README content if it exists
        File readmeFile = new File(projectPath + "/", "README.md");
        if (readmeFile.exists()) {
            projectInfo.append("\nREADME Content:\n");
            try (BufferedReader reader = new BufferedReader(new FileReader(readmeFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    projectInfo.append(line).append("\n");
                }
            } catch (IOException e) {
                projectInfo.append("Could not read README.md\n");
            }
        } else {
            projectInfo.append("README.md not found.\n");
        }
    }
}