package com.example.hospitalmanagementsys.dashboard.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.auth.LoginActivity;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;

public class DoctorDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnManageAppointments;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Doctor Dashboard");
        
        initViews();
        setupClickListeners();
        
        tvWelcome.setText("Welcome Dr. " + FirebaseUtils.getCurrentUser().getEmail());
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        btnManageAppointments = findViewById(R.id.btnManageAppointments);
        btnRegister = findViewById(R.id.btnRegister);
        if (btnRegister != null) {
            btnRegister.setVisibility(View.GONE); // Hide register button for doctors
        }
    }

    private void setupClickListeners() {
        btnManageAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(this, DoctorAppointmentsActivity.class);
            startActivity(intent);
        });
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