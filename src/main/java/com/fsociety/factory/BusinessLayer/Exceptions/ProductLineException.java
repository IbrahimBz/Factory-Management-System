package com.fsociety.factory.BusinessLayer.Exceptions;

public class ProductLineException extends RuntimeException {
    public ProductLineException(String message) {
        super(message);
    }

    public String productLineIsNotAvailableException() {
        return "";
    }


}
