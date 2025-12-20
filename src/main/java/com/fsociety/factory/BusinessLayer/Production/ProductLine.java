package com.fsociety.factory.BusinessLayer.Production;

import com.fsociety.factory.BusinessLayer.Task;
import com.fsociety.factory.BusinessLayer.Util;
import com.fsociety.factory.dataAccessLayer.AccessProductLine;

import java.util.ArrayList;
import java.util.List;

public class ProductLine {

    private int id;
    private String name;
    private int statusID;
    private String statusName; // Helper field for display
    private List<Task> tasks;

    // Note: 'isAvailable' is derived from statusID usually, but kept as requested
    private boolean isAvailable;

    private Util.enObjectMode mode;

    // --- Private Constructor for Loading (Update Mode) ---
    private ProductLine(int id, String name, int statusID) {
        this.id = id;
        this.name = name;
        this.statusID = statusID;
        this.statusName = AccessProductLine.getStatusName(statusID);
        this.tasks = new ArrayList<>();
        this.mode = Util.enObjectMode.UPDATE;

        // Simple logic: If statusID is 1 (Active), set available true. Adjust as needed.
        this.isAvailable = (statusID == 1);
    }

    // --- Public Constructor for Creating (New Mode) ---
    public ProductLine() {
        this.id = -1;
        this.name = "";
        this.statusID = 0; // Default status (e.g., Stopped)
        this.statusName = "Unknown";
        this.tasks = new ArrayList<>();
        this.isAvailable = false;
        this.mode = Util.enObjectMode.ADDNEW;
    }

    // --- Internal CRUD Logic ---

    private boolean _AddNew() {
        int newId = AccessProductLine.addProductLine(this.name, this.statusID);
        if (newId != -1) {
            this.id = newId;
            return true;
        }
        return false;
    }

    private boolean _Update() {
        return AccessProductLine.updateProductLine(this.id, this.name, this.statusID);
    }

    // --- Public Interface ---

    public boolean save() {
        switch (this.mode) {
            case ADDNEW:
                return _AddNew();
            case UPDATE:
                return _Update();
            default:
                return false;
        }
    }

    public static boolean deleteProductLine(int id) {
        return AccessProductLine.deleteProductLine(id);
    }

    // --- Static Finders ---

    public static ProductLine findByID(int id) {
        String[] record = AccessProductLine.findByID(id);
        if (record != null) {
            return new ProductLine(
                    Integer.parseInt(record[0]),
                    record[1],
                    Integer.parseInt(record[2])
            );
        }
        return null;
    }

    public static ProductLine findByName(String name) {
        String[] record = AccessProductLine.findByName(name);
        if (record != null) {
            return new ProductLine(
                    Integer.parseInt(record[0]),
                    record[1],
                    Integer.parseInt(record[2])
            );
        }
        return null;
    }

    public static List<ProductLine> getAllProductLines() {
        List<String[]> records = AccessProductLine.loadProductLines();
        List<ProductLine> lines = new ArrayList<>();

        for (String[] record : records) {
            lines.add(new ProductLine(
                    Integer.parseInt(record[0]),
                    record[1],
                    Integer.parseInt(record[2])
            ));
        }
        return lines;
    }

    // --- Other Business Logic ---

    public void addTask(Task task) {
        tasks.add(task);
    }

    // --- Getters and Setters ---

    public int getID() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStatusID() { return statusID; }
    public void setStatusID(int statusID) {
        this.statusID = statusID;
        this.statusName = AccessProductLine.getStatusName(statusID); // Update name when ID changes
    }

    public String getStatusName() { return statusName; }

    public List<Task> getTasks() { return tasks; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Util.enObjectMode getMode() { return mode; }
}