package com.example.maveninstaller;

import com.example.maveninstaller.GUI.CheckRequirments.RequirementsChecker;
import com.example.maveninstaller.GUI.SimpleUI;

import javax.swing.*;

import static com.example.maveninstaller.GUI.InitializeDefaults.initializeUIDefaults;
import static com.example.maveninstaller.GUI.InitializeDefaults.logFileStream;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //set defaults
            initializeUIDefaults();

            RequirementsChecker.checkAndDisplay();
            SimpleUI.showSimpleUI();
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (logFileStream != null) logFileStream.close();
        }));
    }
}
