package com.issasafar.healthcore.ui.medicalStaff.ui.patients;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.data.model.HealthStatus;
import com.issasafar.healthcore.data.model.Patient;
import com.issasafar.healthcore.databinding.ChangeHealthDialogBinding;
import com.issasafar.healthcore.databinding.PatientItemLayoutBinding;

import java.util.ArrayList;

public class PatientsRecyclerAdapter extends RecyclerView.Adapter<PatientsRecyclerAdapter.ViewHolder> {
    private final ArrayList<Patient> patientList;
    private final ArrayList<String> idList;
    private Context mContext;
    private ChangeHealthDialogBinding changeHealthDialogBinding;

    public PatientsRecyclerAdapter(ArrayList<Patient> patientList, ArrayList<String> idList, Context context) {
        this.patientList = patientList;
        this.idList = idList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public PatientsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PatientItemLayoutBinding patientItemLayoutBinding = PatientItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        changeHealthDialogBinding = ChangeHealthDialogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        mContext = parent.getContext();
        return new ViewHolder(patientItemLayoutBinding);


    }

    @Override
    public void onBindViewHolder(@NonNull PatientsRecyclerAdapter.ViewHolder holder, int position) {

        holder.patientNameTextView.setText(patientList.get(holder.getAdapterPosition()).getFullName());
        holder.patientStatusTextView.setText(patientList.get(holder.getAdapterPosition()).getHealthStatus().getValue());
        if (patientList.get(holder.getAdapterPosition()).getHealthStatus() == HealthStatus.HEALTHY) {
            holder.patientStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.green_color));
        } else if (patientList.get(holder.getAdapterPosition()).getHealthStatus() == HealthStatus.UNHEALTHY) {
            holder.patientStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.orange_color));
        }
        RadioGroup changeHealthGroup = changeHealthDialogBinding.changeHealthGroup;
        if (patientList.get(holder.getAdapterPosition()).getHealthStatus() == HealthStatus.HEALTHY) {
            changeHealthGroup.check(R.id.healthyButton);
        } else {
            changeHealthGroup.check(R.id.unhealthyButton);
        }
        holder.editStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeHealthDialogBinding = ChangeHealthDialogBinding.inflate(LayoutInflater.from(mContext), (ViewGroup) changeHealthDialogBinding.getRoot().getParent(), false);
                RadioGroup changeHealthGroup = changeHealthDialogBinding.changeHealthGroup;
                if (patientList.get(holder.getAdapterPosition()).getHealthStatus() == HealthStatus.HEALTHY) {
                    changeHealthGroup.check(R.id.healthyButton);
                } else {
                    changeHealthGroup.check(R.id.unhealthyButton);
                }
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(mContext, com.google.android.material.R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
                builder.setView(changeHealthDialogBinding.getRoot());
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setTitle("Change Health Status");
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Patient patient;
                        int checked = changeHealthGroup.getCheckedRadioButtonId();
                        if (checked == R.id.healthyButton) {
                            patientList.get(holder.getAdapterPosition()).setHealthStatus(HealthStatus.HEALTHY);
                        } else {
                            patientList.get(holder.getAdapterPosition()).setHealthStatus(HealthStatus.UNHEALTHY);
                        }
                        patient = patientList.get(holder.getAdapterPosition());
                        String patientId = idList.get(holder.getAdapterPosition());
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("users/patient");
                        databaseReference.child(patientId).setValue(patient);
                    }
                });
                // builder.setView(changeHealthDialogBinding.getRoot());
                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView patientNameTextView;
        private final TextView patientStatusTextView;

        private final Button editStatusButton;

        public ViewHolder(PatientItemLayoutBinding binding) {
            super(binding.getRoot());
            patientNameTextView = binding.nameTextView;
            patientStatusTextView = binding.statusTextView;
            editStatusButton = binding.editButton;
        }

        public TextView getPatientNameTextView() {
            return patientNameTextView;
        }

        public TextView getPatientStatusTextView() {
            return patientStatusTextView;
        }

        public Button getEditStatusButton() {
            return editStatusButton;
        }
    }


}
