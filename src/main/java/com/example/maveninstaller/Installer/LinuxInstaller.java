package com.example.maveninstaller.Installer;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.Installer.CreateInstaller.getApplicationName;

class LinuxInstaller {
    public static void createLinuxShortcut(Path dir, String pomPath) throws IOException, InterruptedException {
        String jarPath = dir.toString();
        String appName = getApplicationName(pomPath);
        String iconPath = shortcutIconField.getText().trim();
        String appId = appName.toLowerCase().replaceAll("\\s+", "");
        String userHome = System.getProperty("user.home");

        // Prepare manifest file for Flatpak
        Path buildDir = dir.resolve("flatpak_build");
        Path manifestFile = buildDir.resolve(appId + ".yml");
        Files.createDirectories(buildDir);

        // Collect project files
        List<Path> files = new ArrayList<>();
        Files.walk(dir).filter(Files::isRegularFile).forEach(files::add);

        boolean useFlatpak = false;
        try (BufferedWriter writer = Files.newBufferedWriter(manifestFile)) {
            writer.write("id: com.example." + appId + "\n");
            writer.write("runtime: org.freedesktop.Platform\n");
            writer.write("runtime-version: '23.08'\n");
            writer.write("sdk: org.freedesktop.Sdk\n");
            writer.write("sdk-extensions:\n  - org.freedesktop.Sdk.Extension.openjdk21\n");
            writer.write("modules:\n");
            writer.write("  - name: application\n");
            writer.write("    buildsystem: simple\n");
            writer.write("    build-commands:\n");
            writer.write("      - install -Dm755 -t /app/bin run.sh\n");
            writer.write("    sources:\n");
            for (Path file : files) {
                writer.write("      - type: file\n");
                writer.write("        path: '" + dir.relativize(file).toString() + "'\n");
            }
            writer.write("      - type: script\n");
            writer.write("        dest-filename: run.sh\n");
            writer.write("        commands:\n");
            writer.write("          - \"cd /app/share/com.example." + appId + "\"\n");
            writer.write("          - \"java -jar app.jar\"\n");
            writer.write("command: run.sh\n");
            writer.write("finish-args:\n");
            writer.write("  - \"--env=PATH=/app/bin:/usr/bin\"\n");
            writer.write("  - --share=network\n  - --socket=fallback-x11\n  - --socket=wayland\n  - --device=dri\n");
        }

        try {
            String[] command = {
                    "flatpak-builder", "--user", "--force-clean",
                    "--install-deps-from=flathub", "--repo=repo", "--install",
                    "--state-dir=" + buildDir.resolve("state"),
                    buildDir.resolve("build").toString(), manifestFile.toString()
            };

            Process process = new ProcessBuilder(command).directory(dir.toFile()).redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                outputConsole.append(line + "\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                outputConsole.append("✅ Flatpak package built and installed successfully.\n");
                useFlatpak = true;
            } else {
                outputConsole.append("❌ Flatpak build failed with code: " + exitCode + "\n");
            }
        } catch (Exception e) {
            outputConsole.append("⚠️ Flatpak not available: " + e.getMessage() + "\n");
        }

        if (!useFlatpak) {
            outputConsole.append("⏳ Falling back to .desktop shortcut creation...\n");

            StringBuilder content = new StringBuilder();
            content.append("[Desktop Entry]\n");
            content.append("Type=Application\n");
            content.append("Name=").append(appName).append("\n");
            content.append("Exec=java -jar \"").append(jarPath).append("\"\n");
            content.append("Terminal=false\n");

            if (!iconPath.isEmpty() && iconPath.endsWith(".png")) {
                File iconFile = new File(iconPath);
                if (iconFile.exists()) {
                    content.append("Icon=").append(iconPath).append("\n");
                }
            }

            content.append("Categories=Utility;Application;\n");

            File desktopFile = new File(userHome + "/Desktop/" + appName + ".desktop");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(desktopFile))) {
                writer.write(content.toString());
            }

            Process chmod = new ProcessBuilder("chmod", "+x", desktopFile.getAbsolutePath()).start();
            chmod.waitFor();

            outputConsole.append("✅ Shortcut created on Desktop: " + desktopFile.getAbsolutePath() + "\n");

            File localAppDir = new File(userHome + "/.local/share/applications");
            if (!localAppDir.exists()) localAppDir.mkdirs();

            File appMenuShortcut = new File(localAppDir, appName + ".desktop");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(appMenuShortcut))) {
                writer.write(content.toString());
            }
            outputConsole.append("✅ Added to Applications menu: " + appMenuShortcut.getAbsolutePath() + "\n");
        }
    }
}
