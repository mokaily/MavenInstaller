package com.example.maveninstaller.GUI;

import javax.swing.*;
import java.io.PrintStream;

import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;

public class InitializeDefaults{
    public static PrintStream logFileStream;
    public static JFrame frame;
    public static JTextField repoUrlField, targetPathField;
    public static JTextField gitLabUserNameField;
    public static JTextField gitLabPasswordFieldPassword;
    public static JTextArea outputConsole;
    public static JTextArea ownerInfoArea;
    public static JTextArea readmeArea;
    public static JComboBox<String> branchSelector;
    public static JButton fetchBranchesButton, cloneButton;
    public static JButton buildButton, installButtonAdvanced;
    public static JProgressBar progressBar;
    public static JCheckBox useCustomRepoCheckbox;
    public static JTextField customRepoPathField;
    public static JTextField applicationNameField;
    public static JCheckBox pinToDockCheckbox;
    public static JTextField shortcutIconField;
    public static JButton installButton;
    public static JButton browseButton;
    public static JButton browseButtonSimpleUI;
    public static JButton switchToBasicButton;
    public static JButton advancedBtn;
    public static JButton browseShortcutIconButton;

    public static void initializeUIDefaults() {

        gitLabPasswordFieldPassword = new JPasswordField("");
        gitLabUserNameField = new JTextField("");
        shortcutIconField = new JTextField();
        repoUrlField = new JTextField("");
        targetPathField = new JTextField();

        branchSelector = new JComboBox<>();
        branchSelector.setEnabled(false);
        applicationNameField = new JTextField("");
        pinToDockCheckbox = new JCheckBox();
        pinToDockCheckbox.setSelected(false);
        readmeArea = new JTextArea(4, 60);
        readmeArea.append("");
        ownerInfoArea = new JTextArea(4, 60);
        ownerInfoArea.append("");
        outputConsole = new JTextArea(6, 80);
        appendToConsole("", false);
    }
}
