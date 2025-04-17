package com.example.maveninstaller.Installer;

import mslinks.ShellLink;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.maveninstaller.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.Installer.CreateInstaller.*;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

class WindowsInstaller {
    public static void createWindowsShortcut(Path dir, String pomPath) throws IOException {
        String jarPath = dir.toString();
        String appName = getApplicationName(pomPath);
        String userHome = System.getenv("USERPROFILE");
        String desktopPath = userHome + "\\Desktop";
        String batFilePath = desktopPath + "\\" + appName + ".bat";
        String workingDir = new File(jarPath).getParent();

        // ✅ 1. Create .bat file to run the jar
        String batContent = "cd /d \"" + workingDir + "\"\n" +
                "java -jar \"" + jarPath + "\"\n" +
                "pause";

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(batFilePath))) {
            writer.write(batContent);
        }

        appendToConsole("✅ Created .bat launcher on Desktop: " + batFilePath + "\n", false);

        // 📌 2. Create Start Menu shortcut (.lnk) for the .bat if checkbox is selected
        if (pinToDockCheckbox.isSelected()) {
            String startMenuDir = userHome + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\";
            String startMenuShortcut = startMenuDir + appName + ".lnk";

            ShellLink link = ShellLink.createLink("cmd.exe")
                    .setCMDArgs("/c \"" + batFilePath + "\"");

            // 🎨 Set icon if available
            String iconPath = shortcutIconField.getText().trim();
            if (!iconPath.isEmpty() && iconPath.endsWith(".ico")) {
                File iconFile = new File(iconPath);
                if (iconFile.exists()) {
                    link.setIconLocation(iconPath);
                }
            }

            link.saveTo(startMenuShortcut);
            appendToConsole("📌 Shortcut added to Start Menu: " + startMenuShortcut + "\n", false);
        }
    }
}