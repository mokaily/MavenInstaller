package com.example.maveninstaller.Helpers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.Optional;

import static com.example.maveninstaller.Constants.Error_Title;
import static com.example.maveninstaller.Constants.Pom_Error;
import static com.example.maveninstaller.Helpers.ConsoleLogAppender.appendToConsole;
import static com.example.maveninstaller.UXEnhancer.setButtonsEnabled;

public class PomHelper {
    public static String findPomXml(String rootDir) {
        try (Stream<Path> paths = Files.walk(Paths.get(rootDir))) {
            Optional<Path> pomPath = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals("pom.xml"))
                    .findFirst();

            return pomPath
                    .map(path -> path.getParent().toAbsolutePath().toString() + File.separator)
                    .orElse(null);

        } catch (IOException e) {
            setButtonsEnabled(true);
            appendToConsole(Error_Title + ": " + e.getMessage(), false);
            return null;
        }
    }

    public static String fetchAppName(String pomPath) {
        try {
            File pomFile = new File(pomPath + File.separator + "pom.xml");
            if (!pomFile.exists()) return null;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pomFile);
            doc.getDocumentElement().normalize();

            Element projectElement = doc.getDocumentElement();
            String name = fetchTagValue("name", projectElement);

            return name != null ? name : fetchTagValue("artifactId", projectElement);

        } catch (Exception e) {
            appendToConsole(Pom_Error + e.getMessage(), false);
            return null;
        }
    }

    private static String fetchTagValue(String tag, Element element) {
        try {
            return element.getElementsByTagName(tag).item(0).getTextContent();
        } catch (Exception e) {
            return null;
        }
    }
}