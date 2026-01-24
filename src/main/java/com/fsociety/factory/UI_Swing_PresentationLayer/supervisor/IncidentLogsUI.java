package com.fsociety.factory.UI_Swing_PresentationLayer.supervisor;

import com.fsociety.factory.UI_Swing_PresentationLayer.starting.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class IncidentLogsUI extends BaseFrame {
    private final DefaultTableModel tableModel;



    public IncidentLogsUI() {
        super("Factory OS - Incident & Error Logging");
        setLayout(new BorderLayout());

        // الشريط العلوي
        add(createTopBar("SYSTEM ERROR & INCIDENT LOGS"), BorderLayout.NORTH);

        // --- الجزء الأوسط: الجدول ---
        String[] columns = {"Time", "Level", "Equipment", "Description", "Reported By"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable logTable = new JTable(tableModel);

        // تخصيص مظهر الجدول (أحمر خفيف للحوادث)
        logTable.setRowHeight(35);
        logTable.setSelectionBackground(new Color(255, 235, 235));

        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- الجزء السفلي: أزرار التحكم ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        actionPanel.setBackground(new Color(245, 245, 245));

        JButton btnReport = createStyledButton("REPORT NEW INCIDENT", new Color(192, 57, 43));
        JButton btnExport = createStyledButton("EXPORT TO FILE", new Color(44, 62, 80));
        JButton btnRefresh = createStyledButton("REFRESH", new Color(52, 152, 219));

        actionPanel.add(btnReport);
        actionPanel.add(btnExport);
        actionPanel.add(btnRefresh);

        add(actionPanel, BorderLayout.SOUTH);

        // إضافة بيانات تجريبية لمحاكاة سجلات الملفات
        loadSampleLogs();

        // حدث الإبلاغ عن حادثة
        btnReport.addActionListener(e -> openReportDialog());
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

    private void loadSampleLogs() {
        tableModel.addRow(new Object[]{"08:45 AM", "CRITICAL", "Boiler #4", "Overheating detected", "Supervisor Ahmed"});
        tableModel.addRow(new Object[]{"10:20 AM", "WARNING", "Line A", "Slight sensor delay", "System Auto"});
        tableModel.addRow(new Object[]{"01:15 PM", "INFO", "System", "Software update applied", "Admin"});
    }

    private void openReportDialog() {
        // إنشاء Dialog احترافي للإبلاغ
        JDialog reportDialog = new JDialog(this, "New Incident Report", true);
        reportDialog.setSize(400, 450);
        reportDialog.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"INFO", "WARNING", "CRITICAL"});
        JTextField equipField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        p.add(new JLabel("Severity Level:"));
        p.add(levelCombo);
        p.add(new JLabel("Equipment ID:"));
        p.add(equipField);
        p.add(new JLabel("Description of Issue:"));
        p.add(new JScrollPane(descArea));

        JButton btnSubmit = new JButton("SUBMIT LOG");
        btnSubmit.setBackground(new Color(192, 57, 43));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> {
            // منطق الإضافة للجدول (وللملف لاحقاً)
            tableModel.insertRow(0, new Object[]{
                    "Now",
                    levelCombo.getSelectedItem(),
                    equipField.getText(),
                    descArea.getText(),
                    "Current User"
            });
            reportDialog.dispose();
            showStyledMessage("Incident recorded successfully.", "Log System");
        });

        p.add(btnSubmit);
        reportDialog.add(p);
        reportDialog.setVisible(true);
    }
}