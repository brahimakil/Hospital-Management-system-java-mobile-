package com.example.hospitalmanagementsys.dashboard.doctor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.adapters.AppointmentAdapter;
import com.example.hospitalmanagementsys.models.Appointment;
import com.example.hospitalmanagementsys.models.User;
import com.example.hospitalmanagementsys.models.Doctor;
import com.example.hospitalmanagementsys.utils.Constants;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DoctorAppointmentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private ArrayList<Appointment> appointments;
    private ArrayList<User> users;
    private FloatingActionButton fabAdd;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Calendar selectedDateTime;
    private String currentDoctorId;
    private String doctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Appointments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        currentDoctorId = FirebaseUtils.getCurrentUser().getUid();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectedDateTime = Calendar.getInstance();
        
        loadDoctorData();
        setupViews();
        loadUsers();
        loadAppointments();
    }

    private void loadDoctorData() {
        FirebaseUtils.db.collection(Constants.COLLECTION_DOCTORS)
            .document(currentDoctorId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Doctor doctor = documentSnapshot.toObject(Doctor.class);
                    if (doctor != null) {
                        doctorName = doctor.getName();
                    }
                }
            });
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        
        appointments = new ArrayList<>();
        adapter = new AppointmentAdapter(appointments, this::showEditDialog, this::confirmDelete, true); // Add delete listener
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void loadAppointments() {
        FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS)
            .whereEqualTo("doctorId", currentDoctorId)
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

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_doctor_appointment, null);
        Spinner spinnerUser = dialogView.findViewById(R.id.spinnerUser);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Setup user spinner
        ArrayAdapter<User> userAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, users);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(userAdapter);

        // Setup status spinner with only "scheduled" for new appointments
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, 
            new String[]{"scheduled"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set current date and time
        selectedDateTime = Calendar.getInstance();
        etDate.setText(dateFormat.format(selectedDateTime.getTime()));
        etTime.setText(timeFormat.format(selectedDateTime.getTime()));

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
                User selectedUser = (User) spinnerUser.getSelectedItem();
                if (selectedUser == null) {
                    Toast.makeText(this, "Please select a user", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> appointmentData = new HashMap<>();
                appointmentData.put("userId", selectedUser.getUid());
                appointmentData.put("userName", selectedUser.getName());
                appointmentData.put("doctorId", currentDoctorId);
                appointmentData.put("doctorName", doctorName);
                appointmentData.put("appointmentDate", selectedDateTime.getTime());
                appointmentData.put("status", "scheduled");
                appointmentData.put("createdAt", new Date());
                appointmentData.put("updatedAt", new Date());

                String uid = FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS)
                    .document().getId();
                
                FirebaseUtils.addUserToCollection(Constants.COLLECTION_APPOINTMENTS, uid, appointmentData,
                    aVoid -> Toast.makeText(this, "Appointment added successfully", 
                        Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to create appointment: " + 
                        e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showEditDialog(Appointment appointment) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_doctor_appointment, null);
        Spinner spinnerUser = dialogView.findViewById(R.id.spinnerUser);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Setup spinners
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

        // Find and select current user in spinner
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUid().equals(appointment.getUserId())) {
                spinnerUser.setSelection(i);
                break;
            }
        }

        // Set current status
        for (int i = 0; i < statusAdapter.getCount(); i++) {
            if (statusAdapter.getItem(i).equals(appointment.getStatus())) {
                spinnerStatus.setSelection(i);
                break;
            }
        }

        // Setup date and time pickers
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
                    if (hourOfDay >= 20 || hourOfDay < 7) {
                        Toast.makeText(this, "Please select a time between 7 AM and 8 PM", 
                            Toast.LENGTH_LONG).show();
                        return;
                    }
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
                User selectedUser = (User) spinnerUser.getSelectedItem();
                String status = spinnerStatus.getSelectedItem().toString();

                Map<String, Object> updates = new HashMap<>();
                updates.put("userId", selectedUser.getUid());
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
            .setTitle("Cancel Appointment")
            .setMessage("Are you sure you want to cancel this appointment?")
            .setPositiveButton("Yes", (dialog, which) -> {
                FirebaseUtils.deleteDocument(
                    Constants.COLLECTION_APPOINTMENTS,
                    appointment.getUid(),
                    aVoid -> Toast.makeText(this, "Appointment cancelled successfully", 
                        Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to cancel appointment: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void checkAvailability(String userId, Date date, OnAvailabilityChecked callback) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        
        if (hour >= 20 || hour < 7) {
            Toast.makeText(this, "Appointments can only be made between 7 AM and 8 PM", 
                Toast.LENGTH_LONG).show();
            callback.onResult(false);
            return;
        }

        // Check if user has any appointments at this time
        FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS)
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "scheduled")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                boolean isAvailable = true;
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Appointment existingAppointment = doc.toObject(Appointment.class);
                    if (existingAppointment != null) {
                        Calendar existingCal = Calendar.getInstance();
                        existingCal.setTime(existingAppointment.getAppointmentDate());
                        
                        if (existingCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                            existingCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                            existingCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) &&
                            existingCal.get(Calendar.HOUR_OF_DAY) == cal.get(Calendar.HOUR_OF_DAY)) {
                            isAvailable = false;
                            Toast.makeText(this, "User already has an appointment at " + 
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

    interface OnAvailabilityChecked {
        void onResult(boolean isAvailable);
    }
} 