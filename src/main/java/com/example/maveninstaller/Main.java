package com.example.maveninstaller;

import com.example.maveninstaller.GUI.CheckRequirments.RequirementsChecker;
import com.example.maveninstaller.GUI.GitMavenCloneUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RequirementsChecker.checkAndDisplay();
            GitMavenCloneUI ui = new GitMavenCloneUI();
            ui.createAndShowGUI();
        });
    }
}

