package com.example.hospitalmanagementsys.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.dashboard.AdminDashboardActivity;
import com.example.hospitalmanagementsys.dashboard.doctor.DoctorDashboardActivity;
import com.example.hospitalmanagementsys.dashboard.UserDashboardActivity;
import com.example.hospitalmanagementsys.models.User;
import com.example.hospitalmanagementsys.utils.Constants;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        role = getIntent().getStringExtra("ROLE");
        if (role == null) {
            finish();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void navigateToDashboard() {
        Intent intent;
        switch (role.toLowerCase()) {
            case "doctor":
                intent = new Intent(this, DoctorDashboardActivity.class);
                break;
            case "admin":
                intent = new Intent(this, AdminDashboardActivity.class);
                break;
            default:
                intent = new Intent(this, UserDashboardActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUtils.signUp(email, password, task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                Map<String, Object> userData = new HashMap<>();
                userData.put(Constants.FIELD_NAME, name);
                userData.put(Constants.FIELD_EMAIL, email);
                userData.put(Constants.FIELD_CREATED_AT, new Date());
                userData.put(Constants.FIELD_UPDATED_AT, new Date());

                String collection;
                switch (role.toLowerCase()) {
                    case "doctor":
                        collection = Constants.COLLECTION_DOCTORS;
                        break;
                    case "admin":
                        collection = Constants.COLLECTION_ADMINS;
                        break;
                    default:
                        collection = Constants.COLLECTION_USERS;
                }

                FirebaseUtils.addUserToCollection(collection, uid, userData,
                    aVoid -> {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    },
                    e -> {
                        Toast.makeText(this, "Failed to create profile: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                        // Delete the auth user if profile creation fails
                        FirebaseUtils.getCurrentUser().delete();
                    }
                );
            } else {
                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
} 