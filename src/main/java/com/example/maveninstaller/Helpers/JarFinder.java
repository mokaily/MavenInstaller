package com.example.maveninstaller.Helpers;

import java.io.File;
import java.nio.file.Path;

import static com.example.maveninstaller.Constants.Jar_Not_Found;
import static com.example.maveninstaller.Constants.Target_Folder_Found;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;

public class JarFinder {

    public static Path findJarInTarget(String projectDirPath) {
        File targetDir = new File(projectDirPath, "target");

        if (!targetDir.exists() || !targetDir.isDirectory()) {
            appendToConsole(Target_Folder_Found + targetDir.getAbsolutePath(), false);
            return null;
        }

        File[] jarFiles = targetDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles != null && jarFiles.length > 0) {
            return jarFiles[0].toPath(); // first matching JAR file
        }

        appendToConsole(Jar_Not_Found, false);
        return null;
    }
}
