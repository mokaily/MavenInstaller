package com.example.maveninstaller;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import static com.example.maveninstaller.GitHubCloneUI.*;
import static com.example.maveninstaller.RepositoryUtils.getRepoName;
import static com.example.maveninstaller.RunMaven.runMavenBuild;
import static com.example.maveninstaller.ShortcutManager.createShortcut;

public class CloneRepository {
     public static void cloneRepository() {
        String repoUrl = repoUrlField.getText().trim();
        String targetPath = targetPathField.getText().trim();
        String branch = (String) branchSelector.getSelectedItem();
        outputConsole.append(String.valueOf(repoUrl.isEmpty()));
        outputConsole.append(String.valueOf(!repoUrl.contains("github.")));
        outputConsole.append(String.valueOf(!repoUrl.contains("gitlab.")));

        if (targetPath.isEmpty()) {
            outputConsole.setText("Please select a target folder!\n");
            return;
        }

        if (repoUrl.isEmpty() || !repoUrl.contains("git")) {
            outputConsole.setText("Invalid repository URL!\n");
            return;
        }

        outputConsole.setText("Cloning repository...\n");
        progressBar.setIndeterminate(true);

        // Use SwingWorker to handle the cloning process in the background
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                if(repoUrl.contains("gitlab.")) {
                    try {
                        outputConsole.append("gitlab.\n");
                        String userName = String.valueOf(gitLabUserNameField);
                        String pwd = String.valueOf(gitLabPasswordFieldPassword);
                        String repoUrlEncoded = "https://" + userName + ":" + pwd + "@" + repoUrl.substring(8, repoUrl.length());

                        ProcessBuilder builder = new ProcessBuilder("git", "clone", repoUrlEncoded);
                        builder.directory(new File(targetPath));
                        builder.redirectErrorStream(true);
                        Process process = builder.start();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            publish(line);
                        }
                        process.waitFor();

                        // Check if pom.xml exists and run Maven build if necessary
                        File pomFile = new File(targetPath + "/" + getRepoName(repoUrl) + "/pom.xml");
                        if (pomFile.exists()) {
                            outputConsole.append("Maven project detected.\n");
                            runMavenBuild(targetPath, repoUrl);
                        } else {
                            outputConsole.append("No Maven project found.\n");
                        }

                        // Create shortcut after cloning
                        createShortcut(targetPath);

                    } catch (Exception e) {
                        publish("Error cloning repository!\n" + e.getMessage());
                    }
                }else {
                    try {
                        ProcessBuilder builder = new ProcessBuilder("git", "clone", "--branch", branch, repoUrl);
                        builder.directory(new File(targetPath));
                        builder.redirectErrorStream(true);
                        Process process = builder.start();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            publish(line);
                        }
                        process.waitFor();

                        // Check if pom.xml exists and run Maven build if necessary
                        File pomFile = new File(targetPath + "/" + getRepoName(repoUrl) + "/pom.xml");
                        if (pomFile.exists()) {
                            outputConsole.append("Maven project detected.\n");
                            runMavenBuild(targetPath, repoUrl);
                        } else {
                            outputConsole.append("No Maven project found.\n");
                        }

                        // Create shortcut after cloning
                        createShortcut(targetPath);

                    } catch (Exception e) {
                        publish("Error cloning repository!\n" + e.getMessage());
                    }
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
                outputConsole.append("Cloning completed!\n");
                displayProjectInfo();
            }
        }.execute();
    }
}
