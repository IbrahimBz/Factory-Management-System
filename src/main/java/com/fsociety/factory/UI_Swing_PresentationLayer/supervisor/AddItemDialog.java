//package com.fsociety.factory.UI_Swing_PresentationLayer;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.Objects;
//
//public class AddItemDialog extends JDialog {
//    private final JTextField txtID, txtName, txtQty;
//    private final JComboBox<String> cbStatus;
//
//    public AddItemDialog(InventoryManagementUI parent) {
//        super(parent, "Add New Resource", true);
//        setSize(450, 500);
//        setLocationRelativeTo(parent);
//        setLayout(new BorderLayout());
//
//        JPanel header = new JPanel();
//        header.setBackground(new Color(0, 102, 102)); // Teal
//        JLabel lblTitle = new JLabel("ADD NEW ITEM TO STOCK");
//        lblTitle.setForeground(Color.WHITE);
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        header.add(lblTitle);
//        add(header, BorderLayout.NORTH);
//
//        JPanel body = new JPanel(new GridLayout(4, 2, 15, 25));
//        body.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
//        body.setBackground(Color.WHITE);
//
//        body.add(new JLabel("Item ID (e.g. #R-99):"));
//        txtID = new JTextField();
//        body.add(txtID);
//
//        body.add(new JLabel("Resource Name:"));
//        txtName = new JTextField();
//        body.add(txtName);
//
//        body.add(new JLabel("Quantity (Units):"));
//        txtQty = new JTextField();
//        body.add(txtQty);
//
//        body.add(new JLabel("Initial Status:"));
//        cbStatus = new JComboBox<>(new String[]{"In Stock", "Low", "Out of Stock"});
//        body.add(cbStatus);
//
//        add(body, BorderLayout.CENTER);
//
//        JPanel footer = getJPanel(parent);
//        add(footer, BorderLayout.SOUTH);
//    }
//
//    private JPanel getJPanel(InventoryManagementUI parent) {
//        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
//        footer.setBackground(new Color(245, 245, 245));
//
//        JButton btnCancel = new JButton("Cancel");
//        btnCancel.addActionListener(e -> dispose());
//
//        JButton btnAdd = new JButton("ADD ITEM");
//        btnAdd.setBackground(new Color(0, 102, 102));
//        btnAdd.setForeground(Color.WHITE);
//        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
//
//        btnAdd.addActionListener(e -> {
//            if (txtID.getText().isEmpty() || txtName.getText().isEmpty() || txtQty.getText().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Please fill all fields", "Warning", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            Object[] newRow = {
//                    txtID.getText(),
//                    txtName.getText(),
//                    txtQty.getText(),
//                    Objects.requireNonNull(cbStatus.getSelectedItem()).toString()
//            };
//
//            parent.getTableModel().addRow(newRow);
//            parent.showStyledMessage("New item added to inventory", "Success");
//            dispose();
//        });
//
//        footer.add(btnCancel);
//        footer.add(btnAdd);
//        return footer;
//    }
//}