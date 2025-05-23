package com.example.maveninstaller;

import javax.swing.*;
import java.util.List;

import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.Helpers.CreateInstallerHelper.createInstallerHelper;
import static com.example.maveninstaller.Helpers.PomHelper.findPomXml;
import static com.example.maveninstaller.Helpers.RepositoryHelper.getRepoName;
import static com.example.maveninstaller.Helpers.RepositoryHelper.validateCustomRepo;
import static com.example.maveninstaller.RunMavenBuild.runMavenBuild;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.UXEnhancer.setButtonsEnabled;


public class BuildRepository {
    public static void buildRepository(boolean isOneFunction) {
        setButtonsEnabled(false);
        String repoUrl = repoUrlField.getText().trim();
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 4);
        }
        String targetPath = targetPathField.getText().trim();


        // Validate custom repo path if selected
        validateCustomRepo();

        appendToConsole("⏳ Cloning repository...\n", true);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        progressBar.repaint();

        String finalRepoUrl = repoUrl;

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    String fullPath = targetPath + "/" + getRepoName(finalRepoUrl);
                    String pomPath = findPomXml(fullPath);

                    if (pomPath != null) {
                        appendToConsole(Maven_Detected, false);
                        appendToConsole(pomPath, false);
                        runMavenBuild(pomPath);
                    } else {
                        appendToConsole(Maven_Not_Found, false);
                    }

                    appendToConsole(Build_Completed, false);
                } catch (Exception e) {
                    setButtonsEnabled(true);
                    appendToConsole(Build_Error + e.getMessage(), false);
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
                SwingUtilities.invokeLater(() -> {
                });
                if (isOneFunction) {
                    setButtonsEnabled(false);
                    createInstallerHelper();
                }else{
                    setButtonsEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.repaint();
                }
            }
        }.execute();
    }
}
