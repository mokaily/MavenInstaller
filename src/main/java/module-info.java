module com.example.maveninstaller {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.json;
    requires org.eclipse.jgit;
    requires org.jetbrains.annotations;
    requires org.apache.commons.imaging;
    requires mslinks;
    requires java.desktop;
    requires maven.model;
    requires org.apache.commons.io;


    opens com.example.maveninstaller to javafx.fxml;
    exports com.example.maveninstaller;
    exports com.example.maveninstaller.GUI.CheckRequirments;
    opens com.example.maveninstaller.GUI.CheckRequirments to javafx.fxml;
    exports com.example.maveninstaller.Installer;
    opens com.example.maveninstaller.Installer to javafx.fxml;
    exports com.example.maveninstaller.FetchGitInfo;
    opens com.example.maveninstaller.FetchGitInfo to javafx.fxml;
    exports com.example.maveninstaller.Helpers;
    opens com.example.maveninstaller.Helpers to javafx.fxml;
}