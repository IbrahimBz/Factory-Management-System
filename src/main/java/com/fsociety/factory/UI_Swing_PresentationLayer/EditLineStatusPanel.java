package com.fsociety.factory.UI_Swing_PresentationLayer;

import javax.swing.*;
import java.awt.*;

public class EditLineStatusPanel extends JPanel {

    private static final String[] MOCK_PRODUCTION_LINES = { " line one " , " line two " , " line three " };// should be changed and get the lines from the system

    private JComboBox<String> lineSelectorComboBox ;
    private JComboBox<String> statusComboBox ;
    private JButton updateButton ;

    public EditLineStatusPanel(){

        setBackground(new Color(189 , 195 , 199));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets =new Insets(10 ,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;
        //--------------------------------------------
        JLabel titleLabel = new JLabel("Edit Production Line Status");
        titleLabel.setFont(new Font("Arial" , Font.BOLD , 28));
        gbc.gridx = 0 ;
        gbc.gridy = 0 ;
        gbc.gridwidth = 2 ;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc );

        //---------------------------------------------
        gbc.gridwidth = 1 ;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel selectLineLabel = new JLabel(" Select production Line : ");
        selectLineLabel.setFont(new Font("Arial", Font.PLAIN , 16 ));
        gbc.gridx = 0 ;
        gbc.gridy = 1;
        add(selectLineLabel , gbc );

        lineSelectorComboBox = new JComboBox<>(MOCK_PRODUCTION_LINES);
        lineSelectorComboBox.setPreferredSize(new Dimension(250 ,30 ));
        gbc.gridx =1;
        gbc.gridy = 1 ;
        add(lineSelectorComboBox, gbc);

        //-----------------------------------------------
        JLabel statusLabel = new JLabel(" The new status of Line :") ;
        statusLabel.setFont(new Font("Arial" , Font.PLAIN , 16));
        gbc.gridx = 0 ;
        gbc.gridy = 2 ;
        add(statusLabel , gbc );

        //-----------------------------------------------

        String[] statuses = { " Active " , " Stopped " , " maintenance "};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setPreferredSize( new Dimension( 250 , 30 ));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(statusComboBox , gbc);

        //------------------------------------------------
        updateButton = new JButton(" Status update confirmation ");
        updateButton.setFont( new Font( "Arial" , Font.BOLD , 16));
        updateButton.setBackground( new Color(52 ,152 , 219));
        updateButton.setForeground(Color.WHITE);
        gbc.gridx = 0 ;
        gbc.gridy =3 ;
        gbc.gridwidth = 2 ;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipady = 10 ;
        add(updateButton, gbc);
        //------------------------------------------------
        updateButton.addActionListener( e -> updateLineStatus());
        //------------------------------------------------


    }
    //************************************* method *******************

    private void updateLineStatus (){

        String selectedLine = (String) lineSelectorComboBox.getSelectedItem();
        String newStatus = (String) statusComboBox.getSelectedItem();

        if(selectedLine == null) {

            JOptionPane.showMessageDialog(this, " Please choose a Line first ", " Error ", JOptionPane.ERROR_MESSAGE);
        }

            try{
                // here should change and add system logic *****---*****

                String message = String.format( " the Line Status has been updated : \n the line : %s\n the new status : %s " , selectedLine , newStatus);
                JOptionPane.showMessageDialog(this , message , " Successful update " , JOptionPane.INFORMATION_MESSAGE);

            }catch( Exception ex ){
                // here should change

                JOptionPane.showMessageDialog(this , " An Error occurred during the update  " +ex.getMessage() , " Error " , JOptionPane.ERROR_MESSAGE );
            }
        }

    //****************************************************************
}
