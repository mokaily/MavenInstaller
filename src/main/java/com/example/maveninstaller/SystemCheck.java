package com.example.maveninstaller;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class SystemCheck {

    public static void checkAndDisplay() {
        StringBuilder result = new StringBuilder("Checking system requirements...\n");

        // Check Java installation
        result.append(checkCommand("java -version", "Java"));

        // Check Maven installation
        result.append(checkCommand("mvn -version", "Maven"));

        // Check Git installation
        result.append(checkCommand("git --version", "Git"));

        // Show the results in a message box
        JOptionPane.showMessageDialog(null, result.toString(), "System Check", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String checkCommand(String command, String name) {
        try {
            // Execute the command
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor(); // Wait for the command to finish

            // If it completes successfully, return the success message
            return name + " is installed.\n";
        } catch (Exception e) {
            // If an error occurs, return the failure message
            return name + " is NOT installed.\n";
        }
    }
}
