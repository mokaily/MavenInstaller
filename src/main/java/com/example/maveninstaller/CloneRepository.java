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
         if (repoUrl.endsWith(".git")) {
             repoUrl = repoUrl.substring(0, repoUrl.length() - 4);
         }
         String targetPath = targetPathField.getText().trim();
         String branch = "";
         try {
             branch = (String) branchSelector.getSelectedItem();
         } catch (Exception ignored) {
         }

         if (targetPath.isEmpty()) {
             outputConsole.setText("❗ Please select a target folder!\n");
             return;
         }

         if (repoUrl.isEmpty() || !repoUrl.contains("git")) {
             outputConsole.setText("❗ Invalid repository URL!\n");
             return;
         }

         outputConsole.setText("⏳ Cloning repository...\n");
         progressBar.setIndeterminate(true);

         String finalBranch = branch;
         String finalRepoUrl = repoUrl;

         new SwingWorker<Void, String>() {
             @Override
             protected Void doInBackground() {
                 try {
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

                     String fullPath = targetPath + "/" + getRepoName(finalRepoUrl);
                     String pomPath = findPomXml(fullPath);

                     if (pomPath != null) {
                         publish("✅ Maven project detected.");
                         publish(pomPath);
                         runMavenBuild(pomPath);
                     } else {
                         publish("⚠️ No Maven project found.");
                     }

                     createShortcut(targetPath);

                 } catch (Exception e) {
                     publish("❌ Error cloning repository:\n" + e.getMessage());
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
                     outputConsole.append("✅ Cloning completed!\n");
                     displayProjectInfo();
                 });
             }
         }.execute();
     }
}
