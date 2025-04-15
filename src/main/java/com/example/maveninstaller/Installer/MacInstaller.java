package com.example.maveninstaller.Installer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.maveninstaller.Installer.CreateInstaller.getApplicationName;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class MacInstaller {

    public static void createMacShortcut(Path jarPath, String pomPath) throws IOException {
        String appName = getApplicationName(pomPath);
        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + "/Desktop";
        String commandFilePath = desktopPath + "/" + appName + ".command";
        String workingDir = jarPath.getParent().toString();

        // ✅ 1. Create .command launcher for macOS
        String commandContent = "#!/bin/bash\n" +
                "cd \"" + workingDir + "\"\n" +
                "java -jar \"" + jarPath.toString() + "\"\n";

        Files.writeString(Paths.get(commandFilePath), commandContent);

        // 🧼 Make it executable
        Process chmod = new ProcessBuilder("chmod", "+x", commandFilePath).start();
        try {
            chmod.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        outputConsole.append("✅ Created .command launcher on Desktop: " + commandFilePath + "\n");

        // 📌 2. Optionally attempt to pin to Dock
        if (pinToDockCheckbox.isSelected()) {
            String dockScript = """
                tell application "System Events" to tell dock preferences to set autohide to false
                tell application "Dock" to quit
                delay 1
                do shell script "defaults write com.apple.dock persistent-apps -array-add '{tile-data={file-data={_CFURLString=\\"file://%s\\"; _CFURLStringType=15;};};}'"
                do shell script "killall Dock"
                """.formatted(commandFilePath);

            File scriptFile = File.createTempFile("pin_to_dock", ".applescript");
            Files.writeString(scriptFile.toPath(), dockScript);

            new ProcessBuilder("osascript", scriptFile.getAbsolutePath()).start();
            outputConsole.append("📌 Attempted to pin .command file to Dock\n");
        }
    }
}
