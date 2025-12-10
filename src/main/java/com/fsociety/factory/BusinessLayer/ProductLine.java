package com.fsociety.factory.BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class ProductLine {

    public static enum enProductLineStatus {
        STOPPED,    // متوقف
        ACTIVE,     // نشط
        MAINTENANCE; // صيانة

        @Override
        public String toString() {
            switch (this) {
                case STOPPED: return "Stopped";
                case ACTIVE: return "Active";
                case MAINTENANCE: return "Maintenance";
                default: return super.toString();
            }
        }
    }


    private int id;
    private String name;
    private enProductLineStatus status; // متوقف، نشط، صيانة...
    private List<TaskThread> tasks;
    private boolean isAvailable;

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public ProductLine(int id, String name, enProductLineStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.tasks = new ArrayList<>();
    }

    public void addTask(TaskThread task) {
        tasks.add(task);
    }

    public int getID() { return id; }
    public String getName() { return name; }
    public enProductLineStatus getStatus() { return status; }
    public List<TaskThread> getTasks() { return tasks; }





}
