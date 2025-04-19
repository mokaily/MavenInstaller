package com.example.maveninstaller.Helpers;

import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;

import static com.example.maveninstaller.Constants.Config_Imported;
import static com.example.maveninstaller.Constants.Config_Not_Imported;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.Helpers.OSChecker.isLinux;
import static com.example.maveninstaller.Helpers.OSChecker.isWindows;

public class ConfigImporter {
    public static void importConfig(File jsonFile) {
        try {
            String content = new String(Files.readAllBytes(jsonFile.toPath()));
            JSONObject json = new JSONObject(content);

            // 1. Repo URL
            if (json.has("repo_url")) {
                repoUrlField.setText(json.getString("repo_url"));
            }

            // 2. Application Name
            if (json.has("app_name")) {
                applicationNameField.setText(json.getString("app_name"));
            }

            // 3. Pin to Dock / Start Menu
            if (json.has("pin_to_dock")) {
                pinToDockCheckbox.setSelected(json.getBoolean("pin_to_dock"));
            }

            // 4. Icon Path depending on OS
            if (json.has("icon")) {
                JSONObject icon = json.getJSONObject("icon");
                String osName = System.getProperty("os.name").toLowerCase();
                String iconPath = "";

                if (isWindows()) {
                    iconPath = icon.optString("windows", "");
                } else if (isLinux()) {
                    iconPath = icon.optString("mac", "");
                } else {
                    iconPath = icon.optString("linux", "");
                }

                if (!iconPath.isEmpty()) {
                    shortcutIconField.setText(iconPath);
                }
            }

            appendToConsole(Config_Imported + jsonFile.getAbsolutePath() + "\n", false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, Config_Not_Imported + ex.getMessage());
        }
    }
}
