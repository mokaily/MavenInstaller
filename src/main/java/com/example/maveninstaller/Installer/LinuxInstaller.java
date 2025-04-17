package com.example.maveninstaller.Installer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.Installer.CreateInstaller.getApplicationName;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class LinuxInstaller {

    public static void createLinuxShortcut(Path jarPath, String pomPath) throws IOException, InterruptedException {
        String appName = getApplicationName(pomPath);
        String userHome = System.getProperty("user.home");

        String desktopPath = userHome + "/Desktop";
        String shPath = desktopPath + "/" + appName + ".sh";
        String desktopFilePath = desktopPath + "/" + appName + ".desktop";
        String workingDir = jarPath.getParent().toString();

        // ✅ 1. Create .sh launcher script
        String shContent = "#!/bin/bash\n" +
                "cd \"" + workingDir + "\"\n" +
                "java -jar \"" + jarPath.toString() + "\"\n";

        Files.writeString(Paths.get(shPath), shContent);
        new ProcessBuilder("chmod", "+x", shPath).start().waitFor();
        appendToConsole("✅ Created .sh launcher on Desktop: " + shPath + "\n", false);

        // 🎯 2. Create .desktop file
        String iconPath = shortcutIconField.getText().trim();
        StringBuilder desktopEntry = new StringBuilder();
        desktopEntry.append("[Desktop Entry]\n");
        desktopEntry.append("Type=Application\n");
        desktopEntry.append("Name=").append(appName).append("\n");
        desktopEntry.append("Exec=").append(shPath).append("\n");
        desktopEntry.append("Terminal=false\n");

        if (!iconPath.isEmpty() && iconPath.endsWith(".png")) {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                desktopEntry.append("Icon=").append(iconPath).append("\n");
            }
        }

        desktopEntry.append("Categories=Utility;Application;\n");

        Files.writeString(Paths.get(desktopFilePath), desktopEntry.toString());
        new ProcessBuilder("chmod", "+x", desktopFilePath).start().waitFor();
        appendToConsole("📄 Created .desktop shortcut on Desktop: " + desktopFilePath + "\n", false);

        // 📥 3. Add to Applications menu if selected
        if (pinToDockCheckbox.isSelected()) {
            String localAppPath = userHome + "/.local/share/applications/" + appName + ".desktop";
            Files.copy(Paths.get(desktopFilePath), Paths.get(localAppPath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            appendToConsole("📌 Added to Applications Menu: " + localAppPath + "\n", false);
        }
    }
}
