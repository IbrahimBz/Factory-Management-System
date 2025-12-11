package com.fsociety.factory.dataAccessLayer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccessItems {

    private static String filePath = "src//main//resources//dataFiles//items.csv";

    private static int currentMaxID = -1;

    private static int generateID() {
        if(currentMaxID == -1) {
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {

                String[] record;

                reader.readNext();

                while ((record = reader.readNext()) != null) {

                    currentMaxID = Integer.parseInt(record[0]);
                }

            } catch (IOException | CsvValidationException ex) {
                ErrorLogger.logError(ex.getMessage());
            }
        }

        return ++currentMaxID;
    }


    public static List<String[]> loadItemsFromCSVFile() {

        List<String[]> items = new ArrayList<String[]>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {

            String[] record;

            reader.readNext();

            while ((record = reader.readNext()) != null) {

                items.add(record);

            }


        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return items;
    }

    public static boolean loadItemsToCSVFile(List<String[]> items) {

        try(CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            writer.writeAll(items);

        }
        catch (IOException ex) {
            ErrorLogger.logError(ex.getMessage());
        }

        return false;

    }


    public static int addItem(String name, int categoryID, double price, int availableQuantity, int minAllowedQuantity) {
        try (FileWriter writer = new FileWriter(filePath, true)) {

            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            int id = generateID();

            printer.printRecord(Integer.toString(id), name, Integer.toString(categoryID), Double.toString(price),
                    Integer.toString(availableQuantity),Integer.toString(minAllowedQuantity));

            printer.flush();

            return id;
        }
        catch (IOException ex) {
            ErrorLogger.logError(ex.getMessage());

        }
        return -1;
    }

    public static boolean updateItem(int id, String name, int categoryID, double price, int availableQuantity, int minAllowedQuantity) {

        String [] updatedRecord = {Integer.toString(id), Integer.toString(categoryID), Double.toString(price), Integer.toString(availableQuantity), Integer.toString(minAllowedQuantity)};

        List<String[]> items = loadItemsFromCSVFile();

        for(int i = 0;i < items.size(); i++) {

            if(Integer.parseInt(items.get(i)[0]) == id ) {

                items.remove(i);
                items.add(updatedRecord);
                return loadItemsToCSVFile(items);

            }

        }

        return false;
    }

    public static boolean deleteItem(int id) {
        List<String[]> items = loadItemsFromCSVFile();

        for(int i = 0;i < items.size(); i++) {

            if(Integer.parseInt(items.get(i)[0]) == id ) {

                items.remove(i);
                return loadItemsToCSVFile(items);

            }
        }
        return false;
    }

    public static String[] findByID(int id) {

        List<String[]> items = loadItemsFromCSVFile();

        for(int i = 0;i < items.size(); i++) {

            if(Integer.parseInt(items.get(i)[0]) == id ) {

                return items.get(i);

            }
        }
        return null;

    }

    public static String[] findByName(String name) {

        List<String[]> items = loadItemsFromCSVFile();

        for(int i = 0;i < items.size(); i++) {

            if(items.get(i)[1] == name ) {

                return items.get(i);

            }
        }
        return null;

    }

    public static List<String[]> findItemsByCategory(int categoryID) {

        List<String[]> items = loadItemsFromCSVFile();
        List<String[]> categorizedItems = new ArrayList<>();

        for(int i = 0;i < items.size(); i++) {

            if(Integer.parseInt(items.get(i)[2]) == categoryID ) {

                categorizedItems.add(items.get(i));

            }
        }
        return categorizedItems;

    }

}
