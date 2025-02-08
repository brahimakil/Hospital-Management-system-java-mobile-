package com.example.hospitalmanagementsys.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment {
    private String uid;
    private String doctorId;
    private String doctorName;
    private String userId;
    private String userName;
    private Date appointmentDate;
    private String status; // "scheduled", "completed", "cancelled"

    public Appointment() {
        // Required empty constructor for Firestore
    }

    public String getFormattedDateTime() {
        if (appointmentDate == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(appointmentDate);
    }

    // Getters
    public String getUid() { return uid; }
    public String getDoctorId() { return doctorId; }
    public String getUserId() { return userId; }
    public String getDoctorName() { return doctorName; }
    public String getUserName() { return userName; }
    public Date getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setStatus(String status) { this.status = status; }
} 