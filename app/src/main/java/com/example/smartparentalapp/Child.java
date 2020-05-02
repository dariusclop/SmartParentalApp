package com.example.smartparentalapp;

public class Child {
    private String id;
    private String email;
    private String displayName;
    private String generatedCode;
    private boolean isParent;
    private double latitude;
    private double longitude;

    public Child() {}

    public Child(String id, String email, String displayName, String generatedCode, double latitude, double longitude) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.generatedCode = generatedCode;
        this.isParent = false;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public boolean isParent() {
        return this.isParent;
    }

    //Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
