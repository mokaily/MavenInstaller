package com.example.maveninstaller;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;

public class ShortcutManager {

    public static void createShortcut(String targetPath) {
        String os = System.getProperty("os.name").toLowerCase();

        // Path to the executable (e.g., jar or executable file in the target directory)
        File execFile = new File(targetPath, "target/app.jar");  // Example for JAR file, can be customized

        // Check if the executable exists
        if (!execFile.exists()) {
            JOptionPane.showMessageDialog(null, "Executable file not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (os.contains("win")) {
                createWindowsShortcut(targetPath, execFile);
            } else if (os.contains("mac")) {
                createMacAlias(targetPath, execFile);
            } else {
                JOptionPane.showMessageDialog(null, "Unsupported OS for creating shortcut!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(null, "Shortcut created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error creating shortcut: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Windows Shortcut Creation
    private static void createWindowsShortcut(String targetPath, File execFile) throws IOException {
        String shortcutDir = targetPath + "/StartMenu";  // Directory where the shortcut will be placed
        File shortcutFolder = new File(shortcutDir);
        if (!shortcutFolder.exists()) {
            shortcutFolder.mkdirs();
        }

        String shortcutPath = shortcutDir + "/MyAppShortcut.lnk";

        // Using Windows Script Host to create a shortcut
        String script = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n" +
                "Set oShellLink = WshShell.CreateShortcut(\"" + shortcutPath + "\")\n" +
                "oShellLink.TargetPath = \"" + execFile.getAbsolutePath() + "\"\n" +
                "oShellLink.Save\n";
        File scriptFile = new File(shortcutDir + "/createShortcut.vbs");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile))) {
            writer.write(script);
        }
        ProcessBuilder scriptProcess = new ProcessBuilder("wscript", scriptFile.getAbsolutePath());
        scriptProcess.start();
    }

    // macOS Alias Creation (Symbolic Link)
    private static void createMacAlias(String targetPath, File execFile) throws IOException {
        String shortcutDir = targetPath + "/Applications";  // Directory where the alias will be placed
        File shortcutFolder = new File(shortcutDir);
        if (!shortcutFolder.exists()) {
            shortcutFolder.mkdirs();
        }

        String aliasPath = shortcutDir + "/MyAppShortcut.app";
        File aliasFile = new File(aliasPath);

        // Using symbolic link for creating alias in macOS
        if (!aliasFile.exists()) {
            Files.createSymbolicLink(aliasFile.toPath(), execFile.toPath());
        } else {
            JOptionPane.showMessageDialog(null, "Alias already exists at the specified location.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
