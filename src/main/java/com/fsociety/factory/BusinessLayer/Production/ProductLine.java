package com.fsociety.factory.BusinessLayer.Production;

import com.fsociety.factory.BusinessLayer.Util;
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
    private final AtomicReference<Task> currentTask = new AtomicReference<>(null);


    private ProductLine(int id, String name, int statusID) {
        this.id = id;
        this.name = name;
        this.statusID = statusID;
        this.mode = Util.enObjectMode.UPDATE;
    }

    public ProductLine() {
        this.id = -1;
        this.name = "";
        this.statusID = 0; // Default to 'Stopped' or 'Unknown'
        this.mode = Util.enObjectMode.ADDNEW;
    }


    private boolean _AddNew() {
        int newId = AccessTask.addProductLine(this.name, this.statusID);
        if (newId != -1) {
            this.id = newId;
            return true;
        }
        return false;
    }

    private boolean _Update() {
        return AccessTask.updateProductLine(this.id, this.name, this.statusID);
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
        // يجب أن يكون نشطاً (statusID=1) وغير مشغول بمهمة أخرى (currentTask is null)
        return this.statusID == 1 && this.currentTask.get() == null;
    }
    /**
     * يقوم بتعيين مهمة جديدة لهذا الخط (يجعله مشغولاً).
     * @param task المهمة التي سيتم تعيينها.
     * @return true إذا نجحت عملية التعيين (كان الخط متاحاً).
     */
    public boolean assignTask(Task task) {
        // compareAndSet هي عملية ذرية تضمن عدم حدوث تضارب
        // هي تقوم بتعيين القيمة الجديدة فقط إذا كانت القيمة الحالية هي المتوقعة (null)
        return this.currentTask.compareAndSet(null, task);
    }

    /**
     * يقوم بتحرير الخط من المهمة الحالية (يجعله متاحاً مرة أخرى).
     */
    public void releaseTask() {
        this.currentTask.set(null);
    }

    public Task getCurrentTask() {
        return this.currentTask.get();
    }

    public static ProductLine findByID(int id) {
        List<String[]> records = AccessTask.loadAllProductLines();
        for (String[] record : records) {
            if (Integer.parseInt(record[0]) == id) {
                try {
                    return new ProductLine(
                            Integer.parseInt(record[0]),
                            record[1],
                            Integer.parseInt(record[2])
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
                        Integer.parseInt(record[2])
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

    @Override
    public String toString() {
        return this.name;
    }

}
