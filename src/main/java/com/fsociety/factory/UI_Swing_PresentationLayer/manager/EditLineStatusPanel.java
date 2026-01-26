package com.fsociety.factory.UI_Swing_PresentationLayer.manager;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditLineStatusPanel extends JPanel {

    private static final String[] MOCK_PRODUCTION_LINES = {"Line One", "Line Two", "Line Three"};
    private final JComboBox<String> lineSelectorComboBox;
    private final JComboBox<String> statusComboBox;

    private final Color actionColor = new Color(52, 152, 219);

    public EditLineStatusPanel() {
        Color backgroundColor = new Color(240, 245, 245);
        setBackground(backgroundColor);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel statusCard = new JPanel(new GridBagLayout());
        statusCard.setBackground(Color.WHITE);
        statusCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Modify Operational Status");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        Color primaryColor = new Color(0, 102, 102);
        titleLabel.setForeground(primaryColor);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 35, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        statusCard.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(12, 5, 12, 5);
        gbc.anchor = GridBagConstraints.WEST;

        statusCard.add(createStyledLabel("Target Line:"), getGbc(0, 1, gbc));
        lineSelectorComboBox = new JComboBox<>(MOCK_PRODUCTION_LINES);
        styleComboBox(lineSelectorComboBox);
        statusCard.add(lineSelectorComboBox, getGbc(1, 1, gbc));

        statusCard.add(createStyledLabel("New Protocol:"), getGbc(0, 2, gbc));
        String[] statuses = {"Active", "Stopped", "Maintenance"};
        statusComboBox = new JComboBox<>(statuses);
        styleComboBox(statusComboBox);
        statusCard.add(statusComboBox, getGbc(1, 2, gbc));

        JButton updateButton = new JButton("UPDATE PROTOCOL");
        styleUpdateButton(updateButton);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(35, 0, 10, 0);
        statusCard.add(updateButton, gbc);

        add(statusCard, new GridBagConstraints());
        updateButton.addActionListener(e -> updateLineStatus());
    }

    public void refreshLineList() {
        lineSelectorComboBox.removeAllItems();
        java.util.List<com.fsociety.factory.BusinessLayer.Production.ProductLine> lines = com.fsociety.factory.BusinessLayer.Manager.LinesManager.getInstance().getProductLines();
        for (com.fsociety.factory.BusinessLayer.Production.ProductLine line : lines) {
            lineSelectorComboBox.addItem(line.getName());
        }
    }
    private void updateLineStatus() {
        BaseFrame parent = (BaseFrame) SwingUtilities.getWindowAncestor(this);
        com.fsociety.factory.BusinessLayer.Manager.LinesManager manager = com.fsociety.factory.BusinessLayer.Manager.LinesManager.getInstance();

        int selectedIndex = lineSelectorComboBox.getSelectedIndex();
        if (selectedIndex == -1) return;

        com.fsociety.factory.BusinessLayer.Production.ProductLine selectedLine = manager.getProductLines().get(selectedIndex);
        String newStatusName = (String) statusComboBox.getSelectedItem();
        int newStatusID = statusComboBox.getSelectedIndex() + 1;

        if (parent.showConfirmMessage("Update " + selectedLine.getName() + " to " + newStatusName + "?", "Protocol Override")) {
            try {

                boolean success = manager.editProductLine(selectedLine.getId(), selectedLine.getName(), newStatusID, selectedLine.getNotes());
                if (success) {
                    parent.showStyledMessage("Status Synchronized successfully.", "Success");
                }
            } catch (Exception ex) {
                parent.showStyledMessage("Failed to update system: " + ex.getMessage(), "Terminal Error");
            }
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 70, 70));
        return label;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setPreferredSize(new Dimension(280, 38));
        combo.setBackground(Color.WHITE);
    }

    private void styleUpdateButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(actionColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(240, 48));
    }

    private GridBagConstraints getGbc(int x, int y, GridBagConstraints base) {
        base.gridx = x;
        base.gridy = y;
        return base;
    }
}