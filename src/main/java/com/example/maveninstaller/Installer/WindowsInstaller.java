package com.example.maveninstaller.Installer;

import mslinks.ShellLink;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.GUI.GitMavenCloneUI.outputConsole;
import static com.example.maveninstaller.Installer.CreateInstaller.*;

class WindowsInstaller {
    public static void createWindowsShortcut(Path dir, String pomPath) throws IOException {
        String jarPath = dir.toString();
        String appName = getApplicationName(pomPath);

        //Get javaw path
        String javaPath = "";
        Process process = new ProcessBuilder("where", "javaw").start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            javaPath = reader.readLine();
        }

        if (javaPath == null || javaPath.isEmpty()) {
            outputConsole.append("Could not find javaw.exe in PATH." + "\n");
            throw new IOException("Could not find javaw.exe in PATH.");
        }

        outputConsole.append("\n" + "Using javaw: " + javaPath + "\n");


        // Prepare common shortcut object
        ShellLink link = ShellLink.createLink(javaPath)
                .setCMDArgs("-jar \"" + jarPath + "\"")
                .setName(STR."\{appName}.lnk")
                .setIconLocation(jarPath);

        // Set icon if valid
        String iconPath = shortcutIconField.getText().trim();
        if (!iconPath.isEmpty() && iconPath.endsWith(".ico")) {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                link.setIconLocation(iconPath);
            }
        }

        // Save to Desktop
        String desktopShortcut = System.getenv("USERPROFILE") + "\\Desktop\\" + appName + ".lnk";
        link.saveTo(desktopShortcut);
        outputConsole.append("Shortcut created on Desktop: " + desktopShortcut + "\n");

        if(pinToDockCheckbox.isSelected()){
            // Save to Start Menu
            String startMenuDir = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\";
            String startMenuShortcut = startMenuDir +  appName + ".lnk";
            link.saveTo(startMenuShortcut);
            outputConsole.append("Shortcut created in Start Menu: " + startMenuShortcut + "\n");
        }
    }
}