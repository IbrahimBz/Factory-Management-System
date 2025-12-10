package com.fsociety.factory.BusinessLayer;

public class Item {
    public static enum enItemCategory {
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
    private enItemCategory category;
    private double price;
    private int availableQuantity;
    private int minAllowedQuantity;

    public Item(int id, String name, enItemCategory category, double price, int availableQuantity, int minAllowedQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.minAllowedQuantity = minAllowedQuantity;
    }


}
