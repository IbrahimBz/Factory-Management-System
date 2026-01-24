package com.fsociety.factory.BusinessLayer.Production;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class TaskManager {

    private static TaskManager instance;
    private final List<Task> tasks;
    private final ProductionManager productionManager;

    private TaskManager() {
        this.tasks = new CopyOnWriteArrayList<>(Task.getAllTasks());
        this.productionManager = ProductionManager.getInstance();
    }

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }


    public void submitNewTaskToLine(Product product, int requiredQuantity, int clientID, ProductLine line) {
        if (line == null) {
            return;
        }

        Task newTask = new Task();
        newTask.setProduct(product);
        newTask.setRequiredQuantity(requiredQuantity);
        newTask.setClientID(clientID);

        if (!newTask.save()) {
            return;
        }

        this.tasks.add(newTask);

        assignAndExecuteTask(newTask, line);
    }

    public void cancelTask(int taskId) {
        findTaskById(taskId).ifPresent(task -> {
            if (task.getThread() != null) {
                task.getThread().interrupt();
            } else {
                task.updateStatus(Task.Status.CANCELLED, "Cancelled by user before starting.");
            }
        });
    }


    public void retryPendingAndPausedTasks() {
        List<Task> tasksToRetry = this.tasks.stream()
                .filter(t -> t.getStatusID() == 1 || t.getStatusID() == 4)
                .toList();

        if (!tasksToRetry.isEmpty()) {
            for (Task task : tasksToRetry) {
                submitExistingTask(task);
            }
        }
    }

    public void assignAndExecuteTask(Task task, ProductLine line) {
        if (line == null || !line.isTrulyAvailable()) {
            task.updateStatus(Task.Status.PENDING, "Assignment failed, line was not available.");
            return;
        }

        task.setAssignedLine(line);
        task.save();

        if (line.assignTask(task)) {
            productionManager.executeTask(task);
        } else {
            task.updateStatus(Task.Status.PAUSED, "Line became busy during assignment. Will retry.");
        }
    }

    public void submitExistingTask(Task task) {
        Optional<ProductLine> availableLine = findAvailableLine();
        availableLine.ifPresent(productLine -> assignAndExecuteTask(task, productLine));
    }


    private Optional<ProductLine> findAvailableLine() {
        return productionManager.getProductLines().stream()
                .filter(ProductLine::isTrulyAvailable)
                .findFirst();
    }

    // Getters
    public List<Task> getAllTasks() {
        return this.tasks;
    }

    public List<Task> getTasksByProductLine(int productLineId) {
        return this.tasks.stream()
                .filter(task -> task.getProductLineID() != null && task.getProductLineID() == productLineId)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByProduct(int productId) {
        return this.tasks.stream()
                .filter(task -> task.getProduct().getId() == productId)
                .collect(Collectors.toList());
    }

    public Optional<Task> findTaskById(int id) {
        return this.tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst();
    }

    public List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();

        for(Task task: tasks) {

            if(Objects.equals(task.getStatusName(), "Completed")) completedTasks.add(task);

        }

        return completedTasks;
    }


    public Product getTopProductInDetermineDate(LocalDate start, LocalDate end) {
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }

        Map<Integer, Integer> productTotals = new HashMap<>();
        Product topProduct = null;
        int maxQuantity = -1;

        for (Task task : tasks) {
            LocalDate taskDate = task.getEndDate();

            if (taskDate == null) continue;

            boolean isAfterStart = (start == null) || !taskDate.isBefore(start);
            boolean isBeforeEnd = (end == null) || !taskDate.isAfter(end);

            if (isAfterStart && isBeforeEnd) {
                int prodID = task.getProduct().getId();
                int currentTotal = productTotals.getOrDefault(prodID, 0);
                int newTotal = currentTotal + task.getAchievedQuantity();
                productTotals.put(prodID, newTotal);
            }
        }

        for (Map.Entry<Integer, Integer> entry : productTotals.entrySet()) {
            if (entry.getValue() > maxQuantity) {
                maxQuantity = entry.getValue();
                topProduct = Product.findByID(entry.getKey());
            }
        }

        return topProduct;
    }

}
