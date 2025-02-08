package com.example.hospitalmanagementsys.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.models.Doctor;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {
    private ArrayList<Doctor> doctors;
    private OnDoctorClickListener editListener;
    private OnDoctorClickListener deleteListener;

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorAdapter(ArrayList<Doctor> doctors, 
                        OnDoctorClickListener editListener,
                        OnDoctorClickListener deleteListener) {
        this.doctors = doctors;
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
        Doctor doctor = doctors.get(position);
        holder.tvName.setText(doctor.getName());
        holder.tvEmail.setText(doctor.getEmail());
        
        holder.btnEdit.setOnClickListener(v -> editListener.onDoctorClick(doctor));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDoctorClick(doctor));
    }

    @Override
    public int getItemCount() {
        return doctors.size();
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