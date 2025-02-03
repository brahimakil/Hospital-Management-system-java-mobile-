package com.example.hospitalmanagementsys.dashboard.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.adapters.UserAdapter;
import com.example.hospitalmanagementsys.models.User;
import com.example.hospitalmanagementsys.utils.Constants;
import com.example.hospitalmanagementsys.utils.FirebaseUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import android.widget.Toast;

public class ManageUsersActivity extends BaseManagementActivity {
    private UserAdapter adapter;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Manage Users");
    }

    @Override
    protected void setupRecyclerView() {
        users = new ArrayList<>();
        adapter = new UserAdapter(users, this::showEditDialog, this::confirmDelete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        loadUsers();
    }

    @Override
    protected void setupClickListeners() {
        fabAdd.setOnClickListener(v -> handleAddItem());
    }

    @Override
    protected void handleAddItem() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Add New User")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                
                handleAddUser(name, email, password);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void handleAddUser(String name, String email, String password) {
        // First create Firebase Auth account
        FirebaseUtils.signUp(email, password, task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                
                Map<String, Object> userData = new HashMap<>();
                userData.put(Constants.FIELD_NAME, name);
                userData.put(Constants.FIELD_EMAIL, email);
                userData.put(Constants.FIELD_CREATED_AT, new Date());
                userData.put(Constants.FIELD_UPDATED_AT, new Date());

                FirebaseUtils.addUserToCollection(Constants.COLLECTION_USERS, uid, userData,
                    aVoid -> {
                        Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
                        FirebaseUtils.signOut(); // Sign out after creating the account
                    },
                    e -> {
                        Toast.makeText(this, "Failed to create user: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                        // Delete the auth user if profile creation fails
                        task.getResult().getUser().delete();
                    }
                );
            } else {
                Toast.makeText(this, "Failed to create account: " + task.getException().getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsers() {
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
                adapter.notifyDataSetChanged();
            });
    }

    private void showEditDialog(User user) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);

        etName.setText(user.getName());
        etEmail.setText(user.getEmail());

        new MaterialAlertDialogBuilder(this)
            .setTitle("Edit User")
            .setView(dialogView)
            .setPositiveButton("Save", (dialog, which) -> {
                Map<String, Object> updates = new HashMap<>();
                updates.put(Constants.FIELD_NAME, etName.getText().toString());
                updates.put(Constants.FIELD_EMAIL, etEmail.getText().toString());
                updates.put(Constants.FIELD_UPDATED_AT, new Date());
                
                FirebaseUtils.updateDocument(
                    Constants.COLLECTION_USERS, 
                    user.getUid(), 
                    updates,
                    aVoid -> Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to update user: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void confirmDelete(User user) {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Delete", (dialog, which) -> {
                FirebaseUtils.deleteDocument(
                    Constants.COLLECTION_USERS, 
                    user.getUid(),
                    aVoid -> Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to delete user: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 