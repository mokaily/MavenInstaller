package com.example.maveninstaller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.example.maveninstaller.GitHubCloneUI.aboutConsole;

public class GetGitLabOwnerContact {
    public static void fetchGitLabOwnerContact(String repoUrl) {
        aboutConsole.setText("Fetching GitLab owner contact info...\n");
        // Extract the project ID from the GitLab URL
        String projectPath = repoUrl.replace("https://gitlab.com/", "").replace(".git", "");

        JSONObject jsonResponse = null;
        try {
            // Construct GitLab API URL
            String apiUrl = "https://gitlab.com/api/v4/projects/" + URLEncoder.encode(projectPath, "UTF-8");

            // Open a connection to the GitLab API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Check for a successful response
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                aboutConsole.append("Failed to fetch GitLab owner info!\n");
                return;
            }

            // Read the API response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Parse the JSON response
            jsonResponse = new JSONObject(response.toString());

            // Extract owner information
            JSONObject ownerInfo = jsonResponse.getJSONObject("namespace");
            String ownerUsername = ownerInfo.getString("name");
            String ownerUrl = ownerInfo.getString("web_url");

            // Display the owner's contact information
            aboutConsole.append("Username: " + ownerUsername + "\n");
            aboutConsole.append("URL: " + ownerUrl + "\n");

            // GitLab does not provide email directly through API for privacy reasons.

        } catch (Exception e) {
            aboutConsole.append("Error fetching GitLab owner contact info: \n" + jsonResponse);
        }
    }

}
