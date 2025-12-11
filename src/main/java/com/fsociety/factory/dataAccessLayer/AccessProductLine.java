package com.fsociety.factory.dataAccessLayer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessProductLine {

    // --- FILE PATHS ---
    private static final String PRODUCT_LINE_FILE = "src//main//resources//dataFiles//product-lines.csv";
    private static final String STATUS_FILE = "src//main//resources//dataFiles//product-line-status.csv";

    // --- ID GENERATION ---
    private static int currentMaxID = -1;

    private static int generateID() {
        if (currentMaxID == -1) {
            try (CSVReader reader = new CSVReader(new FileReader(PRODUCT_LINE_FILE))) {
                String[] record;
                reader.readNext(); // Skip Header
                while ((record = reader.readNext()) != null) {
                    try {
                        int id = Integer.parseInt(record[0]);
                        if (id > currentMaxID) currentMaxID = id;
                    } catch (NumberFormatException e) {
                        // Skip invalid rows
                    }
                }
            } catch (IOException | CsvValidationException ex) {
                ErrorLogger.logError(ex.getMessage());
            }
        }
        return ++currentMaxID;
    }

    // --- CORE LOADERS ---

    public static List<String[]> loadProductLines() {
        List<String[]> lines = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(PRODUCT_LINE_FILE))) {
            String[] record;
            reader.readNext(); // Skip Header
            while ((record = reader.readNext()) != null) {
                lines.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError(e.getMessage());
        }
        return lines;
    }

    // Returns a Map of ID -> StatusName for easy lookup
    public static Map<Integer, String> loadStatuses() {
        Map<Integer, String> statuses = new HashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(STATUS_FILE))) {
            String[] record;
            reader.readNext(); // Skip Header
            while ((record = reader.readNext()) != null) {
                statuses.put(Integer.parseInt(record[0]), record[1]);
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError(e.getMessage());

        }
        return statuses;
    }

    // --- WRITE (Rewrite File) ---
    private static boolean saveAllProductLines(List<String[]> lines) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(PRODUCT_LINE_FILE))) {
            // Re-write header if needed, or simply write all data
            // If your file has headers, you might want to manually write them first
            String[] header = {"productLineID", "productLineName", "statusID"};
            writer.writeNext(header);

            writer.writeAll(lines);
            return true;
        } catch (IOException ex) {
            ErrorLogger.logError(ex.getMessage());

            return false;
        }
    }

    // --- CRUD OPERATIONS ---

    public static int addProductLine(String name, int statusID) {
        try (FileWriter writer = new FileWriter(PRODUCT_LINE_FILE, true)) { // Append
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            int id = generateID();

            printer.printRecord(
                    Integer.toString(id),
                    name,
                    Integer.toString(statusID)
            );
            printer.flush();
            return id;
        } catch (IOException ex) {
            ErrorLogger.logError(ex.getMessage());

            return -1;
        }
    }

    public static boolean updateProductLine(int id, String name, int statusID) {
        List<String[]> lines = loadProductLines();
        String[] updatedRecord = {
                Integer.toString(id),
                name,
                Integer.toString(statusID)
        };

        for (int i = 0; i < lines.size(); i++) {
            if (Integer.parseInt(lines.get(i)[0]) == id) {
                lines.set(i, updatedRecord); // Replace
                return saveAllProductLines(lines);
            }
        }
        return false;
    }

    public static boolean deleteProductLine(int id) {
        List<String[]> lines = loadProductLines();
        for (int i = 0; i < lines.size(); i++) {
            if (Integer.parseInt(lines.get(i)[0]) == id) {
                lines.remove(i);
                return saveAllProductLines(lines);
            }
        }
        return false;
    }

    // --- FINDERS ---

    public static String[] findByID(int id) {
        List<String[]> lines = loadProductLines();
        for (String[] record : lines) {
            if (Integer.parseInt(record[0]) == id) {
                return record;
            }
        }
        return null;
    }

    public static String[] findByName(String name) {
        List<String[]> lines = loadProductLines();
        for (String[] record : lines) {
            if (record[1].equalsIgnoreCase(name)) {
                return record;
            }
        }
        return null;
    }

    // Helper to get status name
    public static String getStatusName(int statusID) {
        return loadStatuses().getOrDefault(statusID, "Unknown");
    }
}