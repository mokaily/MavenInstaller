package com.example.maveninstaller;

import java.io.File;
import java.nio.file.Path;

public class JarFinder {

    public static Path findJarInTarget(String projectDirPath) {
        File targetDir = new File(projectDirPath, "target");

        if (!targetDir.exists() || !targetDir.isDirectory()) {
            System.out.println("❌ target/ folder not found at: " + targetDir.getAbsolutePath());
            return null;
        }

        File[] jarFiles = targetDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles != null && jarFiles.length > 0) {
            return jarFiles[0].toPath(); // first matching JAR file
        }

        System.out.println("⚠️ No .jar files found in target/");
        return null;
    }
}
