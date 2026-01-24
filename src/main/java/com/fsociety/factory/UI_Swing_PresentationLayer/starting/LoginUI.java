package com.fsociety.factory.UI_Swing_PresentationLayer.starting;

import com.fsociety.factory.UI_Swing_PresentationLayer.manager.ManagerDashboard;
import com.fsociety.factory.UI_Swing_PresentationLayer.supervisor.SupervisorDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class LoginUI extends BaseFrame {
    private final boolean isManagerRole;

    public LoginUI(boolean isManager) {
        super("Factorial App - System Access");
        this.isManagerRole = isManager;
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

        // --- الجزء الأيسر ---
        JPanel leftSidePanel = new JPanel();
        leftSidePanel.setLayout(new BoxLayout(leftSidePanel, BoxLayout.Y_AXIS));
        leftSidePanel.setBackground(new Color(0, 0, 0, 190));
        leftSidePanel.setBorder(new EmptyBorder(60, 50, 60, 50));

        JLabel lblWelcome = new JLabel("Welcome to");
        lblWelcome.setFont(new Font("Arial", Font.PLAIN, 35));
        lblWelcome.setForeground(new Color(0, 251, 255));
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        String displayRole = isManager ? "Manager Portal" : "Supervisor Portal";
        JLabel lblSystem = new JLabel(displayRole);
        lblSystem.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52));
        lblSystem.setForeground(Color.WHITE);
        lblSystem.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Advanced Factory Management");
        lblSubTitle.setFont(new Font("Arial", Font.ITALIC, 22));
        lblSubTitle.setForeground(new Color(0, 251, 255));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftSidePanel.add(lblWelcome);
        leftSidePanel.add(lblSystem);
        leftSidePanel.add(lblSubTitle);
        leftSidePanel.add(Box.createVerticalStrut(40));
        leftSidePanel.add(getJTextArea());

        gbc.gridx = 0; gbc.weightx = 0.40;
        bgPanel.add(leftSidePanel, gbc);

        // --- الجزء الأيمن (نموذج الدخول) ---
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

        // حقل المستخدم
        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(40, 140, 370, 45);
        styleTextField(txtEmail);
        loginFormContainer.add(txtEmail);

        // حقل كلمة المرور
        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(40, 240, 370, 45);
        styleTextField(txtPass);
        loginFormContainer.add(txtPass);

        // زر الدخول مع التحقق الشرطي
        JButton btnLogin = createStyledButton("LOGIN NOW", new Color(0, 200, 200));
        btnLogin.setBounds(40, 330, 175, 50);
        btnLogin.addActionListener(e -> {
            String user = txtEmail.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (isManagerRole) {
                // بيانات تجريبية للمدير
                if (user.equals("admin") && pass.equals("1234")) {
                    showStyledMessage("Manager Access Granted!", "Success");
                     new ManagerDashboard().setVisible(true);
                    this.dispose();
                } else {
                    showStyledMessage("Invalid Manager Credentials", "Error");
                }
            } else {
                // بيانات تجريبية للمشرف
                if (user.equals("supervisor") && pass.equals("pass123")) {
                    showStyledMessage("Supervisor Access Granted!", "Success");
                    new SupervisorDashboard().setVisible(true);
                    this.dispose();
                } else {
                    showStyledMessage("Invalid Supervisor Credentials", "Error");
                }
            }
        });

        JButton btnCancel = createStyledButton("Cancel", new Color(150, 0, 0));
        btnCancel.setBounds(235, 330, 175, 50);
        btnCancel.addActionListener(e -> {
            new RoleSelectionUI().setVisible(true);
            this.dispose();
        });

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
        rightSidePanel.add(loginFormContainer);

        gbc.gridx = 1; gbc.weightx = 0.60;
        bgPanel.add(rightSidePanel, gbc);

        add(bgPanel, BorderLayout.CENTER);
    }

    // تنسيق الحقول لتبقى بنفس الشكل
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        field.setBackground(new Color(0, 120, 120));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
    }

    private void openSignUpDialog() {
        // إعداد الـ Dialog
        JDialog signUpDialog = new JDialog(this, "System Registration", true);
        signUpDialog.setSize(450, 550);
        signUpDialog.setLocationRelativeTo(this);
        signUpDialog.setResizable(false);

        // الحاوية الرئيسية مع خلفية متناسقة
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(0, 51, 51)); // نفس روح ألوان الـ Dashboard
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        // العنوان
        String roleType = isManagerRole ? "MANAGER" : "SUPERVISOR";
        JLabel lblTitle = new JLabel("CREATE " + roleType + " ACCOUNT");
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 251, 255));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // الحقول النصية (نستخدم نفس تنسيق الـ Login)
        JTextField txtNewUser = new JTextField();
        txtNewUser.setPreferredSize(new Dimension(300, 40));
        styleTextField(txtNewUser);

        JPasswordField txtNewPass = new JPasswordField();
        txtNewPass.setPreferredSize(new Dimension(300, 40));
        styleTextField(txtNewPass);

        JPasswordField txtConfirmPass = new JPasswordField();
        txtConfirmPass.setPreferredSize(new Dimension(300, 40));
        styleTextField(txtConfirmPass);

        // تسميات الحقول (Labels)
        JLabel lblU = createFieldLabel("Choose Username");
        JLabel lblP = createFieldLabel("Choose Password");
        JLabel lblCP = createFieldLabel("Confirm Password");

        // زر التسجيل
        JButton btnSubmit = createStyledButton("REGISTER NOW", new Color(0, 180, 180));
        btnSubmit.setPreferredSize(new Dimension(300, 50));
        btnSubmit.addActionListener(e -> {
            // هنا يمكنك إضافة منطق الحفظ في الملفات لاحقاً
            showStyledMessage("Registration Request Sent to Admin!", "Success");
            signUpDialog.dispose();
        });

        // إضافة العناصر للـ Layout
        gbc.gridy = 0; mainPanel.add(lblTitle, gbc);
        gbc.gridy = 1; mainPanel.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy = 2; mainPanel.add(lblU, gbc);
        gbc.gridy = 3; mainPanel.add(txtNewUser, gbc);
        gbc.gridy = 4; mainPanel.add(lblP, gbc);
        gbc.gridy = 5; mainPanel.add(txtNewPass, gbc);
        gbc.gridy = 6; mainPanel.add(lblCP, gbc);
        gbc.gridy = 7; mainPanel.add(txtConfirmPass, gbc);
        gbc.gridy = 8; mainPanel.add(Box.createVerticalStrut(30), gbc);
        gbc.gridy = 9; mainPanel.add(btnSubmit, gbc);

        signUpDialog.add(mainPanel);
        signUpDialog.setVisible(true);
    }

    // دالة مساعدة لإنشاء نصوص الحقول الصغيرة
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(180, 255, 255));
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        return label;
    }

    private static JTextArea getJTextArea() {
        JTextArea txtDescription = new JTextArea("Authorized personnel only. Access level is determined by the role selected in the previous step.");
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

    public static void main(String[] args) {
        // 1. إنشاء وإظهار شاشة الترحيب أولاً
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        // 2. تشغيل مؤقت أو Thread لمحاكاة التحميل
        new Thread(() -> {
            try {
                // تنفيذ دالة التحميل (التي تحرك شريط التقدم)
                splash.startLoading();

                // انتهاء التحميل وإغلاق الـ Splash
                splash.dispose();

                // 3. فتح واجهة اختيار الأدوار بعد انتهاء التحميل
                SwingUtilities.invokeLater(() -> {
                    new RoleSelectionUI().setVisible(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}