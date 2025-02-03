package com.example.hospitalmanagementsys.models;

import java.util.Date;

public class Appointment {
    private String uid;
    private String doctorId;
    private String userId;
    private String doctorName;
    private String userName;
    private Date appointmentDate;
    private Date createdAt;
    private Date updatedAt;
    private String status; // "scheduled", "completed", "cancelled"

    public Appointment() {
        // Required empty constructor for Firebase
    }

    // Getters
    public String getUid() { return uid; }
    public String getDoctorId() { return doctorId; }
    public String getUserId() { return userId; }
    public String getDoctorName() { return doctorName; }
    public String getUserName() { return userName; }
    public Date getAppointmentDate() { return appointmentDate; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public String getStatus() { return status; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public void setStatus(String status) { this.status = status; }
} 