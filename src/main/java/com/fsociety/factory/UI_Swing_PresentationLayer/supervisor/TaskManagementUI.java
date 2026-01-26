package com.fsociety.factory.UI_Swing_PresentationLayer.supervisor;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import com.fsociety.factory.BusinessLayer.Inventory.Item;
import com.fsociety.factory.BusinessLayer.Production.*;
import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskManagementUI extends BaseFrame {

    // Managers
    private final TaskManager taskManager = TaskManager.getInstance();
    private final ProductionManager productionManager = ProductionManager.getInstance();
    private final Inventory inventory = Inventory.getInstance();

    // UI Components
    private JTabbedPane mainTabbedPane;

    // --- TAB 1: Live Tasks ---
    private JTable taskTable;
    private DefaultTableModel taskModel;
    private JComboBox<Object> comboLiveLines, comboLiveProducts;
    private JComboBox<String> comboLiveStatus;
    private Timer refreshTimer;

    // --- TAB 2: Production History ---
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private JComboBox<Object> comboHistoryLines, comboHistoryProducts;
    private JTextField txtStartDate, txtEndDate;

    public TaskManagementUI() {
        super("Factory OS - Production Control Room");
        setLayout(new BorderLayout());

        // 1. TOP BAR
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(createTopBar("PRODUCTION & ANALYTICS"), BorderLayout.CENTER);

        JButton btnBack = new JButton("⬅ Back");
        styleButton(btnBack, new Color(44, 62, 80));
        btnBack.addActionListener(e -> {
            if (refreshTimer != null) refreshTimer.stop();
            this.dispose();
        });
        topContainer.add(btnBack, BorderLayout.WEST);
        add(topContainer, BorderLayout.NORTH);

        // 2. TABS
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        mainTabbedPane.addTab("⚡ Live Operations", createLiveTasksPanel());
        mainTabbedPane.addTab("📜 Production History", createHistoryPanel());

        add(mainTabbedPane, BorderLayout.CENTER);

        // 3. START TIMER (Auto-refresh every 2 seconds for live tasks)
        refreshTimer = new Timer(2000, e -> {
            // Only refresh if user is not currently using a menu
            if (mainTabbedPane.getSelectedIndex() == 0 && !isMenuVisible()) loadLiveTasks();
        });
        refreshTimer.start();

        // Initial Load
        loadLiveTasks();
        loadHistoryData();
    }

    // Helper to prevent refresh closing the popup
    private boolean isMenuVisible() {
        MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
        return path.length > 0;
    }

    // =================================================================================
    // TAB 1: LIVE TASKS
    // =================================================================================

    private JPanel createLiveTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- Filters ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(235, 245, 245));

        comboLiveLines = new JComboBox<>();
        comboLiveLines.addItem("All Lines");
        productionManager.getProductLines().forEach(comboLiveLines::addItem);

        comboLiveProducts = new JComboBox<>();
        comboLiveProducts.addItem("All Products");
        Product.getAllProducts().forEach(comboLiveProducts::addItem);

        comboLiveStatus = new JComboBox<>(new String[]{"All Status", "PENDING", "RUNNING", "COMPLETED", "PAUSED", "CANCELLED"});

        filterPanel.add(new JLabel("Line:")); filterPanel.add(comboLiveLines);
        filterPanel.add(new JLabel("Product:")); filterPanel.add(comboLiveProducts);
        filterPanel.add(new JLabel("Status:")); filterPanel.add(comboLiveStatus);

        JButton btnFilter = new JButton("Apply Filters");
        btnFilter.addActionListener(e -> loadLiveTasks());
        filterPanel.add(btnFilter);

        panel.add(filterPanel, BorderLayout.NORTH);

        // --- Table Setup ---
        // UPDATED COLUMNS: Added End Date and reordered Qty
        String[] cols = {"ID", "Product", "Line", "Status", "Progress", "Start Date", "End Date", "Qty"};

        taskModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return (c == 4) ? JProgressBar.class : Object.class; }
        };

        taskTable = new JTable(taskModel);
        taskTable.setRowHeight(30);
        taskTable.getColumn("Progress").setCellRenderer(new ProgressRenderer());

        // Center align text columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        taskTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        taskTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Start
        taskTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // End
        taskTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Qty

        // --- ADDING THE RIGHT CLICK MENU HERE ---
        addRightClickMenu(taskTable);

        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // --- Controls ---
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAdd = new JButton("➕ Submit New Task");
        styleButton(btnAdd, new Color(0, 121, 107));
        controls.add(btnAdd);

        panel.add(controls, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> openNewTaskDialog());

        return panel;
    }

    /**
     * Creates and attaches the Right-Click Context Menu logic
     */
    private void addRightClickMenu(JTable table) {
        JPopupMenu popupMenu = new JPopupMenu();

        // Menu Item 1: RESUME
        JMenuItem itemResume = new JMenuItem("▶ Resume / Retry Task");
        itemResume.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                Optional<Task> taskOpt = taskManager.findTaskById(id);
                if (taskOpt.isPresent()) {
                    Task t = taskOpt.get();
                    if (t.getStatusID() == Task.Status.RUNNING.getValue()) {
                        JOptionPane.showMessageDialog(this, "Task is already running!");
                    } else {
                        taskManager.submitExistingTask(t);
                        loadLiveTasks();
                    }
                }
            }
        });

        // Menu Item 2: STOP/CANCEL
        JMenuItem itemCancel = new JMenuItem("⛔ Stop / Cancel Task");
        itemCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                taskManager.cancelTask(id);
                loadLiveTasks();
            }
        });

        // Menu Item 3: FORCE STATUS SUB-MENU
        JMenu menuForceStatus = new JMenu("🛠 Set Status To...");
        for (Task.Status status : Task.Status.values()) {
            JMenuItem statusItem = new JMenuItem(status.name());
            statusItem.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int id = (int) table.getValueAt(row, 0);
                    taskManager.findTaskById(id).ifPresent(t -> {
                        if (t.getStatusID() == Task.Status.RUNNING.getValue() && status != Task.Status.RUNNING) {
                            taskManager.cancelTask(t.getId());
                        }
                        t.updateStatus(status, "Manually set by Supervisor");
                        loadLiveTasks();
                    });
                }
            });
            menuForceStatus.add(statusItem);
        }

        popupMenu.add(itemResume);
        popupMenu.add(itemCancel);
        popupMenu.addSeparator();
        popupMenu.add(menuForceStatus);

        // Mouse Adapter to trigger the menu
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { showPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showPopup(e); }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < table.getRowCount()) {
                        table.setRowSelectionInterval(row, row);
                    } else {
                        table.clearSelection();
                        return;
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void loadLiveTasks() {
        int selectedId = -1;
        if (taskTable.getSelectedRow() != -1)
            selectedId = (int) taskTable.getValueAt(taskTable.getSelectedRow(), 0);

        List<Task> tasks = taskManager.getAllTasks();

        // Filter Logic
        if (comboLiveLines.getSelectedIndex() > 0) {
            ProductLine line = (ProductLine) comboLiveLines.getSelectedItem();
            tasks = tasks.stream().filter(t -> t.getAssignedLine() != null && t.getAssignedLine().getId() == line.getId()).collect(Collectors.toList());
        }
        if (comboLiveProducts.getSelectedIndex() > 0) {
            Product prod = (Product) comboLiveProducts.getSelectedItem();
            tasks = tasks.stream().filter(t -> t.getProduct().getId() == prod.getId()).collect(Collectors.toList());
        }
        String status = (String) comboLiveStatus.getSelectedItem();
        if (!"All Status".equals(status)) {
            tasks = tasks.stream().filter(t -> t.getStatusName().equalsIgnoreCase(status)).collect(Collectors.toList());
        }

        taskModel.setRowCount(0);
        for (Task t : tasks) {
            JProgressBar pb = new JProgressBar(0, 100);
            pb.setValue((int) t.getCompletionRate());
            pb.setStringPainted(true);

            taskModel.addRow(new Object[]{
                    t.getId(),
                    t.getProduct().getName(),
                    t.getAssignedLine() != null ? t.getAssignedLine().getName() : "Wait...",
                    t.getStatusName(),
                    pb,
                    (t.getStartDate() != null) ? t.getStartDate().toString() : "N/A",
                    (t.getEndDate() != null) ? t.getEndDate().toString() : "-",
                    t.getAchievedQuantity() + " / " + t.getRequiredQuantity()
            });
        }

        // Restore Selection
        if (selectedId != -1) {
            for (int i = 0; i < taskTable.getRowCount(); i++) {
                if ((int) taskTable.getValueAt(i, 0) == selectedId) {
                    taskTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- Filters ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(new TitledBorder("History Filters"));

        comboHistoryLines = new JComboBox<>();
        comboHistoryLines.addItem("All Lines");
        productionManager.getProductLines().forEach(comboHistoryLines::addItem);

        comboHistoryProducts = new JComboBox<>();
        comboHistoryProducts.addItem("All Products");
        Product.getAllProducts().forEach(comboHistoryProducts::addItem);

        JButton btnRefreshHist = new JButton("Refresh History");
        btnRefreshHist.addActionListener(e -> loadHistoryData());

        filterPanel.add(new JLabel("Created By Line:")); filterPanel.add(comboHistoryLines);
        filterPanel.add(new JLabel("Product Type:")); filterPanel.add(comboHistoryProducts);
        filterPanel.add(btnRefreshHist);

        topPanel.add(filterPanel);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- Table ---
        String[] cols = {"Product Name", "Created Quantity", "Line Used", "Completion Date", "Client ID"};
        historyModel = new DefaultTableModel(cols, 0);
        historyTable = new JTable(historyModel);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // --- Analytics Section (Top Product) ---
        JPanel analyticsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        analyticsPanel.setBorder(new TitledBorder("Analytics: Top Product"));
        analyticsPanel.setBackground(new Color(240, 240, 240));

        txtStartDate = new JTextField(10);
        txtStartDate.setToolTipText("YYYY-MM-DD");
        txtEndDate = new JTextField(10);
        txtEndDate.setToolTipText("YYYY-MM-DD");

        // Set default dates
        txtEndDate.setText(LocalDate.now().toString());

        JButton btnCalcTop = new JButton("🏆 Calculate Top Product");
        styleButton(btnCalcTop, new Color(211, 84, 0));

        analyticsPanel.add(new JLabel("Start Date (YYYY-MM-DD):")); analyticsPanel.add(txtStartDate);
        analyticsPanel.add(new JLabel("End Date:")); analyticsPanel.add(txtEndDate);
        analyticsPanel.add(btnCalcTop);

        panel.add(analyticsPanel, BorderLayout.SOUTH);

        // Logic for Top Product
        btnCalcTop.addActionListener(e -> {
            try {
                LocalDate start = txtStartDate.getText().isEmpty() ? null : LocalDate.parse(txtStartDate.getText());
                LocalDate end = txtEndDate.getText().isEmpty() ? null : LocalDate.parse(txtEndDate.getText());

                Product top = taskManager.getTopProductInDetermineDate(start, end);

                if (top != null) {
                    JOptionPane.showMessageDialog(this,
                            "Top Product: " + top.getName() + "\nQuantity in Stock: " + top.getQuantityInStock(),
                            "Analysis Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No production data found for this period.");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format! Use YYYY-MM-DD");
            }
        });

        return panel;
    }

    private void loadHistoryData() {
        List<Task> completedTasks = taskManager.getCompletedTasks();

        if (comboHistoryLines.getSelectedIndex() > 0) {
            ProductLine line = (ProductLine) comboHistoryLines.getSelectedItem();
            completedTasks = completedTasks.stream()
                    .filter(t -> t.getAssignedLine() != null && t.getAssignedLine().getId() == line.getId())
                    .collect(Collectors.toList());
        }

        if (comboHistoryProducts.getSelectedIndex() > 0) {
            Product prod = (Product) comboHistoryProducts.getSelectedItem();
            completedTasks = completedTasks.stream()
                    .filter(t -> t.getProduct().getId() == prod.getId())
                    .collect(Collectors.toList());
        }

        historyModel.setRowCount(0);
        for (Task t : completedTasks) {
            historyModel.addRow(new Object[]{
                    t.getProduct().getName(),
                    t.getAchievedQuantity(),
                    t.getAssignedLine().getName(),
                    (t.getEndDate() != null) ? t.getEndDate().toString() : LocalDate.now().toString(),
                    "Client " + t.getId()
            });
        }
    }

    // =================================================================================
    // DIALOGS & HELPERS
    // =================================================================================

    private void openNewTaskDialog() {
        JDialog d = new JDialog(this, "Assign New Production Task", true);
        d.setSize(450, 400);
        d.setLayout(new GridLayout(6, 2, 10, 10));
        ((JPanel)d.getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<Product> prodCombo = new JComboBox<>();
        Product.getAllProducts().forEach(prodCombo::addItem);

        JComboBox<ProductLine> lineCombo = new JComboBox<>();
        productionManager.getProductLines().forEach(lineCombo::addItem);

        JTextField qtyField = new JTextField();
        JTextField clientField = new JTextField("1");

        d.add(new JLabel("Select Product:")); d.add(prodCombo);
        d.add(new JLabel("Select Production Line:")); d.add(lineCombo);
        d.add(new JLabel("Required Quantity:")); d.add(qtyField);
        d.add(new JLabel("Client ID:")); d.add(clientField);

        JButton btnSubmit = new JButton("🚀 Start Production");

        btnSubmit.addActionListener(e -> {
            try {
                Product p = (Product) prodCombo.getSelectedItem();
                ProductLine l = (ProductLine) lineCombo.getSelectedItem();
                int qty = Integer.parseInt(qtyField.getText());
                int client = Integer.parseInt(clientField.getText());

                if (p == null || l == null) return;

                // 1. Check for Missing Items
                Map<Integer, Integer> requirements = p.getRequiredItems();
                StringBuilder missingItemsMsg = new StringBuilder();
                boolean isMissing = false;

                for (Map.Entry<Integer, Integer> req : requirements.entrySet()) {
                    long totalNeeded = (long) req.getValue() * qty;
                    Optional<Item> itemOpt = inventory.findItemByIdInMemory(req.getKey());

                    if (itemOpt.isPresent()) {
                        Item item = itemOpt.get();
                        if (item.getAvailableQuantity() < totalNeeded) {
                            isMissing = true;
                            missingItemsMsg.append("- ")
                                    .append(item.getName())
                                    .append(": Need ").append(totalNeeded)
                                    .append(", Have ").append(item.getAvailableQuantity())
                                    .append("\n");
                        }
                    } else {
                        isMissing = true;
                        missingItemsMsg.append("- ID ").append(req.getKey()).append(" not found in inventory.\n");
                    }
                }

                if (isMissing) {
                    int choice = JOptionPane.showConfirmDialog(d,
                            "⚠️ INSUFFICIENT MATERIALS!\n\nThe following items are missing:\n" + missingItemsMsg.toString() +
                                    "\nDo you want to submit this task anyway? (It will be PAUSED)",
                            "Inventory Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (choice == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                taskManager.submitNewTaskToLine(p, qty, client, l);
                loadLiveTasks();
                d.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Please enter valid numbers for Quantity/Client ID.");
            }
        });

        d.add(new JLabel(""));
        d.add(btnSubmit);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
    }

    class ProgressRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JProgressBar) {
                JProgressBar pb = (JProgressBar) value;
                pb.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                return pb;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}