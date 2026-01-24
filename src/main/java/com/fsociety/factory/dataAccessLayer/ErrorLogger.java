package com.fsociety.factory.dataAccessLayer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {

        private static final String ERROR_FILE = "src//main//resources//error.txt";

    public static void logError(Exception e) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ERROR_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println("[" + timestamp + "] FATAL ERROR:");

            // تحويل الـ StackTrace إلى نص وكتابته في الملف
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            writer.println(sw.toString());

        } catch (IOException ioException) {
            System.err.println("Failed to write to error log: " + ioException.getMessage());
        }
    }

}
