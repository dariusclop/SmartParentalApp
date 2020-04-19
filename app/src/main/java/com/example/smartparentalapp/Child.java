package com.example.smartparentalapp;

public class Child {
    private String id;
    private String email;
    private String displayName;
    private String generatedCode;

    public Child(String id, String email, String displayName, String generatedCode) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.generatedCode = generatedCode;
    }

    //Getters
    public String getChildId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getGeneratedCode() {
        return this.generatedCode;
    }

    //Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
