package com.fsociety.factory.BusinessLayer.Production;

import com.fsociety.factory.BusinessLayer.Util;
import com.fsociety.factory.dataAccessLayer.AccessProductLine;
import com.fsociety.factory.dataAccessLayer.AccessTask;
import com.fsociety.factory.dataAccessLayer.ErrorLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProductLine {

    private int id;
    private String name;
    private int statusID;
    private final Util.enObjectMode mode;
    private String notes;
    private final AtomicReference<Task> currentTask = new AtomicReference<>(null);


    private ProductLine(int id, String name, int statusID, String notes) {
        this.id = id;
        this.name = name;
        this.statusID = statusID;
        this.notes = notes;
        this.mode = Util.enObjectMode.UPDATE;
    }

    public ProductLine() {
        this.id = -1;
        this.name = "";
        this.statusID = 0;
        this.mode = Util.enObjectMode.ADDNEW;
    }


    private boolean _AddNew() {
        int newId = AccessProductLine.addProductLine(this.name, this.statusID, this.notes);
        if (newId != -1) {
            this.id = newId;
            return true;
        }
        return false;
    }

    private boolean _Update() {
        return AccessProductLine.updateProductLine(this.id, this.name, this.statusID, this.notes);
    }


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

    public boolean isTrulyAvailable() {
        return this.statusID == 1 && this.currentTask.get() == null;
    }

    public boolean assignTask(Task task) {
        return this.currentTask.compareAndSet(null, task);
    }

    public void releaseTask() {
        this.currentTask.set(null);
    }

    public Task getCurrentTask() {
        return this.currentTask.get();
    }

    public static ProductLine findByID(int id) {
        List<String[]> records = AccessProductLine.loadProductLines();
        for (String[] record : records) {
            if (Integer.parseInt(record[0]) == id) {
                try {
                    return new ProductLine(
                            Integer.parseInt(record[0]),
                            record[1],
                            Integer.parseInt(record[2]),
                            record[3]
                    );
                } catch (NumberFormatException e) {
                    ErrorLogger.logError(e);
                    return null;
                }
            }
        }
        return null;
    }

    public static List<ProductLine> getAllProductLines() {
        List<ProductLine> lines = new ArrayList<>();
        List<String[]> records = AccessTask.loadAllProductLines();

        for (String[] record : records) {
            try {
                lines.add(new ProductLine(
                        Integer.parseInt(record[0]),
                        record[1],
                        Integer.parseInt(record[2]),
                        record[3]
                ));
            } catch (NumberFormatException e) {
                ErrorLogger.logError(e);
            }
        }
        return lines;
    }

    public boolean isAvailable() {
        return this.statusID == 1;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public String getStatusName() {
        return AccessTask.findStatusNameById(this.statusID);
    }

    public Util.enObjectMode getMode() {
        return mode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return this.name;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductLine that = (ProductLine) o;
        return id == that.id; // التمييز بناءً على الـ ID
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
