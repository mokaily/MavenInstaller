package com.example.maveninstaller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.maveninstaller.GetGitHubOwnerContact.fetchGitHubOwnerContact;
import static com.example.maveninstaller.GetGitLabOwnerContact.fetchGitLabOwnerContact;
import static com.example.maveninstaller.GitHubCloneUI.*;

public class GetGitBranches {
        public static void fetchBranches() {
        branchSelector.removeAllItems();
        outputConsole.setText("");
        String repoUrl = repoUrlField.getText().trim();

         repoUrl= convertSshToHttps(repoUrl);

        // Check if the URL is a valid GitHub or GitLab repository
        if (repoUrl.contains("github.")) {
//            fetchGitHubOwnerContact(repoUrl);
            fetchGitHubBranches(repoUrl);
        } else if (repoUrl.contains("gitlab.")) {
//            fetchGitLabOwnerContact(repoUrl);
            fetchGitLabBranches(repoUrl);
        } else {
            outputConsole.setText("Invalid repository URL!\n");
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

    public static void fetchGitHubBranches(String repoUrl) {
        progressBar.setIndeterminate(true);
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                fetchGitHubOwnerContact(repoUrl);
                    try {
                        ProcessBuilder builder = new ProcessBuilder("git", "ls-remote", "--heads", repoUrl);
                        Process process = builder.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        List<String> branches = new ArrayList<>();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String branch = line.substring(line.lastIndexOf('/') + 1);
                            branches.add(branch);
                        }
                        updateBranchSelector(branches);
                    } catch (Exception e) {
                        outputConsole.append("Failed to fetch GitHub branches!\n" + e.getMessage());
                    }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    outputConsole.append(chunk + "\n");
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
            }
        }.execute();
    }

    public static void fetchGitLabBranches(String repoUrl) {
        progressBar.setIndeterminate(true);
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                fetchGitLabOwnerContact(repoUrl);
                try {
                    URI uri = new URI(repoUrl);
                    String host = uri.getHost();
                    String accessToken = gitLabPasswordFieldPassword.getText().trim();

                    String projectId = getGitLabProjectId(repoUrl, accessToken);

                    if (projectId == null) {
                        outputConsole.append("❌ Could not get project ID for GitLab repository!\n");
                        return null;
                    }

                    String apiUrl = "https://" + host + "/api/v4/projects/" + projectId + "/repository/branches";

                    // first try without token
                    String response = tryFetch(apiUrl, host, null);
                    if (response == null && !accessToken.isBlank()) {
                        // sescond try without token
                        response = tryFetch(apiUrl, host, accessToken);
                    }

                    if (response == null) {
                        outputConsole.append("❌ Failed to fetch branches from GitLab repository!\n");
                        return null;
                    }

                    // Get the project branch
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode branchesJson = objectMapper.readTree(response);
                    List<String> branches = new ArrayList<>();

                    for (JsonNode branch : branchesJson) {
                        branches.add(branch.get("name").asText());
                    }

                    updateBranchSelector(branches);

                } catch (Exception e) {
                    outputConsole.append("❌ Failed to fetch GitLab branches!\n" + e.getMessage() + "\n");
                }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    outputConsole.append(chunk + "\n");
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
            }
        }.execute();
    }

    public static String getGitLabProjectId(String repoUrl, String accessToken) {
        try {
            URI uri = new URI(repoUrl);
            String host = uri.getHost();

            // prepare the url
            String path = uri.getPath();
            if (path.startsWith("/")) path = path.substring(1);
            if (path.endsWith(".git")) path = path.substring(0, path.length() - 4);
            String encodedPath = path.replace("/", "%2F");

            // API Endpoint
            String apiUrl = "https://" + host + "/api/v4/projects/" + encodedPath;

            // First try without access token
            String response = tryFetch(apiUrl, host, null);
            if (response == null && accessToken != null && !accessToken.isBlank()) {
                // second try with access token
                response = tryFetch(apiUrl, host, accessToken);
            }

            if (response == null) {
                outputConsole.append("❌ Failed to fetch project ID.\n");
                return null;
            }

            // fetch project id from the Json file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("id")) {
                String id = rootNode.get("id").asText();
                outputConsole.append("✅ Project ID: " + id + "\n");
                return id;
            } else {
                outputConsole.append("⚠️ 'id' field not found.\n");
            }

        } catch (Exception e) {
            outputConsole.append("Exception: " + e.getMessage() + "\n");
        }

        return null;
    }

    private static String tryFetch(String apiUrl, String host, String token) {
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
            outputConsole.append("🔎 TryFetch (" + (token == null ? "No Token" : "With Token") + ") → HTTP " + status + "\n");

            if (status != 200) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();

        } catch (Exception e) {
            outputConsole.append("Error in tryFetch: " + e.getMessage() + "\n");
            return null;
        }
    }
}
