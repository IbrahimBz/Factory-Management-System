package com.fsociety.factory.presentationLayer;

import com.fsociety.factory.BusinessLayer.Production.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class SupervisorDashboard extends JFrame {

    private final TaskManager taskManager;
    private JComboBox<Product> productFilterCombo;
    private JComboBox<ProductLine> lineFilterCombo;
    private DefaultTableModel tasksTableModel;
    private JTable tasksTable;
    private JTextArea logArea;

    public SupervisorDashboard() {
        this.taskManager = TaskManager.getInstance();
        this.taskManager.setLogger(this::log);

        setTitle("Supervisor Dashboard");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                ProductionManager.getInstance().shutdown();
            }
        });
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Task Management", createTaskManagementPanel());
        tabbedPane.addTab("Finished Products", createFinishedProductsPanel());
        add(tabbedPane, BorderLayout.CENTER);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Live Log"));
        logScrollPane.setPreferredSize(new Dimension(getWidth(), 200));
        add(logScrollPane, BorderLayout.SOUTH);

        new Timer(2000, e -> refreshTasksTable()).start();
        SwingUtilities.invokeLater(taskManager::retryPendingAndPausedTasks);
    }

    private JPanel createTaskManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- Filter Panel ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        productFilterCombo = new JComboBox<>();
        lineFilterCombo = new JComboBox<>();
        JButton applyFilterButton = new JButton("Apply Filters");
        JButton clearFilterButton = new JButton("Clear");
        setupFilterCombos();
        filterPanel.add(new JLabel("Filter by Product:"));
        filterPanel.add(productFilterCombo);
        filterPanel.add(new JLabel("Filter by Line:"));
        filterPanel.add(lineFilterCombo);
        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);
        panel.add(filterPanel, BorderLayout.NORTH);

        // --- Tasks Table ---
        String[] taskColumns = {"ID", "Product", "Assigned Line", "Status", "Progress"};
        tasksTableModel = new DefaultTableModel(taskColumns, 0);
        tasksTable = new JTable(tasksTableModel);
        panel.add(new JScrollPane(tasksTable), BorderLayout.CENTER);

        // --- Context Menu ---
        JPopupMenu taskMenu = new JPopupMenu();
        JMenu assignMenu = new JMenu("Assign to Line");
        JMenuItem cancelItem = new JMenuItem("Cancel Task");
        JMenuItem retryItem = new JMenuItem("Retry (Auto-Assign)");
        taskMenu.add(assignMenu);
        taskMenu.add(retryItem);
        taskMenu.addSeparator();
        taskMenu.add(cancelItem);

        tasksTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tasksTable.rowAtPoint(e.getPoint());
                    if (row == -1) return;
                    tasksTable.setRowSelectionInterval(row, row);

                    int taskId = (int) tasksTableModel.getValueAt(row, 0);
                    Task selectedTask = taskManager.findTaskById(taskId).orElse(null);

                    if (selectedTask != null && (selectedTask.getStatusID() == 1 || selectedTask.getStatusID() == 4)) { // PENDING or PAUSED
                        assignMenu.setEnabled(true);
                        retryItem.setEnabled(true);
                        populateAssignMenu(assignMenu, selectedTask);
                    } else {
                        assignMenu.setEnabled(false);
                        retryItem.setEnabled(false);
                    }
                    taskMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        retryItem.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = (int) tasksTableModel.getValueAt(selectedRow, 0);
                taskManager.findTaskById(taskId).ifPresent(taskManager::submitExistingTask);
            }
        });

        cancelItem.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = (int) tasksTableModel.getValueAt(selectedRow, 0);
                taskManager.cancelTask(taskId);
            }
        });

        // --- Control Panel ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Product> productCreateCombo = new JComboBox<>();
        setupProductCreateCombo(productCreateCombo);
        JTextField quantityField = new JTextField("10", 5);
        JButton createTaskButton = new JButton("Add Task");
        controlPanel.add(new JLabel("New Task:"));
        controlPanel.add(productCreateCombo);
        controlPanel.add(new JLabel("Quantity:"));
        controlPanel.add(quantityField);
        controlPanel.add(createTaskButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

// في كلاس SupervisorDashboard.java، داخل دالة createTaskManagementPanel()

// ---  المنطق الجديد والمحسّن لزر "Add Task" ---
        createTaskButton.addActionListener(e -> {
            Product selectedProduct = (Product) productCreateCombo.getSelectedItem();
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ---  الخطوة الجديدة: التحقق من توفر المواد الخام ---
            if (!selectedProduct.canProduceQuantity(quantity)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot create task: Insufficient raw materials in inventory for the required quantity.",
                        "Inventory Alert",
                        JOptionPane.WARNING_MESSAGE);
                return; // أوقف العملية هنا
            }

            // 1. الحصول على قائمة بخطوط الإنتاج المتاحة (هذا الكود يعمل فقط إذا كان التحقق أعلاه ناجحاً)
            List<ProductLine> availableLines = ProductionManager.getInstance().getProductLines().stream()
                    .filter(ProductLine::isTrulyAvailable)
                    .collect(Collectors.toList());

            if (availableLines.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Materials are available, but there are no free production lines.", "No Lines Available", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. عرض مربع حوار للمستخدم ليختار خط الإنتاج
            ProductLine chosenLine = (ProductLine) JOptionPane.showInputDialog(
                    this,
                    "Select a production line for this task:",
                    "Assign Production Line",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    availableLines.toArray(),
                    availableLines.get(0)
            );

            // 3. إذا اختار المستخدم خطاً، قم بإنشاء المهمة وتعيينها
            if (chosenLine != null) {
                taskManager.submitNewTaskToLine(selectedProduct, quantity, 1, chosenLine);
            }
        });


        applyFilterButton.addActionListener(e -> refreshTasksTable());
        clearFilterButton.addActionListener(e -> {
            productFilterCombo.setSelectedItem(null);
            lineFilterCombo.setSelectedItem(null);
            refreshTasksTable();
        });

        return panel;
    }

    private void populateAssignMenu(JMenu menu, Task task) {
        menu.removeAll();
        List<ProductLine> availableLines = ProductionManager.getInstance().getProductLines().stream()
                .filter(ProductLine::isTrulyAvailable) // --- استخدام الدالة الجديدة ---
                .collect(Collectors.toList());

        if (availableLines.isEmpty()) {
            menu.add(new JMenuItem("No available lines")).setEnabled(false);
        } else {
            for (ProductLine line : availableLines) {
                JMenuItem lineItem = new JMenuItem(line.getName());
                lineItem.addActionListener(e -> taskManager.assignAndExecuteTask(task, line));
                menu.add(lineItem);
            }
        }
    }

    private JPanel createFinishedProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel productsTableModel = new DefaultTableModel(new String[]{"ID", "Product Name", "Quantity in Stock"}, 0);
        JTable productsTable = new JTable(productsTableModel);
        panel.add(new JScrollPane(productsTable), BorderLayout.CENTER);

        new Timer(3000, e -> {
            productsTableModel.setRowCount(0);
            List<Product> products = Product.getAllProducts();
            for (Product p : products) {
                productsTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getQuantityInStock()});
            }
        }).start();
        return panel;
    }

    private void refreshTasksTable() {
        tasksTableModel.setRowCount(0);
        List<Task> tasks;
        Product selectedProduct = (Product) productFilterCombo.getSelectedItem();
        ProductLine selectedLine = (ProductLine) lineFilterCombo.getSelectedItem();

        if (selectedProduct != null) {
            tasks = taskManager.getTasksByProduct(selectedProduct.getId());
        } else if (selectedLine != null) {
            tasks = taskManager.getTasksByProductLine(selectedLine.getId());
        } else {
            tasks = taskManager.getAllTasks();
        }

        for (Task task : tasks) {
            ProductLine assignedLine = task.getAssignedLine();
            String lineName = (assignedLine != null) ? assignedLine.getName() : "N/A";
            tasksTableModel.addRow(new Object[]{
                    task.getId(),
                    task.getProduct().getName(),
                    lineName,
                    task.getStatusName(),
                    String.format("%.1f%%", task.getCompletionRate())
            });
        }
    }

    private void setupFilterCombos() {
        productFilterCombo.removeAllItems();
        productFilterCombo.addItem(null);
        Product.getAllProducts().forEach(productFilterCombo::addItem);

        lineFilterCombo.removeAllItems();
        lineFilterCombo.addItem(null);
        ProductionManager.getInstance().getProductLines().forEach(lineFilterCombo::addItem);
    }

    private void setupProductCreateCombo(JComboBox<Product> combo) {
        combo.removeAllItems();
        Product.getAllProducts().forEach(combo::addItem);
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            if (logArea.getDocument().getLength() > 20000) logArea.setText("");
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
