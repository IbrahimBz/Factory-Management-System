package com.fsociety.factory.BusinessLayer.Production;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import com.fsociety.factory.BusinessLayer.Inventory.Item;
import com.fsociety.factory.BusinessLayer.Util;
import com.fsociety.factory.dataAccessLayer.AccessProducts;
import com.fsociety.factory.dataAccessLayer.ErrorLogger;
import java.util.*;

public class Product {
    private int id;
    private String name;
    private int quantityInStock;
    private final Map<Integer, Integer> requiredItems;
    private final Util.enObjectMode mode;

    private Product(int id, String name, int quantityInStock) {
        this.id = id;
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.requiredItems = new HashMap<>();
        this.mode = Util.enObjectMode.UPDATE;
        _LoadRequirements();
    }

    public Product() {
        this.id = -1;
        this.name = "";
        this.quantityInStock = 0;
        this.requiredItems = new HashMap<>();
        this.mode = Util.enObjectMode.ADDNEW;
    }

    private void _LoadRequirements() {
        List<String[]> reqs = AccessProducts.findRequirementsByProductID(this.id);
        for (String[] req : reqs) {
            try {
                int itemID = Integer.parseInt(req[1]);
                int quantity = Integer.parseInt(req[3]);
                this.requiredItems.put(itemID, quantity);
            } catch (Exception e) {
                ErrorLogger.logError(e);
            }
        }
    }

    private boolean _AddNew() {
        int newId = AccessProducts.addProduct(this.name, this.quantityInStock);
        if (newId == -1) return false;
        this.id = newId;

        boolean allReqsSaved = true;
        for (Map.Entry<Integer, Integer> entry : this.requiredItems.entrySet()) {
            boolean success = AccessProducts.addRequirement(this.id, entry.getKey(), entry.getValue());
            if (!success) allReqsSaved = false;
        }
        return allReqsSaved;
    }

    private boolean _Update() {
        boolean productUpdated = AccessProducts.updateProduct(this.id, this.name, this.quantityInStock);
        if (!productUpdated) return false;

        AccessProducts.deleteRequirementsByProductID(this.id);

        boolean allReqsSaved = true;
        for (Map.Entry<Integer, Integer> entry : this.requiredItems.entrySet()) {
            boolean success = AccessProducts.addRequirement(this.id, entry.getKey(), entry.getValue());
            if (!success) allReqsSaved = false;
        }
        return allReqsSaved;
    }

    public boolean save() {
        return switch (this.mode) {
            case ADDNEW -> _AddNew();
            case UPDATE -> _Update();
            default -> false;
        };
    }

    public boolean canProduceQuantity(int quantityToProduce) {
        if (requiredItems.isEmpty()) {
            return true;
        }

        Inventory inventory = Inventory.getInstance();

        for (Map.Entry<Integer, Integer> requirement : requiredItems.entrySet()) {
            int itemId = requirement.getKey();
            int quantityPerUnit = requirement.getValue();

            long totalRequired = (long) quantityPerUnit * quantityToProduce;

            Optional<Item> itemOpt = inventory.findItemByIdInMemory(itemId);

            if (itemOpt.isPresent()) {
                Item itemInStock = itemOpt.get();
                if (itemInStock.getAvailableQuantity() < totalRequired) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    public static Product findByID(int id) {
        String[] productData = AccessProducts.findProductByID(id);
        if (productData == null) return null;
        return new Product(
                Integer.parseInt(productData[0]),
                productData[1],
                Integer.parseInt(productData[2])
        );
    }


    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        List<String[]> records = AccessProducts.loadAllProducts();
        for (String[] record : records) {
            try {
                products.add(new Product(
                        Integer.parseInt(record[0]),
                        record[1],
                        Integer.parseInt(record[2])
                ));
            } catch (NumberFormatException e) {
                ErrorLogger.logError(e);
            }
        }
        return products;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }
    public Map<Integer, Integer> getRequiredItems() { return requiredItems; }

    @Override
    public String toString() {
        return this.name;
    }

}
