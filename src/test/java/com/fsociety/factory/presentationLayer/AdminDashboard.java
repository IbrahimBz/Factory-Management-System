package com.fsociety.factory.presentationLayer;

import com.fsociety.factory.BusinessLayer.Production.ProductLine;
import com.fsociety.factory.BusinessLayer.Production.ProductionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private DefaultTableModel linesTableModel;
    private JTable linesTable;
    private JTextArea notesArea;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Table Panel ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Production Lines Performance & Status"));

        String[] lineColumns = {"ID", "Line Name", "Status", "Notes"};
        linesTableModel = new DefaultTableModel(lineColumns, 0);
        linesTable = new JTable(linesTableModel);
        tablePanel.add(new JScrollPane(linesTable), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // --- Notes & Control Panel ---
        JPanel eastPanel = new JPanel(new BorderLayout(10, 10));
        notesArea = new JTextArea(10, 25);
        notesArea.setBorder(BorderFactory.createTitledBorder("Notes for Selected Line"));
        eastPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Admin Actions"));
        JButton updateStatusButton = new JButton("Update Line Status");
        JButton saveNotesButton = new JButton("Save Notes");
        JButton addLineButton = new JButton("Add New Line");

        controlPanel.add(addLineButton);
        controlPanel.add(updateStatusButton);
        controlPanel.add(saveNotesButton);
        eastPanel.add(controlPanel, BorderLayout.SOUTH);

        add(eastPanel, BorderLayout.EAST);

        // --- Action Listeners ---
        linesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && linesTable.getSelectedRow() != -1) {
                // ستقوم بتحميل الملاحظات هنا من ملف في المستقبل
                notesArea.setText("Notes for line " + linesTableModel.getValueAt(linesTable.getSelectedRow(), 1));
            }
        });

        addLineButton.addActionListener(e -> {
            String lineName = JOptionPane.showInputDialog(this, "Enter new line name:");
            if (lineName != null && !lineName.trim().isEmpty()) {
                ProductLine newLine = new ProductLine();
                newLine.setName(lineName.trim());
                newLine.setStatusID(0); // يبدأ متوقفاً
                if (newLine.save()) {
                    refreshLinesTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add new line.");
                }
            }
        });

// تفعيل زر تحديث الحالة
        updateStatusButton.addActionListener(e -> {
            int selectedRow = linesTable.getSelectedRow();
            if (selectedRow != -1) {
                int lineId = (int) linesTableModel.getValueAt(selectedRow, 0);
                ProductLine line = ProductLine.findByID(lineId);
                if (line != null) {
                    String[] statuses = {"Stopped", "Active", "Maintenance"};
                    String newStatusStr = (String) JOptionPane.showInputDialog(this, "Select new status for " + line.getName(),
                            "Update Status", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[line.getStatusID()]);

                    if (newStatusStr != null) {
                        int newStatusId = newStatusStr.equals("Active") ? 1 : (newStatusStr.equals("Maintenance") ? 2 : 0);
                        line.setStatusID(newStatusId);
                        if (line.save()) {
                            refreshLinesTable();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update status.");
                        }
                    }
                }
            }
        });

        // --- Initial Load ---
        refreshLinesTable();
    }

    private void refreshLinesTable() {
        linesTableModel.setRowCount(0);
        List<ProductLine> lines = ProductionManager.getInstance().getProductLines();
        for (ProductLine line : lines) {
            linesTableModel.addRow(new Object[]{
                    line.getId(),
                    line.getName(),
                    line.getStatusName(),
                    "" // عمود الملاحظات
            });
        }
    }



}
