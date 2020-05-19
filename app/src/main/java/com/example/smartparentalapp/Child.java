package com.example.smartparentalapp;

public class Child {
    private String childId;
    private String email;
    private String displayName;
    private String generatedCode;
    private boolean isParent;
    private double latitude;
    private double longitude;

    public Child() {}

    public Child(String childId, String email, String displayName, String generatedCode) {
        this.childId = childId;
        this.email = email;
        this.displayName = displayName;
        this.generatedCode = generatedCode;
        this.isParent = false;
        this.latitude = 0;
        this.longitude = 0;
    }

    //Getters
    public String getChildId() {
        return this.childId;
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

    public double getLatitude() { return this.latitude; }

    public double getLongitude() { return this.longitude; }

    //Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }
}
