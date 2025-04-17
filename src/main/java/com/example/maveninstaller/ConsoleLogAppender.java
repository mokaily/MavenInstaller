package com.example.maveninstaller;

import static com.example.maveninstaller.GUI.InitializeDefaults.logFileStream;
import static com.example.maveninstaller.GUI.InitializeDefaults.outputConsole;

public class ConsoleLogAppender extends Thread {
    public static void appendToConsole(String text, Boolean setText) {
        if(setText){
            outputConsole.setText(text);
        }else{
            outputConsole.append(text);
        }
        if (logFileStream != null) {
            logFileStream.print(text);
            logFileStream.flush();
        }
    }
}