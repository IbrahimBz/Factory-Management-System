package com.fsociety.factory;

import com.fsociety.factory.UI_Swing_PresentationLayer.*;
import com.fsociety.factory.dataAccessLayer.AccessItems;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println(
                AccessItems.loadItemsFromCSVFile());

        ManagerDashboard gop = new ManagerDashboard();
        gop.setVisible(true);


    }
}
