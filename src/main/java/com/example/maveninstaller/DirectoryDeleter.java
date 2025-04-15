package com.example.maveninstaller;

import javax.swing.*;
import java.io.File;
import java.util.List;

import static com.example.maveninstaller.GUI.InitializeDefaults.outputConsole;

public class DirectoryDeleter {

    public static void deleteDirectoryAsync(File directory, Runnable onComplete) {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                publish("🧹 Deleting existing directory: " + directory.getName());
                deleteRecursive(directory);
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String msg : chunks) {
                    outputConsole.append(msg + "\n");
                }
            }

            @Override
            protected void done() {
                outputConsole.append("✅ Deletion completed.\n");
                if (onComplete != null) {
                    SwingUtilities.invokeLater(onComplete);
                }
            }

            private void deleteRecursive(File dir) {
                File[] contents = dir.listFiles();
                if (contents != null) {
                    for (File file : contents) {
                        deleteRecursive(file);
                    }
                }
                dir.delete();
            }
        }.execute();
    }
}
