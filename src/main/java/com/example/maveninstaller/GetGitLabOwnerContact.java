package com.example.maveninstaller;

import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.maveninstaller.GitHubCloneUI.*;

public class GetGitLabOwnerContact {
    public static void fetchGitLabOwnerContact(String repoUrl) {
        progressBar.setIndeterminate(true);
        SwingUtilities.invokeLater(() -> ownerInfoArea.setText("Fetching GitLab owner contact info...\n"));

        //todo: repoUrl get hosturl from it and replace it with target below

        // Extract the project ID from the GitLab URL
        String projectPath = repoUrl.replace("https://gitlab.com/", "").replace(".git", "");

        final JSONObject[] jsonResponse = {null};
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Construct GitLab API URL
                    String apiUrl = "https://gitlab.com/api/v4/projects/" + URLEncoder.encode(projectPath, StandardCharsets.UTF_8);

                    // Open a connection to the GitLab API
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");

                    // Check for a successful response
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        SwingUtilities.invokeLater(() -> ownerInfoArea.append("Failed to fetch GitLab owner info!\n"));
                        return null;
                    }

                    // Read the API response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse the JSON response
                    jsonResponse[0] = new JSONObject(response.toString());

                    // Extract owner information
                    JSONObject ownerInfo = jsonResponse[0].getJSONObject("namespace");
                    String ownerUsername = ownerInfo.getString("name");
                    String ownerUrl = ownerInfo.getString("web_url");

                    // Display the owner's contact information
                    SwingUtilities.invokeLater(() ->  ownerInfoArea.append("Username: " + ownerUsername + "\n"));
                    SwingUtilities.invokeLater(() -> ownerInfoArea.append("URL: " + ownerUrl + "\n"));

                    // GitLab does not provide email directly through API for privacy reasons.
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> ownerInfoArea.append("Error fetching GitLab owner contact info: \n" + jsonResponse[0]));
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

}
