package com.example.maveninstaller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.example.maveninstaller.GitHubCloneUI.aboutConsole;

public class GetGitHubOwnerContact {
    public static void fetchGitHubOwnerContact(String repoUrl) {
        aboutConsole.setText("Fetching Github owner contact info...");
        // Extract the owner and repo name from the GitHub URL
        String[] urlParts = repoUrl.split("/");
        String owner = urlParts[3]; // GitHub username (owner)
        String repo = urlParts[4].replace(".git", ""); // Repository name

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
                aboutConsole.append("Failed to fetch GitHub owner info!\n");
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
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Extract owner information (email, if public)
            JSONObject ownerInfo = jsonResponse.getJSONObject("owner");
            String ownerName = ownerInfo.getString("login");
            String ownerEmail = ownerInfo.optString("email", "Email not available");
            String ownerUrl = ownerInfo.optString("url", "url not available");

            // Display the owner's contact information (or "not available" message)
            aboutConsole.append("Owner: " + ownerName + "\n");
            aboutConsole.append("Email: " + ownerEmail + "\n");
            aboutConsole.append("URL: " + ownerUrl + "\n");

        } catch (Exception e) {
            aboutConsole.append("Error fetching GitHub owner contact info: \n");
        }
    }

}
