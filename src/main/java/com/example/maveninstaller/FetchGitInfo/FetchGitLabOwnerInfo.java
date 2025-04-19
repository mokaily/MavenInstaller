package com.example.maveninstaller.FetchGitInfo;

import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class FetchGitLabOwnerInfo {
    public static void fetchGitLabOwnerInfo(String repoUrl) {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        progressBar.repaint();
        SwingUtilities.invokeLater(() -> ownerInfoArea.setText(Fetch_Contact_Start));

        final JSONObject[] jsonResponse = {null};
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    URL parsedUrl = new URL(repoUrl);
                    String host = parsedUrl.getHost();
                    String path = parsedUrl.getPath().replaceFirst("^/", "").replace(".git", "");
                    String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8);
                    String apiUrl = "https://" + host + "/api/v4/projects/" + encodedPath;

                    // Try without token first
                    JSONObject response = tryFetchGitLabInfo(apiUrl, host, null);

                    // If failed, try with token
                    if (response == null) {
                        String accessToken = gitLabPasswordFieldPassword.getText().trim();
                        response = tryFetchGitLabInfo(apiUrl, host, accessToken);
                    }

                    if (response != null) {
                        JSONObject ownerInfo = response.getJSONObject("namespace");
                        String ownerUsername = ownerInfo.getString("name");
                        String ownerUrl = ownerInfo.getString("web_url");

                        SwingUtilities.invokeLater(() -> ownerInfoArea.append("Username: " + ownerUsername + "\n"));
                        SwingUtilities.invokeLater(() -> ownerInfoArea.append("URL: " + ownerUrl + "\n"));
                    } else {
                        SwingUtilities.invokeLater(() -> ownerInfoArea.append(Fetch_Contact_Failed));
                    }

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> ownerInfoArea.append(Fetch_Contact_Error));
                }
                return null;
            }

            private JSONObject tryFetchGitLabInfo(String apiUrl, String host, String token) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");

                    if (token != null && !token.isBlank()) {
                        if (host.contains("gitlab.com")) {
                            connection.setRequestProperty("Authorization", "Bearer " + token);
                        } else {
                            connection.setRequestProperty("PRIVATE-TOKEN", token);
                        }
                    }

                    int status = connection.getResponseCode();
//                    appendToConsole("🔎 TryFetch (" + (token == null ? "No Token" : "With Token") + ") → HTTP " + status + "\n");

                    if (status != HttpURLConnection.HTTP_OK) return null;

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return new JSONObject(response.toString());
                } catch (Exception e) {
//                    appendToConsole("Error in tryFetch: " + e.getMessage() + "\n");
                    return null;
                }
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