module com.example.maveninstaller {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires org.json;
    requires org.eclipse.jgit;
    requires org.apache.commons.io;


    opens com.example.maveninstaller to javafx.fxml;
    exports com.example.maveninstaller;
}