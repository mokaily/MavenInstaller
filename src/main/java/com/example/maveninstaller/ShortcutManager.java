package com.example.maveninstaller;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;

import static com.example.maveninstaller.OperationSystemChecker.*;

public class ShortcutManager {

    public static void createShortcut(String targetPath) {
        File targetDir = new File(targetPath);
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Target folder not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File[] jarFiles = targetDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            JOptionPane.showMessageDialog(null, "No JAR file found in the target directory!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File execFile = jarFiles[0]; // Take the first JAR found

        try {
            if (isWindows()) {
                createWindowsInstaller(targetPath, execFile);
                JOptionPane.showMessageDialog(null, "Windows shortcut created!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else if (isMac()) {
                createMacInstaller(targetPath, execFile);
                JOptionPane.showMessageDialog(null, "macOS installer created!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else if (isLinux()) {
                createLinuxLauncher(targetPath, execFile);
                JOptionPane.showMessageDialog(null, "Linux launcher script created!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Unsupported OS!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error creating shortcut/installer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createWindowsInstaller(String targetPath, File execFile) throws IOException {
        String installerDir = targetPath + "/WindowsInstaller";
        File folder = new File(installerDir);
        folder.mkdirs();

        String shortcutPath = installerDir + "/MyAppShortcut.lnk";
        String script = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n" +
                "Set oShellLink = WshShell.CreateShortcut(\"" + shortcutPath + "\")\n" +
                "oShellLink.TargetPath = \"" + execFile.getAbsolutePath() + "\"\n" +
                "oShellLink.WorkingDirectory = \"" + execFile.getParent() + "\"\n" +
                "oShellLink.Save\n";

        File scriptFile = new File(installerDir + "/createShortcut.vbs");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile))) {
            writer.write(script);
        }
        new ProcessBuilder("wscript", scriptFile.getAbsolutePath()).start();

        // Add to Startup folder (optional)
        String startup = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\MyAppShortcut.lnk";
        Files.copy(Paths.get(shortcutPath), Paths.get(startup), StandardCopyOption.REPLACE_EXISTING);

        JOptionPane.showMessageDialog(null, "Windows installer and shortcut created!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void createMacInstaller(String targetPath, File execFile) throws IOException {
        String appDir = targetPath + "/MyApp.app/Contents/MacOS";
        File appFolder = new File(appDir);
        appFolder.mkdirs();

        File launcher = new File(appFolder, "MyApp");
        String script = "#!/bin/bash\nopen -a Terminal \"java -jar " + execFile.getAbsolutePath() + "\"\n";

        Files.writeString(launcher.toPath(), script);
        launcher.setExecutable(true);

        // Add to Dock using AppleScript
        String appleScript = "tell application \"System Events\" to make new login item at end with properties {path:\"" + targetPath + "/MyApp.app\", hidden:false}";
        File scriptFile = new File(targetPath, "addToDock.scpt");
        Files.writeString(scriptFile.toPath(), appleScript);
        new ProcessBuilder("osascript", scriptFile.getAbsolutePath()).start();

        JOptionPane.showMessageDialog(null, "macOS installer and Dock icon created!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void createLinuxLauncher(String targetPath, File execFile) throws IOException {
        File launcher = new File(targetPath, "myapp.desktop");
        String content = "[Desktop Entry]\n" +
                "Name=MyApp\n" +
                "Exec=java -jar " + execFile.getAbsolutePath() + "\n" +
                "Icon=utilities-terminal\n" +
                "Terminal=false\n" +
                "Type=Application\n" +
                "Categories=Utility;Application;\n";

        Files.writeString(launcher.toPath(), content);
        launcher.setExecutable(true);

        // Try to copy to autostart
        String home = System.getProperty("user.home");
        Path autostart = Paths.get(home, ".config", "autostart", "myapp.desktop");
        Files.createDirectories(autostart.getParent());
        Files.copy(launcher.toPath(), autostart, StandardCopyOption.REPLACE_EXISTING);

        JOptionPane.showMessageDialog(null, "Linux launcher and autostart entry created!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}