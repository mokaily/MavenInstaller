package com.example.maveninstaller.Installer;

import java.io.IOException;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
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
//                createLinuxShortcut(dir);
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

//    private static void createMacShortcut(File dir) throws IOException {
//        File launcher = new File(dir, "run-maven-exec.command");
//        String script = "#!/bin/bash\ncd \"" + dir.getAbsolutePath() + "\"\nmvn exec:exec\n";
//        Files.writeString(launcher.toPath(), script);
//        launcher.setExecutable(true);
//    }

    private static void createLinuxShortcut(File dir) throws IOException {
        File desktopFile = new File(dir, "RunMavenExec.desktop");
        String content = "[Desktop Entry]\n" +
                "Name=Run Maven Exec\n" +
                "Exec=mvn exec:exec\n" +
                "Path=" + dir.getAbsolutePath() + "\n" +
                "Terminal=true\n" +
                "Type=Application\n";

        Files.writeString(desktopFile.toPath(), content);
        desktopFile.setExecutable(true);
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