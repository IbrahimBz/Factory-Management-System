package com.fsociety.factory.BusinessLayer.Manager;

import com.fsociety.factory.BusinessLayer.Production.ProductLine;
import com.fsociety.factory.BusinessLayer.Production.Task;
import com.fsociety.factory.BusinessLayer.Production.TaskManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinesManager {

    private List<ProductLine> productLines;

    private static LinesManager instance;

    public static synchronized LinesManager getInstance() {
        if (instance == null) {
            instance = new LinesManager();
        }
        return instance;
    }

    private LinesManager() {
        this.productLines = ProductLine.getAllProductLines();
    }

    public void save() {
        productLines = ProductLine.getAllProductLines();

    }

    public boolean addNewProductLine(String name, int statusID, String notes) {
        ProductLine productLine = new ProductLine();

        productLine.setName(name);
        productLine.setStatusID(statusID);
        productLine.setNotes(notes);

        if(productLine.save())
        {
            this.save();
            return true;

        }

        return false;
    }

    public boolean editProductLine(int productLineID,String name, int statusID, String notes) {

        ProductLine productLine = ProductLine.findByID(productLineID);

        assert productLine != null;
        productLine.setName(name);
        productLine.setStatusID(statusID);
        productLine.setNotes(notes);

        if(productLine.save())
        {
            this.save();
            return true;

        }

        return false;

    }

    public List<ProductLine> getProductLines() {
        return productLines;
    }

    public Map<ProductLine, Integer> getProductionQuantity() {
        Map<ProductLine, Integer> productionQuantity = new HashMap<>();

        for (ProductLine line : this.productLines) {
            productionQuantity.put(line, 0);
        }

        TaskManager taskManager = TaskManager.getInstance();

        for (Task task : taskManager.getAllTasks()) {
            ProductLine taskLine = task.getProductLine();
            if (productionQuantity.containsKey(taskLine)) {
                int currentQty = productionQuantity.get(taskLine);
                productionQuantity.put(taskLine, currentQty + task.getAchievedQuantity());
            }
        }

        return productionQuantity;
    }

}
