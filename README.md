# GitMaven Installer

GitMaven Installer is a cross-platform desktop application designed to simplify the process of cloning Maven-based projects from GitHub or GitLab, building them, and creating launchable shortcuts on Windows, macOS, and Linux systems.

## Features

- ✨ Graphical User Interface (GUI) using Java Swing
- 💻 Cross-platform support: **Windows**, **macOS**, and **Linux**
- 🚀 Clone repositories from GitHub/GitLab using HTTPS or SSH
- 📚 Branch selection and fetching
- ⛏ Build Maven projects automatically
- ✅ System requirements check for Java, Git, and Maven
- 🔍 Read and display project README.md
- 📄 Fetch and display project owner info (GitHub/GitLab)
- 🔹 Optional custom Maven repository path
- ⚙ Create OS-specific desktop shortcuts for the built JAR
- 🌟 Custom icon and application name support

## Requirements

Before running GitMaven Installer, ensure the following are installed:

- Java 17+ (tested with Java 21 and Java 24)
- Maven 3.10+
- Git 2.40+

## Installation

1. Clone or download this repository.
2. Build the project using Maven:

```bash
mvn clean install
```

3. Run the application:

```bash
java -jar target/gitmaven-installer.jar
```

## Usage

1. **Launch the Application**: Run the built JAR file.
2. **Check Requirements**: The application checks for Java, Git, and Maven installations.
3. **Enter Repository URL**: Paste the GitHub/GitLab repository URL.
4. **Fetch Branches**: Click to fetch available branches.
5. **Set Target Folder**: Choose where the repo should be cloned.
6. **Custom Maven Repo (Optional)**: Specify a local repository path.
7. **Clone & Build**:
    - Clone the repository.
    - Automatically detect Maven project and build it.
8. **Create Shortcut**:
    - Set application name and icon.
    - Create a shortcut:
        - Windows: `.lnk` in Desktop and optionally Start Menu
        - macOS: `.app` bundle in `~/Applications` with Dock pin option
        - Linux: `.desktop` file or Flatpak package

## Screenshots
![GitMaven Installer UI](img.png)

## Supported Repository Hosts

- GitHub (public and private)
- GitLab (with optional access token)

## Developer Notes

- This tool uses `mslinks` for Windows shortcut creation.
- On macOS, AppleScript is used to create `.app` bundles and Dock integration.
- On Linux, Flatpak manifest creation is supported with fallback to `.desktop` files.
- All file paths are handled with cross-platform support.

## License

This project is released under the MIT License.
