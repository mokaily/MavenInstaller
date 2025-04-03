package com.example.maveninstaller.Installer;

import javax.swing.*;
import java.nio.file.*;
import java.util.List;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.Installer.LinuxInstaller.createLinuxShortcut;
import static com.example.maveninstaller.Installer.MacInstaller.createMacShortcut;
import static com.example.maveninstaller.Installer.WindowsInstaller.createWindowsShortcut;
import static com.example.maveninstaller.JarFinder.findJarInTarget;
import static com.example.maveninstaller.OperationSystemChecker.*;
import static com.example.maveninstaller.PomHelper.fetchAppName;

public class CreateInstaller {
    public static void createMavenExecShortcut(String pomPath) {
        Path dir = findJarInTarget(pomPath);
        outputConsole.setText("⏳ Creating installer...\n");
        progressBar.setIndeterminate(true);



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
                    JOptionPane.showMessageDialog(null, "Error creating shortcut: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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