package com.fsociety.factory.dataAccessLayer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserDataAccess {

    private static final String filePath = "src/main/resources/dataFiles/users.csv";

    private static int currentMaxID = -1;


    private static int generateID() {
        if (currentMaxID == -1) {
            currentMaxID = 0;
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                String[] record;
                reader.readNext();

                while ((record = reader.readNext()) != null) {
                    int id = Integer.parseInt(record[0]);
                    if (id > currentMaxID) {
                        currentMaxID = id;
                    }
                }
            } catch (IOException | CsvValidationException | NumberFormatException ex) {
                ErrorLogger.logError(ex);
            }
        }
        return ++currentMaxID;
    }


    public static String[] findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] record;
            reader.readNext();

            while ((record = reader.readNext()) != null) {
                if (record[1].equalsIgnoreCase(username)) {
                    return record;
                }
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError(e);
        }

        return null;
    }

    public static int addUser(String userName, String hashedPassword, String userRole) {
        // نفتح الملف في وضع الإلحاق (append mode = true)
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {

            int newId = generateID();
            String[] record = {
                    String.valueOf(newId),
                    userName,
                    hashedPassword,
                    userRole
            };

            writer.writeNext(record);
            return newId;

        } catch (IOException ex) {
            ErrorLogger.logError(ex);
            return -1;
        }
    }

    public static List<String[]> loadAllUsers() {
        List<String[]> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] record;
            reader.readNext();

            while ((record = reader.readNext()) != null) {
                users.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError(e);
        }
        return users;
    }


    public static boolean saveAllUsers(List<String[]> users) {
        String[] header = {"userID", "userName", "hashedPassword", "userRole"};
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(header);
            writer.writeAll(users);
            return true;
        } catch (IOException ex) {
            ErrorLogger.logError(ex);
        }
            return false;
    }

}
