package com.example.hospitalmanagementsys.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagementsys.R;

public class LoginActivity extends AppCompatActivity {
    private Button btnUser, btnDoctor, btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnUser = findViewById(R.id.btnUser);
        btnDoctor = findViewById(R.id.btnDoctor);
        btnAdmin = findViewById(R.id.btnAdmin);
    }

    private void setupClickListeners() {
        btnUser.setOnClickListener(v -> navigateToRoleLogin("user"));
        btnDoctor.setOnClickListener(v -> navigateToRoleLogin("doctor"));
        btnAdmin.setOnClickListener(v -> navigateToRoleLogin("admin"));
    }

    private void navigateToRoleLogin(String role) {
        Intent intent = new Intent(this, RoleLoginActivity.class);
        intent.putExtra("ROLE", role);
        startActivity(intent);
    }
} 