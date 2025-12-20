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
import java.util.Objects;


public class AccessItems {

    private static final String FILE_PATH = "src/main/resources/dataFiles/items.csv";
    private static final String[] HEADER = {"itemID", "itemName", "categoryID", "price", "availableQuantity", "minAllowedQuantity"};

    public static List<String[]> loadItemsFromCSVFile() {
        List<String[]> items = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return items;
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            reader.readNext(); // تخطي سطر العناوين
            String[] record;
            while ((record = reader.readNext()) != null) {
                items.add(record);
            }
        } catch (IOException | CsvValidationException e) {
        }
        return items;
    }


    private static boolean saveAllItemsToCSVFile(List<String[]> items) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeNext(HEADER); // الخطوة الأهم: كتابة العناوين دائماً
            writer.writeAll(items);
            return true;
        } catch (IOException ex) {
            ErrorLogger.logError(ex);

            return false;
        }
    }


    public static int addItem(String name, int categoryID, double price, int availableQuantity, int minAllowedQuantity) {
        List<String[]> allItems = loadItemsFromCSVFile();

        int newId = allItems.stream()
                .mapToInt(record -> Integer.parseInt(record[0]))
                .max()
                .orElse(0) + 1;

        String[] newRecord = {
                String.valueOf(newId),
                name,
                String.valueOf(categoryID),
                String.valueOf(price),
                String.valueOf(availableQuantity),
                String.valueOf(minAllowedQuantity)
        };

        allItems.add(newRecord);

        if (saveAllItemsToCSVFile(allItems)) {
            return newId;
        } else {
            return -1;
        }
    }


    public static boolean updateItem(int id, String name, int categoryID, double price, int availableQuantity, int minAllowedQuantity) {
        List<String[]> allItems = loadItemsFromCSVFile();
        boolean itemFound = false;

        for (int i = 0; i < allItems.size(); i++) {
            if (Integer.parseInt(allItems.get(i)[0]) == id) {
                allItems.set(i, new String[]{
                        String.valueOf(id),
                        name,
                        String.valueOf(categoryID),
                        String.valueOf(price),
                        String.valueOf(availableQuantity),
                        String.valueOf(minAllowedQuantity)
                });
                itemFound = true;
                break;
            }
        }

        if (itemFound) {
            return saveAllItemsToCSVFile(allItems);
        } else {
            return false;
        }
    }


    public static boolean deleteItem(int id) {
        List<String[]> allItems = loadItemsFromCSVFile();
        boolean removed = allItems.removeIf(record -> Integer.parseInt(record[0]) == id);

        if (removed) {
            return saveAllItemsToCSVFile(allItems);
        } else {
            return false;
        }
    }


    public static String[] findByID(int id) {
        return loadItemsFromCSVFile().stream()
                .filter(record -> Integer.parseInt(record[0]) == id)
                .findFirst()
                .orElse(null);
    }


    public static String[] findByName(String name) {
        return loadItemsFromCSVFile().stream()
                .filter(record -> record.length > 1 && Objects.equals(record[1], name))
                .findFirst()
                .orElse(null);
    }


    public static List<String[]> findItemsByCategory(int categoryID) {
        List<String[]> categorizedItems = new ArrayList<>();
        for (String[] record : loadItemsFromCSVFile()) {
            if (record.length > 2 && Integer.parseInt(record[2]) == categoryID) {
                categorizedItems.add(record);
            }
        }
        return categorizedItems;
    }
}
