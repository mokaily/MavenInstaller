package com.example.maveninstaller.FetchGitInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;

import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.FetchGitInfo.FetchGitOwnerInfo.tryFetch;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.FetchGitInfo.FetchGitHubBranches.fetchGitHubBranches;
import static com.example.maveninstaller.FetchGitInfo.FetchGitLabBranches.fetchGitLabBranches;
import static com.example.maveninstaller.FetchGitInfo.FetchGitOwnerInfo.convertSshToHttps;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class FetchGitBranches {
        public static void fetchBranches() {
        branchSelector.removeAllItems();
        appendToConsole("", true);
        String repoUrl = repoUrlField.getText().trim();

        repoUrl= convertSshToHttps(repoUrl);

        // Check if the URL is a valid GitHub or GitLab repository
        if (repoUrl.contains("github.")) {
            fetchGitHubBranches(repoUrl);
        } else if (repoUrl.contains("gitlab.")) {
            fetchGitLabBranches(repoUrl);
        } else {
            appendToConsole(Invalid_URL, true);
        }
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
                appendToConsole(ProjectID_Fetch_Failed, false);
                return null;
            }

            // fetch project id from the Json file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("id")) {
                String id = rootNode.get("id").asText();
                appendToConsole(ProjectID_Fetch_Done + id + "\n", false);
                return id;
            } else {
                appendToConsole(ProjectID_Fetch_Not_Found, false);
            }

        } catch (Exception e) {
            appendToConsole(Exception + e.getMessage() + "\n", false);
        }

        return null;
    }
}
