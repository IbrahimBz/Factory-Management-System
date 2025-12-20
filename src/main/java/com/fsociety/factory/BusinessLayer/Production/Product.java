package com.fsociety.factory.BusinessLayer.Production;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import com.fsociety.factory.BusinessLayer.Inventory.Item;
import com.fsociety.factory.BusinessLayer.Util;
import com.fsociety.factory.dataAccessLayer.AccessProducts;
import com.fsociety.factory.dataAccessLayer.ErrorLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Assuming the Item class exists and has a public int getId() method.

public class Product {
    private int id;
    private String name;
    private int quantityInStock;
    // itemID, quantity
    private Map<Integer, Integer> requiredItems;
    private Util.enObjectMode mode;


    private Product(int id, String name, int quantityInStock) {
        this.id = id;
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.requiredItems = new HashMap<>();
        this.mode = Util.enObjectMode.UPDATE;
        _LoadRequirements(); // Load the related data when the object is instantiated
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
            // req[1] = itemID, req[3] = itemsQuantity
            try {
                int itemID = Integer.parseInt(req[1]);
                int quantity = Integer.parseInt(req[3]);

                this.requiredItems.put(itemID,quantity);

            } catch (Exception e) {
                ErrorLogger.logError(e);
            }
        }
    }

    private boolean _AddNew() {
        int newId = AccessProducts.addProductRecord(this.name, this.quantityInStock);

        if (newId == -1) return false;
        this.id = newId;

        // 2. Add all associated requirement records
        boolean reqsSaved = true;
        for (Map.Entry<Integer, Integer> entry : this.requiredItems.entrySet()) {
            boolean success = AccessProducts.addRequirementRecord(
                    this.id,
                    entry.getKey(),
                    entry.getValue()
            );
            if (!success) reqsSaved = false;
        }

        return reqsSaved;
    }

    private boolean _Update() {
        boolean productUpdated = AccessProducts.updateProduct(
                this.id,
                this.name,
                this.quantityInStock
        );

        if (!productUpdated) return false;

        AccessProducts.deleteRequirementsByProductID(this.id);

        boolean reqsSaved = true;
        for (Map.Entry<Integer, Integer> entry : this.requiredItems.entrySet()) {
            boolean success = AccessProducts.addRequirementRecord(
                    this.id,
                    entry.getKey(),
                    entry.getValue()
            );
            if (!success) reqsSaved = false;
        }

        return reqsSaved;
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

    public boolean checkItemsQuantityToMake1Product() {

        for(Map.Entry<Integer, Integer> reqItem: requiredItems.entrySet()) {

            Optional<Item> item = Inventory.getInstance().findItemByIdInMemory(reqItem.getKey());

            if( !item.isPresent() || reqItem.getValue() < item.get().getAvailableQuantity()) return false;

        }
        return true;

    }


    public static boolean deleteProduct(int id) {
        return AccessProducts.deleteProduct(id);
    }

    public static Product findByID(int id) {
        String[] productData = AccessProducts.findProductByID(id);
        if (productData == null) return null;

        // Map data: productID (0), productName (1), productQuantity (2)
        return new Product(
                Integer.parseInt(productData[0]),
                productData[1],
                Integer.parseInt(productData[2])
        );
    }

    public static Product findByName(String name) {
        String[] productData = AccessProducts.findProductByName(name);
        if (productData == null) return null;

        return new Product(
                Integer.parseInt(productData[0]),
                productData[1],
                Integer.parseInt(productData[2])
        );
    }






    // --- Getters and Setters ---
    // (Ensure you copy the setters from your Item class if they are not shown here)

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }
    public Map<Integer, Integer> getRequiredItems() { return requiredItems; }
    public Util.enObjectMode getMode() { return mode; }

    // Custom setter to add a new requirement
    public void addRequiredItem(int itemId, int quantity) {
        this.requiredItems.put(itemId, quantity);
    }
}