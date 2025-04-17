package com.example.maveninstaller.FetchGitInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.example.maveninstaller.Helpers.RepositoryHelper.getRepoName;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class FetchReadMeInfo {
    public static void fetchReadMeInfo() {
        String projectPath = targetPathField.getText() + "/" + getRepoName(repoUrlField.getText());
        if(projectPath.endsWith(".git")){
            projectPath = projectPath.substring(0, projectPath.length() - 4);
        }

        StringBuilder projectInfo = new StringBuilder("Project Information:\n");
        projectInfo.append(projectPath);

        // Display the README content if it exists
        File readmeFile = findReadmeFile(projectPath);
        if (readmeFile != null && readmeFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(readmeFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    projectInfo.append(line + "\n");
                }
                readmeArea.append(String.valueOf(projectInfo));
            } catch (IOException e) {
                readmeArea.append("Could not read README.md\n");
            }
        } else {
            readmeArea.append("README.md not found.\n");
        }
    }

    public static File findReadmeFile(String directoryPath) {
        return searchReadme(new File(directoryPath), 0);
    }

    private static File searchReadme(File dir, int depth) {
        if (!dir.isDirectory() || depth > 3) return null;

        File[] files = dir.listFiles();
        if (files == null) return null;

        for (File file : files) {
            if (file.isFile() && file.getName().equalsIgnoreCase("README.md")) {
                return file;
            }
        }

        for (File file : files) {
            if (file.isDirectory()) {
                File found = searchReadme(file, depth + 1);
                if (found != null) return found;
            }
        }

        return null;
    }
}