package com.issasafar.healthcore.ui.medicalStaff.ui.patients;

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
import com.issasafar.healthcore.data.model.Patient;
import com.issasafar.healthcore.databinding.FragmentStaffPatientsBinding;

import java.util.ArrayList;

public class PatientsFragment extends Fragment {

    private FragmentStaffPatientsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentStaffPatientsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ConstraintLayout nothingLayout = binding.nothingLayout;
        RecyclerView recyclerView = binding.patientsRecycler;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ArrayList<Patient> patientList = new ArrayList<>();
        ArrayList<String> idList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference("users/patient");
        ValueEventListener patientEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patientList.clear();
                idList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    patientList.add(snapshot1.getValue(Patient.class));
                    idList.add(snapshot1.getKey());
                }
                if (!patientList.isEmpty()) {
                    nothingLayout.setVisibility(View.GONE);
                    PatientsRecyclerAdapter patientsRecyclerAdapter = new PatientsRecyclerAdapter(patientList, idList, getActivity().getApplicationContext());
                    recyclerView.setAdapter(patientsRecyclerAdapter);
                } else {
                    nothingLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addValueEventListener(patientEventListener);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}