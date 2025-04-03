package com.example.maveninstaller.Installer;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.Installer.CreateInstaller.getApplicationName;
import static com.example.maveninstaller.Installer.CreateInstaller.getDirectoryPath;

public class MacInstaller {
    public static void createMacShortcut(Path dir, String pomPath, String iconPath) throws IOException {
        String appName = getApplicationName(pomPath);
        String dirPath = getDirectoryPath(dir);

        Path appBundle = Path.of(System.getProperty("user.home"), "Applications", appName + ".app");
        Path contents = appBundle.resolve("Contents");
        Path macos = contents.resolve("MacOS");
        Path resources = contents.resolve("Resources");

        Files.createDirectories(macos);
        Files.createDirectories(resources);

        // Create launcher script
        Path launcher = macos.resolve(appName);
        String script = "#!/bin/bash\ncd \"" + dirPath + "\"\njava -jar \"" + dir.toString() + "\"\n";
        Files.writeString(launcher, script);
        launcher.toFile().setExecutable(true);

        // Copy icon if specified and valid (.icns)
        String iconName = null;

        if (iconPath != null && !iconPath.isEmpty()) {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                if (iconPath.endsWith(".icns")) {
                    iconName = iconFile.getName();
                    Files.copy(iconFile.toPath(), resources.resolve(iconName), StandardCopyOption.REPLACE_EXISTING);
                } else if (iconPath.endsWith(".png") || iconPath.endsWith(".ico")) {
                    BufferedImage image = Imaging.getBufferedImage(iconFile);
                    iconName = appName + ".icns";
                    File icnsFile = resources.resolve(iconName).toFile();
                    Imaging.writeImage(image, icnsFile, ImageFormats.ICNS);
                }
            }
        }

        // Create Info.plist
        Path plistPath = contents.resolve("Info.plist");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(plistPath.toFile()))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n");
            writer.write("<plist version=\"1.0\">\n<dict>\n");
            writer.write("<key>CFBundleExecutable</key><string>" + appName + "</string>\n");
            writer.write("<key>CFBundleIdentifier</key><string>com.example." + appName.toLowerCase() + "</string>\n");
            writer.write("<key>CFBundleDisplayName</key><string>" + appName + "</string>\n");
            writer.write("<key>CFBundlePackageType</key><string>APPL</string>\n");
            writer.write("<key>CFBundleInfoDictionaryVersion</key><string>1.0</string>\n");
            writer.write("<key>CFBundleVersion</key><string>1.0</string>\n");
            if (iconName != null) {
                writer.write("<key>CFBundleIconFile</key><string>" + iconName.replace(".icns", "") + "</string>\n");
            }
            writer.write("</dict>\n</plist>\n");
        }

        outputConsole.append("Created macOS .app bundle at: " + appBundle.toString() + "\n");

        // Create alias to Desktop via AppleScript
        if (pinToDockCheckbox.isSelected()) {
            String dockScript = "tell application \"System Events\" to tell dock preferences to set autohide to false"
                    + "tell application \"Dock\" to quit "
                    + "delay 1"
                    + "do shell script \"defaults write com.apple.dock persistent-apps -array-add '{tile-data={file-data={_CFURLString=\"file://" + appBundle.toAbsolutePath().toString() + "\"; _CFURLStringType=15;};};}'\" "
                    + "do shell script \"killall Dock\"";

            File dockScriptFile = Files.createTempFile("pin_to_dock", ".applescript").toFile();
            Files.writeString(dockScriptFile.toPath(), dockScript);
            new ProcessBuilder("osascript", dockScriptFile.getAbsolutePath()).start();
            outputConsole.append("Attempted to pin app to Dock");
            JOptionPane.showMessageDialog(null,
                    "The app was added to the Dock. If it doesn't appear immediately, try launching it manually then right-click → Options → Keep in Dock.",
                    "Dock Pin Notice",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // Create alias to Desktop via AppleScript
        String[] scriptArgs = new String[] {
                "-e", "tell application \"Finder\"",
                "-e", "set theTgt to POSIX file \"" + appBundle.toString() + "\" as alias",
                "-e", "make new alias to theTgt at POSIX file \"" + System.getProperty("user.home") + "/Desktop\"",
                "-e", "set name of result to \"" + appName + "\"",
                "-e", "end tell"
        };
        String[] command = new String[scriptArgs.length + 1];
        command[0] = "osascript";
        System.arraycopy(scriptArgs, 0, command, 1, scriptArgs.length);
        Process pb = new ProcessBuilder(command).start();
        try {
            int code = pb.waitFor();
            outputConsole.append("Shortcut created on Desktop (exit code: " + code + ")\n");
        } catch (InterruptedException e) {
            outputConsole.append("AppleScript execution interrupted.\n");
        }
    }
}