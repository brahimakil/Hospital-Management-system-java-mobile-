package com.example.hospitalmanagementsys.utils;

public class Constants {
    // Firebase Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_DOCTORS = "doctors";
    public static final String COLLECTION_ADMINS = "admins";
    public static final String COLLECTION_APPOINTMENTS = "appointments";
    
    // Common Fields
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SPECIALTY = "specialty";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_BIRTH_DATE = "birthDate";
    public static final String FIELD_BLOOD_TYPE = "bloodType";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_UPDATED_AT = "updatedAt";
    
    // Common Status
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    
    // Gender Options
    public static final String[] GENDER_OPTIONS = {"Male", "Female", "Other"};
    
    // Blood Type Options
    public static final String[] BLOOD_TYPE_OPTIONS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
} 