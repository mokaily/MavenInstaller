package com.example.maveninstaller;

import javax.swing.*;
import java.io.File;
import java.util.List;

import static com.example.maveninstaller.GUI.InitializeDefaults.outputConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.progressBar;

public class Cleaner {

    public static void deleteAllExceptTarget(File directory) {
        outputConsole.append("🧹 Cleaning project folder...\n");
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

                for (File file : files) {
                    if (file.getName().equals("target")) {
                        publish("➡️ Skipped: " + file.getName());
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
                    outputConsole.append(message + "\n");
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.repaint();
                outputConsole.append("✅ Cleaning completed.\n");
            }
        }.execute();
    }
}
