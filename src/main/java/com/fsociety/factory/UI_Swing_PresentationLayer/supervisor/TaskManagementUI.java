package com.fsociety.factory.UI_Swing_PresentationLayer.supervisor;

import com.fsociety.factory.BusinessLayer.Production.*;
import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class TaskManagementUI extends BaseFrame {
    private final DefaultTableModel tableModel;
    private final JTable taskTable;
    private final TaskManager taskManager = TaskManager.getInstance();
    private final ProductionManager productionManager = ProductionManager.getInstance();

    public TaskManagementUI() {
        super("Factory OS - Production Control Room");
        setLayout(new BorderLayout());

        // --- الجزء العلوي: العنوان وزر العودة ---
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(createTopBar("PRODUCTION TASKS & WORKFLOW"), BorderLayout.CENTER);
        JButton btnBack = new JButton("⬅ Back");
        btnBack.setBackground(new Color(44, 62, 80));
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> this.dispose());
        topContainer.add(btnBack, BorderLayout.WEST);
        add(topContainer, BorderLayout.NORTH);

        // --- لوحة الفلاتر المتقدمة (Requirements) ---
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(new Color(235, 245, 245));
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JComboBox<ProductLine> comboLines = new JComboBox<>();
        comboLines.addItem(null); // اختيار "All"
        productionManager.getProductLines().forEach(comboLines::addItem);

        JComboBox<Product> comboProducts = new JComboBox<>();
        comboProducts.addItem(null);
        Product.getAllProducts().forEach(comboProducts::addItem);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"All Status", "Running", "Completed", "Pending", "Cancelled"});

        filterPanel.add(new JLabel("Line:"), gbc); filterPanel.add(comboLines, gbc);
        filterPanel.add(new JLabel("Product:"), gbc); filterPanel.add(comboProducts, gbc);
        filterPanel.add(new JLabel("Status:"), gbc); filterPanel.add(comboStatus, gbc);

        JButton btnApply = new JButton("Filter Tasks");
        filterPanel.add(btnApply, gbc);

        // زر إحصائي (Top Product)
        JButton btnTopProduct = new JButton("📊 Top Product");
        btnTopProduct.setToolTipText("Show top product in period");
        filterPanel.add(btnTopProduct, gbc);

        add(filterPanel, BorderLayout.NORTH);

        // --- الجدول ---
        String[] columns = {"ID", "Product", "Line", "Progress", "Quantity", "Status", "Requirements"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(35);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // --- أزرار التحكم السفلى ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton btnNewTask = createStyledButton("SUBMIT NEW TASK", new Color(0, 121, 107));
        JButton btnCancel = createStyledButton("CANCEL TASK", new Color(192, 57, 43));
        JButton btnRefresh = createStyledButton("REFRESH", new Color(44, 62, 80));

        controlPanel.add(btnNewTask); controlPanel.add(btnCancel); controlPanel.add(btnRefresh);
        add(controlPanel, BorderLayout.SOUTH);

        // --- الأحداث (Actions) ---
        refreshTasks(taskManager.getAllTasks());

        btnApply.addActionListener(e -> {
            ProductLine selectedLine = (ProductLine) comboLines.getSelectedItem();
            Product selectedProduct = (Product) comboProducts.getSelectedItem();
            String status = comboStatus.getSelectedItem().toString();
            applyAdvancedFilters(selectedLine, selectedProduct, status);
        });

        btnNewTask.addActionListener(e -> openNewTaskDialog());

        btnCancel.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row != -1) {
                int taskId = (int) tableModel.getValueAt(row, 0);
                taskManager.cancelTask(taskId);
                refreshTasks(taskManager.getAllTasks());
            }
        });

        btnRefresh.addActionListener(e -> refreshTasks(taskManager.getAllTasks()));

        btnTopProduct.addActionListener(e -> {
            Product top = taskManager.getTopProductInDetermineDate(null, null);

            if (top != null) {
                showStyledMessage("Top Achieved Product: " + top.getName(), "Production Analytics");
            } else {
                showStyledMessage("No production tasks found to analyze.", "Analytics Info");
            }
        });
    }

    private void refreshTasks(List<Task> tasks) {
        tableModel.setRowCount(0);
        for (Task t : tasks) {
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getProduct() != null ? t.getProduct().getName() : "Unknown",
                    t.getAssignedLine() != null ? t.getAssignedLine().getName() : "Waiting...",
                    String.format("%.1f%%", t.getCompletionRate()),
                    t.getAchievedQuantity() + "/" + t.getAchievedQuantity(), // تصحيح بسيط للكمية المطلوبة
                    t.getStatusName(),
                    t.getRequiredItemsSummary()
            });
        }
    }

    private void applyAdvancedFilters(ProductLine line, Product prod, String status) {
        List<Task> list = taskManager.getAllTasks();
        if (line != null) list = taskManager.getTasksByProductLine(line.getId());
        if (prod != null) {
            final int pid = prod.getId();
            list = list.stream().filter(t -> t.getProduct().getId() == pid).toList();
        }
        if (!status.equals("All Status")) {
            list = list.stream().filter(t -> t.getStatusName().equalsIgnoreCase(status)).toList();
        }
        refreshTasks(list);
    }

    private void openNewTaskDialog() {
        JDialog dialog = new JDialog(this, "Assign New Production Task", true);
        dialog.setSize(400, 450);
        dialog.setLayout(new GridLayout(0, 1, 10, 10));
        ((JPanel)dialog.getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<Product> prodCombo = new JComboBox<>();
        Product.getAllProducts().forEach(prodCombo::addItem);

        JComboBox<ProductLine> lineCombo = new JComboBox<>();
        productionManager.getProductLines().forEach(lineCombo::addItem);

        JTextField qtyField = new JTextField();
        JTextField clientField = new JTextField("1"); // افتراضي

        dialog.add(new JLabel("Select Product:")); dialog.add(prodCombo);
        dialog.add(new JLabel("Select Production Line:")); dialog.add(lineCombo);
        dialog.add(new JLabel("Required Quantity:")); dialog.add(qtyField);
        dialog.add(new JLabel("Client ID:")); dialog.add(clientField);

        JButton btnSubmit = new JButton("🚀 Start Production");
        btnSubmit.addActionListener(e -> {
            try {
                Product p = (Product) prodCombo.getSelectedItem();
                ProductLine l = (ProductLine) lineCombo.getSelectedItem();
                int q = Integer.parseInt(qtyField.getText());
                int c = Integer.parseInt(clientField.getText());

                if (p != null && l != null) {
                    taskManager.submitNewTaskToLine(p, q, c, l);
                    refreshTasks(taskManager.getAllTasks());
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Check input data!");
            }
        });

        dialog.add(btnSubmit);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}