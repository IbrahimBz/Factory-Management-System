package com.fsociety.factory.BusinessLayer;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import com.fsociety.factory.BusinessLayer.Production.Product;
import com.fsociety.factory.BusinessLayer.Production.ProductLine;
import com.fsociety.factory.dataAccessLayer.ErrorLogger;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;

public class Task implements Runnable {

    public enum Status { PENDING, RUNNING, COMPLETED, CANCELLED, PAUSED }

    private final int id;
    private final Product product;
    private final int requiredQuantity;
    private int achievedQuantity;
    private Status status;
    private final LocalDate startDate;
    private ProductLine assignedLine;
    private final Consumer<String> logger; // لإرسال التحديثات للواجهة

    public Task(int id, Product product, int requiredQuantity, Consumer<String> logger) {
        this.id = id;
        this.product = product;
        this.requiredQuantity = requiredQuantity;
        this.achievedQuantity = 0;
        this.status = Status.PENDING;
        this.startDate = LocalDate.now();
        this.logger = logger;
    }

    public void assignToLine(ProductLine line) {
        this.assignedLine = line;
    }

    @Override
    public void run() {

        if(!assignedLine.isAvailable()) return;
        if(!product.checkItemsQuantityToMake1Product()) return;

        assignedLine.setAvailable(false);

        Inventory inventory = Inventory.getInstance();


        for(int i = 0; i < requiredQuantity;i++) {
            try{
                Map<Integer,Integer> requiredItems = product.getRequiredItems();

                Thread.sleep(500);

                product.checkItemsQuantityToMake1Product();

                requiredItems.forEach( (itemID, quantity )->
                        {
                             if(inventory.consumeItemQuantity(itemID, quantity)) return;
                        }

                );


            } catch (InterruptedException e) {
                ErrorLogger.logError(e);
                this.status = Status.PAUSED;
            } catch (Exception e) {
                ErrorLogger.logError(e);
                this.status = Status.PAUSED;
            } finally {

            }

        }



    }
}
