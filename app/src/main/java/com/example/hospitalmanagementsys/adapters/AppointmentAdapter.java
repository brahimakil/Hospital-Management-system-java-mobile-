package com.example.hospitalmanagementsys.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.models.Appointment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private ArrayList<Appointment> appointments;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;
    private boolean showActions;
    private SimpleDateFormat dateFormat;

    public AppointmentAdapter(ArrayList<Appointment> appointments, 
                            OnEditClickListener editListener,
                            OnDeleteClickListener deleteListener,
                            boolean showActions) {
        this.appointments = appointments;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        this.showActions = showActions;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.tvDoctorName.setText("Doctor: " + appointment.getDoctorName());
        holder.tvUserName.setText("Patient: " + appointment.getUserName());
        holder.tvDate.setText("Date: " + dateFormat.format(appointment.getAppointmentDate()));
        holder.tvStatus.setText("Status: " + appointment.getStatus());
        
        // Hide or show action buttons based on showActions flag
        if (showActions) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        
        holder.btnEdit.setOnClickListener(v -> editListener.onAppointmentClick(appointment));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onAppointmentClick(appointment));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public interface OnEditClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public interface OnDeleteClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvUserName, tvDate, tvStatus;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
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