package com.fsociety.factory.UI_Swing_PresentationLayer.supervisor;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProductionLinesUI extends BaseFrame {

    public ProductionLinesUI() {
        super("Factory OS - Production Line Monitor");
        setLayout(new BorderLayout());

        add(createTopBar("LIVE PRODUCTION STATUS"), BorderLayout.NORTH);

        // لوحة الشبكة لعرض خطوط الإنتاج كبطاقات
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        gridPanel.setBackground(new Color(245, 245, 245));

        // إضافة خطوط إنتاج تجريبية
        gridPanel.add(createLineCard("LINE A - ASSEMBLY", "RUNNING", new Color(46, 204, 113)));
        gridPanel.add(createLineCard("LINE B - PACKAGING", "STOPPED", new Color(231, 76, 60)));
        gridPanel.add(createLineCard("LINE C - QUALITY", "MAINTENANCE", new Color(241, 196, 15)));
        gridPanel.add(createLineCard("LINE D - PAINTING", "RUNNING", new Color(46, 204, 113)));

        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        // أزرار التحكم في الأسفل
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddLine = createStyledButton("+ ADD NEW LINE", new Color(44, 62, 80));
        bottomPanel.add(btnAddLine);
        add(bottomPanel, BorderLayout.SOUTH);

        // في نهاية الـ Constructor لكل واجهة
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

    private JPanel createLineCard(String name, String status, Color statusColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setBorder(new EmptyBorder(15, 0, 10, 0));

        JLabel lblStatus = new JLabel(status, SwingConstants.CENTER);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(statusColor);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setPreferredSize(new Dimension(0, 40));

        card.add(lblName, BorderLayout.CENTER);
        card.add(lblStatus, BorderLayout.SOUTH);
        return card;
    }
}