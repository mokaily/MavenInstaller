package com.example.maveninstaller;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import static com.example.maveninstaller.FetchGitInfo.FetchReadMeInfo.fetchReadMeInfo;
import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.PomHelper.fetchAppName;
import static com.example.maveninstaller.PomHelper.findPomXml;
import static com.example.maveninstaller.RepositoryHelper.getRepoName;
import static com.example.maveninstaller.RepositoryHelper.validateCustomRepo;
import static com.example.maveninstaller.RunMavenBuild.runMavenBuild;

public class BuildRepository {
    public static void buildRepository() {
        String repoUrl = repoUrlField.getText().trim();
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 4);
        }
        String targetPath = targetPathField.getText().trim();


        // Validate custom repo path if selected
        validateCustomRepo();

        outputConsole.setText("⏳ Cloning repository...\n");
        progressBar.setIndeterminate(true);

        String finalRepoUrl = repoUrl;

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    String fullPath = targetPath + "/" + getRepoName(finalRepoUrl);
                    String pomPath = findPomXml(fullPath);

                    if (pomPath != null) {
                        publish("✅ Maven project detected.");
                        publish(pomPath);
                        runMavenBuild(pomPath);
                    } else {
                        publish("⚠️ No Maven project found.");
                    }

                    outputConsole.append("✅ Building completed!\n");
                } catch (Exception e) {
                    publish("❌ Error building repository:\n" + e.getMessage());
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
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.repaint();
                });
            }
        }.execute();
    }
}
