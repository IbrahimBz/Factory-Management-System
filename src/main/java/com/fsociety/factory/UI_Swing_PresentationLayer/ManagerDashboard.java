package com.fsociety.factory.UI_Swing_PresentationLayer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ManagerDashboard extends JFrame {


    //------------------------------------------
    private final int  FRAME_WIDTH = 1280;
    private final int FRAME_HEIGHT = 720 ;
    //------------------------------------------
    private CardLayout cardLayout ;
    private JPanel contentPanel;
    //------------------------------------------

    private static final String ADD_LINE_CARD = "AddProductionLine" ;
    private static final String EDIT_STATUS_CARD = "EditLineStatus";
    private static final String VIEW_PERFORMANCE_CARD = "ViewPerformance";
    private static final String DEFAULT_CARD = "DefaultView ";

    //***************************************************************************
   public ManagerDashboard(){

       this.setLocationRelativeTo(null);
       this.setLocation(100,50);
       this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
    // this.setResizable(false);             //   *********** off ************
       this.setTitle(" Manager Dashboard ");
       this.setSize( FRAME_WIDTH,  FRAME_HEIGHT);
       //------------------------------
        JPanel navigationPanel = createNavigationPanel();
       this.add(navigationPanel , BorderLayout.WEST);
       //------------------------------
       cardLayout = new CardLayout();
       contentPanel = new JPanel(cardLayout);
       addInitialCards(contentPanel);
       this.add(contentPanel ,BorderLayout.CENTER);
       //------------------------------
       cardLayout.show(contentPanel, DEFAULT_CARD);
       //*********************
       this.setVisible(true);
       //*********************
   }
    //***************************************************************************
   private JPanel createNavigationPanel(){

       JPanel sidebar = new JPanel();
       sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
       sidebar.setPreferredSize(new Dimension(250 , FRAME_HEIGHT));
       sidebar.setBackground(new Color( 0 , 102 , 102 ));
       //-----------------------------
       sidebar.setBorder( BorderFactory.createEmptyBorder(30 , 10 , 30 ,10));
       //-----------------------------

       JButton addProductLineBtn = buttonMaker(" Add Product Line ");
       JButton editLineStatusBtn = buttonMaker(" Edit Line Status ");
       JButton viewPerformanceBtn  =  buttonMaker(" Display Line Info ");

       //-----------------------------
       JButton logoutBtn = new JButton( " Exit ");
       logoutBtn.setFont(new Font("Arial" , Font.BOLD, 20));
       logoutBtn.setForeground(new Color(165, 6, 6));
       logoutBtn.setBackground(new Color(228, 95, 97));
       logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
       //-----------------------------
       //--------------------------------------------------------
       addProductLineBtn.addActionListener(e -> cardLayout.show(contentPanel, ADD_LINE_CARD));
       editLineStatusBtn.addActionListener(e -> cardLayout.show(contentPanel, EDIT_STATUS_CARD));
       viewPerformanceBtn.addActionListener(e -> cardLayout.show(contentPanel, VIEW_PERFORMANCE_CARD));
       logoutBtn.addActionListener(e -> {
                   JOptionPane.showMessageDialog(this, " You will logged out ");
                   this.dispose();});
       //--------------------------------------------------------
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(addProductLineBtn);
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(editLineStatusBtn);
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(viewPerformanceBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));
       //-----------------------------
       return sidebar;

   }
       //*******************************  method
      private JButton buttonMaker( String text ){

        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200,50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(new Color(0,102,102));
        button.setBackground(new Color(173,216,230));
        button.setFocusPainted(false);

        return button ;

   }
     //********************************


    private void addInitialCards(JPanel panel) {


        JPanel defaultView = new JPanel();
        defaultView.setLayout(new GridLayout());
        defaultView.setBackground(Color.WHITE);
        ImageIcon icon = new ImageIcon(ManagerDashboard.class.getResource("/images/manager_image.jpeg"));                   //************ remember add icon *****************
        Image scaledImage = icon.getImage().getScaledInstance(1000, 800,Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel label = new JLabel(scaledIcon);
        defaultView.add(label);
        panel.add(defaultView, DEFAULT_CARD);

        //-------------------------------------------------------------------------------------------

        AddProductionLinePanel addLinePanel = new AddProductionLinePanel();
        panel.add(addLinePanel, ADD_LINE_CARD);

        //-------------------------------------------------------------------------------------------

        EditLineStatusPanel editStatusPanel = new EditLineStatusPanel();
        panel.add(editStatusPanel, EDIT_STATUS_CARD);

        //-------------------------------------------------------------------------------------------

        ViewPerformancePanel viewPerformancePanel = new ViewPerformancePanel();
        panel.add(viewPerformancePanel, VIEW_PERFORMANCE_CARD);


    }

}







