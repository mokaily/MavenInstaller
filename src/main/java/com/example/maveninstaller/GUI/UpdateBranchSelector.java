package com.example.maveninstaller.GUI;

import java.util.List;

import static com.example.maveninstaller.Constants.Branches_Fetched_Successfully;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;


public class UpdateBranchSelector{
    public static void updateBranchSelector(List<String> branches) {
        branchSelector.removeAllItems();
        branches.forEach(branchSelector::addItem);
        branchSelector.setEnabled(true);
        appendToConsole(Branches_Fetched_Successfully, false);
    }
}