package com.example.maveninstaller.FetchGitInfo;

import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class FetchGitHubOwnerInfo {
    public static void fetchGitHubOwnerInfo(String repoUrl) {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        progressBar.repaint();
        SwingUtilities.invokeLater(() -> ownerInfoArea.setText(Fetch_Contact_Start));

        if(repoUrl.endsWith(".git")) { repoUrl = repoUrl.substring(0, repoUrl.length() - 4); }
        // Extract the owner and repo name from the GitHub URL
        String[] urlParts = repoUrl.split("/");
        String owner = urlParts[3]; // GitHub username (owner)
        String repo = urlParts[4].replace(".git", ""); // Repository name

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Construct GitHub API URL
                    String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo;

                    // Open a connection to the GitHub API
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");

                    // Check for a successful response
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        SwingUtilities.invokeLater(() -> ownerInfoArea.append(Fetch_Contact_Failed));
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
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Extract owner information (email, if public)
                    JSONObject ownerInfo = jsonResponse.getJSONObject("owner");
                    String ownerName = ownerInfo.getString("login");
                    String ownerEmail = ownerInfo.optString("email", "Email not available");
                    String ownerUrl = ownerInfo.optString("url", "url not available");

                    // Display the owner's contact information (or "not available" message)
                    SwingUtilities.invokeLater(() -> ownerInfoArea.append(Owner + ownerName + "\n"));
                    SwingUtilities.invokeLater(() -> ownerInfoArea.append(Email + ownerEmail + "\n"));
                    SwingUtilities.invokeLater(() -> ownerInfoArea.append(URL + ownerUrl + "\n"));
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> ownerInfoArea.append(Fetch_Contact_Error));
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
