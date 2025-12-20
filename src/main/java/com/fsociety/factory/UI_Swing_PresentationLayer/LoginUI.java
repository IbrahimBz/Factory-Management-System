package com.fsociety.factory.UI_Swing_PresentationLayer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class LoginUI extends BaseFrame {
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, " Verification error\n", JOptionPane.ERROR_MESSAGE);
    }

    public LoginUI() {
        super("Factorial App - System Access");
        setLayout(new BorderLayout());

        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/UI/1111.jpg")));
                    g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(0, 40, 40));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        JPanel leftSidePanel = new JPanel();
        leftSidePanel.setLayout(new BoxLayout(leftSidePanel, BoxLayout.Y_AXIS));
        leftSidePanel.setBackground(new Color(0, 0, 0, 190));
        leftSidePanel.setBorder(new EmptyBorder(60, 50, 60, 50));

        JLabel lblWelcome = new JLabel("Welcome to");
        lblWelcome.setFont(new Font("Arial", Font.PLAIN, 35));
        lblWelcome.setForeground(new Color(0, 251, 255));
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSystem = new JLabel("Factorial System");
        lblSystem.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52));
        lblSystem.setForeground(Color.WHITE);
        lblSystem.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Advanced Factory Management");
        lblSubTitle.setFont(new Font("Arial", Font.ITALIC, 22));
        lblSubTitle.setForeground(new Color(0, 251, 255));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtDescription = getJTextArea();

        JLabel lblSecure = new JLabel("  Secure Access Required");
        lblSecure.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSecure.setForeground(new Color(0, 251, 255));
        lblSecure.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(0, 251, 255)));
        lblSecure.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftSidePanel.add(lblWelcome);
        leftSidePanel.add(lblSystem);
        leftSidePanel.add(lblSubTitle);
        leftSidePanel.add(Box.createVerticalStrut(40));
        leftSidePanel.add(txtDescription);
        leftSidePanel.add(Box.createVerticalStrut(30));
        leftSidePanel.add(lblSecure);
        gbc.gridx = 0;
        gbc.weightx = 0.40;
        bgPanel.add(leftSidePanel, gbc);

        JPanel rightSidePanel = new JPanel(new GridBagLayout());
        rightSidePanel.setBackground(new Color(0, 102, 102, 220));
        JPanel loginFormContainer = new JPanel(null);
        loginFormContainer.setOpaque(false);
        loginFormContainer.setPreferredSize(new Dimension(450, 450));

        JLabel lblLogin = new JLabel("LOGIN", SwingConstants.CENTER);
        lblLogin.setFont(new Font("Segoe UI Black", Font.BOLD, 55));
        lblLogin.setForeground(Color.WHITE);
        lblLogin.setBounds(0, 20, 450, 60);
        loginFormContainer.add(lblLogin);

        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(new Color(180, 255, 255));
        lblUser.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        lblUser.setBounds(40, 110, 370, 25);
        loginFormContainer.add(lblUser);

        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(40, 140, 370, 45);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtEmail.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        txtEmail.setBackground(new Color(0, 120, 120));
        txtEmail.setForeground(Color.WHITE);
        txtEmail.setCaretColor(Color.WHITE);
        loginFormContainer.add(txtEmail);

        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(new Color(180, 255, 255));
        lblPass.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        lblPass.setBounds(40, 210, 370, 25);
        loginFormContainer.add(lblPass);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(40, 240, 370, 45);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPass.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        txtPass.setBackground(new Color(0, 120, 120));
        txtPass.setForeground(Color.WHITE);
        txtPass.setCaretColor(Color.WHITE);
        loginFormContainer.add(txtPass);

        JButton btnLogin = createStyledButton("LOGIN NOW", new Color(0, 200, 200));
        btnLogin.setBounds(40, 330, 175, 50);
        btnLogin.addActionListener(e -> {
            String username = txtEmail.getText().trim();
            String password = new String(txtPass.getPassword());

            if (username.equals("admin") && password.equals("1234")) {
                showStyledMessage("Access Granted. Welcome back!", "Success");

                new MainDashboard().setVisible(true);
                this.dispose();
            } else {
                showStyledMessage("Invalid credentials.", "Auth Error");
            }
        });

        JButton btnCancel = createStyledButton("Cancel", new Color(150, 0, 0));
        btnCancel.setBounds(235, 330, 175, 50);

        btnCancel.addActionListener(e -> {
            if (showConfirmMessage("Are you sure you want to exit the system?", "Confirm Exit")) {
                System.exit(0);
            }
        });

        loginFormContainer.add(btnLogin);
        loginFormContainer.add(btnCancel);

        rightSidePanel.add(loginFormContainer);

        gbc.gridx = 1;
        gbc.weightx = 0.60;
        bgPanel.add(rightSidePanel, gbc);

        add(bgPanel, BorderLayout.CENTER);

        JButton btnSignUp = new JButton("Don't have an account? Sign Up Here");
        btnSignUp.setBounds(40, 400, 370, 30);
        btnSignUp.setForeground(new Color(180, 255, 255));
        btnSignUp.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        btnSignUp.setContentAreaFilled(false);
        btnSignUp.setBorderPainted(false);
        btnSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSignUp.addActionListener(e -> openSignUpDialog());

        loginFormContainer.add(btnLogin);
        loginFormContainer.add(btnCancel);
        loginFormContainer.add(btnSignUp);

    }

    private static JTextArea getJTextArea() {
        JTextArea txtDescription = new JTextArea("An integrated environment for tracking inventory, " +
                "managing production, and monitoring real-time performance.");
        txtDescription.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 18));
        txtDescription.setForeground(new Color(220, 220, 220));
        txtDescription.setOpaque(false);
        txtDescription.setEditable(false);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setMaximumSize(new Dimension(380, 150));
        txtDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
        return txtDescription;
    }

    private void openSignUpDialog() {
        JDialog signUpDialog = new JDialog(this, "Registration", true);
        signUpDialog.setUndecorated(true);
        signUpDialog.setSize(450, 520);
        signUpDialog.setLocationRelativeTo(this);
        signUpDialog.setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(235, 245, 245));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                g2d.setColor(primaryColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 40, 40);
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // __
        JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        header.setOpaque(false);
        JLabel lblClose = new JLabel("✕");
        lblClose.setFont(new Font("Arial", Font.BOLD, 20));
        lblClose.setForeground(primaryColor);
        lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                signUpDialog.dispose();
            }
        });
        header.add(lblClose);

        JLabel lblTitle = new JLabel("New System User");
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 26));
        lblTitle.setForeground(primaryColor);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        fieldsPanel.add(Box.createVerticalStrut(30));

        JTextField txtUser = createModernField();
        fieldsPanel.add(txtUser);

        fieldsPanel.add(Box.createVerticalStrut(20));

        JPasswordField txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(400, 50));
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPass.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 220, 220)), "Password"));
        fieldsPanel.add(txtPass);

        fieldsPanel.add(Box.createVerticalStrut(20));

        String[] roles = {"Production Supervisor", "Warehouse Manager"};
        JComboBox<String> combo = new JComboBox<>(roles);
        combo.setMaximumSize(new Dimension(400, 50));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primaryColor), "Select Access Role"));
        fieldsPanel.add(combo);

        JButton btnRegister = createStyledButton("REQUEST ACCESS", primaryColor);
        btnRegister.setPreferredSize(new Dimension(300, 55));
        btnRegister.addActionListener(e -> {
            showStyledMessage("Account created successfully! Use these credentials to login.", "Success");
            signUpDialog.dispose();
        });

        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnContainer.setOpaque(false);
        btnContainer.add(btnRegister);

        mainPanel.add(header, BorderLayout.NORTH);

        JPanel centerContent = new JPanel(new BorderLayout());
        centerContent.setOpaque(false);
        centerContent.add(lblTitle, BorderLayout.NORTH);
        centerContent.add(fieldsPanel, BorderLayout.CENTER);
        centerContent.add(btnContainer, BorderLayout.SOUTH);

        mainPanel.add(centerContent, BorderLayout.CENTER);

        signUpDialog.add(mainPanel);
        signUpDialog.setVisible(true);
    }

    private JTextField createModernField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(400, 50));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Desired Username"));
        return field;
    }

    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        splash.startLoading();

        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
}