package com.example.maveninstaller;

import javax.swing.*;

public class GitHubCloneApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SystemCheck.checkAndDisplay();
            GitHubCloneUI ui = new GitHubCloneUI();
            ui.createAndShowGUI();
        });
    }
}

