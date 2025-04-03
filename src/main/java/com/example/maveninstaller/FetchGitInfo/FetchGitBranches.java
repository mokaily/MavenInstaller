package com.example.maveninstaller.FetchGitInfo;

import static com.example.maveninstaller.FetchGitInfo.FetchGitHubBranches.fetchGitHubBranches;
import static com.example.maveninstaller.FetchGitInfo.FetchGitLabBranches.fetchGitLabBranches;
import static com.example.maveninstaller.FetchGitInfo.FetchGitOwnerInfo.convertSshToHttps;
import static com.example.maveninstaller.GUI.GitMavenCloneUI.*;

public class FetchGitBranches {
        public static void fetchBranches() {
        branchSelector.removeAllItems();
        outputConsole.setText("");
        String repoUrl = repoUrlField.getText().trim();

        repoUrl= convertSshToHttps(repoUrl);

        // Check if the URL is a valid GitHub or GitLab repository
        if (repoUrl.contains("github.")) {
            fetchGitHubBranches(repoUrl);
        } else if (repoUrl.contains("gitlab.")) {
            fetchGitLabBranches(repoUrl);
        } else {
            outputConsole.setText("Invalid repository URL!\n");
        }
    }
}
