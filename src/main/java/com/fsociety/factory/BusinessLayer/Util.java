package com.fsociety.factory.BusinessLayer;

public class Util {

    public static enum enObjectMode {

        ADDNEW,
        UPDATE,
        DELETE;

        @Override
        public String toString() {
            switch (this) {
                case ADDNEW:
                    return "Add new mode";
                case UPDATE:
                    return "Update mode";
                case DELETE:
                    return "Delete mode";
                default:
                    return super.toString();
            }
        }



    }

}
