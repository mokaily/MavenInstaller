package com.example.maveninstaller;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import static com.example.maveninstaller.GitHubCloneUI.*;
import static com.example.maveninstaller.PomFinder.findPomXml;
import static com.example.maveninstaller.RepositoryUtils.getRepoName;
import static com.example.maveninstaller.RunMaven.runMavenBuild;
import static com.example.maveninstaller.ShortcutManager.createShortcut;

public class CloneRepository {
     public static void cloneRepository() {
        String repoUrl = repoUrlField.getText().trim();
        String targetPath = targetPathField.getText().trim();
        String branch = "";
        try{
            branch = (String) branchSelector.getSelectedItem();
        }catch (Exception ignored){}

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
         String finalBranch = branch;
         new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                if(repoUrl.contains("gitlab.")) {
                    try {
                        outputConsole.append("gitlab.\n");
                        String userName = gitLabUserNameField.getText().trim();
                        userName = userName.replace("@", "%40");
                        String pwd = gitLabPasswordFieldPassword.getText().trim();
                        String repoUrlEncoded = "https://" + userName + ":" + pwd + "@" + repoUrl.substring(8, repoUrl.length());
                        outputConsole.append(repoUrl + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
                        outputConsole.append(repoUrlEncoded + "\n");
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
                        String findPomXml = findPomXml(targetPath + "/" + getRepoName(repoUrl));
//                        File pomFile = new File(targetPath + "/" + getRepoName(repoUrl) + "/pom.xml");
                        if (findPomXml != null) {
                            outputConsole.append("Maven project detected.\n");

                            outputConsole.append(findPomXml);
                            runMavenBuild(findPomXml);
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
                        ProcessBuilder builder ;
                        outputConsole.append(finalBranch + "\n");

                        if(finalBranch == "") {
                            builder = new ProcessBuilder("git", "clone", repoUrl);
                        }else{
                            builder = new ProcessBuilder("git", "clone", "--branch", finalBranch, repoUrl);
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

                        // Check if pom.xml exists and run Maven build if necessary
                        String findPomXml = findPomXml(targetPath + "/" + getRepoName(repoUrl));
//                        File pomFile = new File(targetPath + "/" + getRepoName(repoUrl) + "/pom.xml");
                        if (findPomXml != null) {
                            outputConsole.append("Maven project detected.\n");
                            outputConsole.append(findPomXml);
                            runMavenBuild(findPomXml);
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
