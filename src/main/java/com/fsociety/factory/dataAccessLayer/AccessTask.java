package com.fsociety.factory.dataAccessLayer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccessTask {

    private static final String TASKS_FILE = "src/main/resources/dataFiles/production-tasks.csv";
    private static final String LINES_FILE = "src/main/resources/dataFiles/product-lines.csv";
    private static final String STATUS_FILE = "src/main/resources/dataFiles/task-statuses.csv";

    private static final String[] TASKS_HEADER = {"taskID", "productLineID", "productID", "requiredProductQuantity", "achievedProductQuantity", "startDate", "endDate", "taskStatusID", "clientID"};

    public static List<String[]> loadAllTasks() {
        List<String[]> tasks = new ArrayList<>();
        File file = new File(TASKS_FILE);
        if (!file.exists()) {
            return tasks;
        }
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            reader.readNext(); // Skip header
            String[] record;
            while ((record = reader.readNext()) != null) {
                tasks.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError(e);
        }
        return tasks;
    }

    private static boolean saveAllTasks(List<String[]> tasks) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(TASKS_FILE))) {
            writer.writeNext(TASKS_HEADER);
            writer.writeAll(tasks);
            return true;
        } catch (IOException ex) {
            ErrorLogger.logError(ex);
            return false;
        }
    }

    public static int addTask(int productID, int requiredQuantity, int clientID) {
        List<String[]> allTasks = loadAllTasks();

        int newId = allTasks.stream()
                .filter(r -> r != null && r.length > 0 && !r[0].isEmpty())
                .mapToInt(r -> Integer.parseInt(r[0]))
                .max()
                .orElse(0) + 1;

        String[] newRecord = new String[TASKS_HEADER.length];
        newRecord[0] = String.valueOf(newId);
        newRecord[1] = ""; // productLineID
        newRecord[2] = String.valueOf(productID);
        newRecord[3] = String.valueOf(requiredQuantity);
        newRecord[4] = "0"; // achievedProductQuantity
        newRecord[5] = java.time.LocalDate.now().toString();
        newRecord[6] = ""; // endDate
        newRecord[7] = "1"; // taskStatusID (1 = PENDING)
        newRecord[8] = String.valueOf(clientID);

        allTasks.add(newRecord);

        if (saveAllTasks(allTasks)) {
            return newId;
        }
        return -1;
    }

    public static boolean updateTask(int id, Integer productLineID, int productID, int requiredQuantity, int achievedQuantity, String startDate, String endDate, int statusID, int clientID) {
        List<String[]> allTasks = loadAllTasks();
        boolean found = false;
        for (int i = 0; i < allTasks.size(); i++) {
            // --- التصحيح هنا: إضافة فحص أمان ---
            if (!allTasks.get(i)[0].isEmpty() && Integer.parseInt(allTasks.get(i)[0]) == id) {
                String[] record = allTasks.get(i);
                // تحديث الحقول فقط
                record[1] = (productLineID != null) ? String.valueOf(productLineID) : ""; // تحويل آمن
                record[2] = String.valueOf(productID);
                record[3] = String.valueOf(requiredQuantity);
                record[4] = String.valueOf(achievedQuantity);
                record[5] = startDate;
                record[6] = endDate;
                record[7] = String.valueOf(statusID);
                record[8] = String.valueOf(clientID);
                allTasks.set(i, record);
                found = true;
                break;
            }
        }
        return found && saveAllTasks(allTasks);
    }

    public static String[] findTaskByID(int id) {
        return loadAllTasks().stream()
                .filter(r -> Integer.parseInt(r[0]) == id)
                .findFirst().orElse(null);
    }

    public static List<String[]> loadAllProductLines() {
        List<String[]> lines = new ArrayList<>();
        File file = new File(LINES_FILE);
        if (!file.exists()) {
            return lines;
        }
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            reader.readNext(); // Skip header
            String[] record;
            while ((record = reader.readNext()) != null) {
                lines.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError(e);
        }
        return lines;
    }

    public static String findStatusNameById(int statusId) {
        List<String[]> statuses = new ArrayList<>();
        File file = new File(STATUS_FILE);
        if (!file.exists()) {
            return "Unknown";
        }
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            reader.readNext(); // Skip header
            String[] record;
            while ((record = reader.readNext()) != null) {
                statuses.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError(e);
        }

        return statuses.stream()
                .filter(r -> Integer.parseInt(r[0]) == statusId)
                .map(r -> r[1])
                .findFirst().orElse("Unknown");
    }



}
