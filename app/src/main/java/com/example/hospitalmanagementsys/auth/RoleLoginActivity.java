package com.example.hospitalmanagementsys.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.dashboard.AdminDashboardActivity;
import com.example.hospitalmanagementsys.dashboard.doctor.DoctorDashboardActivity;
import com.example.hospitalmanagementsys.dashboard.UserDashboardActivity;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.textfield.TextInputEditText;

public class RoleLoginActivity extends AppCompatActivity {
    private TextView tvRoleTitle;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_login);

        role = getIntent().getStringExtra("ROLE");
        if (role == null) {
            finish();
            return;
        }

        initViews();
        setupViews();
        setupClickListeners();
    }

    private void initViews() {
        tvRoleTitle = findViewById(R.id.tvRoleTitle);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupViews() {
        String titleText = role.substring(0, 1).toUpperCase() + role.substring(1) + " Login";
        tvRoleTitle.setText(titleText);
        
        // Hide register button for admin and doctor roles
        if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("doctor")) {
            btnRegister.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
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

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUtils.signIn(email, password, task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                // Check user's role in specific collection
                FirebaseUtils.getUserRole(uid, role, roleTask -> {
                    if (roleTask.isSuccessful() && roleTask.getResult().exists()) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        Toast.makeText(this, "Unauthorized access for this role", Toast.LENGTH_SHORT).show();
                        FirebaseUtils.signOut();
                    }
                });
            } else {
                Toast.makeText(this, "Login failed: " + task.getException().getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("ROLE", role);
        startActivity(intent);
    }
} 