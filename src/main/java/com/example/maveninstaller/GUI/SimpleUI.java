package com.example.maveninstaller.GUI;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import java.awt.*;

import static com.example.maveninstaller.CloneRepository.cloneRepository;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class SimpleUI {
    public static void showSimpleUI() {
        gitLabUserNameField = new JTextField("okaily@uni-marburg.de");
        gitLabPasswordFieldPassword = new JPasswordField("7HWB5r-z1kN2yLzk_aJ_");
        pinToDockCheckbox.setSelected(false);

        JFrame frame = new JFrame("GitMaven Simple UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

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

        // === Target Folder ===
        JPanel pathPanel = new JPanel(new BorderLayout(5, 5));
        pathPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pathPanel.setBorder(BorderFactory.createTitledBorder("Target Folder"));
        JButton browse = new JButton("Browse");
        browse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                targetPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        pathPanel.add(targetPathField, BorderLayout.CENTER);
        pathPanel.add(browse, BorderLayout.EAST);
        panel.add(pathPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // === Build Button ===
        JButton buildButton = new JButton("Install");
        buildButton.setPreferredSize(new Dimension(0, 40));
        buildButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buildButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Important
        buildButton.addActionListener(e -> {
                cloneRepository(true);
        }
        );

        // Wrap the button in a transparent panel that forces full width
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.X_AXIS));
        buttonWrapper.setOpaque(false); // keep it visually flat
        buttonWrapper.add(buildButton);

        panel.add(buildButton);

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
        JButton advancedBtn = new JButton("Switch to Advanced UI");
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
