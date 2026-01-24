package com.fsociety.factory.presentationLayer;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import com.fsociety.factory.BusinessLayer.Inventory.Item;
import com.fsociety.factory.BusinessLayer.Production.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class AdminDashboardUI extends JFrame {

    private final TaskManager taskManager;
    private final Inventory inventory;

    private JTextArea logArea;
    private JTable tasksTable;
    private DefaultTableModel tasksTableModel;
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;

    public AdminDashboardUI() {
        this.taskManager = TaskManager.getInstance();
        this.inventory = Inventory.getInstance();
        this.taskManager.setLogger(this::log);

        setTitle("Factory Admin Dashboard");
        // --- زيادة حجم النافذة ---
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                log(">> System shutting down...");
                ProductionManager.getInstance().shutdown();
            }
        });
        setLayout(new BorderLayout(10, 10));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setResizeWeight(0.65);
        add(mainSplit, BorderLayout.CENTER);

        JTabbedPane topTabbedPane = new JTabbedPane();
        mainSplit.setTopComponent(topTabbedPane);

        JPanel productionPanel = createProductionPanel();
        topTabbedPane.addTab("Production Management", productionPanel);

        JPanel inventoryPanel = createInventoryPanel();
        topTabbedPane.addTab("Inventory Management", inventoryPanel);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Live Log"));
        mainSplit.setBottomComponent(logScrollPane);

        log("UI Initialized. Loading data...");
        refreshTables();

        Timer timer = new Timer(2000, e -> refreshTables());
        timer.start();

        SwingUtilities.invokeLater(taskManager::retryPendingAndPausedTasks);
    }

    private JPanel createProductionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- تحديث أعمدة جدول المهام ---
        String[] taskColumns = {"ID", "Product", "Status", "Progress", "Required Items", "Reserved Items"};
        tasksTableModel = new DefaultTableModel(taskColumns, 0);
        tasksTable = new JTable(tasksTableModel);
        panel.add(new JScrollPane(tasksTable), BorderLayout.CENTER);

        // ... (بقية كود لوحة التحكم وقائمة السياق يبقى كما هو)
        JPopupMenu taskMenu = new JPopupMenu();
        JMenuItem cancelItem = new JMenuItem("Cancel Task");
        JMenuItem retryItem = new JMenuItem("Retry Task");
        taskMenu.add(cancelItem);
        taskMenu.add(retryItem);

        tasksTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tasksTable.rowAtPoint(e.getPoint());
                    tasksTable.setRowSelectionInterval(row, row);
                    taskMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        cancelItem.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = (int) tasksTableModel.getValueAt(selectedRow, 0);
                taskManager.cancelTask(taskId);
                log(">> Sent cancellation request for Task #" + taskId);
            }
        });

        retryItem.addActionListener(e -> {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = (int) tasksTableModel.getValueAt(selectedRow, 0);
                taskManager.findTaskById(taskId).ifPresent(taskManager::submitExistingTask);
            }
        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Product> productComboBox = new JComboBox<>();
        setupProductComboBox(productComboBox);
        JTextField quantityField = new JTextField("10", 5);
        JButton createTaskButton = new JButton("Create New Task");
        controlPanel.add(new JLabel("Product:"));
        controlPanel.add(productComboBox);
        controlPanel.add(new JLabel("Quantity:"));
        controlPanel.add(quantityField);
        controlPanel.add(createTaskButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        createTaskButton.addActionListener(e -> {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            if (selectedProduct == null) return;
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                taskManager.submitNewTaskToLine(selectedProduct, quantity, 1, ProductLine.findByID(0));
            } catch (NumberFormatException ex) {
                showError("Invalid quantity.");
            }
        });
        return panel;
    }

    private JPanel createInventoryPanel() {
        // ... (هذه الدالة تبقى كما هي تماماً)
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] inventoryColumns = {"ID", "Item Name", "Category", "Price", "Available Qty", "Min Qty"};
        inventoryTableModel = new DefaultTableModel(inventoryColumns, 0);
        inventoryTable = new JTable(inventoryTableModel);
        panel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Add Item");
        JButton editItemButton = new JButton("Edit Selected Item");
        JButton deleteItemButton = new JButton("Delete Selected Item");
        controlPanel.add(addItemButton);
        controlPanel.add(editItemButton);
        controlPanel.add(deleteItemButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        addItemButton.addActionListener(e -> showItemDialog(null));
        editItemButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow != -1) {
                int itemId = (int) inventoryTableModel.getValueAt(selectedRow, 0);
                inventory.findItemByIdInMemory(itemId).ifPresent(item -> showItemDialog(item));
            } else {
                showError("Please select an item to edit.");
            }
        });
        deleteItemButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow != -1) {
                int itemId = (int) inventoryTableModel.getValueAt(selectedRow, 0);
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete item #" + itemId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    inventory.deleteItem(itemId);
                }
            } else {
                showError("Please select an item to delete.");
            }
        });
        return panel;
    }

    private void refreshTables() {
        // --- تحديث منطق ملء جدول المهام ---
        tasksTableModel.setRowCount(0);
        for (Task task : taskManager.getAllTasks()) {
            Vector<Object> row = new Vector<>();
            row.add(task.getId());
            row.add(task.getProduct().getName());
            row.add(task.getStatusName());
            row.add(String.format("%.1f%%", task.getCompletionRate()));
            row.add(task.getRequiredItemsSummary()); // العمود الجديد
            row.add("Not Implemented"); // عمود المواد المحجوزة
            tasksTableModel.addRow(row);
        }

        // ... (تحديث جدول المخزون يبقى كما هو)
        inventoryTableModel.setRowCount(0);
        for (Item item : inventory.getAllItems()) {
            Vector<Object> row = new Vector<>();
            row.add(item.getId());
            row.add(item.getName());
            row.add(item.getCategoryName());
            row.add(item.getPrice());
            row.add(item.getAvailableQuantity());
            row.add(item.getMinAllowedQuantity());
            inventoryTableModel.addRow(row);
        }
    }

    // ... (بقية الدوال المساعدة تبقى كما هي)
    private void showItemDialog(Item item) {
        boolean isNew = (item == null);
        JTextField nameField = new JTextField(isNew ? "" : item.getName());
        JTextField categoryField = new JTextField(isNew ? "0" : String.valueOf(item.getCategoryID()));
        JTextField priceField = new JTextField(isNew ? "0.0" : String.valueOf(item.getPrice()));
        JTextField qtyField = new JTextField(isNew ? "0" : String.valueOf(item.getAvailableQuantity()));
        JTextField minQtyField = new JTextField(isNew ? "0" : String.valueOf(item.getMinAllowedQuantity()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Category ID:")); panel.add(categoryField);
        panel.add(new JLabel("Price:")); panel.add(priceField);
        panel.add(new JLabel("Available Quantity:")); panel.add(qtyField);
        panel.add(new JLabel("Minimum Quantity:")); panel.add(minQtyField);

        int result = JOptionPane.showConfirmDialog(this, panel, isNew ? "Add New Item" : "Edit Item #" + item.getId(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Item itemToSave = isNew ? new Item() : item;
                itemToSave.setName(nameField.getText());
                itemToSave.setCategoryID(Integer.parseInt(categoryField.getText()));
                itemToSave.setPrice(Double.parseDouble(priceField.getText()));
                itemToSave.setAvailableQuantity(Integer.parseInt(qtyField.getText()));
                itemToSave.setMinAllowedQuantity(Integer.parseInt(minQtyField.getText()));

                if (isNew) {
                    inventory.addNewItem(itemToSave);
                } else {
                    inventory.updateItem(itemToSave);
                }
            } catch (NumberFormatException e) {
                showError("Invalid number format in one of the fields.");
            }
        }
    }

    private void setupProductComboBox(JComboBox<Product> comboBox) {
        List<Product> products = Product.getAllProducts();
        for (Product p : products) comboBox.addItem(p);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) setText(((Product) value).getName());
                return this;
            }
        });
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            if (logArea.getDocument().getLength() > 20000) logArea.setText("");
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        Inventory.getInstance();
        TaskManager.getInstance();
        SwingUtilities.invokeLater(() -> new AdminDashboardUI().setVisible(true));
    }
}
