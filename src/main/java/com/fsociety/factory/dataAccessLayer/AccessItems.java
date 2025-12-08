package com.fsociety.factory.dataAccessLayer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccessItems {

    public static enum Categories{ electronics, clothes, nutrition };


    private static String filePath = "src//main//resources//dataFiles//items.csv";

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


}
