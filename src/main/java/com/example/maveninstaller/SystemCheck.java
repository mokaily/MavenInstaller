package com.example.maveninstaller;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemCheck {

    public static void checkAndDisplay() {
        JEditorPane outputPane = new JEditorPane();
        outputPane.setContentType("text/html");
        outputPane.setEditable(false);
        outputPane.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputPane.setText("<html><body><p>🔧 Checking system requirements...</p></body></html>");

        outputPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        StringBuilder result = new StringBuilder();
        result.append("<html><body style='font-family:consolas;font-size:12pt;'>");
        result.append("<h3>🔧 System Requirements Check</h3><hr><br>");

        boolean allToolsAvailable = true;

        boolean javaOk = checkCommand(new String[]{"java", "-version"}, "Java", result, "24", "https://www.oracle.com/de/java/technologies/downloads/#java" + "24");
        boolean mavenOk = checkCommand(new String[]{"mvn.cmd", "-version"}, "Maven", result, "3.10", "https://maven.apache.org/download.cgi");
        boolean gitOk = checkCommand(new String[]{"git", "--version"}, "Git", result, "2.49.0", "https://git-scm.com/downloads");

        allToolsAvailable = javaOk && mavenOk && gitOk;

        result.append("</body></html>");
        outputPane.setText(result.toString());

        JScrollPane scrollPane = new JScrollPane(outputPane);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        if (allToolsAvailable) {
            JOptionPane.showMessageDialog(null, scrollPane, "System Check Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int option = JOptionPane.showOptionDialog(
                    null,
                    scrollPane,
                    "Missing Requirements",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    new Object[]{"Exit Application"},
                    "Exit Application"
            );
            System.exit(0);
        }
    }

    private static boolean checkCommand(String[] command, String name, StringBuilder result, String latestVersion, String downloadLink) {
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
                result.append("✅ Installed (v").append(version).append(")<br>");
                if (isVersionLower(version, latestVersion)) {
                    result.append("⚠️ A newer version is available: v").append(latestVersion).append("<br>");
                    result.append("🔗 <a href='").append(downloadLink).append("'>Download here</a><br>");
                }
                result.append("<br>");
            } else {
                result.append("✅ Installed<br><br>");
            }
            return true;
        } catch (IOException | InterruptedException e) {
            result.append("❌ Not found or not in PATH<br>");
            result.append("🔗 <a href='").append(downloadLink).append("'>Download here</a><br><br>");
            return false;
        }
    }

    private static String extractVersion(String output) {
        Matcher matcher = Pattern.compile("\\b\\d+(\\.\\d+){1,2}\\b").matcher(output);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private static boolean isVersionLower(String current, String latest) {
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
