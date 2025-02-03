package com.example.hospitalmanagementsys.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.adapters.DoctorCardAdapter;
import com.example.hospitalmanagementsys.auth.LoginActivity;
import com.example.hospitalmanagementsys.dashboard.UserAppointmentsActivity;
import com.example.hospitalmanagementsys.models.Doctor;
import com.example.hospitalmanagementsys.utils.Constants;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class UserDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DoctorCardAdapter adapter;
    private ArrayList<Doctor> doctors;
    private Button btnManageAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Available Doctors");

        initViews();
        loadDoctors();
        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnManageAppointments = findViewById(R.id.btnManageAppointments);
        
        doctors = new ArrayList<>();
        adapter = new DoctorCardAdapter(doctors, this::showAppointmentDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnManageAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserAppointmentsActivity.class);
            startActivity(intent);
        });
    }

    private void loadDoctors() {
        FirebaseUtils.db.collection(Constants.COLLECTION_DOCTORS)
            .addSnapshotListener((value, error) -> {
                if (error != null) return;
                doctors.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Doctor doctor = doc.toObject(Doctor.class);
                    if (doctor != null) {
                        doctor.setUid(doc.getId());
                        doctors.add(doctor);
                    }
                }
                adapter.notifyDataSetChanged();
            });
    }

    private void showAppointmentDialog(Doctor doctor) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_appointment, null);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);
        TextView tvDoctorName = dialogView.findViewById(R.id.tvDoctorName);
        TextView tvUserEmail = dialogView.findViewById(R.id.tvUserEmail);
        
        // Show doctor and user info
        tvDoctorName.setVisibility(View.VISIBLE);
        tvUserEmail.setVisibility(View.VISIBLE);
        tvDoctorName.setText("Doctor: Dr. " + doctor.getName());
        tvUserEmail.setText("Patient: " + FirebaseUtils.getCurrentUser().getEmail());
        
        Calendar selectedDateTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDateTime.set(year, month, dayOfMonth);
                etDate.setText(dateFormat.format(selectedDateTime.getTime()));
            }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                etTime.setText(timeFormat.format(selectedDateTime.getTime()));
            }, selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE), true).show();
        });

        new MaterialAlertDialogBuilder(this)
            .setTitle("Book Appointment")
            .setView(dialogView)
            .setPositiveButton("Book", (dialog, which) -> {
                String date = etDate.getText().toString();
                String time = etTime.getText().toString();
                
                if (date.isEmpty() || time.isEmpty()) {
                    Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                // First get the current user's data
                FirebaseUtils.db.collection(Constants.COLLECTION_USERS)
                    .document(FirebaseUtils.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Map<String, Object> appointmentData = new HashMap<>();
                        appointmentData.put("doctorId", doctor.getUid());
                        appointmentData.put("doctorName", doctor.getName());
                        appointmentData.put("userId", FirebaseUtils.getCurrentUser().getUid());
                        appointmentData.put("userEmail", FirebaseUtils.getCurrentUser().getEmail());
                        appointmentData.put("userName", documentSnapshot.getString("name")); // Get user's name
                        appointmentData.put("appointmentDate", selectedDateTime.getTime());
                        appointmentData.put("status", "scheduled");
                        appointmentData.put("createdAt", new Date());
                        appointmentData.put("updatedAt", new Date());

                        String uid = FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS).document().getId();
                        FirebaseUtils.addUserToCollection(Constants.COLLECTION_APPOINTMENTS, uid, appointmentData,
                            aVoid -> Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(this, "Failed to book appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(this, "Failed to get user data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
            })
            .setNegativeButton("Cancel", null)
            .show();
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