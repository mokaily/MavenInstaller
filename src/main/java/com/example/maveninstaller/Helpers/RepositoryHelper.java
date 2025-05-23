package com.example.maveninstaller.Helpers;

import java.io.File;

import static com.example.maveninstaller.Constants.Custom_Maven_Empty;
import static com.example.maveninstaller.Constants.Custom_Maven_Invalid;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
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
                appendToConsole(Custom_Maven_Empty, true);
                return;
            }
            File repoDir = new File(customRepoPath);
            if (!repoDir.exists() || !repoDir.isDirectory()) {
                appendToConsole(Custom_Maven_Invalid, true);
                return;
            }
        }
    }
}
