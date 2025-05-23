package com.example.maveninstaller.FetchGitInfo;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.maveninstaller.Constants.Branches_Fetch_Failed;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.UpdateBranchSelector.updateBranchSelector;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

class FetchGitHubBranches {
    public static void fetchGitHubBranches(String repoUrl) {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        progressBar.repaint();
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    ProcessBuilder builder = new ProcessBuilder("git", "ls-remote", "--heads", repoUrl);
                    Process process = builder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    List<String> branches = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String branch = line.substring(line.lastIndexOf('/') + 1);
                        branches.add(branch);
                    }
                    updateBranchSelector(branches);
                } catch (Exception e) {
                    appendToConsole(Branches_Fetch_Failed + e.getMessage(), false);
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
                progressBar.setIndeterminate(false);
                progressBar.repaint();
            }
        }.execute();
    }
}