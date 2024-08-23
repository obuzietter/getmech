package com.example.getmech;

public class Request {
    private String customerID;

    public Request() {
        // Default constructor required for calls to DataSnapshot.getValue(Request.class)
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
}