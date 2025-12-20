package com.fsociety.factory.UI_Swing_PresentationLayer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;

public class InventoryManagementUI extends BaseFrame {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private int hoveredRow = -1;
    private final JLabel lblTotalStockValue;
    private final JLabel lblLowStockValue;

    public InventoryManagementUI() {
        super("Inventory Management System");

        add(createTopBar("INVENTORY MANAGEMENT"), BorderLayout.NORTH);

        JPanel container = getJPanel();

        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);

        JPanel cardTotal = createStatCard("Total Items", "0", primaryColor);
        lblTotalStockValue = (JLabel) cardTotal.getComponent(1);

        JPanel cardLow = createStatCard("Low Stock Items", "0", Color.RED);
        lblLowStockValue = (JLabel) cardLow.getComponent(1);

        stats.add(cardTotal);
        stats.add(cardLow);
        stats.add(createStatCard("Categories", "12 Items", accentColor));
        container.add(stats, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);

        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 45));
        txtSearch.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primaryColor), "Quick Search"));

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = txtSearch.getText().toLowerCase();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
            }
        });
        centerPanel.add(txtSearch, BorderLayout.NORTH);

        String[] columns = {"ID", "Resource Name", "Quantity", "Status"};
        Object[][] data = {
                {"#R-01", "Steel", "500kg", "In Stock"},
                {"#R-02", "Oil", "10L", "Low"},
                {"#R-03", "Aluminum", "200kg", "In Stock"}
        };
        tableModel = new DefaultTableModel(data, columns);
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("Segoe UI Bold", Font.BOLD, 14));

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    table.repaint();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                table.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row == -1) {
                    table.clearSelection();
                }
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = table.getValueAt(row, 3).toString();

                if ("Low".equalsIgnoreCase(status)) {
                    c.setBackground(new Color(255, 230, 230));
                    c.setForeground(new Color(180, 0, 0));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                if (row == hoveredRow && !isSelected) {
                    c.setBackground(new Color(235, 245, 255));
                }

                if (isSelected) {
                    c.setBackground(new Color(0, 150, 150));
                    c.setForeground(Color.WHITE);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        scrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                table.clearSelection();
            }
        });

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        container.add(centerPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton btnAdd = createStyledButton("ADD ITEM", primaryColor);
        btnAdd.addActionListener(e -> new AddItemDialog(this).setVisible(true));

        JButton btnEdit = createStyledButton("EDIT ITEM", new Color(70, 130, 180));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String id = table.getValueAt(row, 0).toString();
                String name = table.getValueAt(row, 1).toString();
                String qty = table.getValueAt(row, 2).toString();
                String status = table.getValueAt(row, 3).toString();
                new EditItemDialog(this, id, name, qty, status).setVisible(true);
            } else {
                showStyledMessage("Please select an item to edit!", "Selection Required");
            }
        });

        JButton btnDelete = createStyledButton("DELETE", new Color(128, 0, 0));
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String name = table.getValueAt(row, 1).toString();
                if (showConfirmMessage("Are you sure you want to delete [" + name + "]?", "Confirm Deletion")) {
                    tableModel.removeRow(row);
                    updateDashboardStats();
                    showStyledMessage("Item deleted successfully.", "Done");
                }
            } else {
                showStyledMessage("Please select an item to delete!", "Selection Required");
            }
        });

        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnDelete);
        container.add(actions, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);
        updateDashboardStats();
    }

    private static JPanel getJPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(210, 225, 225),
                        0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        container.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        container.setOpaque(false);
        return container;
    }

    public void updateDashboardStats() {
        int totalRows = tableModel.getRowCount();
        int lowStockCount = 0;
        for (int i = 0; i < totalRows; i++) {
            if ("Low".equalsIgnoreCase(tableModel.getValueAt(i, 3).toString())) {
                lowStockCount++;
            }
        }
        lblTotalStockValue.setText(totalRows + " Items");
        lblLowStockValue.setText(lowStockCount + " Items");
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTable() { return table; }
}