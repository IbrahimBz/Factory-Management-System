package com.fsociety.factory.BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class ProductLine {
    private int id;
    private String name;
    private String status; // متوقف، نشط، صيانة...
    private List<Task> tasks;

    public ProductLine(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public int getID() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public List<Task> getTasks() { return tasks; }



}
