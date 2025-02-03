package com.example.hospitalmanagementsys;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hospitalmanagementsys.auth.LoginActivity;
import com.example.hospitalmanagementsys.dashboard.AdminDashboardActivity;
import com.example.hospitalmanagementsys.dashboard.doctor.DoctorDashboardActivity;
import com.example.hospitalmanagementsys.dashboard.UserDashboardActivity;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            // Check in admin collection first
            FirebaseUtils.getUserRole(uid, "admin", adminTask -> {
                if (adminTask.isSuccessful() && adminTask.getResult().exists()) {
                    startActivity(new Intent(this, AdminDashboardActivity.class));
                    finish();
                } else {
                    // Check in doctor collection
                    FirebaseUtils.getUserRole(uid, "doctor", doctorTask -> {
                        if (doctorTask.isSuccessful() && doctorTask.getResult().exists()) {
                            startActivity(new Intent(this, DoctorDashboardActivity.class));
                        } else {
                            startActivity(new Intent(this, UserDashboardActivity.class));
                        }
                        finish();
                    });
                }
            });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}