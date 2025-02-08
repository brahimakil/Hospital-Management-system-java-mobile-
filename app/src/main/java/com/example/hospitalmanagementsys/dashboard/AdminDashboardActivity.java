package com.example.hospitalmanagementsys.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.auth.LoginActivity;
import com.example.hospitalmanagementsys.dashboard.admin.*;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button btnManageUsers, btnManageDoctors, btnManageAdmins, btnManageAppointments;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Dashboard");

        initViews();
        setupClickListeners();
        
        tvWelcome.setText("Welcome Admin: " + FirebaseUtils.getCurrentUser().getEmail());
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageDoctors = findViewById(R.id.btnManageDoctors);
        btnManageAdmins = findViewById(R.id.btnManageAdmins);
        btnManageAppointments = findViewById(R.id.btnManageAppointments);
    }

    private void setupClickListeners() {
        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        btnManageDoctors.setOnClickListener(v -> startActivity(new Intent(this, ManageDoctorsActivity.class)));
        btnManageAdmins.setOnClickListener(v -> startActivity(new Intent(this, ManageAdminsActivity.class)));
        btnManageAppointments.setOnClickListener(v -> startActivity(new Intent(this, ManageAppointmentsActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseUtils.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 