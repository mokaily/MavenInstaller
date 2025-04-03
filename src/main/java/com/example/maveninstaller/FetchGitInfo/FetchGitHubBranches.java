package com.example.maveninstaller.FetchGitInfo;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.outputConsole;
import static com.example.maveninstaller.GUI.GitMavenCloneUI.progressBar;
import static com.example.maveninstaller.GUI.UpdateBranchSelector.updateBranchSelector;

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
                    outputConsole.append("Failed to fetch GitHub branches!\n" + e.getMessage());
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
                progressBar.setVisible(true);
                progressBar.repaint();
            }
        }.execute();
    }
}