package com.example.maveninstaller.GUI;

import com.example.maveninstaller.GUI.CheckRequirments.RequirementsChecker;
import com.example.maveninstaller.Helpers.ConfigImporter;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static com.example.maveninstaller.CloneRepository.cloneRepository;
import static com.example.maveninstaller.Constants.*;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;
import static com.example.maveninstaller.Main.logBuffer;

public class SimpleUI {
    public static void showSimpleUI() {
        browseButton = new JButton(Browse);
        useCustomRepoCheckbox = new JCheckBox(Use_Custom_Local_Maven_Repository);
        switchToBasicButton = new JButton(Switch_To_Basic_UI);
        fetchBranchesButton = new JButton(Fetch_Branches);
        cloneButton = new JButton(Clone_Repository);
        buildButton = new JButton(Build_Jar);
        installButtonAdvanced = new JButton(Create_Installer);
        browseShortcutIconButton = new JButton(Browse);


        gitLabUserNameField = new JTextField("");
        gitLabPasswordFieldPassword = new JPasswordField("");
        pinToDockCheckbox.setSelected(false);

        JFrame frame = new JFrame(GitMaven_Simple_UI);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(RequirementsChecker.class.getResource(Icon_Path)));
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
        repoPanel.setBorder(BorderFactory.createTitledBorder(Repository_URL));
        repoPanel.add(repoUrlField, BorderLayout.CENTER);
        panel.add(repoPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // === Install Path ===
        JPanel pathPanel = new JPanel(new BorderLayout(5, 5));
        pathPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pathPanel.setBorder(BorderFactory.createTitledBorder(Install_Path));

        browseButtonSimpleUI = new JButton(Browse);
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

        JLabel exportLogLink = new JLabel("<html><u><font color='blue'>" + Export_Log_To_File + "</font></u></html>");
        exportLogLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

        JPanel leftLinksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftLinksPanel.add(importConfigLink);
        leftLinksPanel.add(exportLogLink);

        installButton = new JButton(Install);
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
        outputConsole.setFont(new Font(Monospaced, Font.PLAIN, 12));
        JScrollPane outputScrollPane = new JScrollPane(outputConsole);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder(Console_Output));
        outputScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.add(outputScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Footer Panel with switch button
        JPanel footerPanel = new JPanel(new BorderLayout());

        // App Info on the left
        JTextArea appInfo = new JTextArea(Footer_Info);
        appInfo.setEditable(false);
        appInfo.setBackground(new Color(240, 240, 240));
        appInfo.setFont(new Font(Segoe_UI, Font.PLAIN, 12));
        footerPanel.add(appInfo, BorderLayout.CENTER);
        panel.add(footerPanel);

        // === Switch Button ===
        JPanel advancedBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        advancedBtn = new JButton(Switch_To_Advanced_UI);
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
