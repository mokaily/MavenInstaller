package com.example.maveninstaller.Installer;

import javax.swing.*;
import java.io.File;
import java.nio.file.*;
import java.util.List;

import static com.example.maveninstaller.Helpers.Cleaner.deleteAllExceptTarget;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.Installer.LinuxInstaller.createLinuxShortcut;
import static com.example.maveninstaller.Installer.MacInstaller.createMacShortcut;
import static com.example.maveninstaller.Installer.WindowsInstaller.createWindowsShortcut;
import static com.example.maveninstaller.Helpers.JarFinder.findJarInTarget;
import static com.example.maveninstaller.Helpers.OSChecker.*;
import static com.example.maveninstaller.Helpers.PomHelper.fetchAppName;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.UXEnhancer.setButtonsEnabled;


public class CreateInstaller {
    public static void createMavenExecShortcut(String pomPath) {
        setButtonsEnabled(false);
        Path dir = findJarInTarget(pomPath);
        appendToConsole("⏳ Creating installer...\n", true);
        progressBar.setIndeterminate(true);
        progressBar.repaint();
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    if (isWindows()) {
                        createWindowsShortcut(dir, pomPath);
                    } else if (isMac()) {
                        createMacShortcut(dir, pomPath);
                    } else if (isLinux()) {
                        createLinuxShortcut(dir, pomPath);
                    } else {
                        JOptionPane.showMessageDialog(null, "Unsupported OS!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    setButtonsEnabled(true);
                    JOptionPane.showMessageDialog(null, "Error creating shortcut: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                setButtonsEnabled(true);
                try{
                    deleteAllExceptTarget(new File(pomPath));
                }catch(Exception e){

                }
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.repaint();
                });
            }
        }.execute();
    }

    public static String getApplicationName(String pomPath) {
        String appName = applicationNameField.getText().trim();

        if (appName.isEmpty()) {
            appName = fetchAppName(pomPath);
        }

        return appName;
    }

    public static String getDirectoryPath(Path dir) {
        return dir.getParent().toString();
    }
}