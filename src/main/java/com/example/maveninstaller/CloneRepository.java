package com.example.maveninstaller;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import static com.example.maveninstaller.BuildRepository.*;
import static com.example.maveninstaller.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.FetchGitInfo.FetchGitOwnerInfo.fetchGitOwnerInfo;
import static com.example.maveninstaller.FetchGitInfo.FetchReadMeInfo.fetchReadMeInfo;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.PomHelper.findPomXml;
import static com.example.maveninstaller.PomHelper.fetchAppName;
import static com.example.maveninstaller.RepositoryHelper.*;
import static com.example.maveninstaller.UXEnhancer.setButtonsEnabled;

public class CloneRepository {
    public static void cloneRepository(boolean isOneFunction) {
        setButtonsEnabled(false);
        String repoUrl = repoUrlField.getText().trim();
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 4);
        }
        String targetPath = targetPathField.getText().trim();

        if (targetPath.isEmpty()) {
            setButtonsEnabled(true);
            appendToConsole("❗ Please select a install path!\n", true);
            return;
        }

        if (repoUrl.isEmpty() || !repoUrl.contains("git")) {
            setButtonsEnabled(true);
            appendToConsole("❗ Invalid repository URL!\n", true);
            return;
        }


        String branch = "";
        try {
            branch = (String) branchSelector.getSelectedItem();
        } catch (Exception ignored) {
            setButtonsEnabled(true);
        }

        // Validate custom repo path if selected
        validateCustomRepo();

        appendToConsole("⏳ Cloning repository...\n", true);
        progressBar.setIndeterminate(true);
        progressBar.repaint();

        String finalBranch = branch;
        String finalRepoUrl = repoUrl;

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    String fullPath = targetPath + "/" + getRepoName(finalRepoUrl);
                    String pomPath = findPomXml(fullPath);

//                    // Check if the project directory already exists
//                    File projectDir = new File(fullPath);
//                    if (projectDir.exists()) {
//                        publish("⚠️ Project already exists. Deleting old version...");
//                        deleteDirectory(projectDir);
//                        publish("🗑️ Old project deleted.");
//                    }

                    ProcessBuilder builder;
                    String actualRepoUrl = finalRepoUrl;

                    if (finalRepoUrl.contains("gitlab.")) {
                        String userName = gitLabUserNameField.getText().trim().replace("@", "%40");
                        String pwd = gitLabPasswordFieldPassword.getText().trim();
                        actualRepoUrl = "https://" + userName + ":" + pwd + "@" + finalRepoUrl.substring(8);
                    }

                    if (finalBranch == null || finalBranch.isEmpty()) {
                        builder = new ProcessBuilder("git", "clone", actualRepoUrl);
                    } else {
                        builder = new ProcessBuilder("git", "clone", "--branch", finalBranch, actualRepoUrl);
                    }

                    builder.directory(new File(targetPath));
                    builder.redirectErrorStream(true);
                    Process process = builder.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        publish(line);
                    }
                    process.waitFor();


                    if (pomPath != null) {
                        applicationNameField.setText(fetchAppName(pomPath));
                        fetchGitOwnerInfo();
                        fetchReadMeInfo();
                    }

                    if (pomPath != null) {
                        publish("✅ Maven project detected.");
                        publish(pomPath);
                    } else {
                        publish("⚠️ No Maven project found.");
                    }

                    appendToConsole("✅ Cloning completed!\n", false);

                } catch (Exception e) {
                    setButtonsEnabled(true);
                    publish("❌ Error cloning repository:\n" + e.getMessage());
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
                    buildRepository(isOneFunction);
                }else{
                    setButtonsEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.repaint();
                }
            }
        }.execute();
    }
}
