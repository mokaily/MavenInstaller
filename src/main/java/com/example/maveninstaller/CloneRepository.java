package com.example.maveninstaller;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static com.example.maveninstaller.BuildRepository.*;
import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.FetchGitInfo.FetchGitOwnerInfo.fetchGitOwnerInfo;
import static com.example.maveninstaller.FetchGitInfo.FetchReadMeInfo.fetchReadMeInfo;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.Helpers.PomHelper.findPomXml;
import static com.example.maveninstaller.Helpers.PomHelper.fetchAppName;
import static com.example.maveninstaller.Helpers.RepositoryHelper.*;
import static com.example.maveninstaller.UXEnhancer.setButtonsEnabled;

public class CloneRepository {
    private static boolean cloneSuccess = false;
    public static void cloneRepository(boolean isOneFunction) {
        cloneSuccess = false;
        setButtonsEnabled(false);
        String repoUrl = repoUrlField.getText().trim();
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 4);
        }
        String targetPath = targetPathField.getText().trim();

        if (targetPath.isEmpty()) {
            setButtonsEnabled(true);
            appendToConsole(Select_Install_Path, true);
            return;
        }

        if (repoUrl.isEmpty() || !repoUrl.contains("git")) {
            setButtonsEnabled(true);
            appendToConsole(Invalid_Repository, true);
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

        appendToConsole(Cloning_Repository, true);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        progressBar.repaint();

        String finalBranch = branch;
        String finalRepoUrl = repoUrl;

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    String fullPath = targetPath + "/" + getRepoName(finalRepoUrl);
                    String pomPath = findPomXml(fullPath);

                    // Check if the project directory already exists
                    File projectDir = new File(fullPath);
                    if (projectDir.exists()) {
                        appendToConsole(Project_Exist, false);
                        try {
                            FileUtils.deleteDirectory(projectDir);
                            appendToConsole(Project_Deleted, false);
                        } catch (IOException e) {
                            appendToConsole(Project_Deleted_Failed + e.getMessage(), false);
                            return null;
                        }
                    }

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
                        appendToConsole(line, false);
                    }
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        cloneSuccess = true;
                    } else {
                        cloneSuccess = false;
                        appendToConsole(Clone_Failed + exitCode, false);
                    }

                    if (pomPath != null) {
                        applicationNameField.setText(fetchAppName(pomPath));
                        fetchGitOwnerInfo();
                        fetchReadMeInfo();
                    }

                    if (pomPath != null) {
                        appendToConsole(Maven_Detected, false);
                        appendToConsole(pomPath, false);
                    } else {
                        appendToConsole(Maven_Not_Found, false);
                    }

                    appendToConsole(Clone_Completed, false);

                } catch (Exception e) {
                    cloneSuccess = false;
                    setButtonsEnabled(true);
                    appendToConsole(Clone_Error + e.getMessage(), false);
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
                if (isOneFunction && cloneSuccess) {
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
