package com.example.hospitalmanagementsys.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.models.Appointment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private ArrayList<Appointment> appointments;
    private OnAppointmentClickListener editListener;
    private OnAppointmentClickListener deleteListener;
    private boolean isAdmin;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public AppointmentAdapter(ArrayList<Appointment> appointments, 
                            OnAppointmentClickListener editListener,
                            OnAppointmentClickListener deleteListener,
                            boolean isAdmin) {
        this.appointments = appointments;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.tvDoctorName.setText(appointment.getDoctorName());
        holder.tvUserName.setText(appointment.getUserName());
        holder.tvDate.setText(appointment.getFormattedDateTime());
        holder.tvStatus.setText(appointment.getStatus());
        
        // Set click listeners
        holder.btnEdit.setOnClickListener(v -> editListener.onAppointmentClick(appointment));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onAppointmentClick(appointment));
        
        // Configure status chip
        configureStatusChip(holder.tvStatus, appointment.getStatus());
    }

    private void configureStatusChip(Chip chip, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                chip.setChipBackgroundColorResource(R.color.status_pending);
                break;
            case "confirmed":
                chip.setChipBackgroundColorResource(R.color.status_confirmed);
                break;
            case "cancelled":
                chip.setChipBackgroundColorResource(R.color.status_cancelled);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvUserName, tvDate;
        Chip tvStatus;
        MaterialButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 