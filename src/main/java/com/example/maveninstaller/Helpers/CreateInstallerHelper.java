package com.example.maveninstaller.Helpers;

import static com.example.maveninstaller.Installer.CreateInstaller.createMavenExecShortcut;
import static com.example.maveninstaller.Helpers.PomHelper.findPomXml;
import static com.example.maveninstaller.Helpers.RepositoryHelper.getRepoName;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class CreateInstallerHelper {
    public static void createInstallerHelper() {
        String targetPath = targetPathField.getText().trim();
        String repoUrl = repoUrlField.getText().trim();
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 4);
        }
        String fullPath = targetPath + "/" + getRepoName(repoUrl);
        String pomPath = findPomXml(fullPath);
        createMavenExecShortcut(pomPath);
    }
}
