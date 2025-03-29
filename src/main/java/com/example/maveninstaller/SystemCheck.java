package com.example.maveninstaller;

import javax.swing.*;
import java.io.*;

public class SystemCheck {

    public static void checkAndDisplay() {
        StringBuilder result = new StringBuilder("🔎 Checking system requirements...\n\n");

        // Check Java installation
        result.append(checkCommand(new String[]{"java", "-version"}, "Java"));
        // Check Java installation
        result.append(checkCommand(new String[]{"mvn.cmd", "-version"}, "Maven"));
        // Check Java installation
        result.append(checkCommand(new String[]{"git", "--version"}, "Git"));

        JOptionPane.showMessageDialog(null, result.toString(), "System Check", JOptionPane.INFORMATION_MESSAGE);
    }


    private static String checkCommand(String[] commandParts, String name) {
        if (commandParts[0] == null) {
            return name + " is NOT installed or not in PATH.\n";
        }

        try {
            Process process = Runtime.getRuntime().exec(commandParts);

            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = stdOut.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = stdErr.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                String version = extractVersion(output.toString());
                if (version != null) {
                    return name + " is installed. (Version: " + version + ")\n\n";
                } else {
                    return name + " is installed.\n\n";
                }
            } else {
                return name + " might not be installed properly.\n\n";
            }

        } catch (IOException | InterruptedException e) {
            return name + " is NOT installed.\nError: " + e.getMessage() + "\n\n";
        }
    }

    private static String extractVersion(String output) {
        // Check Tool version only
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\b\\d+(\\.\\d+){1,2}\\b").matcher(output);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
