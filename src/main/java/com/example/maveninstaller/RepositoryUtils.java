package com.example.maveninstaller;

public class RepositoryUtils {
    public static String getRepoName(String repoUrl) {
        // Validate the URL format
        if (repoUrl == null || repoUrl.isEmpty()) {
            return null;
        }

        // Extract the repository name from the URL
        String repoName = repoUrl.substring(repoUrl.lastIndexOf('/') + 1);

        return repoName;
    }

}
