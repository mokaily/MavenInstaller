package com.example.maveninstaller.GUI.CheckRequirments;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;

import static com.example.maveninstaller.GUI.CheckRequirments.CheckerHelper.checkCommand;
import static com.example.maveninstaller.OperationSystemChecker.isWindows;

public class RequirementsChecker {
    public static void checkAndDisplay() {
        JEditorPane outputPane = new JEditorPane();
        outputPane.setContentType("text/html");
        outputPane.setEditable(false);
        outputPane.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputPane.setText("<html><body><p>\uD83D\uDD27 Checking system requirements...</p></body></html>");

        outputPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        StringBuilder result = new StringBuilder();
        result.append("<html><body style='font-family:consolas;font-size:12pt;'>");
        result.append("<h3>\ud83d\udd27 System Requirements Check</h3><hr><br>");

        boolean allToolsAvailable = true;

        String mavenCommand = isWindows() ? "mvn.cmd" : "mvn";

        boolean javaOk = checkCommand(new String[]{"java", "-version"}, "Java", result, "24", "https://www.oracle.com/de/java/technologies/downloads/#java" + "24");
        boolean mavenOk = checkCommand(new String[]{mavenCommand, "-version"}, "Maven", result, "3.10", "https://maven.apache.org/download.cgi");
        boolean gitOk = checkCommand(new String[]{"git", "--version"}, "Git", result, "2.49.0", "https://git-scm.com/downloads");

        allToolsAvailable = javaOk && mavenOk && gitOk;

        result.append("</body></html>");
        outputPane.setText(result.toString());

        JScrollPane scrollPane = new JScrollPane(outputPane);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        if (allToolsAvailable) {
            JCheckBox agreeCheckBox = new JCheckBox("I agree to the Terms and Conditions");
            JButton viewTermsButton = new JButton("View Terms");
            Object[] message = {scrollPane, viewTermsButton, agreeCheckBox};

            final JOptionPane optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = optionPane.createDialog("System Check Results");

            JButton continueButton = getButton(optionPane, JOptionPane.OK_OPTION);
            if (continueButton != null) {
                continueButton.setEnabled(false);
                continueButton.setText("Continue");
            }

            agreeCheckBox.addActionListener(e -> {
                if (continueButton != null) continueButton.setEnabled(agreeCheckBox.isSelected());
            });

            viewTermsButton.addActionListener(e -> {
                JTextArea termsArea = new JTextArea("TERMS AND CONDITIONS\n\n1. Usage is at your own risk.\n2. No warranty provided.\n3. Do not redistribute without permission.");
                termsArea.setEditable(false);
                termsArea.setLineWrap(true);
                termsArea.setWrapStyleWord(true);
                JScrollPane termsScroll = new JScrollPane(termsArea);
                termsScroll.setPreferredSize(new Dimension(500, 300));
                JOptionPane.showMessageDialog(null, termsScroll, "Terms and Conditions", JOptionPane.INFORMATION_MESSAGE);
            });

            dialog.setVisible(true);

            if (!agreeCheckBox.isSelected() || optionPane.getValue() == null || (int) optionPane.getValue() != JOptionPane.OK_OPTION) {
                System.exit(0);
            }

        } else {
            int option = JOptionPane.showOptionDialog(
                    null,
                    scrollPane,
                    "Missing Requirements",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    new Object[]{"Exit Application"},
                    "Exit Application"
            );
            System.exit(0);
        }
    }

    private static JButton getButton(JOptionPane optionPane, int option) {
        for (Component c : optionPane.getComponents()) {
            if (c instanceof JPanel) {
                for (Component b : ((JPanel) c).getComponents()) {
                    if (b instanceof JButton && ((JButton) b).getText().equals("OK")) {
                        return (JButton) b;
                    }
                }
            }
        }
        return null;
    }
}
