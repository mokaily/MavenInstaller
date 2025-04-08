package com.example.maveninstaller.GUI;

import javax.swing.*;

public class InitializeDefaults{
    public static JFrame frame;
    public static JTextField repoUrlField, targetPathField;
    public static JTextField gitLabUserNameField;
    public static JTextField gitLabPasswordFieldPassword;
    public static JTextArea outputConsole;
    public static JTextArea ownerInfoArea;
    public static JTextArea readmeArea;
    public static JComboBox<String> branchSelector;
    public static JButton fetchBranchesButton, cloneButton;
    public static JProgressBar progressBar;
    public static JCheckBox useCustomRepoCheckbox;
    public static JTextField customRepoPathField;
    public static JTextField applicationNameField;
    public static JCheckBox pinToDockCheckbox;
    public static JTextField shortcutIconField;

    public static void inializeUIDefaults() {
        gitLabPasswordFieldPassword = new JPasswordField("7HWB5r-z1kN2yLzk_aJ_");
        gitLabUserNameField = new JTextField("okaily@uni-marburg.de");
        shortcutIconField = new JTextField();
        repoUrlField = new JTextField("https://gitlab.uni-marburg.de/kertels/erma.git");
        targetPathField = new JTextField();
        useCustomRepoCheckbox = new JCheckBox("Use custom local Maven repository");
        useCustomRepoCheckbox.setSelected(false);
        applicationNameField = new JTextField("");
        pinToDockCheckbox = new JCheckBox();
        pinToDockCheckbox.setSelected(false);
        readmeArea = new JTextArea(4, 60);
        readmeArea.append("");
        ownerInfoArea = new JTextArea(4, 60);
        ownerInfoArea.append("");
    }
}
