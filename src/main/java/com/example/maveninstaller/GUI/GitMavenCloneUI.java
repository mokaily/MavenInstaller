package com.example.maveninstaller.GUI;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import java.awt.*;
import java.util.Objects;

import static com.example.maveninstaller.CloneRepository.cloneRepository;
import static com.example.maveninstaller.GetGitBranches.fetchBranches;

public class GitMavenCloneUI {
    public static JFrame frame;
    public static JTextField repoUrlField, targetPathField;
    public static JTextField gitLabUserNameField, gitLabPasswordFieldPassword;
    public static JTextArea outputConsole;
    public static JTextArea ownerInfoArea;
    public static JTextArea readmeArea;
    public static JComboBox<String> branchSelector;
    public static JButton fetchBranchesButton, cloneButton;
    public static JProgressBar progressBar;
    public static JCheckBox useCustomRepoCheckbox;
    public static JTextField customRepoPathField;

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
        userInfoPanel.add(new JLabel("GitLab Username:"), gbc);
        gbc.gridx = 1;
        gitLabUserNameField = new JTextField("okaily@uni-marburg.de");
        userInfoPanel.add(gitLabUserNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        userInfoPanel.add(new JLabel("Access Token:"), gbc);
        gbc.gridx = 1;
        gitLabPasswordFieldPassword = new JPasswordField("7HWB5r-z1kN2yLzk_aJ_");
        userInfoPanel.add(gitLabPasswordFieldPassword, gbc);

        userInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, userInfoPanel.getPreferredSize().height));
        mainPanel.add(userInfoPanel);

        // Repo Info
        JPanel repoPanel = new JPanel(new BorderLayout(5, 5));
        repoPanel.setBorder(BorderFactory.createTitledBorder("Repository Info"));
        repoUrlField = new JTextField("https://gitlab.uni-marburg.de/kertels/erma.git");
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
        branchSelector = new JComboBox<>();
        branchSelector.setEnabled(false);
        branchPanel.add(branchSelector, branchGbc);

        branchGbc.gridx = 2;
        fetchBranchesButton = new JButton("Fetch Branches");
        fetchBranchesButton.addActionListener(e -> fetchBranches());
        branchPanel.add(fetchBranchesButton, branchGbc);

        branchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, branchPanel.getPreferredSize().height));
        mainPanel.add(branchPanel);

        // Target Path
        JPanel targetPanel = new JPanel(new BorderLayout(5, 5));
        targetPanel.setBorder(BorderFactory.createTitledBorder("Target Folder"));
        targetPathField = new JTextField();
        JButton browseButton = new JButton("Browse");
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

        // Custom Maven Repo Options
        JPanel repoOptionPanel = new JPanel(new BorderLayout(5, 5));
        useCustomRepoCheckbox = new JCheckBox("Use custom local Maven repository");
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cloneButton = new JButton("Clone Repository");
        cloneButton.addActionListener(e -> cloneRepository());
        buttonPanel.add(cloneButton);

        JButton buildButton = new JButton("Build Executable");
        buildButton.setEnabled(false);
        buildButton.addActionListener(e -> {
            outputConsole.append("Building project into executable JAR...\n");
            // TODO: implement build logic here
        });
        buttonPanel.add(buildButton);

        // Action Panel with progress bar
        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        progressBar = new JProgressBar(0, 100);
        progressBar.setUI((ProgressBarUI) UIManager.getUI(progressBar));

        actionPanel.add(buttonPanel, BorderLayout.WEST);
        actionPanel.add(progressBar, BorderLayout.CENTER);

        mainPanel.add(actionPanel);

        // Owner Info
        ownerInfoArea = new JTextArea(4, 40);
        ownerInfoArea.setEditable(false);
        ownerInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ownerInfoArea.setBorder(BorderFactory.createTitledBorder("Project Owner Info"));
        mainPanel.add(ownerInfoArea);

        // README Area
        readmeArea = new JTextArea(8, 40);
        readmeArea.setEditable(false);
        readmeArea.setLineWrap(true);
        readmeArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane readmeScrollPane = new JScrollPane(readmeArea);
        readmeScrollPane.setBorder(BorderFactory.createTitledBorder("README.md Content"));
        mainPanel.add(readmeScrollPane);

        // Output Console
        outputConsole = new JTextArea(10, 100);
        outputConsole.setEditable(false);
        outputConsole.setAutoscrolls(true);
        outputConsole.setLineWrap(true);
        outputConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane outputScrollPane = new JScrollPane(outputConsole);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Console Output"));
        mainPanel.add(outputScrollPane);

        // Footer
        JTextArea appInfo = new JTextArea("GitMaven Installer v1.0\nDeveloped to simplify managing, cloning and compiling Maven projects from GitHub and GitLab.");
        appInfo.setEditable(false);
        appInfo.setBackground(new Color(240, 240, 240));
        appInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainPanel.add(appInfo);

        frame.add(new JScrollPane(mainPanel));
        frame.setVisible(true);
    }
}