package com.example.maveninstaller.GUI;

import com.example.maveninstaller.GUI.CheckRequirments.RequirementsChecker;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

import static com.example.maveninstaller.CloneRepository.cloneRepository;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class SimpleUI {
    public static void showSimpleUI() {
        browseButton = new JButton("Browse");
        useCustomRepoCheckbox = new JCheckBox("Use custom local Maven repository");
        switchToBasicButton = new JButton("Switch to Basic UI");
        fetchBranchesButton = new JButton("Fetch Branches");
        cloneButton = new JButton("Clone Repository");
        buildButton = new JButton("Build Jar");
        installButtonAdvanced = new JButton("Create Installer");
        browseShortcutIconButton = new JButton("Browse");


        gitLabUserNameField = new JTextField("okaily@uni-marburg.de");
        gitLabPasswordFieldPassword = new JPasswordField("7HWB5r-z1kN2yLzk_aJ_");
        pinToDockCheckbox.setSelected(false);

        JFrame frame = new JFrame("GitMaven Simple UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(RequirementsChecker.class.getResource("/GitMavenLogoSmall.png")));
        frame.setIconImage(icon.getImage());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // === Repo URL ===
        JPanel repoPanel = new JPanel(new BorderLayout(5, 5));
        repoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        repoPanel.setBorder(BorderFactory.createTitledBorder("Repository URL"));
        repoPanel.add(repoUrlField, BorderLayout.CENTER);
        panel.add(repoPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // === Install Path ===
        JPanel pathPanel = new JPanel(new BorderLayout(5, 5));
        pathPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pathPanel.setBorder(BorderFactory.createTitledBorder("Install Path"));

        browseButtonSimpleUI = new JButton("Browse");
        browseButtonSimpleUI.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                targetPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        pathPanel.add(targetPathField, BorderLayout.CENTER);
        pathPanel.add(browseButtonSimpleUI, BorderLayout.EAST);
        panel.add(pathPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // === Install + Links in one row ===
        JLabel exportLogLink = new JLabel("<html><u><font color='blue'>Export Log to File</font></u></html>");
        exportLogLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
                        appendToConsole("📥 Loaded config from: " + selectedFile.getAbsolutePath() + "\n", false);
                        // parse and use if needed
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error reading config file: " + ex.getMessage());
                    }
                }
            }
        });

        JPanel leftLinksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftLinksPanel.add(exportLogLink);
        leftLinksPanel.add(importConfigLink);

        installButton = new JButton("Install");
        installButton.setPreferredSize(new Dimension(200, 40));
        installButton.addActionListener(e -> cloneRepository(true));

        JPanel topRowPanel = new JPanel(new BorderLayout());
        topRowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        topRowPanel.add(leftLinksPanel, BorderLayout.WEST);
        topRowPanel.add(installButton, BorderLayout.EAST);

        panel.add(topRowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Action Panel with progress bar
        JPanel actionPanel = new JPanel(new BorderLayout(1, 10));
        progressBar = new JProgressBar(0, 100);
        progressBar.setUI((ProgressBarUI) UIManager.getUI(progressBar));
        actionPanel.add(progressBar, BorderLayout.CENTER);
        panel.add(actionPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // === Console Output ===
        outputConsole = new JTextArea(6, 60);
        outputConsole.setEditable(false);
        outputConsole.setAutoscrolls(true);
        outputConsole.setLineWrap(true);
        outputConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane outputScrollPane = new JScrollPane(outputConsole);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Console Output"));
        outputScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.add(outputScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // === Switch Button ===
        JPanel advancedBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        advancedBtn = new JButton("Switch to Advanced UI");
        advancedBtn.addActionListener(e -> {
            frame.dispose();
            GitMavenCloneUI ui = new GitMavenCloneUI();
            ui.createAndShowGUI();
        });
        advancedBtnPanel.add(advancedBtn);
        panel.add(advancedBtnPanel);

        container.add(panel);
        frame.add(container);
        frame.setVisible(true);
    }
}
