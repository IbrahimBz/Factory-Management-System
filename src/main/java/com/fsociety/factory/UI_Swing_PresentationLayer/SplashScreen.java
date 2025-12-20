package com.fsociety.factory.UI_Swing_PresentationLayer;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    private final JProgressBar progressBar;
    private final JLabel lblStatus;

    public SplashScreen() {
        setSize(600, 350);
        setLocationRelativeTo(null);
        //_
        JPanel contentPane = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 51, 51),
                        getWidth(), getHeight(), new Color(20, 20, 20));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        contentPane.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        contentPane.setOpaque(false);
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("FACTORY OS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 45));
        lblTitle.setForeground(Color.WHITE);
        contentPane.add(lblTitle, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setOpaque(false);

        lblStatus = new JLabel("Loading modules...", SwingConstants.LEFT);
        lblStatus.setForeground(new Color(150, 150, 150));
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        bottomPanel.add(lblStatus);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(getWidth(), 8));
        progressBar.setForeground(new Color(0, 255, 180));
        progressBar.setBackground(new Color(40, 40, 40));
        progressBar.setBorderPainted(false);
        bottomPanel.add(progressBar);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        setBackground(new Color(0, 0, 0, 0));
    }

    public void startLoading() {
        try {
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(35);
                progressBar.setValue(i);

                if (i == 10) lblStatus.setText("Connecting to Database...");
                if (i == 40) lblStatus.setText("Loading Security Modules...");
                if (i == 70) lblStatus.setText("Optimizing UI Components...");
                if (i == 90) lblStatus.setText("Launching System...");

                if (i == 100) {
                    this.dispose();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}