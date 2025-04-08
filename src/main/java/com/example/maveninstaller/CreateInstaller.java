package com.example.maveninstaller;

import javax.swing.*;
import java.util.List;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;
import static com.example.maveninstaller.Installer.CreateInstaller.createMavenExecShortcut;
import static com.example.maveninstaller.PomHelper.findPomXml;
import static com.example.maveninstaller.RepositoryHelper.getRepoName;
import static com.example.maveninstaller.RepositoryHelper.validateCustomRepo;
import static com.example.maveninstaller.RunMavenBuild.runMavenBuild;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class CreateInstaller {
    public static void createInstaller() {
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
