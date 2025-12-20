package com.fsociety.factory.UI_Swing_PresentationLayer;

import javax.swing.*;
import java.awt.*;

public class EditItemDialog extends JDialog {
    private final JTextField txtName, txtQty;
    private final JComboBox<String> cbStatus;

    public EditItemDialog(InventoryManagementUI parent, String id, String name, String qty, String status) {
        super(parent, "Edit Item: " + id, true);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Resource Name:"));
        txtName = new JTextField(name);
        mainPanel.add(txtName);

        mainPanel.add(new JLabel("Quantity:"));
        txtQty = new JTextField(qty);
        mainPanel.add(txtQty);

        mainPanel.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"In Stock", "Low", "Out of Stock"});
        cbStatus.setSelectedItem(status);
        mainPanel.add(cbStatus);

        JButton btnSave = getJButton(parent);

        add(mainPanel, BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);
    }

    private JButton getJButton(InventoryManagementUI parent) {
        JButton btnSave = new JButton("UPDATE CHANGES");
        btnSave.setBackground(new Color(0, 102, 102));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnSave.addActionListener(e -> {
            int selectedRow = parent.getTable().getSelectedRow();
            if (selectedRow != -1) {

                parent.getTableModel().setValueAt(txtName.getText(), selectedRow, 1);
                parent.getTableModel().setValueAt(txtQty.getText(), selectedRow, 2);
                parent.getTableModel().setValueAt(cbStatus.getSelectedItem(), selectedRow, 3);

                parent.showStyledMessage("Item updated successfully!", "Success");
                dispose();
            }
        });
        return btnSave;
    }
}