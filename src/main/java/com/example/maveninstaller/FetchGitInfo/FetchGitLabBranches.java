package com.example.maveninstaller.FetchGitInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.FetchGitInfo.FetchGitBranches.getGitLabProjectId;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.FetchGitInfo.FetchGitOwnerInfo.tryFetch;
import static com.example.maveninstaller.GUI.UpdateBranchSelector.updateBranchSelector;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

class FetchGitLabBranches {

    public static void fetchGitLabBranches(String repoUrl) {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        progressBar.repaint();
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    URI uri = new URI(repoUrl);
                    String host = uri.getHost();
                    String accessToken = gitLabPasswordFieldPassword.getText().trim();

                    String projectId = getGitLabProjectId(repoUrl, accessToken);

                    if (projectId == null) {
                        appendToConsole(ProjectID_Fetch_Failed, false);
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
                        appendToConsole(Branches_Fetch_Failed, false);
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
                    appendToConsole(Branches_Fetch_Failed + e.getMessage() + "\n", false);
                }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    appendToConsole(chunk + "\n", false);
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.repaint();
            }
        }.execute();
    }


}