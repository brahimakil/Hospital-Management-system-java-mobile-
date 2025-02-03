package com.example.hospitalmanagementsys.models;

import java.util.Date;

public class Admin {
    private String uid;
    private String name;
    private String email;
    private Date createdAt;
    private Date updatedAt;

    public Admin() {
        // Required empty constructor for Firebase
    }

    // Getters
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
} 