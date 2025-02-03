package com.example.hospitalmanagementsys.dashboard;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.adapters.AppointmentAdapter;
import com.example.hospitalmanagementsys.models.Appointment;
import com.example.hospitalmanagementsys.utils.Constants;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class UserAppointmentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private ArrayList<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        
        setupToolbar();
        setupViews();
        loadAppointments();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Appointments");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setVisibility(View.GONE);
        
        appointments = new ArrayList<>();
        adapter = new AppointmentAdapter(appointments, null, null, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadAppointments() {
        String currentUserId = FirebaseUtils.getCurrentUser().getUid();
        FirebaseUtils.db.collection(Constants.COLLECTION_APPOINTMENTS)
            .whereEqualTo("userId", currentUserId)
            .orderBy("appointmentDate", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null || value == null) return;
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
} 