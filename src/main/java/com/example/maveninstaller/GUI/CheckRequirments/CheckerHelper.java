package com.example.maveninstaller.GUI.CheckRequirments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.maveninstaller.Constants.*;

class CheckerHelper {
    public static boolean checkCommand(String[] command, String name, StringBuilder result, String latestVersion, String downloadLink) {
        result.append("<b>🔸 ").append(name).append("</b>: ");
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
            String version = extractVersion(output.toString());

            if (version != null) {
                result.append(Installed + " (v\"").append(version).append(")<br>");
                if (isVersionLower(version, latestVersion)) {
                    result.append(Newer_Version_Available).append(latestVersion).append("<br>");
                    result.append("🔗 <a href='").append(downloadLink).append("'>" + Download_Here + "</a><br>");
                }
                result.append("<br>");
            } else {
                result.append(Installed + "<br><br>");
            }
            return true;
        } catch (IOException | InterruptedException e) {
            result.append(Path_Not_Found + "<br>");
            result.append("🔗 <a href='").append(downloadLink).append("'>" + Download_Here + "</a><br><br>");
            return false;
        }
    }

    public static String extractVersion(String output) {
        Matcher matcher = Pattern.compile("\\b\\d+(\\.\\d+){1,2}\\b").matcher(output);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static boolean isVersionLower(String current, String latest) {
        String[] currentParts = current.split("\\.");
        String[] latestParts = latest.split("\\.");
        int length = Math.max(currentParts.length, latestParts.length);

        for (int i = 0; i < length; i++) {
            int cur = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int lat = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            if (cur < lat) return true;
            if (cur > lat) return false;
        }
        return false;
    }
}