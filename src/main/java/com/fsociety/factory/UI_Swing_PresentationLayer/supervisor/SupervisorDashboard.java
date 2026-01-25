package com.fsociety.factory.UI_Swing_PresentationLayer.supervisor;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;
import com.fsociety.factory.UI_Swing_PresentationLayer.starting.RoleSelectionUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SupervisorDashboard extends BaseFrame {

    public SupervisorDashboard() {
        super("Factory OS - Supervisor Control Center");
        setLayout(new BorderLayout());

        // شريط علوي يحتوي على زر تسجيل الخروج
        add(createTopPanel(), BorderLayout.NORTH);

        // المحتوى الرئيسي مقسم لبطاقات مهام المشرف
        JPanel mainContent = new JPanel(new GridLayout(2, 2, 25, 25));
        mainContent.setBorder(new EmptyBorder(40, 50, 40, 50));
        mainContent.setBackground(new Color(240, 248, 248));

        // البطاقة 1: إدارة المخزون (صلاحية أساسية للمشرف)
        mainContent.add(createTaskCard("INVENTORY & STOCK",
                "Monitor raw materials and update stock levels for production.",
                new Color(0, 121, 107), "📦", e -> {
                    // هنا تفتح واجهة المخزون الخاصة بك
                     new InventoryManagementUI().setVisible(true);
                }));

        // البطاقة 2: حالة خطوط الإنتاج
        mainContent.add(createTaskCard("PRODUCTION LINES",
                "View real-time status of active machinery and lines.",
                new Color(44, 62, 80), "⚙️", e -> {
                    new ProductionLinesUI().setVisible(true);
                }));

        // البطاقة 3: المهام والأوامر (جوهر عمل المشرف)
        mainContent.add(createTaskCard("TASKS & ORDERS",
                "Assign production batches and monitor daily progress.",
                new Color(38, 166, 154), "📋", e -> {
                   new TaskManagementUI().setVisible(true);
                }));

        mainContent.add(createTaskCard("INCIDENT LOGS",
                "Report machinery issues and view maintenance logs.",
                new Color(192, 57, 43), "⚠️", e -> {
                    new IncidentLogsUI().setVisible(true);
                }));

        add(mainContent, BorderLayout.CENTER);

        add(createFooterStatus(), BorderLayout.SOUTH);

    }



    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createTopBar("SUPERVISOR DASHBOARD - PRODUCTION NODE"), BorderLayout.CENTER);

        JButton btnLogout = new JButton("LOGOUT");
        btnLogout.setBackground(new Color(150, 0, 0));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            new RoleSelectionUI().setVisible(true);
            this.dispose();
        });

        topPanel.add(btnLogout, BorderLayout.EAST);
        return topPanel;
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

        JButton btnAction = createStyledButton("MANAGE MODULE", theme);
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

        JLabel status = new JLabel("  STATUS: SYSTEM ONLINE | ROLE: PRODUCTION SUPERVISOR | LOG: ACTIVE");
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Segoe UI", Font.BOLD, 12));

        footer.add(status, BorderLayout.WEST);
        return footer;
    }
}