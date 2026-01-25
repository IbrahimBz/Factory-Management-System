package com.fsociety.factory.UI_Swing_PresentationLayer.supervisor;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import com.fsociety.factory.BusinessLayer.Inventory.Item;
import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManagementUI extends BaseFrame {
    private final DefaultTableModel tableModel;
    private final JTable inventoryTable;
    private final Inventory inventoryLogic = Inventory.getInstance();

    public InventoryManagementUI() {
        super("Factory OS - Inventory Control System");
        setLayout(new BorderLayout());

        // --- Header Section ---
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(createTopBar("MATERIAL & PRODUCT INVENTORY"), BorderLayout.CENTER);

        JButton btnBack = new JButton("⬅ Back");
        btnBack.setBackground(new Color(44, 62, 80));
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> this.dispose());
        topContainer.add(btnBack, BorderLayout.WEST);
        add(topContainer, BorderLayout.NORTH);

        // --- Filter Panel (Requirement: Search by Name, Category, Status) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(new Color(230, 240, 240));

        JTextField txtSearch = new JTextField(12);
        JComboBox<String> comboStatus = new JComboBox<>(new String[]{
                "All Items", "Available", "Empty (0)", "Under Minimum"
        });
        JButton btnApplyFilter = new JButton("🔍 Apply Filter");

        filterPanel.add(new JLabel("Search Name:")); filterPanel.add(txtSearch);
        filterPanel.add(new JLabel("Stock Status:")); filterPanel.add(comboStatus);
        filterPanel.add(btnApplyFilter);

        // --- Table Section ---
        String[] columns = {"ID", "Item Name", "Category", "Price", "Available", "Min Level", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(30);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- Control Buttons ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton btnAdd = createStyledButton("ADD NEW ITEM", new Color(0, 121, 107));
        JButton btnUpdate = createStyledButton("UPDATE SELECTED", new Color(44, 62, 80));
        JButton btnDelete = createStyledButton("DELETE ITEM", new Color(192, 57, 43));

        controlPanel.add(btnAdd); controlPanel.add(btnUpdate); controlPanel.add(btnDelete);
        add(controlPanel, BorderLayout.SOUTH);

        // --- Logic & Events ---
        refreshTable();

        btnAdd.addActionListener(e -> openItemDialog(null));

        btnUpdate.addActionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);
                inventoryLogic.findItemByIdInMemory(id).ifPresent(this::openItemDialog);
            } else {
                showStyledMessage("Please select an item to update", "Selection Required");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?");
                if (confirm == JOptionPane.YES_OPTION) {
                    if (inventoryLogic.deleteItem(id)) {
                        refreshTable();
                    }
                }
            }
        });

        btnApplyFilter.addActionListener(e -> applyFilters(
                txtSearch.getText().toLowerCase(),
                comboStatus.getSelectedItem().toString()
        ));
    }

    private void refreshTable() {
        populateTable(inventoryLogic.getAllItems());
    }

    private void populateTable(List<Item> items) {
        tableModel.setRowCount(0);
        for (Item item : items) {
            String status = "OK";
            if (item.getAvailableQuantity() <= 0) status = "EMPTY";
            else if (item.getAvailableQuantity() < item.getMinAllowedQuantity()) status = "LOW STOCK";

            tableModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getCategoryName(), // يستخدم ميثود getCategoryName الجديدة
                    item.getPrice(),
                    item.getAvailableQuantity(),
                    item.getMinAllowedQuantity(),
                    status
            });
        }
    }

    private void applyFilters(String name, String status) {
        List<Item> filtered = inventoryLogic.getAllItems().stream()
                .filter(i -> i.getName().toLowerCase().contains(name))
                .filter(i -> {
                    if (status.equals("All Items")) return true;
                    if (status.equals("Empty (0)")) return i.getAvailableQuantity() <= 0;
                    if (status.equals("Under Minimum")) return i.getAvailableQuantity() < i.getMinAllowedQuantity();
                    return i.getAvailableQuantity() > 0;
                })
                .collect(Collectors.toList());
        populateTable(filtered);
    }

    // --- Dynamic Dialog for Add/Update ---
    private void openItemDialog(Item itemToEdit) {
        boolean isUpdateMode = (itemToEdit != null);
        JDialog dialog = new JDialog(this, isUpdateMode ? "Update Item Details" : "Register New Item", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        JTextField nameField = new JTextField(isUpdateMode ? itemToEdit.getName() : "", 15);
        JTextField categoryIdField = new JTextField(isUpdateMode ? String.valueOf(itemToEdit.getCategoryID()) : "", 15);
        JTextField priceField = new JTextField(isUpdateMode ? String.valueOf(itemToEdit.getPrice()) : "", 15);
        JTextField qtyField = new JTextField(isUpdateMode ? String.valueOf(itemToEdit.getAvailableQuantity()) : "", 15);
        JTextField minField = new JTextField(isUpdateMode ? String.valueOf(itemToEdit.getMinAllowedQuantity()) : "", 15);

        // Layout Adding
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Category ID:"), gbc);
        gbc.gridx = 1; dialog.add(categoryIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; dialog.add(priceField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; dialog.add(qtyField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Min Allowed:"), gbc);
        gbc.gridx = 1; dialog.add(minField, gbc);

        JButton btnSave = new JButton(isUpdateMode ? "Apply Changes" : "Save Item");
        btnSave.setBackground(new Color(0, 121, 107));
        btnSave.setForeground(Color.WHITE);

        btnSave.addActionListener(e -> {
            try {
                Item item;
                if (isUpdateMode) {
                    item = itemToEdit;
                } else {
                    item = new Item(); // سيعين الـ mode إلى ADDNEW تلقائياً بناءً على الـ Constructor
                }

                item.setName(nameField.getText());
                item.setCategoryID(Integer.parseInt(categoryIdField.getText()));
                item.setPrice(Double.parseDouble(priceField.getText()));
                item.setAvailableQuantity(Integer.parseInt(qtyField.getText()));
                item.setMinAllowedQuantity(Integer.parseInt(minField.getText()));

                boolean success = isUpdateMode ? inventoryLogic.updateItem(item) : inventoryLogic.addNewItem(item);

                if (success) {
                    refreshTable();
                    dialog.dispose();
                } else {
                    showStyledMessage("Failed to save. Check data source.", "Error");
                }
            } catch (NumberFormatException ex) {
                showStyledMessage("Please ensure IDs and quantities are numbers.", "Input Error");
            }
        });

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        dialog.add(btnSave, gbc);
        dialog.setVisible(true);
    }
}