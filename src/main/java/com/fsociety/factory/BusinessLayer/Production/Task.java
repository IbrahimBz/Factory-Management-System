package com.fsociety.factory.BusinessLayer.Production;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import com.fsociety.factory.BusinessLayer.Inventory.Item;
import com.fsociety.factory.BusinessLayer.Util;
import com.fsociety.factory.dataAccessLayer.AccessTask;
import com.fsociety.factory.dataAccessLayer.ErrorLogger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Task implements Runnable {

    private int id;
    private Integer productLineID; // --- تم التغيير إلى Integer ليكون قابلاً لـ null ---
    private int productID;
    private int requiredQuantity;
    private int achievedQuantity;
    private final LocalDate startDate;
    private LocalDate endDate;
    private int statusID;
    private int clientID;
    private final Util.enObjectMode mode;

    private transient Thread thread;
    private transient Product product;
    private transient ProductLine assignedLine; // --- تم تغيير الاسم ليكون أوضح ---
    private transient Consumer<String> logger = (msg) -> {};

    private Task(int id, Integer productLineID, int productID, int requiredQuantity, int achievedQuantity,
                 LocalDate startDate, LocalDate endDate, int statusID, int clientID) {
        this.id = id;
        this.productLineID = productLineID;
        this.productID = productID;
        this.requiredQuantity = requiredQuantity;
        this.achievedQuantity = achievedQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.statusID = statusID;
        this.clientID = clientID;
        this.product = Product.findByID(productID);
        if (this.productLineID != null) {
            this.assignedLine = ProductionManager.getInstance().findLineById(this.productLineID);
        }
        this.mode = Util.enObjectMode.UPDATE;
    }

    public Task() {
        this.id = -1;
        this.productLineID = null;
        this.productID = -1;
        this.requiredQuantity = 0;
        this.achievedQuantity = 0;
        this.startDate = LocalDate.now();
        this.endDate = null;
        this.statusID = 1; // PENDING
        this.clientID = -1;
        this.mode = Util.enObjectMode.ADDNEW;
    }

    private boolean _AddNew() {
        int newId = AccessTask.addTask(this.productID, this.requiredQuantity, this.clientID);
        if (newId != -1) {
            this.id = newId;
            return true;
        }
        return false;
    }

    private boolean _Update() {
        return AccessTask.updateTask(
                this.id,
                this.productLineID,
                this.productID,
                this.requiredQuantity,
                this.achievedQuantity,
                (this.startDate != null) ? this.startDate.toString() : "",
                (this.endDate != null) ? this.endDate.toString() : "",
                this.statusID,
                this.clientID
        );
    }

    public boolean save() {
        return switch (this.mode) {
            case ADDNEW -> _AddNew();
            case UPDATE -> _Update();
            default -> false;
        };
    }


    public static Task findByID(int id) {
        String[] data = AccessTask.findTaskByID(id);
        if (data == null) return null;
        try {
            String lineIdStr = data[1];
            Integer lineId = (lineIdStr == null || lineIdStr.isEmpty()) ? null : Integer.parseInt(lineIdStr);
            return new Task(
                    Integer.parseInt(data[0]),
                    lineId,
                    Integer.parseInt(data[2]),
                    Integer.parseInt(data[3]),
                    Integer.parseInt(data[4]),
                    LocalDate.parse(data[5]),
                    data[6].isEmpty() ? null : LocalDate.parse(data[6]),
                    Integer.parseInt(data[7]),
                    Integer.parseInt(data[8])
            );
        } catch (Exception e) {
            ErrorLogger.logError(e);
            return null;
        }
    }

    public static List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        List<String[]> records = AccessTask.loadAllTasks();
        for (String[] record : records) {
            try {
                String lineIdStr = record[1];
                Integer lineId = (lineIdStr == null || lineIdStr.isEmpty()) ? null : Integer.parseInt(lineIdStr);
                tasks.add(new Task(
                        Integer.parseInt(record[0]),
                        lineId,
                        Integer.parseInt(record[2]),
                        Integer.parseInt(record[3]),
                        Integer.parseInt(record[4]),
                        LocalDate.parse(record[5]),
                        record[6].isEmpty() ? null : LocalDate.parse(record[6]),
                        Integer.parseInt(record[7]),
                        Integer.parseInt(record[8])
                ));
            } catch (Exception e) { ErrorLogger.logError(e); }
        }
        return tasks;
    }

    public void updateStatus(Status newStatus, String logMessage) {
        this.statusID = newStatus.getValue();
        log(">> Task #" + this.id + " status changed to " + newStatus + ". Reason: " + logMessage);
        save();
    }


    @Override
    public void run() {
        this.thread = Thread.currentThread();
        try {
            // 1. Initial Status Update
            updateStatus(Status.RUNNING, "Starting production on " + assignedLine.getName());

            Inventory inventory = Inventory.getInstance();

            // 2. Production Loop
            for (int i = achievedQuantity; i < requiredQuantity; i++) {

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Task cancelled by user.");
                }

                boolean canProduce = true;
                for (Map.Entry<Integer, Integer> req : product.getRequiredItems().entrySet()) {
                    Item item = inventory.findItemByIdInMemory(req.getKey()).orElse(null);
                    if (item == null || item.getAvailableQuantity() < req.getValue()) {
                        canProduce = false;
                        break;
                    }
                }

                if (!canProduce) {
                    updateStatus(Status.PAUSED, "Insufficient materials in Inventory. Pausing...");
                    return;
                }

                consumeMaterialsForOneUnit();
                Thread.sleep(1000);

                this.achievedQuantity++;
                save();
            }

            this.endDate = LocalDate.now();
            this.product.setQuantityInStock(this.product.getQuantityInStock() + this.requiredQuantity);
            this.product.save();

            updateStatus(Status.COMPLETED, "Production finished successfully.");

        } catch (InterruptedException e) {
            updateStatus(Status.CANCELLED, "Production stopped by user.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            updateStatus(Status.FAILED, "Error: " + e.getMessage());
            ErrorLogger.logError(e);
        } finally {
            if (this.statusID != Status.PAUSED.getValue() && this.assignedLine != null) {
                this.assignedLine.releaseTask();
            }
        }
    }

    private void consumeMaterialsForOneUnit() throws IllegalStateException {
        Inventory inventory = Inventory.getInstance();
        for (Map.Entry<Integer, Integer> req : product.getRequiredItems().entrySet()) {
            if (!inventory.consumeItemQuantity(req.getKey(), req.getValue())) {
                throw new IllegalStateException("Insufficient material: " + inventory.findItemByIdInMemory(req.getKey()).map(Item::getName).orElse("ID " + req.getKey()));
            }
        }
    }

    private void log(String message) { if (logger != null) logger.accept(message); }

    public String getRequiredItemsSummary() {
        if (product == null || product.getRequiredItems().isEmpty()) return "N/A";
        StringBuilder summary = new StringBuilder();
        Inventory inv = Inventory.getInstance();
        for (Map.Entry<Integer, Integer> req : product.getRequiredItems().entrySet()) {
            String itemName = inv.findItemByIdInMemory(req.getKey()).map(Item::getName).orElse("Unknown");
            summary.append(itemName).append(" (").append(req.getValue()).append("), ");
        }
        return summary.length() > 2 ? summary.substring(0, summary.length() - 2) : summary.toString();
    }

    public double getCompletionRate() {
        return (requiredQuantity == 0) ? 0.0 : ((double) achievedQuantity / requiredQuantity) * 100.0;
    }

    public int getAchievedQuantity() {
        return achievedQuantity;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public enum Status {
        PENDING(1), RUNNING(2), COMPLETED(3), PAUSED(4), CANCELLED(5), FAILED(6);
        private final int value;
        Status(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public Product getProduct() {
        return Product.findByID(this.productID);
    }

    public ProductLine getProductLine() {
        return ProductLine.findByID(this.productID);
    }


    // --- Getters and Setters ---
    public void setLogger(Consumer<String> logger) { this.logger = logger; }
    public void setProduct(Product product) { this.product = product; this.productID = product.getId(); }
    public void setRequiredQuantity(int quantity) { this.requiredQuantity = quantity; }
    public void setClientID(int clientID) { this.clientID = clientID; }
    public void setAssignedLine(ProductLine line) {
        this.assignedLine = line;
        this.productLineID = (line != null) ? line.getId() : null;
    }

    public int getId() { return id; }
    public int getStatusID() { return statusID; }
    public String getStatusName() { return AccessTask.findStatusNameById(this.statusID); }
    public Thread getThread() { return thread; }
    public Integer getProductLineID() { return productLineID; }
    public ProductLine getAssignedLine() { return assignedLine; }
    public LocalDate getStartDate() { return this.startDate; }
    public int getRequiredQuantity() { return this.requiredQuantity; }
}
