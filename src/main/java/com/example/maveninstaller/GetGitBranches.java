package com.example.maveninstaller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
//        readmeConsole.setText("");
        String repoUrl = repoUrlField.getText().trim();

        // Check if the URL is a valid GitHub or GitLab repository
        if (repoUrl.contains("github.")) {
            fetchGitHubOwnerContact(repoUrl);
            fetchGitHubBranches(repoUrl);
        } else if (repoUrl.contains("gitlab.")) {
            if(repoUrl.endsWith(".git")) { repoUrl = repoUrl.substring(0, repoUrl.length() - 4); }
            fetchGitLabOwnerContact(repoUrl);
            fetchGitLabBranches(repoUrl);
        } else {
            outputConsole.setText("Invalid repository URL!\n");
        }
    }

    public static void fetchGitHubBranches(String repoUrl) {
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
    }

    public static void fetchGitLabBranches(String repoUrl) {
        try {
            // Convert the GitLab URL to the project ID
            String projectId = getGitLabProjectId(repoUrl);

            if (projectId == null) {
                outputConsole.append("Could not get project ID for GitLab repository!\n");
                return;
            }

            // API URL to fetch branches
            String apiUrl = "https://gitlab.com/api/v4/projects/" + projectId + "/repository/branches";

            // Send HTTP request to fetch branches
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                outputConsole.append("Failed to fetch branches from GitLab repository!\n");
                return;
            }

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Parse the JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode branchesJson = objectMapper.readTree(response.toString());
            List<String> branches = new ArrayList<>();

            // Extract branch names
            for (JsonNode branch : branchesJson) {
                branches.add(branch.get("name").asText());
            }

            updateBranchSelector(branches);

        } catch (Exception e) {
            outputConsole.append("Failed to fetch GitLab branches!\n" + e.getMessage());
        }
    }


    public static String getGitLabProjectId(String repoUrl) {
        try {
            // Convert the GitLab repository URL to the API URL
            String projectPath = repoUrl.substring(repoUrl.indexOf("gitlab.com/") + 11); // Extract the "namespace/repository"
            projectPath = projectPath.replace("/", "%2F");  // URL-encode the "/"

            // Form the API URL to get project details
            String apiUrl = "https://gitlab.com/api/v4/projects/" + projectPath;

            // Send HTTP request to get the project details
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check for successful response
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                outputConsole.append("Failed to fetch project ID from GitLab!\n");
                return null;
            }

            // Read the response from GitLab API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Parse the response to get the project ID
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            return rootNode.get("id").asText();  // Extract the project ID from the response

        } catch (Exception e) {
            outputConsole.append("Error fetching project ID: " + e.getMessage() + "\n");
        }
        return null;
    }


}
