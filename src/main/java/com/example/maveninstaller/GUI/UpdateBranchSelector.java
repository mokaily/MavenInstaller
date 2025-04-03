package com.example.maveninstaller.GUI;

import java.util.List;

import static com.example.maveninstaller.GUI.GitMavenCloneUI.branchSelector;
import static com.example.maveninstaller.GUI.GitMavenCloneUI.outputConsole;

public class UpdateBranchSelector{
    public static void updateBranchSelector(List<String> branches) {
        branchSelector.removeAllItems();
        branches.forEach(branchSelector::addItem);
        branchSelector.setEnabled(true);
        outputConsole.append("Branches fetched successfully!\n");
    }
}