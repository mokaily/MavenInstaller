package com.example.maveninstaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.Optional;

public class PomFinder {
    public static String findPomXml(String rootDir) {
        try (Stream<Path> paths = Files.walk(Paths.get(rootDir))) {
            Optional<Path> pomPath = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals("pom.xml"))
                    .findFirst();

            return pomPath
                    .map(path -> path.getParent().toAbsolutePath().toString() + File.separator)
                    .orElse(null);

//            return pomPath.map(Path::toAbsolutePath)
//                    .map(Path::toString)
//                    .orElse(null);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }
}