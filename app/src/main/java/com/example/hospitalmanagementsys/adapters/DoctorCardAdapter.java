package com.example.hospitalmanagementsys.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitalmanagementsys.R;
import com.example.hospitalmanagementsys.models.Doctor;

import java.util.List;
import java.util.function.Consumer;

public class DoctorCardAdapter extends RecyclerView.Adapter<DoctorCardAdapter.ViewHolder> {
    private List<Doctor> doctors;
    private Consumer<Doctor> onBookAppointment;

    public DoctorCardAdapter(List<Doctor> doctors, Consumer<Doctor> onBookAppointment) {
        this.doctors = doctors;
        this.onBookAppointment = onBookAppointment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_doctor_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.tvName.setText("Dr. " + doctor.getName());
        holder.tvSpecialty.setText(doctor.getSpecialty());
        holder.btnBookAppointment.setOnClickListener(v -> onBookAppointment.accept(doctor));
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty;
        Button btnBookAppointment;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvSpecialty = view.findViewById(R.id.tvSpecialty);
            btnBookAppointment = view.findViewById(R.id.btnBookAppointment);
        }
    }
} 