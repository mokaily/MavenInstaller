package com.example.maveninstaller.Helpers;
import static com.example.maveninstaller.GUI.InitializeDefaults.outputConsole;
import static com.example.maveninstaller.Main.logBuffer;

public class ConsoleLogAppender extends Thread {

    public static void appendToConsole(String text, Boolean setText) {
        if(setText){
            outputConsole.setText(text);
        }else{
            outputConsole.append(text);
        }
        logBuffer.append(text);
    }
}