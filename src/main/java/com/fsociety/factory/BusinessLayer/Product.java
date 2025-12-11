package com.fsociety.factory.BusinessLayer;

import com.fsociety.factory.dataAccessLayer.AccessProducts;
import com.fsociety.factory.dataAccessLayer.ErrorLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Assuming the Item class exists and has a public int getId() method.

public class Product {
    private int id;
    private String name;
    private int quantityInStock;
    private Map<Item, Integer> requiredItems;
    private Util.enObjectMode mode;


    // --- Private Constructor for Loading/Updating (Called by static finders) ---
    private Product(int id, String name, int quantityInStock) {
        this.id = id;
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.requiredItems = new HashMap<>();
        this.mode = Util.enObjectMode.UPDATE;
        loadRequirements(); // Load the related data when the object is instantiated
    }

    // --- Public Constructor for Creating New Product (Default) ---
    public Product() {
        this.id = -1;
        this.name = "";
        this.quantityInStock = 0;
        this.requiredItems = new HashMap<>();
        this.mode = Util.enObjectMode.ADDNEW;
    }

    // --- Helper method to map requirements from CSV to the Map ---
    private void loadRequirements() {
        List<String[]> reqs = AccessProducts.findRequirementsByProductID(this.id);

        for (String[] req : reqs) {
            // req[1] = itemID, req[3] = itemsQuantity
            try {
                int itemID = Integer.parseInt(req[1]);
                int quantity = Integer.parseInt(req[3]);

                // IMPORTANT: You MUST implement a static Item.findByID method that returns a FULL Item object
                Item requiredItem = Item.findByID(itemID);

                if (requiredItem != null) {
                    this.requiredItems.put(requiredItem, quantity);
                }

            } catch (Exception e) {
                ErrorLogger.logError(e.getMessage());
            }
        }
    }


    // --- CRUD Logic ---

    private boolean _AddNew() {
        // 1. Add the main product record (and get the new ID)
        int newId = AccessProducts.addProductRecord(this.name, this.quantityInStock);

        if (newId == -1) return false;
        this.id = newId;

        // 2. Add all associated requirement records
        boolean reqsSaved = true;
        for (Map.Entry<Item, Integer> entry : this.requiredItems.entrySet()) {
            boolean success = AccessProducts.addRequirementRecord(
                    this.id,
                    entry.getKey().getId(), // Get the Item's ID
                    entry.getValue()
            );
            if (!success) reqsSaved = false;
        }

        return reqsSaved;
    }

    private boolean _Update() {
        // 1. Update the main product record
        boolean productUpdated = AccessProducts.updateProduct(
                this.id,
                this.name,
                this.quantityInStock
        );

        if (!productUpdated) return false;

        // 2. Delete all OLD requirements for this product ID (cleaning up the file)
        AccessProducts.deleteRequirementsByProductID(this.id);

        // 3. Insert all CURRENT requirements (writing the new list)
        boolean reqsSaved = true;
        for (Map.Entry<Item, Integer> entry : this.requiredItems.entrySet()) {
            boolean success = AccessProducts.addRequirementRecord(
                    this.id,
                    entry.getKey().getId(),
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

    // --- Static Finders/Deleters (Matching Item class functionality) ---

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
    public Map<Item, Integer> getRequiredItems() { return requiredItems; }
    public Util.enObjectMode getMode() { return mode; }

    // Custom setter to add a new requirement
    public void addRequiredItem(Item item, int quantity) {
        this.requiredItems.put(item, quantity);
    }
}