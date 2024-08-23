package com.example.getmech;

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean isMechanic;

    public User(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;

    }

    public User(String firstName, String lastName, String email, String phone, boolean isMechanic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.isMechanic = isMechanic;
    }


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }



    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String firstName, String lastName, String email, boolean isMechanic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isMechanic = isMechanic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public boolean getIsMechanic() {
        return isMechanic;
    }

    public void setIsMechanic(boolean isMechanic) {
        this.isMechanic = isMechanic;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
