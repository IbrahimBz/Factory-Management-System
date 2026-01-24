package com.fsociety.factory.presentationLayer;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    public LoginView() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 5, 5));

        add(new JLabel("Select Role:"));

        String[] roles = {"Production Supervisor", "Admin"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        add(roleComboBox);

        add(new JLabel("")); // Placeholder

        JButton loginButton = new JButton("Login");
        add(loginButton);

        loginButton.addActionListener(e -> {
            String selectedRole = (String) roleComboBox.getSelectedItem();
            this.dispose(); // أغلق نافذة تسجيل الدخول

            if ("Admin".equals(selectedRole)) {
                SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
            } else {
                SwingUtilities.invokeLater(() -> new SupervisorDashboard().setVisible(true));
            }
        });
    }

    public static void main(String[] args) {
        // تهيئة جميع الـ Singletons أولاً
        com.fsociety.factory.BusinessLayer.Inventory.Inventory.getInstance();
        com.fsociety.factory.BusinessLayer.Production.TaskManager.getInstance();

        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
