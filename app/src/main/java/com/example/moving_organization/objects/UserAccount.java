package com.example.moving_organization.objects;

public class UserAccount {

    private String username;
    private String password;


    // Constructor


    public UserAccount()
    {}


    public UserAccount(String user, String pass)
    {
        this.username = user;
        this.password = pass;
    }


    // Getters and Setters


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
