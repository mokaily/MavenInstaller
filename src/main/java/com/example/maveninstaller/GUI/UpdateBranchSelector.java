package com.example.maveninstaller.GUI;

import java.util.List;

import static com.example.maveninstaller.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;


public class UpdateBranchSelector{
    public static void updateBranchSelector(List<String> branches) {
        branchSelector.removeAllItems();
        branches.forEach(branchSelector::addItem);
        branchSelector.setEnabled(true);
        appendToConsole("Branches fetched successfully!\n", false);
    }
}