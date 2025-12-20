package com.fsociety.factory.UI_Swing_PresentationLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class BaseFrame extends JFrame {
    protected final Color primaryColor = new Color(0, 102, 102);
    protected final Color accentColor = new Color(0, 251, 255);
    protected final Color backgroundColor = new Color(240, 245, 245);

    public BaseFrame(String title) {
        setTitle(title);
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(backgroundColor);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        try {
            ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/UI/logo.png")));
            setIconImage(img.getImage());
        } catch (Exception e) {
            System.out.println("Logo icon not found, using default.");
        }
    }

    protected JPanel createStatCard(String title, String value, Color borderCol) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, borderCol),
                BorderFactory.createEmptyBorder(15, 20, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 15));
        lblTitle.setForeground(new Color(100, 100, 100));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI Bold", Font.BOLD, 26));
        lblValue.setForeground(primaryColor);

        card.add(lblTitle);
        card.add(lblValue);
        return card;
    }

    protected JPanel createTopBar(String inventoryManagement) {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setPreferredSize(new Dimension(getWidth(), 65));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, accentColor));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        titlePanel.setOpaque(false);

        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/UI/logo.png")));
            Image scaledLogo = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaledLogo));
            titlePanel.add(lblLogo);
        } catch (Exception e) {/* Ignore if no image is found */}
        JLabel lblTitle = new JLabel("  " + "WAREHOUSE CONTROL - PRODUCTION SUPERVISOR");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 20));
        topBar.add(lblTitle, BorderLayout.WEST);
        return topBar;
    }

    protected JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 4;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.brighter());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    protected void showStyledMessage(String message, String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(primaryColor, 2));

        JLabel lbl = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton btn = createStyledButton("OK", primaryColor);
        btn.addActionListener(e -> dialog.dispose());

        JPanel btnPnl = new JPanel();
        btnPnl.setOpaque(false);
        btnPnl.add(btn);

        panel.add(lbl, BorderLayout.CENTER);
        panel.add(btnPnl, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    protected boolean showConfirmMessage(String message, String title) {
        final boolean[] result = {false};

        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(primaryColor, 3));

        JLabel lbl = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));

        JButton btnYes = createStyledButton("YES", new Color(46, 204, 113));
        btnYes.setPreferredSize(new Dimension(120, 40));
        btnYes.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        JButton btnNo = createStyledButton("NO", new Color(231, 76, 60));
        btnNo.setPreferredSize(new Dimension(120, 40));
        btnNo.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPnl.setOpaque(false);
        btnPnl.add(btnYes);
        btnPnl.add(btnNo);

        panel.add(lbl, BorderLayout.CENTER);
        panel.add(btnPnl, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);

        return result[0];
    }
}