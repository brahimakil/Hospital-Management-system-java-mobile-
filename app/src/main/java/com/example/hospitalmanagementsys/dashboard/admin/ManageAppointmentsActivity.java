package com.example.hospitalmanagementsys.dashboard.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.adapters.AppointmentAdapter;
import com.example.hospitalmanagementsys.models.Appointment;
import com.example.hospitalmanagementsys.models.Doctor;
import com.example.hospitalmanagementsys.models.User;
import com.example.hospitalmanagementsys.utils.Constants;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ManageAppointmentsActivity extends BaseManagementActivity {
    private AppointmentAdapter adapter;
    private ArrayList<Appointment> appointments;
    private ArrayList<Doctor> doctors;
    private ArrayList<User> users;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Manage Appointments");
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectedDateTime = Calendar.getInstance();
        loadDoctors();
        loadUsers();
    }

    @Override
    protected void setupRecyclerView() {
        appointments = new ArrayList<>();
        adapter = new AppointmentAdapter(appointments, this::showEditDialog, this::confirmDelete, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        loadAppointments();
    }

    @Override
    protected void setupClickListeners() {
        fabAdd.setOnClickListener(v -> handleAddItem());
    }

    @Override
    protected void handleAddItem() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_appointment, null);
        Spinner spinnerDoctor = dialogView.findViewById(R.id.spinnerDoctor);
        Spinner spinnerUser = dialogView.findViewById(R.id.spinnerUser);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Setup spinners
        ArrayAdapter<Doctor> doctorAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, doctors);
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setAdapter(doctorAdapter);

        ArrayAdapter<User> userAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, users);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(userAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, 
            new String[]{"scheduled", "completed", "cancelled"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Setup date picker
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(year, month, dayOfMonth);
                    etDate.setText(dateFormat.format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Setup time picker
        etTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    etTime.setText(timeFormat.format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true);
            timePickerDialog.show();
        });

        new MaterialAlertDialogBuilder(this)
            .setTitle("Add New Appointment")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                Doctor selectedDoctor = (Doctor) spinnerDoctor.getSelectedItem();
                User selectedUser = (User) spinnerUser.getSelectedItem();
                String status = spinnerStatus.getSelectedItem().toString();

                // Check if date and time are selected
                if (etDate.getText().toString().isEmpty() || etTime.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check availability
                checkDoctorAvailability(selectedDoctor.getUid(), selectedDateTime.getTime(), 
                    selectedUser, isAvailable -> {
                    if (isAvailable) {
                        String uid = FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS)
                            .document().getId();
                        
                        Map<String, Object> appointmentData = new HashMap<>();
                        appointmentData.put("doctorId", selectedDoctor.getUid());
                        appointmentData.put("userId", selectedUser.getUid());
                        appointmentData.put("doctorName", selectedDoctor.getName());
                        appointmentData.put("userName", selectedUser.getName());
                        appointmentData.put("appointmentDate", selectedDateTime.getTime());
                        appointmentData.put("status", status);
                        appointmentData.put("createdAt", new Date());
                        appointmentData.put("updatedAt", new Date());

                        FirebaseUtils.addUserToCollection(Constants.COLLECTION_APPOINTMENTS, 
                            uid, appointmentData,
                            aVoid -> Toast.makeText(this, "Appointment added successfully", 
                                Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(this, "Failed to create appointment: " + 
                                e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        Toast.makeText(this, "Doctor is not available at selected time", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void loadDoctors() {
        doctors = new ArrayList<>();
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
            });
    }

    private void loadUsers() {
        users = new ArrayList<>();
        FirebaseUtils.db.collection(Constants.COLLECTION_USERS)
            .addSnapshotListener((value, error) -> {
                if (error != null) return;
                users.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        user.setUid(doc.getId());
                        users.add(user);
                    }
                }
            });
    }

    private void loadAppointments() {
        FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS)
            .addSnapshotListener((value, error) -> {
                if (error != null) return;
                appointments.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Appointment appointment = doc.toObject(Appointment.class);
                    if (appointment != null) {
                        appointment.setUid(doc.getId());
                        appointments.add(appointment);
                    }
                }
                adapter.notifyDataSetChanged();
            });
    }

    private void checkDoctorAvailability(String doctorId, Date date, User selectedUser, OnAvailabilityChecked callback) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        
        if (hour >= 20 || hour < 7) {
            Toast.makeText(this, "Appointments can only be made between 7 AM and 8 PM", 
                Toast.LENGTH_LONG).show();
            callback.onResult(false);
            return;
        }

        FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS)
            .whereEqualTo("doctorId", doctorId)
            .whereEqualTo("status", "scheduled")  // Only check scheduled appointments
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                boolean isAvailable = true;
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Appointment existingAppointment = doc.toObject(Appointment.class);
                    if (existingAppointment != null) {
                        Calendar existingCal = Calendar.getInstance();
                        existingCal.setTime(existingAppointment.getAppointmentDate());
                        
                        // Check if same year, day and hour
                        if (existingCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                            existingCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                            existingCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) &&
                            existingCal.get(Calendar.HOUR_OF_DAY) == cal.get(Calendar.HOUR_OF_DAY)) {
                            isAvailable = false;
                            Toast.makeText(ManageAppointmentsActivity.this, 
                                "Doctor already has an appointment at " + 
                                timeFormat.format(existingAppointment.getAppointmentDate()), 
                                Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
                if (isAvailable) {
                    checkUserAvailability(selectedUser.getUid(), date, callback);
                } else {
                    callback.onResult(false);
                }
            })
            .addOnFailureListener(e -> callback.onResult(false));
    }

    private void checkUserAvailability(String userId, Date date, OnAvailabilityChecked callback) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS)
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "scheduled")  // Only check scheduled appointments
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                boolean isAvailable = true;
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Appointment existingAppointment = doc.toObject(Appointment.class);
                    if (existingAppointment != null) {
                        Calendar existingCal = Calendar.getInstance();
                        existingCal.setTime(existingAppointment.getAppointmentDate());
                        
                        // Check if same year, day and hour
                        if (existingCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                            existingCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                            existingCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) &&
                            existingCal.get(Calendar.HOUR_OF_DAY) == cal.get(Calendar.HOUR_OF_DAY)) {
                            isAvailable = false;
                            Toast.makeText(ManageAppointmentsActivity.this, 
                                "User already has an appointment at " + 
                                timeFormat.format(existingAppointment.getAppointmentDate()), 
                                Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
                callback.onResult(isAvailable);
            })
            .addOnFailureListener(e -> callback.onResult(false));
    }

    private void showEditDialog(Appointment appointment) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_appointment, null);
        Spinner spinnerDoctor = dialogView.findViewById(R.id.spinnerDoctor);
        Spinner spinnerUser = dialogView.findViewById(R.id.spinnerUser);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Setup spinners (same as in handleAddItem)
        ArrayAdapter<Doctor> doctorAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, doctors);
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setAdapter(doctorAdapter);

        ArrayAdapter<User> userAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, users);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(userAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, 
            new String[]{"scheduled", "completed", "cancelled"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set current values
        selectedDateTime.setTime(appointment.getAppointmentDate());
        etDate.setText(dateFormat.format(selectedDateTime.getTime()));
        etTime.setText(timeFormat.format(selectedDateTime.getTime()));

        // Find and select current doctor and user in spinners
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getUid().equals(appointment.getDoctorId())) {
                spinnerDoctor.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUid().equals(appointment.getUserId())) {
                spinnerUser.setSelection(i);
                break;
            }
        }
        
        // Set current status
        for (int i = 0; i < spinnerStatus.getAdapter().getCount(); i++) {
            if (spinnerStatus.getAdapter().getItem(i).toString().equals(appointment.getStatus())) {
                spinnerStatus.setSelection(i);
                break;
            }
        }

        // Setup date and time pickers (same as in handleAddItem)
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(year, month, dayOfMonth);
                    etDate.setText(dateFormat.format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        etTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    etTime.setText(timeFormat.format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true);
            timePickerDialog.show();
        });

        new MaterialAlertDialogBuilder(this)
            .setTitle("Edit Appointment")
            .setView(dialogView)
            .setPositiveButton("Save", (dialog, which) -> {
                Doctor selectedDoctor = (Doctor) spinnerDoctor.getSelectedItem();
                User selectedUser = (User) spinnerUser.getSelectedItem();
                String status = spinnerStatus.getSelectedItem().toString();

                Map<String, Object> updates = new HashMap<>();
                updates.put("doctorId", selectedDoctor.getUid());
                updates.put("userId", selectedUser.getUid());
                updates.put("doctorName", selectedDoctor.getName());
                updates.put("userName", selectedUser.getName());
                updates.put("appointmentDate", selectedDateTime.getTime());
                updates.put("status", status);
                updates.put("updatedAt", new Date());

                FirebaseUtils.updateDocument(
                    Constants.COLLECTION_APPOINTMENTS,
                    appointment.getUid(),
                    updates,
                    aVoid -> Toast.makeText(this, "Appointment updated successfully", 
                        Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to update appointment: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void confirmDelete(Appointment appointment) {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Delete Appointment")
            .setMessage("Are you sure you want to delete this appointment?")
            .setPositiveButton("Delete", (dialog, which) -> {
                FirebaseUtils.deleteDocument(
                    Constants.COLLECTION_APPOINTMENTS,
                    appointment.getUid(),
                    aVoid -> Toast.makeText(this, "Appointment deleted successfully", 
                        Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to delete appointment: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    interface OnAvailabilityChecked {
        void onResult(boolean isAvailable);
    }
} 