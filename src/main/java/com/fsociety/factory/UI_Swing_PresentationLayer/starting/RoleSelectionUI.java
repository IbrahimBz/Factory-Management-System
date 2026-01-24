package com.fsociety.factory.UI_Swing_PresentationLayer.starting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class RoleSelectionUI extends BaseFrame {

    public RoleSelectionUI() {
        super("Factory OS - Select Access Level");
        setLayout(new GridLayout(1, 2));

        // تأكد من وجود الصور في مسار resources/UI/
        add(createRolePanel("MANAGER",
                "System Control, Line Management, and Performance Analytics.",
                new Color(0, 51, 51), "/UI/manger.png", true));

        add(createRolePanel("SUPERVISOR",
                "Inventory Control, Task Assignment, and Production Monitoring.",
                primaryColor, "/UI/supervisor.png", false));
    }

    private JPanel createRolePanel(String title, String desc, Color bgColor, String iconPath, boolean isManager) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 20, 10, 20);

        // --- إضافة الصورة هنا ---
        try {
            // تحميل الصورة من المسار
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(iconPath)));
            // تغيير حجم الصورة (مثلاً 140x140 بكسل) لتكون متناسقة
            Image scaledImg = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            JLabel lblIcon = new JLabel(new ImageIcon(scaledImg));

            gbc.gridy = 0; // الصورة في الأعلى
            panel.add(lblIcon, gbc);
        } catch (Exception e) {
            // في حال فشل تحميل الصورة، يترك مساحة فارغة أو يطبع خطأ بسيط
            System.err.println("Could not load image: " + iconPath);
        }

        // العنوان (تحت الصورة)
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 45));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 1;
        panel.add(lblTitle, gbc);

        // الوصف (تحت العنوان)
        JLabel lblDesc = new JLabel("<html><center>" + desc + "</center></html>", SwingConstants.CENTER);
        lblDesc.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
        lblDesc.setForeground(new Color(200, 230, 230));
        lblDesc.setPreferredSize(new Dimension(300, 80));
        gbc.gridy = 2;
        panel.add(lblDesc, gbc);

        // أحداث الماوس
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(bgColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginUI(isManager).setVisible(true);
                dispose();
            }
        });

        return panel;
    }
}