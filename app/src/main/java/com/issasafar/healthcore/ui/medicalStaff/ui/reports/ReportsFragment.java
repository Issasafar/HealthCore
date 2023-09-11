package com.issasafar.healthcore.ui.medicalStaff.ui.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.issasafar.healthcore.databinding.FragmentStaffReportsBinding;

import java.util.ArrayList;


public class ReportsFragment extends Fragment {

    private FragmentStaffReportsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentStaffReportsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        RecyclerView recyclerView = binding.reportsRecycler;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Report> list = new ArrayList<>();
        ConstraintLayout nothingLayout = binding.nothingLayout;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference("reports");
        ValueEventListener reportEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Report> oldList = new ArrayList<>();
                oldList = list;
                list.clear();
                for (DataSnapshot reportSnap : snapshot.getChildren()) {
                    String id = reportSnap.getKey();
                    String reportMessage = reportSnap.getValue(String.class);
                    Report report = new Report(reportMessage, id);
                    list.add(report);
                }


                if (list.isEmpty()) {
                    nothingLayout.setVisibility(View.VISIBLE);

                } else {
                    nothingLayout.setVisibility(View.GONE);
                    ReportsRecyclerAdapter reportsRecyclerAdapter = new ReportsRecyclerAdapter(list);
                    recyclerView.setAdapter(reportsRecyclerAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addValueEventListener(reportEventListener);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}