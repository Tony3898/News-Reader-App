package com.android.tony.newsx;

public class Users {
    private String name, email;

    Users() {

    }

    Users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
