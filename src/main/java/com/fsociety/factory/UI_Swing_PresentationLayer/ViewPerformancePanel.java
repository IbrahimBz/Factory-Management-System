package com.fsociety.factory.UI_Swing_PresentationLayer;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ViewPerformancePanel extends JPanel {

    private JTable performanceTable;
    private JScrollPane tableScrollPane;
    private JButton evaluateButton;

    //***************************************
    private Object[][] mockData = {

            {
                    " line one : Electronic assembly  ", " Active ", 85, " 1000 Ipad ", " No  Comments "
            },
            {
                    " line two : Packaging  ", " maintenance ", 10, " 500 cardboard boxes ", " it needs emergency maintenance "
            },
            {
                    " line three : inspection and quality ", " Active  ", 100, " 200 laptop ", " Excellent Performance , no flaws  "
            }

    };
    //****************************************


    private String[] columnNames = { " Line Name "," Status ",  " Completion Rate (%)" ,"Last Product " , " Notes "};

    public ViewPerformancePanel() {

        setBackground(new Color(149, 165, 166));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //------------------------------------------------------------
        JLabel titleLabel = new JLabel(" Display Performance of Production Lines and evaluation ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);
        //------------------------------------------------------------
        DefaultTableModel tableModel = new DefaultTableModel(mockData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return (column == 4);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {

                if (columnIndex == 2) {
                    return Integer.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        //--------------------------------------------
        performanceTable = new JTable(tableModel);
        performanceTable.setRowHeight(30);
        performanceTable.setFont(new Font("Arial", Font.BOLD, 14));
        performanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        performanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //---------------------------------------------
        tableScrollPane = new JScrollPane(performanceTable);
        add(tableScrollPane, BorderLayout.CENTER);
        //---------------------------------------------
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        //---------------------------------------------
        //---------------------------------------------

        evaluateButton = new JButton(" Save Notes ");
        evaluateButton.setFont(new Font("Arial", Font.BOLD, 16));
        evaluateButton.setBackground(new Color(230, 126, 34));
        evaluateButton.setForeground(Color.WHITE);
        evaluateButton.setFocusPainted(false);
        controlsPanel.add(evaluateButton);
        add(controlsPanel, BorderLayout.SOUTH);
        //--------------------------------------------
        evaluateButton.addActionListener(e -> saveEvaluation());
        //--------------------------------------------


    }

    private void saveEvaluation(){

        DefaultTableModel model = (DefaultTableModel) performanceTable.getModel();
        int rowCount = model.getRowCount();
        int selectedRow = performanceTable.getSelectedRow();

        if( selectedRow == -1 ){
            JOptionPane.showMessageDialog( this , " Please select production line from the table to save its rating. " , " Alert " , JOptionPane.WARNING_MESSAGE);
            return;
        }

        String lineName = (String) model.getValueAt(selectedRow , 0);
        String newNote = (String) model.getValueAt(selectedRow ,4);


        //*******************
        //******************* change here to system info
        //*******************
    JOptionPane.showMessageDialog( this , String.format(" The Notes and Evaluation have been saved : %s\n Recorded Notes : %s " , lineName , newNote));

    }


}
