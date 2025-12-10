package com.fsociety.factory.BusinessLayer;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private int id;
    private String name;
    private Map<Item, Integer> requiredItems;


    public Product(int id, String name) {
        this.id = id;
        this.name = name;
        this.requiredItems = new HashMap<>();
    }

    public void addMaterial(Item item, int quantityNeeded) {
        requiredItems.put(item, quantityNeeded);
    }

    public int getID() { return id; }
    public String getName() { return name; }
    public Map<Item, Integer> getRequiredItems() { return requiredItems; }
}


