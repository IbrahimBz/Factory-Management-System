package com.fsociety.factory.BusinessLayer.Login;

import com.fsociety.factory.dataAccessLayer.ErrorLogger;
import com.fsociety.factory.dataAccessLayer.UserDataAccess;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * كلاس User (طبقة منطق العمل) بأسلوب Active Record.
 * هذا الكلاس يحتوي على منطق العمل ويعتمد على UserDataAccess لجلب البيانات.
 */
public class User {

    public enum UserRole {
        MANAGER,
        PRODUCTION_SUPERVISOR,
        UNKNOWN;

        public static UserRole fromString(String roleString) {
            try {
                return UserRole.valueOf(roleString.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return UNKNOWN;
            }
        }
    }

    // 2. خصائص الكائن (Instance Fields)
    private final int userID;
    private final String username;
    private final String hashedPassword;
    private final UserRole role;

    private User(int userID, String username, String hashedPassword, UserRole role) {
        this.userID = userID;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public static User findByUsername(String username) {
        String[] record = UserDataAccess.findByUsername(username);

        if (record == null) {
            return null;
        }

        try {
            int id = Integer.parseInt(record[0]);
            String name = record[1];
            String hash = record[2];
            UserRole role = UserRole.fromString(record[3]);

            return new User(id, name, hash, role);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            ErrorLogger.logError(e);
            return null;
        }
    }

    public boolean checkPassword(char[] password) {
        // تحقق من أن المدخلات ليست فارغة لزيادة الأمان
        if (password == null || password.length == 0 || this.hashedPassword == null || this.hashedPassword.isEmpty()) {
            return false;
        }

        // استخدام BCrypt للمقارنة الآمنة
        return BCrypt.checkpw(new String(password), this.hashedPassword);
    }

    public static User createNewUser(String username, char[] password, UserRole role) {
        if (username == null || username.trim().isEmpty() || password == null || password.length < 6) {
            return null;
        }
        if (User.findByUsername(username) != null) {
            return null;
        }

        // 2. تشفير كلمة المرور (Hashing)
        String hashedPassword = BCrypt.hashpw(new String(password), BCrypt.gensalt(12));

        int newId = UserDataAccess.addUser(username, hashedPassword, role.name());

        if (newId != -1) {
            return new User(newId, username, hashedPassword, role);
        } else {
            return null;
        }
    }

    public static List<User> getAllUsers() {
        List<String[]> records = UserDataAccess.loadAllUsers();
        List<User> users = new ArrayList<>();

        for (String[] record : records) {
            try {
                User user = new User(
                        Integer.parseInt(record[0]),
                        record[1],
                        record[2],
                        UserRole.fromString(record[3])
                );
                users.add(user);
            } catch (Exception e) {
                ErrorLogger.logError(e);
            }
        }
        return users;
    }

    public static boolean deleteUser(int userIdToDelete) {
        List<String[]> allUserRecords = UserDataAccess.loadAllUsers();

        List<String[]> updatedUserRecords = allUserRecords.stream()
                .filter(record -> Integer.parseInt(record[0]) != userIdToDelete)
                .collect(Collectors.toList());

        if (allUserRecords.size() == updatedUserRecords.size()) {
            return false;
        }

        return UserDataAccess.saveAllUsers(updatedUserRecords);
    }


    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }

}
