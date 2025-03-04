package com.example.hospitalmanagementsys.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.models.Admin;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {
    private ArrayList<Admin> admins;
    private OnAdminClickListener editListener;
    private OnAdminClickListener deleteListener;

    public interface OnAdminClickListener {
        void onAdminClick(Admin admin);
    }

    public AdminAdapter(ArrayList<Admin> admins, 
                       OnAdminClickListener editListener,
                       OnAdminClickListener deleteListener) {
        this.admins = admins;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Admin admin = admins.get(position);
        holder.tvName.setText(admin.getName());
        holder.tvEmail.setText(admin.getEmail());
        
        holder.btnEdit.setOnClickListener(v -> editListener.onAdminClick(admin));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onAdminClick(admin));
    }

    @Override
    public int getItemCount() {
        return admins.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        MaterialButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 