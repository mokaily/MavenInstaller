package com.example.maveninstaller.GUI;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

import static com.example.maveninstaller.BuildRepository.buildRepository;
import static com.example.maveninstaller.CloneRepository.cloneRepository;
import static com.example.maveninstaller.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.CreateInstaller.createInstaller;
import static com.example.maveninstaller.FetchGitInfo.FetchGitBranches.fetchBranches;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.OperationSystemChecker.isMac;
import static com.example.maveninstaller.OperationSystemChecker.isWindows;

public class GitMavenCloneUI {


    public void createAndShowGUI() {
        frame = new JFrame("GitMaven Installer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900);
        frame.setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/GitMavenLogoSmall.png")));
        frame.setIconImage(icon.getImage());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // User Info Section (full width)
        JPanel userInfoPanel = new JPanel(new GridBagLayout());
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        userInfoPanel.add(new JLabel("Git Username:"), gbc);
        gbc.gridx = 1;
        gitLabUserNameField = new JTextField("okaily@uni-marburg.de");
        userInfoPanel.add(gitLabUserNameField, gbc);

        // Access Token + Link
        JPanel accessLabelWithHelp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel accessTokenLabel = new JLabel("Access Token: ");
        JLabel helpLink = new JLabel("<html><a href='#'>How to get?</a></html>");
        helpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpLink.setForeground(Color.BLUE);
        helpLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JTextArea stepsArea = new JTextArea(
                        "💡 How to get your GitLab Access Token:\n\n" +
                                "1. Go to your GitLab profile.\n" +
                                "2. Click on 'Edit Profile' > 'Access Tokens'.\n" +
                                "3. Enter a name, expiry date, and select scopes (e.g., 'api').\n" +
                                "4. Click 'Create token' and copy the token shown.\n\n" +
                                "⚠️ Keep it safe! You won't see it again.");
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
        gitLabPasswordFieldPassword = new JPasswordField("7HWB5r-z1kN2yLzk_aJ_");
        userInfoPanel.add(gitLabPasswordFieldPassword, gbc);

        userInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, userInfoPanel.getPreferredSize().height));
        mainPanel.add(userInfoPanel);

        // Repo Info
        JPanel repoPanel = new JPanel(new BorderLayout(5, 5));
        repoPanel.setBorder(BorderFactory.createTitledBorder("Repository URL"));
        repoUrlField.setToolTipText("Examples:\n:https://gitlab.uni-marburg.de/kertels/erma.git \nhttps://gitlab.com/gnutools/gcc");
        repoPanel.add(repoUrlField, BorderLayout.CENTER);
        mainPanel.add(repoPanel);

        // Branch Selector (full width)
        JPanel branchPanel = new JPanel(new GridBagLayout());
        branchPanel.setBorder(BorderFactory.createTitledBorder("Branch Selection"));
        GridBagConstraints branchGbc = new GridBagConstraints();
        branchGbc.insets = new Insets(5, 5, 5, 5);
        branchGbc.fill = GridBagConstraints.HORIZONTAL;
        branchGbc.weightx = 1.0;

        branchGbc.gridx = 0;
        branchGbc.gridy = 0;
        branchPanel.add(new JLabel("Branch:"), branchGbc);

        branchGbc.gridx = 1;
        branchPanel.add(branchSelector, branchGbc);

        branchGbc.gridx = 2;
        fetchBranchesButton = new JButton("Fetch Branches");
        fetchBranchesButton.addActionListener(e -> fetchBranches());
        branchPanel.add(fetchBranchesButton, branchGbc);

        branchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, branchPanel.getPreferredSize().height));
        mainPanel.add(branchPanel);

        // Target Path
        JPanel targetPanel = new JPanel(new BorderLayout(5, 5));
        targetPanel.setBorder(BorderFactory.createTitledBorder("Install Path"));

        browseButton = new JButton("Browse");
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
        shortcutConfiguration.setBorder(BorderFactory.createTitledBorder("Shortcut Configuration"));
        GridBagConstraints scGbc = new GridBagConstraints();
        scGbc.insets = new Insets(5, 5, 5, 5);
        scGbc.fill = GridBagConstraints.HORIZONTAL;
        scGbc.weightx = 1.0;

        scGbc.gridx = 0; scGbc.gridy = 0;
        shortcutConfiguration.add(new JLabel("Application Name:"), scGbc);
        scGbc.gridx = 1;
        shortcutConfiguration.add(applicationNameField, scGbc);

        scGbc.gridx = 0; scGbc.gridy = 1;
        shortcutConfiguration.add(new JLabel("Pin to Dock (macOS)/ Start App Menu (Windows):"), scGbc);
        scGbc.gridx = 1;
        pinToDockCheckbox = new JCheckBox();
        shortcutConfiguration.add(pinToDockCheckbox, scGbc);

        // --- Shortcut Icon Picker (.ico only) ---
        scGbc.gridx = 0; scGbc.gridy = 2;
        shortcutConfiguration.add(new JLabel("Shortcut Icon (.ico(windows), .icns(mac), .png(linux)):"), scGbc);
        scGbc.gridx = 1;
        shortcutIconField = new JTextField();
        browseShortcutIconButton = new JButton("Browse");
        JPanel iconPickerPanel = new JPanel(new BorderLayout());
        iconPickerPanel.add(shortcutIconField, BorderLayout.CENTER);
        iconPickerPanel.add(browseShortcutIconButton, BorderLayout.EAST);
        shortcutConfiguration.add(iconPickerPanel, scGbc);

        String iconExtension = isMac() ? "icns" : isWindows() ? "ico" : "png";
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
        repoOptionPanel.setBorder(BorderFactory.createTitledBorder("Default/ Custom Maven"));
        customRepoPathField = new JTextField();
        customRepoPathField.setEnabled(false);
        customRepoPathField.setToolTipText("Examples:\nWindows: C:/Users/Name/custom-m2\nmacOS: /Users/name/maven-repo\nLinux: /home/name/maven-repo");
        JButton browseRepoButton = new JButton("Browse");

        browseRepoButton.addActionListener(e -> {
            JFileChooser repoChooser = new JFileChooser();
            repoChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (repoChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                customRepoPathField.setText(repoChooser.getSelectedFile().getAbsolutePath());
            }
        });
        useCustomRepoCheckbox = new JCheckBox("Use custom local Maven repository");
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
        cloneButton = new JButton("Clone Repository");
        cloneButton.addActionListener(e -> cloneRepository(false));
        clonePanel.add(cloneButton);

        buildButton = new JButton("Build Jar");
        buildButton.addActionListener(e -> buildRepository(false));
        clonePanel.add(buildButton);

        installButtonAdvanced = new JButton("Create Installer");
        installButtonAdvanced.addActionListener(e -> createInstaller());
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
        ownerInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ownerInfoArea.setBorder(BorderFactory.createTitledBorder("Project Owner Info"));
        mainPanel.add(ownerInfoArea);

        // README Area
        readmeArea.setEditable(false);
        readmeArea.setLineWrap(true);
        readmeArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane readmeScrollPane = new JScrollPane(readmeArea);
        readmeScrollPane.setBorder(BorderFactory.createTitledBorder("README.md Content"));
        mainPanel.add(readmeScrollPane);

        // Output Console
        outputConsole.setEditable(false);
        outputConsole.setAutoscrolls(true);
        outputConsole.setLineWrap(true);
        outputConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane outputScrollPane = new JScrollPane(outputConsole);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Console Output"));
        mainPanel.add(outputScrollPane);

        // === import config file ===
        JLabel importConfigLink = new JLabel("<html><u><font color='blue'>Import Config File</font></u></html>");
        importConfigLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        importConfigLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        String content = new String(java.nio.file.Files.readAllBytes(selectedFile.toPath()));
                        // Do something with the content
                        appendToConsole("📥 Loaded config from: " + selectedFile.getAbsolutePath() + "\n", false);

                        // Example: parse JSON if needed
                        JSONObject json = new JSONObject(content);
                        appendToConsole("✔ Config: " + json.toString(2) + "\n", false);

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error reading config file: " + ex.getMessage());
                    }
                }
            }
        });

        // === export log file ===
        JLabel exportLogLink = new JLabel("<html><u><font color='blue'>Export Log to File</font></u></html>");
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
                        if (logFileStream != null) {
                            logFileStream.close();
                        }
                        logFileStream = new PrintStream(fileChooser.getSelectedFile(), "UTF-8");
                        appendToConsole("📁 Logging to file: " + fileChooser.getSelectedFile().getAbsolutePath() + "\n", false);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error saving log file: " + ex.getMessage());
                    }
                }
            }
        });

        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linksPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        linksPanel.add(importConfigLink);
        linksPanel.add(exportLogLink);

        mainPanel.add(linksPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Footer Panel with switch button
        JPanel footerPanel = new JPanel(new BorderLayout());

        // App Info on the left
        JTextArea appInfo = new JTextArea("GitMaven Installer v2.0\nDeveloped to simplify testing and building maven projects from GitHub and GitLab.");
        appInfo.setEditable(false);
        appInfo.setBackground(new Color(240, 240, 240));
        appInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerPanel.add(appInfo, BorderLayout.CENTER);

        // Switch to Basic UI button on the bottom right
        switchToBasicButton = new JButton("Switch to Basic UI");
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