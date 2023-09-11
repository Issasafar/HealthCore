package com.issasafar.healthcore.ui.medicalStaff.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.issasafar.healthcore.databinding.ReportItemLayoutBinding;

import java.util.ArrayList;

public class ReportsRecyclerAdapter extends RecyclerView.Adapter<ReportsRecyclerAdapter.ViewHolder> {
    private final ArrayList<Report> reportsList;


    public ReportsRecyclerAdapter(ArrayList<Report> list) {

        this.reportsList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReportItemLayoutBinding reportItemLayoutBinding = ReportItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(reportItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getReportTextView().setText(reportsList.get(position).getMessage());
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("reports/" + reportsList.get(holder.getAdapterPosition()).getId());
                databaseReference.removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView reportTextView;
        private final Button deleteButton;

        public ViewHolder(ReportItemLayoutBinding binding) {
            super(binding.getRoot());
            // Define click listener for the ViewHolder's View
            reportTextView = binding.reportText;
            deleteButton = binding.reportDeleteButton;

            // textView = (TextView) view.findViewById(R.id.textView);
        }


        public TextView getReportTextView() {
            return reportTextView;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }
    }


}
