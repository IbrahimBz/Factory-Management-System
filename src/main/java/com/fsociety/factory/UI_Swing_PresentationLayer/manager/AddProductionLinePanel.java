package com.fsociety.factory.UI_Swing_PresentationLayer.manager;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddProductionLinePanel extends JPanel {

    private final JTextField lineNameField;
    private final JComboBox<String> statusComboBox;

    public AddProductionLinePanel() {
        Color backgroundColor = new Color(240, 245, 245);
        setBackground(backgroundColor);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(10, 10, 10, 10);
        fgbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Add New Production Line");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        Color primaryColor = new Color(0, 102, 102);
        titleLabel.setForeground(primaryColor);
        fgbc.gridx = 0; fgbc.gridy = 0; fgbc.gridwidth = 2;
        fgbc.insets = new Insets(0, 0, 30, 0);
        fgbc.anchor = GridBagConstraints.CENTER;
        formCard.add(titleLabel, fgbc);

        fgbc.gridwidth = 1;
        fgbc.insets = new Insets(10, 5, 10, 5);
        fgbc.anchor = GridBagConstraints.WEST;


        formCard.add(createStyledLabel("Line Name:"), getGbc(0, 1, fgbc));
        lineNameField = createStyledTextField();
        formCard.add(lineNameField, getGbc(1, 1, fgbc));

        formCard.add(createStyledLabel("Operational Status:"), getGbc(0, 2, fgbc));
        String[] statuses = {"Active", "Stopped", "Maintenance"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusComboBox.setPreferredSize(new Dimension(250, 38));
        formCard.add(statusComboBox, getGbc(1, 2, fgbc));

        JButton addButton = new JButton("DEPLOY LINE");
        styleDeployButton(addButton);
        fgbc.gridx = 0; fgbc.gridy = 3; fgbc.gridwidth = 2;
        fgbc.fill = GridBagConstraints.NONE;
        fgbc.anchor = GridBagConstraints.CENTER;
        fgbc.insets = new Insets(35, 0, 10, 0);
        formCard.add(addButton, fgbc);

        add(formCard, new GridBagConstraints());
        addButton.addActionListener(e -> addNewProductionLine());
    }

    private void addNewProductionLine() {
        BaseFrame parent = (BaseFrame) SwingUtilities.getWindowAncestor(this);
        try {
            String lineName = lineNameField.getText().trim();
            if (lineName.isEmpty()) {
                if (parent != null) parent.showStyledMessage("Error: Line Name is mandatory!", "Input Validation");
                return;
            }

            int statusID = statusComboBox.getSelectedIndex() + 1;

            if (parent != null && parent.showConfirmMessage("Deploy new production line to factory network?", "Confirm Deployment")) {
                boolean success = com.fsociety.factory.BusinessLayer.Manager.LinesManager.getInstance()
                        .addNewProductLine(lineName, statusID, "Initial system deployment");

                if (success) {
                    parent.showStyledMessage("Line (" + lineName + ") deployed successfully!", "System Deployed");
                    lineNameField.setText("");
                    statusComboBox.setSelectedIndex(0);
                } else {
                    parent.showStyledMessage("Failed to save to database.", "System Error");
                }
            }
        } catch (Exception ex) {
            if (parent != null) parent.showStyledMessage("An error occurred: " + ex.getMessage(), "Critical Error");
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 70, 70));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(250, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void styleDeployButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(46, 204, 113));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 48));
    }

    private GridBagConstraints getGbc(int x, int y, GridBagConstraints base) {
        base.gridx = x;
        base.gridy = y;
        return base;
    }
}