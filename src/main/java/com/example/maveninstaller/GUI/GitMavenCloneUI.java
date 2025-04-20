package com.example.maveninstaller.GUI;

import com.example.maveninstaller.Helpers.ConfigImporter;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static com.example.maveninstaller.BuildRepository.buildRepository;
import static com.example.maveninstaller.CloneRepository.cloneRepository;
import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.Helpers.CreateInstallerHelper.createInstallerHelper;
import static com.example.maveninstaller.FetchGitInfo.FetchGitBranches.fetchBranches;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.Helpers.OSChecker.isMac;
import static com.example.maveninstaller.Helpers.OSChecker.isWindows;
import static com.example.maveninstaller.Main.logBuffer;

public class GitMavenCloneUI {
    public void createAndShowGUI() {
        frame = new JFrame(Git_Maven_Installer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(910, 910);
        frame.setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(Icon_Path)));
        frame.setIconImage(icon.getImage());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // User Info Section (full width)
        JPanel userInfoPanel = new JPanel(new GridBagLayout());
        userInfoPanel.setBorder(BorderFactory.createTitledBorder(User_Information));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        userInfoPanel.add(new JLabel(Git_Username), gbc);
        gbc.gridx = 1;
        gitLabUserNameField = new JTextField("");
        userInfoPanel.add(gitLabUserNameField, gbc);

        // Access Token + Link
        JPanel accessLabelWithHelp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel accessTokenLabel = new JLabel(Access_Token);
        JLabel helpLink = new JLabel("<html><a href='#'> " + How_To_Get + "</a></html>");
        helpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpLink.setForeground(Color.BLUE);
        helpLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JTextArea stepsArea = new JTextArea(How_To_Get_Instructions);
                stepsArea.setEditable(false);
                stepsArea.setLineWrap(true);
                stepsArea.setWrapStyleWord(true);

                JScrollPane scroll = new JScrollPane(stepsArea);
                scroll.setPreferredSize(new Dimension(500, 300));
                JOptionPane.showMessageDialog(null, scroll, "Where to Get Access Token", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        accessLabelWithHelp.add(accessTokenLabel);
        accessLabelWithHelp.add(helpLink);

        gbc.gridx = 0; gbc.gridy = 1;
        userInfoPanel.add(accessLabelWithHelp, gbc);

        gbc.gridx = 1;
        gitLabPasswordFieldPassword = new JPasswordField("");
        userInfoPanel.add(gitLabPasswordFieldPassword, gbc);

        userInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, userInfoPanel.getPreferredSize().height));
        mainPanel.add(userInfoPanel);

        // Repo Info
        JPanel repoPanel = new JPanel(new BorderLayout(5, 5));
        repoPanel.setBorder(BorderFactory.createTitledBorder(Repository_URL));
        repoUrlField.setToolTipText(Repository_URL_Examples);
        repoPanel.add(repoUrlField, BorderLayout.CENTER);
        mainPanel.add(repoPanel);

        // Branch Selector (full width)
        JPanel branchPanel = new JPanel(new GridBagLayout());
        branchPanel.setBorder(BorderFactory.createTitledBorder(Branch_Selection));
        GridBagConstraints branchGbc = new GridBagConstraints();
        branchGbc.insets = new Insets(5, 5, 5, 5);
        branchGbc.fill = GridBagConstraints.HORIZONTAL;
        branchGbc.weightx = 1.0;

        branchGbc.gridx = 0;
        branchGbc.gridy = 0;
        branchPanel.add(new JLabel(Branch), branchGbc);

        branchGbc.gridx = 1;
        branchPanel.add(branchSelector, branchGbc);

        branchGbc.gridx = 2;
        fetchBranchesButton = new JButton(Fetch_Branches);
        fetchBranchesButton.addActionListener(e -> fetchBranches());
        branchPanel.add(fetchBranchesButton, branchGbc);

        branchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, branchPanel.getPreferredSize().height));
        mainPanel.add(branchPanel);

        // Target Path
        JPanel targetPanel = new JPanel(new BorderLayout(5, 5));
        targetPanel.setBorder(BorderFactory.createTitledBorder(Install_Path));

        browseButton = new JButton(Browse);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        browseButton.addActionListener(e -> {
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                targetPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        targetPanel.add(targetPathField, BorderLayout.CENTER);
        targetPanel.add(browseButton, BorderLayout.EAST);
        mainPanel.add(targetPanel);


        // --- Shortcut Configuration Panel ---
        JPanel shortcutConfiguration = new JPanel(new GridBagLayout());
        shortcutConfiguration.setBorder(BorderFactory.createTitledBorder(Shortcut_Configuration));
        GridBagConstraints scGbc = new GridBagConstraints();
        scGbc.insets = new Insets(5, 5, 5, 5);
        scGbc.fill = GridBagConstraints.HORIZONTAL;
        scGbc.weightx = 1.0;

        scGbc.gridx = 0; scGbc.gridy = 0;
        shortcutConfiguration.add(new JLabel(Application_Name), scGbc);
        scGbc.gridx = 1;
        shortcutConfiguration.add(applicationNameField, scGbc);

        scGbc.gridx = 0; scGbc.gridy = 1;
        shortcutConfiguration.add(new JLabel(Pin_To_Dock), scGbc);
        scGbc.gridx = 1;
        pinToDockCheckbox = new JCheckBox();
        shortcutConfiguration.add(pinToDockCheckbox, scGbc);

        // --- Shortcut Icon Picker (.ico only) ---
        scGbc.gridx = 0; scGbc.gridy = 2;
        shortcutConfiguration.add(new JLabel(Shortcut_Icon), scGbc);
        scGbc.gridx = 1;
        shortcutIconField = new JTextField();
        browseShortcutIconButton = new JButton(Browse);
        JPanel iconPickerPanel = new JPanel(new BorderLayout());
        iconPickerPanel.add(shortcutIconField, BorderLayout.CENTER);
        iconPickerPanel.add(browseShortcutIconButton, BorderLayout.EAST);
        shortcutConfiguration.add(iconPickerPanel, scGbc);

        String iconExtension = isMac() ? Icns : isWindows() ? Ico : Png;
        browseShortcutIconButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("." + iconExtension, iconExtension));
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                shortcutIconField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        mainPanel.add(shortcutConfiguration);

        // Custom Maven Repo Options
        JPanel repoOptionPanel = new JPanel(new BorderLayout(5, 5));
        repoOptionPanel.setBorder(BorderFactory.createTitledBorder(Default_Custom_Maven));
        customRepoPathField = new JTextField();
        customRepoPathField.setEnabled(false);
        customRepoPathField.setToolTipText(Default_Custom_Maven_Examples);
        JButton browseRepoButton = new JButton(Browse);

        browseRepoButton.addActionListener(e -> {
            JFileChooser repoChooser = new JFileChooser();
            repoChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (repoChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                customRepoPathField.setText(repoChooser.getSelectedFile().getAbsolutePath());
            }
        });
        useCustomRepoCheckbox = new JCheckBox(Use_Custom_Local_Maven_Repository);
        useCustomRepoCheckbox.addActionListener(e -> {
            boolean enabled = useCustomRepoCheckbox.isSelected();
            customRepoPathField.setEnabled(enabled);
            browseRepoButton.setEnabled(enabled);
        });

        repoOptionPanel.add(useCustomRepoCheckbox, BorderLayout.WEST);
        repoOptionPanel.add(customRepoPathField, BorderLayout.CENTER);
        repoOptionPanel.add(browseRepoButton, BorderLayout.EAST);
        mainPanel.add(repoOptionPanel);

        // Clone + Build Buttons
        JPanel clonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cloneButton = new JButton(Clone_Repository);
        cloneButton.addActionListener(e -> cloneRepository(false));
        clonePanel.add(cloneButton);

        buildButton = new JButton(Build_Jar);
        buildButton.addActionListener(e -> buildRepository(false));
        clonePanel.add(buildButton);

        installButtonAdvanced = new JButton(Create_Installer);
        installButtonAdvanced.addActionListener(e -> createInstallerHelper());
        clonePanel.add(installButtonAdvanced);

        // Action Panel with progress bar
        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        progressBar = new JProgressBar(0, 100);
        progressBar.setUI((ProgressBarUI) UIManager.getUI(progressBar));

        actionPanel.add(clonePanel, BorderLayout.WEST);
        actionPanel.add(progressBar, BorderLayout.CENTER);

        mainPanel.add(actionPanel);

        // Owner Info
        ownerInfoArea.setEditable(false);
        ownerInfoArea.setFont(new Font(Monospaced, Font.PLAIN, 12));
        ownerInfoArea.setBorder(BorderFactory.createTitledBorder(Project_Owner_Info));
        mainPanel.add(ownerInfoArea);

        // README Area
        readmeArea.setEditable(false);
        readmeArea.setLineWrap(true);
        readmeArea.setFont(new Font(Monospaced, Font.PLAIN, 12));
        JScrollPane readmeScrollPane = new JScrollPane(readmeArea);
        readmeScrollPane.setBorder(BorderFactory.createTitledBorder(README_Content));
        mainPanel.add(readmeScrollPane);

        // Output Console
        outputConsole.setEditable(false);
        outputConsole.setAutoscrolls(true);
        outputConsole.setLineWrap(true);
        outputConsole.setFont(new Font(Monospaced, Font.PLAIN, 12));
        JScrollPane outputScrollPane = new JScrollPane(outputConsole);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder(Console_Output));
        mainPanel.add(outputScrollPane);

        // === import config file ===
        JLabel importConfigLink = new JLabel("<html><u><font color='blue'>" + Import_Config_File + "</font></u></html>");
        importConfigLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        importConfigLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ConfigImporter.importConfig(selectedFile);
                }
            }
        });

        // === export log file ===
        JLabel exportLogLink = new JLabel("<html><u><font color='blue'>" + Export_Log_To_File + "</font></u></html>");
        exportLogLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportLogLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportLogLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("gitmaven-log.txt"));
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        FileWriter writer = new FileWriter(fileChooser.getSelectedFile(), false);
                        writer.write(logBuffer.toString());
                        writer.close();
                        appendToConsole(Log_Saved_To + fileChooser.getSelectedFile().getAbsolutePath() + "\n", false);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, Failed_To_Save_Log + ex.getMessage());
                    }
                }
            }
        });

        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linksPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        linksPanel.add(importConfigLink);
        linksPanel.add(exportLogLink);

        mainPanel.add(linksPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Footer Panel with switch button
        JPanel footerPanel = new JPanel(new BorderLayout());

        // App Info on the left
        JTextArea appInfo = new JTextArea(Footer_Info);
        appInfo.setEditable(false);
        appInfo.setBackground(new Color(240, 240, 240));
        appInfo.setFont(new Font(Segoe_UI, Font.PLAIN, 12));
        footerPanel.add(appInfo, BorderLayout.CENTER);

        // Switch to Basic UI button on the bottom right
        switchToBasicButton = new JButton(Switch_To_Basic_UI);
        switchToBasicButton.addActionListener(e -> {
            frame.dispose();
            com.example.maveninstaller.GUI.SimpleUI.showSimpleUI();
        });
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonWrapper.add(switchToBasicButton);
        footerPanel.add(buttonWrapper, BorderLayout.SOUTH);

        mainPanel.add(footerPanel);

        frame.add(new JScrollPane(mainPanel));
        frame.setVisible(true);
    }
}