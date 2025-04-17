package com.example.maveninstaller.FetchGitInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.FetchGitInfo.FetchGitHubOwnerInfo.fetchGitHubOwnerInfo;
import static com.example.maveninstaller.FetchGitInfo.FetchGitLabOwnerInfo.fetchGitLabOwnerInfo;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class FetchGitOwnerInfo {
        public static void fetchGitOwnerInfo() {
        String repoUrl = repoUrlField.getText().trim();

         repoUrl= convertSshToHttps(repoUrl);

        // Check if the URL is a valid GitHub or GitLab repository
        if (repoUrl.contains("github.")) {
            fetchGitHubOwnerInfo(repoUrl);
        } else if (repoUrl.contains("gitlab.")) {
            fetchGitLabOwnerInfo(repoUrl);
        } else {
            appendToConsole("Invalid repository URL!\n", true);
        }
    }

    public static String convertSshToHttps(String sshUrl) {
            // replacing "git@" with "https://" straightforward result an error where the ":" in https: is not stored.
        if (sshUrl.startsWith("git@")) {
            sshUrl = sshUrl.replace("git@", ""); // "gitlab.com:user/project.git"
            String[] parts = sshUrl.split(":", 2); // [ "gitlab.com", "user/project.git" ]
            if (parts.length == 2) {
                return "https://" + parts[0] + "/" + parts[1]; // https://gitlab.com/user/project.git
            }
        }
        if(sshUrl.endsWith(".git")) { sshUrl = sshUrl.substring(0, sshUrl.length() - 4); }

        return sshUrl;
    }

    public static String tryFetch(String apiUrl, String host, String token) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // add the token if exist
            if (token != null && !token.isBlank()) {
                if (host.equalsIgnoreCase("gitlab.com")) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                } else {
                    connection.setRequestProperty("PRIVATE-TOKEN", token);
                }
            }

            int status = connection.getResponseCode();
//            appendToConsole("🔎 TryFetch (" + (token == null ? "No Token" : "With Token") + ") → HTTP " + status + "\n", false);

            if (status != 200) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();

        } catch (Exception e) {
//            appendToConsole("Error in tryFetch: " + e.getMessage() + "\n", false);
            return null;
        }
    }
}
