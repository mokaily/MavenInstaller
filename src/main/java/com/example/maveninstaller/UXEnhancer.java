package com.example.maveninstaller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.maveninstaller.Installer.CreateInstaller.getApplicationName;
import static com.example.maveninstaller.GUI.InitializeDefaults.*;

public class UXEnhancer {
    public static void setButtonsEnabled(boolean enabled) {
        try{
            browseButton.setEnabled(enabled);
            browseButtonSimpleUI.setEnabled(enabled);
            installButton.setEnabled(enabled);
            advancedBtn.setEnabled(enabled);
            switchToBasicButton.setEnabled(enabled);
            branchSelector.setEnabled(enabled);
            targetPathField.setEnabled(enabled);
            gitLabPasswordFieldPassword.setEnabled(enabled);
            gitLabUserNameField.setEnabled(enabled);
            shortcutIconField.setEnabled(enabled);
            repoUrlField.setEnabled(enabled);
            pinToDockCheckbox.setEnabled(enabled);
            applicationNameField.setEnabled(enabled);
            fetchBranchesButton.setEnabled(enabled);
            cloneButton.setEnabled(enabled);
            browseShortcutIconButton.setEnabled(enabled);
            installButtonAdvanced.setEnabled(enabled);
            buildButton.setEnabled(enabled);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
