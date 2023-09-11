package com.issasafar.healthcore.ui.login;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {
    private ActivityResetPasswordBinding mActivityResetPasswordBinding;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityResetPasswordBinding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(mActivityResetPasswordBinding.getRoot());
        mContext = this;
        TextInputEditText emailEditText = mActivityResetPasswordBinding.emailField;
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isEmailValid(emailEditText.getText().toString())) {
                    emailEditText.setError(getString(R.string.not_valid_email));

                } else {
                    mActivityResetPasswordBinding.resetPasswordButton.setEnabled(true);
                }
            }
        };
        emailEditText.addTextChangedListener(textWatcher);
        mActivityResetPasswordBinding.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailEditText.getText().toString().trim());
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(mContext, com.google.android.material.R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
                builder.setTitle("Reset Password").setMessage("password reset instructions has been sent to: " + mActivityResetPasswordBinding.emailField.getText().toString() + "\nPlease check your inbox.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private boolean isEmailValid(String email) {
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return false;
        }
    }
}