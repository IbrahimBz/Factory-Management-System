package com.fsociety.factory.BusinessLayer;

import com.fsociety.factory.dataAccessLayer.AccessItems;

import java.util.ArrayList;
import java.util.List;

public class Item {

    // This static field holds the highest ID used so far

    public static enum enItemCategory {
        UNDETERMINED,
        ELECTRONICS,
        CLOTHING,
        FOOD;

        @Override
        public String toString() {
            switch (this) {
                case ELECTRONICS: return "Electronics";
                case CLOTHING: return "Clothing";
                case FOOD: return "Food";
                default: return super.toString();
            }
        }
    }

    private int id;
    private String name;
    private int categoryID;
    private double price;
    private int availableQuantity;
    private int minAllowedQuantity;
    private Util.enObjectMode mode;


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
        switch (this.mode) {
            case ADDNEW:
                return _AddNew();
            case UPDATE:
                return _Update();
            default:
                return false;
        }
    }

    public static boolean deleteItem(int id) {
        return AccessItems.deleteItem(id);
    }
    public static Item findByID(int id) {

        String[] record = AccessItems.findByID(id);;

        if(record == null ) return null;
        return new Item(Integer.parseInt(record[0]),record[1],Integer.parseInt(record[2])
                ,Double.parseDouble(record[3]),Integer.parseInt(record[4]),Integer.parseInt(record[5]));


    }
    public static Item findByName(String name) {


        String[] record = AccessItems.findByName(name);

        if(record == null ) return null;
        return new Item(Integer.parseInt(record[0]),record[1],Integer.parseInt(record[2])
                ,Double.parseDouble(record[3]),Integer.parseInt(record[4]),Integer.parseInt(record[5]));


    }
    public static List<Item> findByCategoryID(int categoryID) {
        List<String[]> itemsRecords = AccessItems.findItemsByCategory(categoryID);

        if(itemsRecords.size() <= 1 || itemsRecords == null) return null;

        List<Item> items = new ArrayList<>();

        for(int i = 0; i < itemsRecords.size();i++) {
            String[] record = itemsRecords.get(i);
            items.add(new Item(Integer.parseInt(record[0]),record[1],Integer.parseInt(record[2])
                    ,Double.parseDouble(record[3]),Integer.parseInt(record[4]),Integer.parseInt(record[5])));

        }
        return items;

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

    public Util.enObjectMode getMode() {
        return mode;
    }


}
