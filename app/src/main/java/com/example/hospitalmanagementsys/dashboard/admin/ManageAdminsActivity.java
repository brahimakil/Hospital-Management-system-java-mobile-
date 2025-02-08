package com.example.hospitalmanagementsys.dashboard.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.adapters.AdminAdapter;
import com.example.hospitalmanagementsys.models.Admin;
import com.example.hospitalmanagementsys.utils.Constants;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import android.widget.Toast;

public class ManageAdminsActivity extends BaseManagementActivity {
    private AdminAdapter adapter;
    private ArrayList<Admin> admins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Manage Admins");
    }

    @Override
    protected void setupRecyclerView() {
        admins = new ArrayList<>();
        adapter = new AdminAdapter(admins, this::showEditDialog, this::confirmDelete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        loadAdmins();
    }

    @Override
    protected void setupClickListeners() {
        fabAdd.setOnClickListener(v -> handleAddItem());
    }

    @Override
    protected void handleAddItem() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_admin, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Add New Admin")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                
                // Generate a unique ID for the admin
                String uid = FirebaseUtils.db.collection(Constants.COLLECTION_ADMINS).document().getId();
                Map<String, Object> adminData = new HashMap<>();
                adminData.put(Constants.FIELD_NAME, name);
                adminData.put(Constants.FIELD_EMAIL, email);
                adminData.put(Constants.FIELD_PASSWORD, password);
                adminData.put(Constants.FIELD_CREATED_AT, new Date());
                adminData.put(Constants.FIELD_UPDATED_AT, new Date());

                FirebaseUtils.addUserToCollection(Constants.COLLECTION_ADMINS, uid, adminData,
                    aVoid -> Toast.makeText(this, "Admin added successfully", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to create admin: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void loadAdmins() {
        FirebaseUtils.db.collection(Constants.COLLECTION_ADMINS)
            .addSnapshotListener((value, error) -> {
                if (error != null) return;
                admins.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Admin admin = doc.toObject(Admin.class);
                    if (admin != null) {
                        admin.setUid(doc.getId());
                        admins.add(admin);
                    }
                }
                adapter.notifyDataSetChanged();
            });
    }

    private void showEditDialog(Admin admin) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_admin, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);

        etName.setText(admin.getName());
        etEmail.setText(admin.getEmail());

        new MaterialAlertDialogBuilder(this)
            .setTitle("Edit Admin")
            .setView(dialogView)
            .setPositiveButton("Save", (dialog, which) -> {
                Map<String, Object> updates = new HashMap<>();
                updates.put(Constants.FIELD_NAME, etName.getText().toString());
                updates.put(Constants.FIELD_EMAIL, etEmail.getText().toString());
                updates.put(Constants.FIELD_UPDATED_AT, new Date());
                
                FirebaseUtils.updateDocument(
                    Constants.COLLECTION_ADMINS, 
                    admin.getUid(), 
                    updates,
                    aVoid -> Toast.makeText(this, "Admin updated successfully", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to update admin: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void confirmDelete(Admin admin) {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this admin?")
            .setPositiveButton("Delete", (dialog, which) -> {
                FirebaseUtils.deleteDocument(Constants.COLLECTION_ADMINS, admin.getUid(),
                    aVoid -> Toast.makeText(this, "Admin deleted successfully", 
                        Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to delete admin: " + 
                        e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 