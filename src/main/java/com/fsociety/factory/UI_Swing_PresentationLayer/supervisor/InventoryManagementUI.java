package com.fsociety.factory.UI_Swing_PresentationLayer.supervisor;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventoryManagementUI extends BaseFrame {
    private final DefaultTableModel tableModel;

    public InventoryManagementUI() {
        super("Factory OS - Inventory Control System");
        setLayout(new BorderLayout());

        // --- الجزء العلوي: العنوان ---
        add(createTopBar("MATERIAL & PRODUCT INVENTORY"), BorderLayout.NORTH);

        // --- الجزء الأوسط: الجدول ---
        String[] columns = {"Item ID", "Item Name", "Category", "Quantity", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable inventoryTable = new JTable(tableModel);

        // تحسين مظهر الجدول
        inventoryTable.setRowHeight(30);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- الجزء السفلي: أزرار التحكم ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(240, 248, 248));

        JButton btnAdd = createStyledButton("ADD ITEM", new Color(0, 121, 107));
        JButton btnUpdate = createStyledButton("UPDATE STOCK", new Color(44, 62, 80));
        JButton btnDelete = createStyledButton("REMOVE", new Color(192, 57, 43));
        JButton btnBack = createStyledButton("BACK TO MENU", Color.GRAY);

        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnDelete);
        controlPanel.add(btnBack);

        add(controlPanel, BorderLayout.SOUTH);

        // إضافة بيانات تجريبية مبدئياً
        loadDummyData();

        // حدث العودة
        btnBack.addActionListener(e -> {
            this.dispose(); // تعود للـ Dashboard المفتوحة مسبقاً
        });

    }

    private void loadDummyData() {
        tableModel.addRow(new Object[]{"M-101", "Steel Sheets", "Raw Material", "500 kg", "In Stock"});
        tableModel.addRow(new Object[]{"P-502", "Engine Blocks", "Parts", "12 Units", "Low Stock"});
        tableModel.addRow(new Object[]{"M-202", "Hydraulic Oil", "Liquids", "200 Liters", "In Stock"});
    }
}