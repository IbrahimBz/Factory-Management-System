package com.fsociety.factory.BusinessLayer;

import com.fsociety.factory.BusinessLayer.Exceptions.ProductLineException;

import java.time.LocalDate;

public class TaskThread extends Thread {

    public static enum enTaskStatus {
                PENDING,        // المهمة لم تبدأ بعد
                IN_PROGRESS,    // قيد التنفيذ
                COMPLETED,      // مكتملة
                CANCELLED,      // ملغاة
                DELIVERED;      // تم التسليم

        @Override
        public String toString() {
            switch (this) {
                case PENDING: return "Pending";
                case IN_PROGRESS: return "In Progress";
                case COMPLETED: return "Completed";
                case CANCELLED: return "Cancelled";
                case DELIVERED: return "Delivered";
                default: return super.toString();
            }
        }
    }


    private int id;
    private int productID;
    private ProductLine productLine;
    private int requiredProductQuantity;
    private int achievedProductQuantity;
    private int clientID;
    private LocalDate startDate;
    private LocalDate endDate;
    private enTaskStatus  status;
    private ProductLine assignedLine;

    public TaskThread(int id, int productID, int requiredProductQuantity, LocalDate startDate, ProductLine productLine, int achievedProductQuantity, int clientID, LocalDate endDate, enTaskStatus status, ProductLine assignedLine) {
        this.id = id;
        this.productID = productID;
        this.requiredProductQuantity = requiredProductQuantity;
        this.startDate = startDate;
        this.productLine = productLine;
        this.achievedProductQuantity = achievedProductQuantity;
        this.clientID = clientID;
        this.endDate = endDate;
        this.status = status;
        this.assignedLine = assignedLine;
    }

    public ProductLine getAssignedLine() {
        return assignedLine;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getProductID() {
        return productID;
    }

    public int getAchievedProductQuantity() {
        return achievedProductQuantity;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public enTaskStatus getStatus() {
        return status;
    }

    public int getClientID() {
        return clientID;
    }

    public int getRequiredProductQuantity() {
        return requiredProductQuantity;
    }

    public int getID() {
        return id;
    }

    public double getCompletionRate() {
        return (double)(achievedProductQuantity * 100) / requiredProductQuantity;
    }

    public void run() throws ProductLineException {

        if(!productLine.isAvailable()) throw new ProductLineException("Product Line Is Not Available");

        productLine.setAvailable(false);



    }

}
