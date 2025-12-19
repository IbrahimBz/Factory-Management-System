package com.fsociety.factory.UI_Swing_PresentationLayer;
import javax.swing.*;
import java.awt.*;

public class AddProductionLinePanel extends JPanel {

    private JTextField lineIdField;
    private JTextField lineNameField;
    private JComboBox<String> statusComboBox ;
    private JButton addButton ;

    public AddProductionLinePanel(){

        setBackground(new Color(181, 175, 175));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10 ,10,10 ,10 );
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel(" Add New Production Line ");
        titleLabel.setFont(new Font("Arial", Font.BOLD , 29 ));

        gbc.gridx = 0 ;
        gbc.gridy = 0 ;
        gbc.gridwidth = 3 ;
        gbc.anchor = GridBagConstraints.CENTER;

        add(titleLabel , gbc );

        gbc.gridwidth = 1 ;
        gbc.anchor = GridBagConstraints.WEST;
        //--------------------------------------------------------------
        JLabel idLabel = new JLabel(" Number of Line : ");
        idLabel.setFont(new Font( "Arial" , Font.PLAIN , 16 ));
        gbc.gridx =  0 ;
        gbc.gridy = 1 ;
        add(idLabel, gbc );

        lineIdField = new JTextField(20) ;
        gbc.gridx = 1 ;
        gbc.gridy = 1 ;
        add(lineIdField , gbc);
        //---------------------------------------------------------------
        JLabel nameLabel = new JLabel(" Name of Line :");
        nameLabel.setFont(new Font( "Arial" , Font.PLAIN , 16));

        gbc.gridx = 0 ;
        gbc.gridy = 2 ;
        add(nameLabel,gbc);

        lineNameField = new JTextField(20);
        gbc.gridx = 1 ;
        gbc.gridy = 2 ;
        add(lineNameField , gbc);

        //---------------------------------------------------------------

        JLabel statusLabel = new JLabel(" Line Status :");
        statusLabel.setFont(new Font( "Arial" , Font.PLAIN , 16));
        gbc.gridx = 0 ;
        gbc.gridy = 3 ;
        add( statusLabel , gbc);

        //---------------------------------------------------------------

        String[] statuses = {" Active " , " Stopped " , " maintenance "};
        //***
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setPreferredSize( new Dimension(200 ,30));
        statusComboBox.setBackground(new Color(104, 108, 162));
        gbc.gridx = 1 ;
        gbc.gridy = 3 ;
        add( statusComboBox , gbc);

        //----------------------------------------------------------------

        addButton = new JButton(" add new line ");
        addButton.setFont(new Font( "Arial" , Font.BOLD , 16));
        addButton.setBackground(new Color(46 ,204 ,113));
        addButton.setForeground(Color.WHITE);

        gbc.gridx= 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2 ;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipady = 10;
        add(addButton ,gbc );
        //---------------------------------------------------------------
        addButton.addActionListener(e -> addNewProductionLine());
        //---------------------------------------------------------------

    }

    private void addNewProductionLine(){

        try{
            int lineId = Integer.parseInt(lineIdField.getText().trim());
            String lineName = lineNameField.getText().trim();

            if(lineName.isEmpty()){
                JOptionPane.showMessageDialog(this , " the name field cannot be left blank ", " Input Error ", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = (String) statusComboBox.getSelectedItem();
            String message = String.format(" the line has been successfully added : \n number: %d\n name: %s\n status: %s  ", lineId, lineName,status);
            JOptionPane.showMessageDialog(this, message , "Success" , JOptionPane.INFORMATION_MESSAGE);

            lineIdField.setText("");
            lineNameField.setText("");
            statusComboBox.setSelectedIndex(0);

        }catch (NumberFormatException ex){

            JOptionPane.showMessageDialog(this , " Please enter the correct number ", " Input Error ", JOptionPane.ERROR_MESSAGE);

        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(this , " An unexpected error occurred ", " Error ", JOptionPane.ERROR_MESSAGE);
        }

    }
}
