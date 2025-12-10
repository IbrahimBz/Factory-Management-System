package com.fsociety.factory.dataAccessLayer;

import com.fsociety.factory.BusinessLayer.Item;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

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


            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }


        }

        return ++currentMaxID;

    }


    public static List<String[]> getItems() {

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


    public static int addItem(String name, int categoryID, double price, int availableQuantity, int minAllowedQuantity) {
        try (FileWriter writer = new FileWriter(filePath, true)) {

            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            int id = generateID();

            printer.printRecord(Integer.toString(id), name, Integer.toString(categoryID), Double.toString(price),
                    Integer.toString(availableQuantity),Integer.toString(minAllowedQuantity));

            printer.flush();

            return id;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
