package com.fsociety.factory.BusinessLayer.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class Inventory {

    // --- Singleton Implementation ---
    private static Inventory instance;

    public static synchronized Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    private final List<Item> items;

    private Inventory() {

        List<Item> initialItems = Item.getAllItems();

        if (initialItems.size() != 0) {
            this.items = new CopyOnWriteArrayList<>(initialItems);
        } else {
            this.items = new CopyOnWriteArrayList<>();
        }
    }


    public List<Item> getAllItems() {
        return this.items;
    }


    public Optional<Item> findItemByIdInMemory(int itemId) {
        return this.items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }


    public synchronized boolean addNewItem(Item newItem) {
        boolean success = newItem.save();

        if (success) {
            Item savedItem = Item.findByID(newItem.getId());
            if (savedItem != null) {
                this.items.add(savedItem);
            }
        }
        return success;
    }

    public synchronized boolean deleteItem(int itemId) {
        boolean success = Item.deleteItem(itemId);

        if (success) {
            this.items.removeIf(item -> item.getId() == itemId);
        }
        return success;
    }


    public synchronized boolean updateItem(Item itemToUpdate) {
        boolean success = itemToUpdate.save();

        if (success) {
            findItemByIdInMemory(itemToUpdate.getId()).ifPresent(itemInMemory -> {
                itemInMemory.setName(itemToUpdate.getName());
                itemInMemory.setCategoryID(itemToUpdate.getCategoryID());
                itemInMemory.setPrice(itemToUpdate.getPrice());
                itemInMemory.setAvailableQuantity(itemToUpdate.getAvailableQuantity());
                itemInMemory.setMinAllowedQuantity(itemToUpdate.getMinAllowedQuantity());
            });
        }
        return success;
    }


    public synchronized boolean consumeItemQuantity(int itemId, int quantityToConsume) {
        Optional<Item> itemOpt = findItemByIdInMemory(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            if (item.getAvailableQuantity() >= quantityToConsume) {
                item.setAvailableQuantity(item.getAvailableQuantity() - quantityToConsume);
                return true;
            }
        }
        return false;
    }

    public synchronized void persistChanges() {
        for (Item item : this.items) {
            item.save();
        }
    }
}
