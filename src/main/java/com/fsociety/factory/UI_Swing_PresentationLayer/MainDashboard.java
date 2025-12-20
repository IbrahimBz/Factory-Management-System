package com.fsociety.factory.UI_Swing_PresentationLayer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainDashboard extends BaseFrame {

    public MainDashboard() {
        super("Factory Control Center - Integrated Dashboard");
        setLayout(new BorderLayout());

        add(createTopBar("FACTORY GLOBAL CONTROL PANEL"), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridLayout(2, 2, 25, 25));
        mainContent.setBorder(new EmptyBorder(40, 50, 40, 50));
        mainContent.setBackground(new Color(240, 248, 248));

        mainContent.add(createTaskCard("INVENTORY & STOCK",
                "Manage Raw Materials, Products, and Levels.",
                new Color(0, 121, 107), "📦", e -> new InventoryManagementUI().setVisible(true)));

        mainContent.add(createTaskCard("PRODUCTION LINES",
                "Add Production Lines & Modify Line Status.",
                new Color(44, 62, 80), "⚙️", e -> {
                    showStyledMessage("Opening Production Line Manager...", "Admin Access");
                }));

        mainContent.add(createTaskCard("TASKS & ORDERS",
                "Assign, Cancel, and Monitor Production Tasks.",
                new Color(38, 166, 154), "📋", e -> {
                    showStyledMessage("Opening Task Management Panel...", "Supervisor Mode");
                }));

        mainContent.add(createTaskCard("ANALYTICS & LOGS",
                "View Efficiency Rates and System Error Logs.",
                new Color(192, 57, 43), "📊", e -> {
                    showStyledMessage("Generating Performance Reports...", "System Analysis");
                }));

        add(mainContent, BorderLayout.CENTER);

        add(createFooterStatus(), BorderLayout.SOUTH);
    }

    private JPanel createTaskCard(String title, String description, Color theme, String icon, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 230), 1),
                BorderFactory.createMatteBorder(0, 10, 0, 0, theme)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(icon + " " + title);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblTitle.setForeground(theme);

        JLabel lblDesc = new JLabel("<html><body style='width: 200px'>" + description + "</body></html>");
        lblDesc.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 14));
        lblDesc.setForeground(Color.DARK_GRAY);

        textPanel.add(lblTitle);
        textPanel.add(lblDesc);

        JButton btnAction = createStyledButton("OPEN MODULE", theme);
        btnAction.setPreferredSize(new Dimension(120, 45));
        btnAction.addActionListener(action);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(btnAction, BorderLayout.SOUTH);
        card.setBorder(BorderFactory.createCompoundBorder(card.getBorder(), BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        return card;
    }

    private JPanel createFooterStatus() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(primaryColor);
        footer.setPreferredSize(new Dimension(getWidth(), 35));

        JLabel status = new JLabel("  System Status: Active | Role: Authorized User | File: error.txt Connected");
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Segoe UI", Font.BOLD, 12));

        footer.add(status, BorderLayout.WEST);
        return footer;
    }
}