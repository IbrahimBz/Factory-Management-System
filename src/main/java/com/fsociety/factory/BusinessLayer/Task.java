package com.fsociety.factory.BusinessLayer;

import java.time.LocalDate;

public class Task {

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
    private int requiredProductQuantity;
    private int achievedProductQuantity;
    private int clientID;
    private LocalDate startDate;
    private LocalDate endDate;
    private enTaskStatus  status;
    private ProductLine assignedLine;

    public Task(int id, int productID, int requiredQuantity, int clientID,
                LocalDate startDate, LocalDate dueDate, enTaskStatus status, ProductLine assignedLine) {
        this.id = id;
        this.productID = productID;
        this.requiredProductQuantity = requiredQuantity;
        this.clientID = clientID;
        this.startDate = startDate;
        this.endDate = dueDate;
        this.status = status;
        this.assignedLine = assignedLine;
        this.achievedProductQuantity = 0;
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

}
