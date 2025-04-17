package com.example.maveninstaller.Helpers;

public class OSChecker {

    public enum OSType {
        WINDOWS, MAC, LINUX, OTHER
    }

    private static OSType detectedOS;

    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
                detectedOS = OSType.WINDOWS;
            } else if (osName.contains("mac")) {
                detectedOS = OSType.MAC;
            } else if (osName.contains("nux") || osName.contains("nix")) {
                detectedOS = OSType.LINUX;
            } else {
                detectedOS = OSType.OTHER;
            }
        }
        return detectedOS;
    }

    public static boolean isWindows() {
        return getOperatingSystemType() == OSType.WINDOWS;
    }

    public static boolean isMac() {
        return getOperatingSystemType() == OSType.MAC;
    }

    public static boolean isLinux() {
        return getOperatingSystemType() == OSType.LINUX;
    }
}
