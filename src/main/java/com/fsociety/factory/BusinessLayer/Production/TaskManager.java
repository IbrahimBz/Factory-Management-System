package com.fsociety.factory.BusinessLayer.Production;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TaskManager {

    private static TaskManager instance;
    private final List<Task> tasks;
    private final ProductionManager productionManager;
    private Consumer<String> logger = (msg) -> {};

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
            logger.accept("!! ERROR: A specific production line must be provided.");
            return;
        }

        Task newTask = new Task();
        newTask.setProduct(product);
        newTask.setRequiredQuantity(requiredQuantity);
        newTask.setClientID(clientID);
        newTask.setLogger(this.logger);

        if (!newTask.save()) {
            logger.accept("!! FATAL: Could not save new task to the data file.");
            return;
        }

        this.tasks.add(newTask);
        logger.accept(">> New Task #" + newTask.getId() + " created for product '" + product.getName() + "'.");

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


    public void setLogger(Consumer<String> logger) {
        this.logger = logger;
        this.tasks.forEach(task -> task.setLogger(logger));
    }

    public void retryPendingAndPausedTasks() {
        List<Task> tasksToRetry = this.tasks.stream()
                .filter(t -> t.getStatusID() == 1 || t.getStatusID() == 4)
                .collect(Collectors.toList());

        if (!tasksToRetry.isEmpty()) {
            logger.accept(">> Retrying " + tasksToRetry.size() + " pending/paused tasks...");
            for (Task task : tasksToRetry) {
                submitExistingTask(task);
            }
        }
    }

    public void assignAndExecuteTask(Task task, ProductLine line) {
        if (line == null || !line.isTrulyAvailable()) {
            logger.accept("!! ERROR: Cannot assign Task #" + task.getId() + " to line '" + (line != null ? line.getName() : "null") + "'. Line is not available or is busy.");
            task.updateStatus(Task.Status.PENDING, "Assignment failed, line was not available.");
            return;
        }

        task.setAssignedLine(line);
        task.save();

        // --- استخدام assignTask الذرية لمحاولة حجز الخط ---
        if (line.assignTask(task)) {
            logger.accept(">> Line '" + line.getName() + "' has been successfully reserved for Task #" + task.getId() + ".");
            productionManager.executeTask(task);
        } else {
            // هذا يحدث في حالة السباق النادرة حيث يحجز thread آخر الخط في نفس اللحظة
            task.updateStatus(Task.Status.PAUSED, "Line became busy during assignment. Will retry.");
            logger.accept("!! WARNING: Line '" + line.getName() + "' was busy. Task #" + task.getId() + " is paused.");
        }
    }

    public boolean submitExistingTask(Task task) {
        Optional<ProductLine> availableLine = findAvailableLine();
        if (availableLine.isPresent()) {
            assignAndExecuteTask(task, availableLine.get());
            return true;
        } else {
            logger.accept(">> WARNING: No available lines for Task #" + task.getId() + ". It will remain in its current state.");
            return false;
        }
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

    public List<Task> getRunningTasks() {
        List<Task> runningTasks = new ArrayList<>();

        for(Task task: tasks) {

            if(task.getStatusName() == "Running") runningTasks.add(task);

        }

        return runningTasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();

        for(Task task: tasks) {

            if(Objects.equals(task.getStatusName(), "Completed")) completedTasks.add(task);

        }

        return completedTasks;
    }

    public List<ProductLine> getProductLinesByProduct(int productId) {


        List<ProductLine> productLines = new ArrayList<>();


        for(Task task: getTasksByProduct(productId)) {

            productLines.add(task.getProductLine());

        }

        return productLines;
    }

    public List<Product> getProductsByline(int productId){
        List<Product> products = new ArrayList<>();



        for(Task task: getTasksByProduct(productId)) {

            if(task.getProductLineID() == productId)
                products.add(task.getProduct());


        }


        return products;
    }

    public List<Product> getAllProducts() {
        return Product.getAllProducts();
    }

    public Product getTopProductInDetermineDate(LocalDate first , LocalDate end) {

        List<Product> products = new ArrayList<>();


        int max = tasks.get(0).getProduct().getQuantityInStock();

        for(Task task: getAllTasks() ){

            max = Math.max(max, task.getAchievedQuantity());





        }


        return products.get(0);



    }

}
