package com.example.maveninstaller;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Objects;

import static com.example.maveninstaller.CloneRepository.cloneRepository;
import static com.example.maveninstaller.GetGitBranches.fetchBranches;
import static com.example.maveninstaller.RepositoryUtils.getRepoName;

class GitHubCloneUI {
    public static JFrame frame;
    public static JTextField repoUrlField, targetPathField;
    public static JTextField gitLabUserNameField, gitLabPasswordFieldPassword;
    public static JTextArea outputConsole;
    public static JTextArea aboutConsole;
    public static JComboBox<String> branchSelector;
    public static JButton fetchBranchesButton, cloneButton;
    public static JProgressBar progressBar;

    public void createAndShowGUI() {
        frame = new JFrame("GitMaven Installer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        // Load the logo (icon) image
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/GitMavenLogoSmall.png")));
        frame.setIconImage(icon.getImage());

        // Add some basic components
        JLabel label = new JLabel("GitMaven Installer v1.0", JLabel.CENTER);
        frame.add(label);


        JPanel panel = new JPanel(new GridLayout(12, 1)); // Increase grid rows for additional fields

        // GitLab credentials
        JPanel gitLabPanel = new JPanel(new GridLayout(2, 2));  // Panel for username and password fields
        JLabel gitLabUserLabel = new JLabel("GitLab Username:");
        gitLabUserNameField = new JTextField("okaily@uni-marburg.de");

        JLabel gitLabPasswordLabel = new JLabel("GitLab AccessCode:");
        gitLabPasswordFieldPassword = new JPasswordField("7HWB5r-z1kN2yLzk_aJ_");
        gitLabPanel.add(gitLabUserLabel);
        gitLabPanel.add(gitLabUserNameField);
        gitLabPanel.add(gitLabPasswordLabel);
        gitLabPanel.add(gitLabPasswordFieldPassword);

        // Add GitLab credentials panel at the beginning of the form
        panel.add(gitLabPanel);

        repoUrlField = new JTextField("https://gitlab.uni-marburg.de/kertels/erma.git");
        targetPathField = new JTextField();
        JButton browseButton = new JButton("Browse Target Folder");
        fetchBranchesButton = new JButton("Fetch Branches");
        cloneButton = new JButton("Clone Repository");
        progressBar = new JProgressBar(0, 100);

        outputConsole = new JTextArea("Logs: \n");
        aboutConsole = new JTextArea("About: This application clones GitHub repositories.");
        outputConsole.setEditable(false);
        aboutConsole.setEditable(false);

        branchSelector = new JComboBox<>();
        branchSelector.setEnabled(false);

        fetchBranchesButton.addActionListener(e -> fetchBranches());

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        browseButton.addActionListener(e -> {
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                targetPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        cloneButton.addActionListener(e -> cloneRepository());

        panel.add(repoUrlField);
        panel.add(fetchBranchesButton);
        panel.add(branchSelector);
        panel.add(browseButton);
        panel.add(targetPathField);
        panel.add(cloneButton);
        panel.add(progressBar);
        panel.add(new JScrollPane(outputConsole));
        panel.add(new JScrollPane(aboutConsole));

        // Adding About Section at Bottom
        JTextArea appInfo = new JTextArea("GitMaven Installer v1.0\nDeveloped to simplify managing, cloning and compiling maven projects from GitHub and GitLab.");
        appInfo.setEditable(false);
        appInfo.setBackground(new Color(240, 240, 240));
        appInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(new JScrollPane(appInfo));

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void updateBranchSelector(List<String> branches) {
        branchSelector.removeAllItems();
        branches.forEach(branchSelector::addItem);
        branchSelector.setEnabled(true);
        outputConsole.append("Branches fetched successfully!\n");
    }

    public static void displayProjectInfo() {
        String projectPath = targetPathField.getText() + "/" + getRepoName(repoUrlField.getText());

        StringBuilder projectInfo = new StringBuilder("\nProject Information:\n");
        projectInfo.append(repoUrlField.getText());

        // Display the README content if it exists
        File readmeFile = new File(projectPath + "/", "README.md");
        if (readmeFile.exists()) {
            projectInfo.append("\nREADME Content:\n");
            try (BufferedReader reader = new BufferedReader(new FileReader(readmeFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    projectInfo.append(line).append("\n");
                }
            } catch (IOException e) {
                projectInfo.append("Could not read README.md\n");
            }
        } else {
            projectInfo.append("README.md not found.\n");
        }
    }
}