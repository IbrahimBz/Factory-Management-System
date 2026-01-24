package com.fsociety.factory.presentationLayer;

import com.fsociety.factory.BusinessLayer.Inventory.Item;

public class TestItems {

    public static boolean testAddNewItem() {
        Item item = new Item();

        item.setName("Silver");
        item.setPrice(1003.3);
        item.setAvailableQuantity(1000);
        item.setCategoryID(1);
        item.setMinAllowedQuantity(10);

        return item.save();
    }


    public static void main(String[] args) {
        System.out.println(testAddNewItem());
    }
}
