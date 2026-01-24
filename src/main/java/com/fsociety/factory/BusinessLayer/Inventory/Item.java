package com.fsociety.factory.BusinessLayer.Inventory;

import com.fsociety.factory.BusinessLayer.Util;
import com.fsociety.factory.dataAccessLayer.AccessCategory;
import com.fsociety.factory.dataAccessLayer.AccessItems;
import com.fsociety.factory.dataAccessLayer.ErrorLogger;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private int id;
    private String name;
    private int categoryID;
    private double price;
    private int availableQuantity;
    private int minAllowedQuantity;
    private final Util.enObjectMode mode;


    private Item(int id, String name, int categoryID, double price, int availableQuantity, int minAllowedQuantity) {
        this.id = id;
        this.name = name;

        this.categoryID = categoryID;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.minAllowedQuantity = minAllowedQuantity;
        this.mode = Util.enObjectMode.UPDATE;
    }

    private boolean _AddNew() {
        int id = AccessItems.addItem(this.name,this.categoryID,this.price,this.availableQuantity,this.minAllowedQuantity);
        this.id = id;
        return (id != -1);
    }

    private boolean _Update() {
        return AccessItems.updateItem(this.id,this.name,this.categoryID,this.price,this.availableQuantity,this.minAllowedQuantity);
    }


    public Item() {
        this.id = -1;
        this.name = "";
        this.categoryID = -1;
        this.price = 0.00;
        this.availableQuantity = 0;
        this.minAllowedQuantity = 0;
        this.mode = Util.enObjectMode.ADDNEW;
    }


    public boolean save() {
        return switch (this.mode) {
            case ADDNEW -> _AddNew();
            case UPDATE -> _Update();
            default -> false;
        };
    }

    public static boolean deleteItem(int id) {
        return AccessItems.deleteItem(id);
    }

    public static Item findByID(int id) {

        String[] record = AccessItems.findByID(id);

        if(record == null ) return null;
        return new Item(Integer.parseInt(record[0]),record[1],Integer.parseInt(record[2])
                ,Double.parseDouble(record[3]),Integer.parseInt(record[4]),Integer.parseInt(record[5]));


    }


    public static List<Item> getAllItems() {
        List<String[]> itemsRecords = AccessItems.loadItemsFromCSVFile();

        if (itemsRecords.isEmpty()) {
            return new ArrayList<>(); // أرجع قائمة فارغة بدلاً من null
        }

        List<Item> items = new ArrayList<>();
        for (String[] record : itemsRecords) {
            try {
                items.add(new Item(
                        Integer.parseInt(record[0]),
                        record[1],
                        Integer.parseInt(record[2]),
                        Double.parseDouble(record[3]),
                        Integer.parseInt(record[4]),
                        Integer.parseInt(record[5])
                ));
            } catch (Exception e) {
                ErrorLogger.logError(e);
            }
        }
        return items;
    }

    public String getCategoryName() {
        String[] categoryRecord = AccessCategory.findByID(this.categoryID);
        if (categoryRecord != null && categoryRecord.length > 1) {
            return categoryRecord[1];
        }
        return "N/A";
    }



    // Getters and Setters

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getMinAllowedQuantity() {
        return minAllowedQuantity;
    }

    public void setMinAllowedQuantity(int minAllowedQuantity) {
        this.minAllowedQuantity = minAllowedQuantity;
    }


}
