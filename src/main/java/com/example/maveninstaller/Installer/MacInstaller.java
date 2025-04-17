package com.example.maveninstaller.Installer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.Installer.CreateInstaller.getApplicationName;
import static com.example.maveninstaller.Installer.CreateInstaller.getDirectoryPath;

public class MacInstaller {
    public static void createMacShortcut(Path dir, String pomPath) throws IOException {
        String appName = getApplicationName(pomPath);
        String dirPath = getDirectoryPath(dir);

        Path appBundle = Path.of(System.getProperty("user.home"), "Applications", appName + ".app");
        Path contents = appBundle.resolve("Contents");
        Path macos = contents.resolve("MacOS");
        Path resources = contents.resolve("Resources");

        Files.createDirectories(macos);
        Files.createDirectories(resources);

        // Create launcher script with -XstartOnFirstThread
        Path launcher = macos.resolve(appName);
        String script = "#!/bin/bash\ncd \"" + dirPath + "\"\njava -XstartOnFirstThread -jar \"" + dir.toString() + "\"\n";
        Files.writeString(launcher, script);
        launcher.toFile().setExecutable(true);

        // Handle icon file (.icns or convert from image)
        String iconPath = shortcutIconField.getText().trim();
        String iconName = null;
        if (!iconPath.isEmpty()) {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                if (iconPath.toLowerCase().endsWith(".icns")) {
                    iconName = iconFile.getName();
                    Files.copy(iconFile.toPath(), resources.resolve(iconName), StandardCopyOption.REPLACE_EXISTING);
                    appendToConsole("✅ Copied .icns icon to Resources: " + iconName + "\n", false);
                } else {
                    BufferedImage original = ImageIO.read(iconFile);
                    if (original != null) {
                        File iconset = Files.createTempDirectory("iconset").toFile();
                        iconset.deleteOnExit();
                        int[] sizes = {16, 32, 64, 128, 256, 512, 1024};
                        for (int size : sizes) {
                            BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g = resized.createGraphics();
                            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                            g.drawImage(original, 0, 0, size, size, null);
                            g.dispose();

                            File pngFile = new File(iconset, "icon_" + size + "x" + size + ".png");
                            ImageIO.write(resized, "png", pngFile);
                        }

                        File renamedSet = new File(iconset.getAbsolutePath() + ".iconset");
                        iconset.renameTo(renamedSet);
                        File icnsFile = resources.resolve(appName + ".icns").toFile();
                        Process p = new ProcessBuilder("iconutil", "-c", "icns", renamedSet.getAbsolutePath(), "-o", icnsFile.getAbsolutePath()).start();
                        try {
                            int exit = p.waitFor();
                            if (exit == 0) {
                                iconName = icnsFile.getName();
                                appendToConsole("✅ Created .icns icon at: " + icnsFile.getAbsolutePath() + "\n", false);
                            } else {
                                appendToConsole("⚠️ iconutil failed.\n", false);
                            }
                        } catch (InterruptedException e) {
                            appendToConsole("⚠️ iconutil interrupted.\n", false);
                        }
                    }
                }
            } else {
                appendToConsole("⚠️ Icon file not found: " + iconPath + "\n", false);
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

        appendToConsole("✅ Created macOS .app bundle at: " + appBundle.toString() + "\n", false);

        if (pinToDockCheckbox.isSelected()) {
            String dockScript = "tell application \"System Events\" to tell dock preferences to set autohide to false\n"
                    + "tell application \"Dock\" to quit\n"
                    + "delay 1\n"
                    + "do shell script \"defaults write com.apple.dock persistent-apps -array-add '{tile-data={file-data={_CFURLString=\"file://"
                    + appBundle.toAbsolutePath().toString() + "\"; _CFURLStringType=15;};};}'\"\n"
                    + "do shell script \"killall Dock\"";

            File dockScriptFile = Files.createTempFile("pin_to_dock", ".applescript").toFile();
            Files.writeString(dockScriptFile.toPath(), dockScript);
            new ProcessBuilder("osascript", dockScriptFile.getAbsolutePath()).start();
            appendToConsole("📌 Attempted to pin app to Dock\n", false);
            JOptionPane.showMessageDialog(null,
                    "The app was added to the Dock. If it doesn't appear immediately, try launching it manually then right-click → Options → Keep in Dock.",
                    "Dock Pin Notice",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // Create alias to Desktop
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
            appendToConsole("📁 Shortcut created on Desktop (exit code: " + code + ")\n", false);
        } catch (InterruptedException e) {
            appendToConsole("⚠️ AppleScript execution interrupted.\n", false);
        }
    }
}
