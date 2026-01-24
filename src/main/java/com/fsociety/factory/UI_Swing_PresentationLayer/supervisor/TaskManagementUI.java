package com.fsociety.factory.UI_Swing_PresentationLayer.supervisor;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskManagementUI extends BaseFrame {

    public TaskManagementUI() {
        super("Factory OS - Task Assignment");
        setLayout(new BorderLayout());

        add(createTopBar("PRODUCTION ORDERS & WORKFLOW"), BorderLayout.NORTH);

        // لوحة المهام
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // إضافة مهام تجريبية
        listPanel.add(createTaskItem("Order #9021 - Steel Forging", "Assigned to: Ahmed", "High Priority"));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createTaskItem("Order #9022 - Valve Assembly", "Assigned to: Sarah", "Normal"));

        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        // أضف هذا الكود في الجزء العلوي (North) لكل واجهة من الواجهات الأربع
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createTopBar("TITLE OF INTERFACE"), BorderLayout.CENTER);

        JButton btnBack = new JButton("⬅ Back");
        btnBack.setBackground(new Color(44, 62, 80));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            this.dispose(); // إغلاق الواجهة الحالية والعودة للتي خلفها
        });

        topPanel.add(btnBack, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createTaskItem(String title, String assigned, String priority) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        item.setBackground(new Color(248, 249, 250));
        item.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(52, 152, 219)));

        JPanel textSide = new JPanel(new GridLayout(2, 1));
        textSide.setOpaque(false);
        textSide.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblDetails = new JLabel(assigned + " | Priority: " + priority);
        lblDetails.setForeground(Color.GRAY);

        textSide.add(lblTitle);
        textSide.add(lblDetails);

        JButton btnComplete = new JButton("Mark Done");
        item.add(textSide, BorderLayout.CENTER);
        item.add(btnComplete, BorderLayout.EAST);

        return item;
    }
}