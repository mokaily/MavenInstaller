package com.example.maveninstaller.Helpers;

import javax.swing.*;
import java.io.File;
import java.util.List;

import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class Cleaner {

    public static void deleteAllExceptTarget(File directory) {
        appendToConsole("🧹 Cleaning project folder...\n", false);
        progressBar.setIndeterminate(true);
        progressBar.repaint();

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                File[] files = directory.listFiles();
                if (files == null) {
                    publish("⚠️ Directory is empty or inaccessible.");
                    return null;
                }

                File targetDir = new File(directory, "target");

                for (File file : files) {
                    if (file.getName().equals("target")) {
                        publish("➡️ Skipped: " + file.getName());
                        continue;
                    }

                    if (file.getName().toLowerCase().endsWith(".yaml")) {
                        File destination = new File(targetDir, file.getName());
                        boolean success = file.renameTo(destination);
                        if (success) {
                            publish("📦 Moved: " + file.getName() + " → target/");
                        } else {
                            publish("⚠️ Failed to move: " + file.getName());
                        }
                        continue;
                    }

                    deleteRecursively(file);
                    publish("🗑️ Deleted: " + file.getName());
                }

                return null;
            }

            private void deleteRecursively(File file) {
                if (file.isDirectory()) {
                    File[] subFiles = file.listFiles();
                    if (subFiles != null) {
                        for (File subFile : subFiles) {
                            deleteRecursively(subFile);
                        }
                    }
                }
                file.delete();
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    appendToConsole(message + "\n", false);
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.repaint();
                appendToConsole("✅ Cleaning completed.\n", false);
            }
        }.execute();
    }
}
