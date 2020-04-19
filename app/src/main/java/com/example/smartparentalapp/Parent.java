package com.example.smartparentalapp;

import java.util.ArrayList;
import java.util.List;

public class Parent {
    private String id;
    private String email;
    private String displayName;
    private List <Long> childIds;

    public Parent(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.childIds = new ArrayList<>();
    }

    //Getters
    public String getParentId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    //Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    //Methods
    public void connectToChild(Long childId) {
        this.childIds.add(childId);
    }
}
