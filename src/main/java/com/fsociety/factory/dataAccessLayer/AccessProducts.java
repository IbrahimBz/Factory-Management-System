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
import java.util.List;
import java.util.stream.Collectors;

public class AccessProducts {

    // --- FILE PATHS (Adjust these paths to your project) ---
    private static final String PRODUCTS_FILE_PATH = "src//main//resources//dataFiles//products.csv";
    private static final String REQUIREMENTS_FILE_PATH = "src//main//resources//dataFiles//production-required-items.csv";

    // --- ID GENERATION FIELDS ---
    private static int currentMaxProductID = -1;
    private static int currentMaxRequirementID = -1;

    private static final String[] PRODUCT_HEADERS = {"productID", "productName", "productQuantity"};
    private static final String[] REQUIREMENT_HEADERS = {"productionRequiredItemID", "itemID", "productID", "itemsQuantity"};


    // --- ID GENERATION LOGIC ---

    // Initializes the Max IDs by scanning the files (Called once)
    private static void initializeMaxIDs() {
        if (currentMaxProductID != -1 && currentMaxRequirementID != -1) return;

        // Initialize Product ID (scanning PRODUCTS_FILE_PATH)
        try (CSVReader reader = new CSVReader(new FileReader(PRODUCTS_FILE_PATH))) {
            String[] record;
            reader.readNext(); // Skip header
            while ((record = reader.readNext()) != null) {
                currentMaxProductID = Math.max(currentMaxProductID, Integer.parseInt(record[0]));
            }
        } catch (IOException | CsvValidationException ex) {
            ErrorLogger.logError("Error initializing Product IDs: " + ex.getMessage());
        }

        // Initialize Requirement ID (scanning REQUIREMENTS_FILE_PATH)
        try (CSVReader reader = new CSVReader(new FileReader(REQUIREMENTS_FILE_PATH))) {
            String[] record;
            reader.readNext(); // Skip header
            while ((record = reader.readNext()) != null) {
                currentMaxRequirementID = Math.max(currentMaxRequirementID, Integer.parseInt(record[0]));
            }
        } catch (IOException | CsvValidationException ex) {
            ErrorLogger.logError("Error initializing Requirement IDs: " + ex.getMessage());
        }
    }

    public static int generateProductID() {
        initializeMaxIDs();
        return ++currentMaxProductID;
    }

    public static int generateRequirementID() {
        initializeMaxIDs();
        return ++currentMaxRequirementID;
    }

    // --- CORE FILE R/W UTILITIES ---

    // General method to load any CSV
    private static List<String[]> loadCSV(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.readNext(); // Skip header
            String[] record;
            while ((record = reader.readNext()) != null) {
                records.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError("Error loading CSV: " + filePath + " " + e.getMessage());
        }
        return records;
    }

    // General method to overwrite/write all records
    private static boolean writeCSV(String filePath, List<String[]> records, String[] headers) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, false))) { // false = overwrite
            writer.writeNext(headers);
            writer.writeAll(records);
            return true;
        } catch (IOException ex) {
            ErrorLogger.logError("Error writing CSV: " + filePath + " " + ex.getMessage());
            return false;
        }
    }

    // --- CREATE (Insert) Operations ---

    public static int addProductRecord(String name, int quantity) {
        try (FileWriter writer = new FileWriter(PRODUCTS_FILE_PATH, true)) { // Append mode
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            int id = generateProductID();

            printer.printRecord(
                    Integer.toString(id),
                    name,
                    Integer.toString(quantity)
            );
            printer.flush();
            return id;
        } catch (IOException ex) {
            ErrorLogger.logError("Error adding product: " + ex.getMessage());
            return -1;
        }
    }

    public static boolean addRequirementRecord(int productID, int itemID, int itemsQuantity) {
        try (FileWriter writer = new FileWriter(REQUIREMENTS_FILE_PATH, true)) { // Append mode
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            int id = generateRequirementID();

            printer.printRecord(
                    Integer.toString(id),
                    Integer.toString(itemID),
                    Integer.toString(productID),
                    Integer.toString(itemsQuantity)
            );
            printer.flush();
            return true;
        } catch (IOException ex) {
            ErrorLogger.logError("Error adding requirement: " + ex.getMessage());
            return false;
        }
    }

    // --- READ (Finders) Operations ---

    public static String[] findProductByID(int id) {
        return loadCSV(PRODUCTS_FILE_PATH).stream()
                .filter(p -> Integer.parseInt(p[0]) == id)
                .findFirst()
                .orElse(null);
    }

    public static String[] findProductByName(String name) {
        return loadCSV(PRODUCTS_FILE_PATH).stream()
                .filter(p -> p[1].equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static List<String[]> findRequirementsByProductID(int productID) {
        return loadCSV(REQUIREMENTS_FILE_PATH).stream()
                .filter(r -> Integer.parseInt(r[2]) == productID) // r[2] is productID
                .collect(Collectors.toList());
    }

    // --- UPDATE/DELETE UTILITIES (Requires full file rewrite) ---

    // Updates Product record and returns true if successful
    public static boolean updateProduct(int id, String name, int quantity) {
        List<String[]> products = loadCSV(PRODUCTS_FILE_PATH);
        String[] updatedRecord = {Integer.toString(id), name, Integer.toString(quantity)};

        for (int i = 0; i < products.size(); i++) {
            if (Integer.parseInt(products.get(i)[0]) == id) {
                products.set(i, updatedRecord);
                return writeCSV(PRODUCTS_FILE_PATH, products, PRODUCT_HEADERS);
            }
        }
        return false;
    }

    // Deletes Product and its associated requirements
    public static boolean deleteProduct(int id) {
        // 1. Delete from Products file
        List<String[]> products = loadCSV(PRODUCTS_FILE_PATH);
        List<String[]> filteredProducts = products.stream()
                .filter(p -> Integer.parseInt(p[0]) != id)
                .collect(Collectors.toList());

        boolean deletedProduct = filteredProducts.size() < products.size() && writeCSV(PRODUCTS_FILE_PATH, filteredProducts, PRODUCT_HEADERS);

        // 2. Delete associated requirements (r[2] is productID)
        List<String[]> requirements = loadCSV(REQUIREMENTS_FILE_PATH);
        List<String[]> filteredRequirements = requirements.stream()
                .filter(r -> Integer.parseInt(r[2]) != id)
                .collect(Collectors.toList());

        boolean deletedRequirements = writeCSV(REQUIREMENTS_FILE_PATH, filteredRequirements, REQUIREMENT_HEADERS);

        return deletedProduct && deletedRequirements;
    }

    // Deletes ALL requirements for a specific product ID (used during update)
    public static void deleteRequirementsByProductID(int productID) {
        List<String[]> requirements = loadCSV(REQUIREMENTS_FILE_PATH);
        List<String[]> filteredRequirements = requirements.stream()
                .filter(r -> Integer.parseInt(r[2]) != productID)
                .collect(Collectors.toList());
        writeCSV(REQUIREMENTS_FILE_PATH, filteredRequirements, REQUIREMENT_HEADERS);
    }
}