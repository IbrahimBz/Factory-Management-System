package com.fsociety.factory.dataAccessLayer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;


public class AccessCategory {

    private static final String filePath = "src//main//resources//dataFiles//categories.csv";

    public static String[] findByID(int id) {


            String[] record;

            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {


                reader.readNext();

                while ((record = reader.readNext()) != null) {

                    int categoryID = Integer.parseInt(record[0]);

                    if(categoryID == id)  return  record;
                }


                }


             catch (IOException | CsvValidationException e) {
                 ErrorLogger.logError(e);
            }

            return null;
}

}
