package com.fsociety.factory.UI_Swing_PresentationLayer.manager;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ViewPerformancePanel extends JPanel {

    private JTable performanceTable;

    private final Color primaryColor = new Color(0, 102, 102);
    private final Color highlightColor = new Color(230, 126, 34);

    private final Object[][] mockData = {
            {"Line 01: Electronic Assembly", "Active", 85, "1000 iPad", "Steady output, target almost reached."},
            {"Line 02: Packaging Unit", "Maintenance", 10, "500 Boxes", "Emergency sensor replacement needed."},
            {"Line 03: Inspection/Quality", "Active", 100, "200 Laptop", "Perfect score. Efficiency at peak."}
    };

    private final String[] columnNames = {"Line Name", "Status", "Completion (%)", "Last Product", "Manager Notes"};

    public ViewPerformancePanel() {
        Color backgroundColor = new Color(240, 245, 245);
        setBackground(backgroundColor);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Production Intelligence & Executive Evaluation");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        titleLabel.setForeground(primaryColor);
        add(titleLabel, BorderLayout.NORTH);

        setupPerformanceTable();

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel infoLabel = new JLabel("<html><i>Note: Double-click 'Manager Notes' column to edit.</i></html>");
        infoLabel.setForeground(Color.GRAY);

        JButton saveButton = new JButton("COMMIT EVALUATIONS");
        styleSaveButton(saveButton);
        saveButton.addActionListener(e -> saveEvaluation());

        bottomPanel.add(infoLabel, BorderLayout.WEST);
        bottomPanel.add(saveButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupPerformanceTable() {
        DefaultTableModel tableModel = new DefaultTableModel(mockData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        performanceTable = new JTable(tableModel);
        performanceTable.setRowHeight(45);
        performanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        performanceTable.getTableHeader().setFont(new Font("Segoe UI Bold", Font.BOLD, 15));
        performanceTable.getTableHeader().setBackground(primaryColor);
        performanceTable.getTableHeader().setForeground(Color.WHITE);

        performanceTable.getColumnModel().getColumn(1).setCellRenderer(new StatusRenderer());

        JScrollPane scrollPane = new JScrollPane(performanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void saveEvaluation() {
        BaseFrame parent = (BaseFrame) SwingUtilities.getWindowAncestor(this);
        int selectedRow = performanceTable.getSelectedRow();

        if (selectedRow == -1) {
            if (parent != null) parent.showStyledMessage("Please select a line to evaluate.", "Selection Required");
            return;
        }

        if (parent != null) {
            boolean confirm = parent.showConfirmMessage("Do you want to finalize the evaluation and notes for this line?", "Confirm Review");

            if (confirm) {
                String lineName = (String) performanceTable.getValueAt(selectedRow, 0);
                String note = (String) performanceTable.getValueAt(selectedRow, 4);

                parent.showStyledMessage("Evaluation for " + lineName + " has been synchronized.", "Data Secured");
            }
        }
    }

    private void styleSaveButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(highlightColor);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value.toString().trim();
            setHorizontalAlignment(SwingConstants.CENTER);
            if (status.equalsIgnoreCase("Active")) {
                setForeground(new Color(39, 174, 96));
                setFont(c.getFont().deriveFont(Font.BOLD));
            } else if (status.equalsIgnoreCase("Maintenance")) {
                setForeground(new Color(192, 57, 43));
            } else {
                setForeground(Color.BLACK);
            }
            return c;
        }
    }
}