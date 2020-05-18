package com.example.smartparentalapp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Parent {
    private String parentId;
    private String email;
    private String displayName;
    private List <Long> childIds;
    private String generatedCode;
    private boolean isParent;

    public Parent() {}

    public Parent(String parentId, String email, String displayName) {
        this.parentId = parentId;
        this.email = email;
        this.displayName = displayName;
        this.childIds = new ArrayList<>();
        this.generatedCode = parentId;
        this.isParent = true;
    }

    //Getters
    public String getParentId() {
        return this.parentId;
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

    public void setGeneratedCode(String generatedCode) {
        this.generatedCode = generatedCode;
    }

    //Methods
    public void connectToChild(Long childId) {
        this.childIds.add(childId);
    }

    public String generateCodeForChild() {
        setGeneratedCode(UUID.randomUUID().toString());
        return generatedCode;
    }
}
