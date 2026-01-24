package com.fsociety.factory.dataAccessLayer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccessProducts {

    private static final String PRODUCTS_FILE = "src/main/resources/dataFiles/products.csv";
    private static final String REQUIREMENTS_FILE = "src/main/resources/dataFiles/production-required-items.csv";
    private static final String[] PRODUCTS_HEADER = {"productID", "productName", "productQuantity"};
    private static final String[] REQUIREMENTS_HEADER = {"productionRequiredItemID", "itemID", "productID", "itemsQuantity"};

    private static List<String[]> loadData(String path) {
        List<String[]> data = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return data;
        }
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            reader.readNext(); // Skip header
            String[] record;
            while ((record = reader.readNext()) != null) {
                data.add(record);
            }
        } catch (Exception e) {
            ErrorLogger.logError(e);
        }
        return data;
    }

    private static boolean saveData(String path, String[] header, List<String[]> data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeNext(header);
            writer.writeAll(data);
            return true;
        } catch (IOException ex) {
            ErrorLogger.logError(ex);
            return false;
        }
    }


    public static List<String[]> loadAllProducts() {
        return loadData(PRODUCTS_FILE);
    }

    public static int addProduct(String name, int quantity) {
        List<String[]> allProducts = loadAllProducts();
        int newId = allProducts.stream().mapToInt(r -> Integer.parseInt(r[0])).max().orElse(0) + 1;
        allProducts.add(new String[]{String.valueOf(newId), name, String.valueOf(quantity)});
        if (saveData(PRODUCTS_FILE, PRODUCTS_HEADER, allProducts)) {
            return newId;
        }
        return -1;
    }

    public static boolean updateProduct(int id, String name, int quantity) {
        List<String[]> allProducts = loadAllProducts();
        boolean found = false;
        for (int i = 0; i < allProducts.size(); i++) {
            if (Integer.parseInt(allProducts.get(i)[0]) == id) {
                allProducts.set(i, new String[]{String.valueOf(id), name, String.valueOf(quantity)});
                found = true;
                break;
            }
        }
        return found && saveData(PRODUCTS_FILE, PRODUCTS_HEADER, allProducts);
    }
    

    public static String[] findProductByID(int id) {
        return loadAllProducts().stream()
                .filter(r -> Integer.parseInt(r[0]) == id)
                .findFirst().orElse(null);
    }
    


    public static List<String[]> findRequirementsByProductID(int productID) {
        return loadData(REQUIREMENTS_FILE).stream()
                .filter(r -> Integer.parseInt(r[2]) == productID)
                .collect(Collectors.toList());
    }

    public static boolean addRequirement(int productID, int itemID, int quantity) {
        List<String[]> allReqs = loadData(REQUIREMENTS_FILE);
        int newId = allReqs.stream().mapToInt(r -> Integer.parseInt(r[0])).max().orElse(0) + 1;
        allReqs.add(new String[]{String.valueOf(newId), String.valueOf(itemID), String.valueOf(productID), String.valueOf(quantity)});
        return saveData(REQUIREMENTS_FILE, REQUIREMENTS_HEADER, allReqs);
    }

    public static void deleteRequirementsByProductID(int productID) {
        List<String[]> allReqs = loadData(REQUIREMENTS_FILE);
        boolean removed = allReqs.removeIf(r -> Integer.parseInt(r[2]) == productID);
        if (removed) {
            saveData(REQUIREMENTS_FILE, REQUIREMENTS_HEADER, allReqs);
        }
    }
}
