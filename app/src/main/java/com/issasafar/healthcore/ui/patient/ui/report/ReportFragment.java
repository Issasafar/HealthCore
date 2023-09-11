package com.issasafar.healthcore.ui.patient.ui.report;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.databinding.FragmentPatientReportBinding;
import com.issasafar.healthcore.databinding.WriteReportBinding;

public class ReportFragment extends Fragment {

    private FragmentPatientReportBinding binding;
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        ReportViewModel reportViewModel =
//                new ViewModelProvider(this).get(ReportViewModel.class);

        binding = FragmentPatientReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mContext = getContext();
//
//        final TextView textView = binding.textGallery;
//        reportViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        final FloatingActionButton writeReport = binding.writeReport;
        writeReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleReport();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void handleReport() {
        WriteReportBinding mWriteReportBinding = WriteReportBinding.inflate(getLayoutInflater());
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mWriteReportBinding.reportEditText.getText() != null) {
//                    mWriteReportBinding.doneButton.setEnabled(true);
                }
            }
        };
        mWriteReportBinding.reportEditText.addTextChangedListener(textWatcher);

        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(mContext, com.google.android.material.R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("reports");
                String reportText = mWriteReportBinding.reportEditText.getText().toString().trim();

                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(reportText);

                dialogInterface.dismiss();
                Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.report_added, Snackbar.LENGTH_LONG);
                snackbar.show();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        builder.setView(mWriteReportBinding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();
//        FragmentManager fm = getParentFragmentManager();
//        ReportDialog reportDialog = new ReportDialog(binding.getRoot());
//        reportDialog.show(fm,"");


    }

}