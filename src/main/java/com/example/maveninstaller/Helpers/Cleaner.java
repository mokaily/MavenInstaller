package com.example.maveninstaller.Helpers;

import javax.swing.*;
import java.io.File;
import java.util.List;

import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class Cleaner {

    public static void deleteAllExceptTarget(File directory) {
        appendToConsole(Clean_Start, false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        progressBar.repaint();

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try{
                    File[] files = directory.listFiles();
                    if (files == null) {
                        appendToConsole(Dir_Is_Empty, false);
                        return null;
                    }

                    File targetDir = new File(directory, "target");

                    for (File file : files) {
                        if (file.getName().equals("target")) {
                            appendToConsole(Skipped + file.getName(), false);
                            continue;
                        }

                        if (file.getName().toLowerCase().endsWith(".yaml")) {
                            File destination = new File(targetDir, file.getName());
                            boolean success = file.renameTo(destination);
                            if (success) {
                                appendToConsole(Moved + file.getName() + " → target/", false);
                            } else {
                                appendToConsole(Failed_To_Move + file.getName(), false);
                            }
                            continue;
                        }

                        deleteRecursively(file);
                        appendToConsole(Deleted + file.getName(), false);
                    }
                }catch (Exception e){
                    progressBar.setIndeterminate(false);
                    progressBar.repaint();
                    e.printStackTrace();
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
                appendToConsole(Clean_Completed, false);
            }
        }.execute();
    }
}
