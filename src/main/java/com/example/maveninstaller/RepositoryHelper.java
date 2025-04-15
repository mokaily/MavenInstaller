package com.example.maveninstaller;

import java.io.File;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class RepositoryHelper {
    public static String getRepoName(String repoUrl) {
        // Validate the URL format
        if (repoUrl == null || repoUrl.isEmpty()) {
            return null;
        }

        // Extract the repository name from the URL
        String repoName = repoUrl.substring(repoUrl.lastIndexOf('/') + 1);

        return repoName;
    }

    public static void validateCustomRepo() {
        if (useCustomRepoCheckbox.isSelected()) {
            String customRepoPath = customRepoPathField.getText().trim();
            if (customRepoPath.isEmpty()) {
                outputConsole.setText("❗ Custom Maven repository path is empty!\n");
                return;
            }
            File repoDir = new File(customRepoPath);
            if (!repoDir.exists() || !repoDir.isDirectory()) {
                outputConsole.setText("❗ Custom Maven repository path is invalid or not a directory!\n");
                return;
            }
        }
    }

    public static void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

}
