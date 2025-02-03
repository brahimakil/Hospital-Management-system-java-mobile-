package com.example.hospitalmanagementsys.models;

import java.util.Date;

public class Doctor {
    private String uid;
    private String name;
    private String email;
    private String gender;
    private Date birthDate;
    private String bloodType;
    private double height;
    private double weight;
    private String specialty;
    private Date createdAt;
    private Date updatedAt;

    public Doctor() {
        // Required empty constructor for Firebase
    }

    public Doctor(String uid, String name, String email, String specialty) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.specialty = specialty;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public Date getBirthDate() { return birthDate; }
    public String getBloodType() { return bloodType; }
    public double getHeight() { return height; }
    public double getWeight() { return weight; }
    public String getSpecialty() { return specialty; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setHeight(double height) { this.height = height; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name;  // This will show in the Spinner
    }
} 