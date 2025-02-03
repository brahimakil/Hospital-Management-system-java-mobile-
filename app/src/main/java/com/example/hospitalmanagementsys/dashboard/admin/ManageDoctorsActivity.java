package com.example.hospitalmanagementsys.dashboard.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.adapters.DoctorAdapter;
import com.example.hospitalmanagementsys.auth.LoginActivity;
import com.example.hospitalmanagementsys.models.Doctor;
import com.example.hospitalmanagementsys.utils.Constants;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class ManageDoctorsActivity extends BaseManagementActivity {
    private DoctorAdapter adapter;
    private ArrayList<Doctor> doctors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Manage Doctors");
    }

    @Override
    protected void setupRecyclerView() {
        doctors = new ArrayList<>();
        adapter = new DoctorAdapter(doctors, this::showEditDialog, this::confirmDelete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        loadDoctors();
    }

    @Override
    protected void setupClickListeners() {
        fabAdd.setOnClickListener(v -> handleAddItem());
    }

    @Override
    protected void handleAddItem() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_doctor, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        EditText etSpecialty = dialogView.findViewById(R.id.etSpecialty);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Add New Doctor")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String specialty = etSpecialty.getText().toString();
                
                // Generate a unique ID for the doctor
                String uid = FirebaseUtils.db.collection(Constants.COLLECTION_DOCTORS).document().getId();
                Map<String, Object> doctorData = new HashMap<>();
                doctorData.put(Constants.FIELD_NAME, name);
                doctorData.put(Constants.FIELD_EMAIL, email);
                doctorData.put(Constants.FIELD_PASSWORD, password); // Store encrypted or hashed password
                doctorData.put(Constants.FIELD_SPECIALTY, specialty);
                doctorData.put(Constants.FIELD_CREATED_AT, new Date());
                doctorData.put(Constants.FIELD_UPDATED_AT, new Date());

                FirebaseUtils.addUserToCollection(Constants.COLLECTION_DOCTORS, uid, doctorData,
                    aVoid -> Toast.makeText(this, "Doctor added successfully", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to create doctor: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
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

    private void showEditDialog(Doctor doctor) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_doctor, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etSpecialty = dialogView.findViewById(R.id.etSpecialty);

        etName.setText(doctor.getName());
        etEmail.setText(doctor.getEmail());
        etSpecialty.setText(doctor.getSpecialty());

        new MaterialAlertDialogBuilder(this)
            .setTitle("Edit Doctor")
            .setView(dialogView)
            .setPositiveButton("Save", (dialog, which) -> {
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", etName.getText().toString());
                updates.put("email", etEmail.getText().toString());
                updates.put("specialty", etSpecialty.getText().toString());
                updates.put("updatedAt", new Date());
                FirebaseUtils.updateDocument(Constants.COLLECTION_DOCTORS, doctor.getUid(), updates);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void confirmDelete(Doctor doctor) {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Delete Doctor")
            .setMessage("Are you sure you want to delete this doctor?")
            .setPositiveButton("Delete", (dialog, which) -> {
                FirebaseUtils.deleteDocument(
                    Constants.COLLECTION_DOCTORS, 
                    doctor.getUid(),
                    aVoid -> Toast.makeText(this, "Doctor deleted successfully", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to delete doctor: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 