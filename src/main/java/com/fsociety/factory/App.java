package com.fsociety.factory;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;

public class App {

    public static void main(String[] args) {

        Inventory.getInstance();

        System.out.println("Hello World");
    }
}