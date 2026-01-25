package com.fsociety.factory.UI_Swing_PresentationLayer.manager;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;
import com.fsociety.factory.UI_Swing_PresentationLayer.starting.RoleSelectionUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class ManagerDashboard extends BaseFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    // تعريف الألواح كمتغيرات للوصول لدوال التحديث
    private final AddProductionLinePanel addProductionLinePanel;
    private final EditLineStatusPanel editLineStatusPanel;
    private final ViewPerformancePanel viewPerformancePanel;

    private static final String ADD_LINE_CARD = "AddProductionLine";
    private static final String EDIT_STATUS_CARD = "EditLineStatus";
    private static final String VIEW_PERFORMANCE_CARD = "ViewPerformance";
    private static final String DEFAULT_CARD = "DefaultView";

    public ManagerDashboard() {
        super("Manager Dashboard - Factory Strategic Control");
        setLayout(new BorderLayout());

        // تهيئة الألواح أولاً
        addProductionLinePanel = new AddProductionLinePanel();
        editLineStatusPanel = new EditLineStatusPanel();
        viewPerformancePanel = new ViewPerformancePanel();

        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(backgroundColor);

        addInitialCards(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, DEFAULT_CARD);
        setVisible(true);
    }

    // ميثود التبديل مع تحديث البيانات
    private void showPanel(String cardName) {
        switch (cardName) {
            case EDIT_STATUS_CARD:
                editLineStatusPanel.refreshLineList(); // تحديث قائمة الخطوط
                break;
            case VIEW_PERFORMANCE_CARD:
                viewPerformancePanel.refreshTableData(); // تحديث جدول الأداء
                break;
        }
        cardLayout.show(contentPanel, cardName);
    }

    private JPanel createNavigationPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBackground(primaryColor);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, accentColor));

        JLabel lblMenu = new JLabel("EXECUTIVE MENU");
        lblMenu.setForeground(accentColor);
        lblMenu.setFont(new Font("Segoe UI Bold", Font.BOLD, 18));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMenu.setBorder(new EmptyBorder(40, 0, 40, 0));

        JButton addProductLineBtn = buttonMaker("Add Product Line");
        JButton editLineStatusBtn = buttonMaker("Edit Line Status");
        JButton viewPerformanceBtn = buttonMaker("Display Line Info");

        JButton logoutBtn = createStyledButton("LOGOUT SESSION", new Color(192, 57, 43));
        logoutBtn.setMaximumSize(new Dimension(220, 45));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // تعديل الـ ActionListeners لاستخدام ميثود التبديل المطورة
        addProductLineBtn.addActionListener(e -> showPanel(ADD_LINE_CARD));
        editLineStatusBtn.addActionListener(e -> showPanel(EDIT_STATUS_CARD));
        viewPerformanceBtn.addActionListener(e -> showPanel(VIEW_PERFORMANCE_CARD));

        logoutBtn.addActionListener(e -> {
            if (showConfirmMessage("Do you want to logout and return to role selection?", "Confirm Logout")) {
                new RoleSelectionUI().setVisible(true);
                this.dispose();
            }
        });

        sidebar.add(lblMenu);
        sidebar.add(addProductLineBtn);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(editLineStatusBtn);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(viewPerformanceBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(40));

        return sidebar;
    }

    private JButton buttonMaker(String text) {
        JButton button = createStyledButton(text, new Color(0, 150, 150));
        button.setMaximumSize(new Dimension(240, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void addInitialCards(JPanel panel) {
        JPanel defaultView = new JPanel(new BorderLayout());
        defaultView.setBackground(Color.WHITE);

        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/manager_image.jpeg")));
            Image scaledImage = icon.getImage().getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaledImage));
            defaultView.add(label, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel lblFallback = new JLabel("Welcome, Manager", SwingConstants.CENTER);
            lblFallback.setFont(new Font("Segoe UI", Font.BOLD, 40));
            lblFallback.setForeground(primaryColor);
            defaultView.add(lblFallback);
        }

        panel.add(defaultView, DEFAULT_CARD);
        panel.add(addProductionLinePanel, ADD_LINE_CARD);
        panel.add(editLineStatusPanel, EDIT_STATUS_CARD);
        panel.add(viewPerformancePanel, VIEW_PERFORMANCE_CARD);
    }
}