package com.fsociety.factory.BusinessLayer.Production;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProductionManager {

    private static ProductionManager instance;
    private final ExecutorService executorService; // تم تغيير الاسم ليكون أوضح
    private final List<ProductLine> productLines;

    private ProductionManager() {
        this.productLines = ProductLine.getAllProductLines();
        // استخدام CachedThreadPool أكثر مرونة، حيث ينشئ threads عند الحاجة
        this.executorService = Executors.newCachedThreadPool();
    }

    public static synchronized ProductionManager getInstance() {
        if (instance == null) {
            instance = new ProductionManager();
        }
        return instance;
    }

    /**
     * هذه الدالة الآن مسؤولة فقط عن تسليم المهمة إلى الـ pool لتشغيلها.
     * منطق الحجز والتعيين يحدث في TaskManager.
     */
    public void executeTask(Runnable task) {
        if (!executorService.isShutdown()) {
            executorService.submit(task);
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            // حفظ حالة المخزون عند إغلاق البرنامج
            Inventory.getInstance().persistChanges();
        }
    }

    public List<ProductLine> getProductLines() {
        return this.productLines;
    }

    public ProductLine findLineById(int lineId) {
        return this.productLines.stream()
                .filter(line -> line.getId() == lineId)
                .findFirst()
                .orElse(null);
    }
}
