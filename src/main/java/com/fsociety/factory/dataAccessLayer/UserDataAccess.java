package com.fsociety.factory.dataAccessLayer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * هذا الكلاس مسؤول عن كل عمليات الوصول المباشر لملف user.csv.
 * تم تصميمه ليطابق تماماً أسلوب كلاس AccessItems.
 */
public class UserDataAccess {

    // 1. تحديد مسار ملف المستخدمين
    private static final String filePath = "src/main/resources/dataFiles/users.csv";

    // 2. متغير لتتبع أعلى ID تم استخدامه لتوليد ID جديد
    private static int currentMaxID = -1;


    private static int generateID() {
        if (currentMaxID == -1) {
            // نقوم بتهيئة القيمة الأولية بـ 0 في حال كان الملف فارغاً
            currentMaxID = 0;
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                String[] record;
                reader.readNext(); // تخطي سطر العناوين (Header)

                while ((record = reader.readNext()) != null) {
                    int id = Integer.parseInt(record[0]);
                    if (id > currentMaxID) {
                        currentMaxID = id;
                    }
                }
            } catch (IOException | CsvValidationException | NumberFormatException ex) {
                ErrorLogger.logError(ex.toString());
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
            ErrorLogger.logError(e.toString());
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
            ErrorLogger.logError(ex.toString());
            return -1; // فشلت عملية الإضافة
        }
    }

    public static List<String[]> loadAllUsers() {
        List<String[]> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] record;
            reader.readNext(); // تخطي سطر العناوين

            while ((record = reader.readNext()) != null) {
                users.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            ErrorLogger.logError(e.toString());
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
            ErrorLogger.logError(ex.toString());
        }
            return false;
    }

}
