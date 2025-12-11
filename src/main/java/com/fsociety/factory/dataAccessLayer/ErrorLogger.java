package com.fsociety.factory.dataAccessLayer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {

        private static final String ERROR_FILE = "src//main//resources//error.txt";

        public static void logError(String message) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(ERROR_FILE, true))) {
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.println("[" + timestamp + "] ERROR: " + message);
            } catch (IOException e) {
                System.err.println("Failed to write to error log: " + e.getMessage());
            }
        }

}
