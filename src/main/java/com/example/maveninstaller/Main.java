package com.example.maveninstaller;

import com.example.maveninstaller.GUI.CheckRequirments.RequirementsChecker;
import com.example.maveninstaller.GUI.SimpleUI;

import javax.swing.*;

import static com.example.maveninstaller.GUI.InitializeDefaults.inializeUIDefaults;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //set defaults
            inializeUIDefaults();


            RequirementsChecker.checkAndDisplay();
            SimpleUI.showSimpleUI();
        });
    }
}
