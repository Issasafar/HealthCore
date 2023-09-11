package com.issasafar.healthcore.ui.login;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.issasafar.healthcore.LocationTrack;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.data.model.AccountType;
import com.issasafar.healthcore.data.model.MedicalStaff;
import com.issasafar.healthcore.data.model.Patient;
import com.issasafar.healthcore.databinding.ActivityLoginBinding;
import com.issasafar.healthcore.ui.register.RegisterActivity;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {


    private final static int INITIAL_PERMISSIONS_RESULTS = 101;
    private final String SHARED_PREF_FILE = "com.issasafar.healthcore";
    private final int RESULT_BACK = 10;
    private final ArrayList<String> permissions = new ArrayList<>();
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private ActivityLoginBinding mLoginBinding;
    private TextInputEditText passwordField;
    private TextInputEditText emailField;
    private LottieAnimationView heartAnimation;
    private ConstraintLayout loginSubLayout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences mPreferences;
    private ArrayList<String> permissionsToRequest;
    private LocationTrack mLocationTrack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        emailField = mLoginBinding.emailField;
        passwordField = mLoginBinding.passwordField;
        final TextView forgotPasswordText = mLoginBinding.forgotPasswordText;
        final Button signinButton = mLoginBinding.loginButton;
        final Button registerButton = mLoginBinding.registerButton;
        setContentView(mLoginBinding.getRoot());
        //SnackbarHelper.showTopSnackbar(this, "", 1);
        ///////////////////////////////
        SharedPreferences mPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        // add permissions to permissions arraylist
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findDeniedPermissions(permissions);
        if (!permissionsToRequest.isEmpty()) {
            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), INITIAL_PERMISSIONS_RESULTS);
        }
        mLocationTrack = new LocationTrack(LoginActivity.this);
        double latitude = 0;
        double longitude = 0;
        if (mLocationTrack.canGetLocation()) {
            latitude = mLocationTrack.getLatitude();
            longitude = mLocationTrack.getLongitude();
        } else {
            mLocationTrack.showSettingsAlert();
        }
        // storing data longitude and latitude locally
        SharedPreferences.Editor spEditor = mPreferences.edit();
        spEditor.putString("latitude", String.valueOf(latitude));
        spEditor.putString("longitude", String.valueOf(longitude));
        spEditor.apply();
        //////////////////////////////

        assert forgotPasswordText != null;
        forgotPasswordText.setOnClickListener(view -> {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        });
        final Intent registerActivityIntent = new Intent(this, RegisterActivity.class);
        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // you will get result here in result.getData()
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Snackbar snackbar = Snackbar.make(mLoginBinding.getRoot(), R.string.something_went_wrong, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        assert registerButton != null;
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startForResult.launch(new Intent(registerActivityIntent));
            }
        });
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmailValid(emailField.getText().toString()) && isPasswordValid(passwordField.getText().toString())) {

                    signinButton.setEnabled(true);

                } else {
                    if (!isEmailValid(emailField.getText().toString())) {
                        emailField.setError(getString(R.string.not_valid_email));
                    } else if (!isPasswordValid(passwordField.getText().toString())) {
                        passwordField.setError(getString(R.string.invalid_password));
                    }
                    signinButton.setEnabled(false);

                }
            }
        };
        emailField.addTextChangedListener(afterTextChangedListener);
        passwordField.addTextChangedListener(afterTextChangedListener);
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!isEmailValid(emailField.getText().toString())) {
                        emailField.setError(getString(R.string.not_valid_email));
                    } else if (!isPasswordValid(passwordField.getText().toString())) {
                        passwordField.setError(getString(R.string.invalid_password));
                    } else {
                        attemptLogin(emailField.getText().toString(), passwordField.getText().toString());
                    }
                }

                return false;
            }
        });
        assert signinButton != null;
        signinButton.setOnClickListener(view -> {
            attemptLogin(emailField.getText().toString(), passwordField.getText().toString());
        });
    }

    private void attemptLogin(String email, String password) {
        heartAnimation = mLoginBinding.heartAnimation;
        loginSubLayout = mLoginBinding.loginSubLayout;
        heartAnimation.playAnimation();
        heartAnimation.setVisibility(View.VISIBLE);
        loginSubLayout.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabaseReference.addValueEventListener(new ValueEventListener() {
                        String fullName, email, gender, accountType, status;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = mPreferences.edit();

                            if (snapshot.child(AccountType.PATIENT.getValue()).child(firebaseUser.getUid()).hasChildren()) {
                                Log.e("Login::type", "patient");
                                DataSnapshot userNode = snapshot.child(AccountType.PATIENT.getValue()).child(firebaseUser.getUid());
                                Patient patientUser = userNode.getValue(Patient.class);
                                editor.putString("healthStatus", patientUser.getHealthStatus().getValue());
                                //++++++++++++++++++++++++++++++++++++++++++++++++++++++//
                                editor.putString("fullName", patientUser.getFullName());
                                editor.putString("email", patientUser.getEmail());
                                editor.putString("gender", patientUser.getGender().getValue());
                                editor.putString("accountType", patientUser.getAccountType().getValue());
                                editor.apply();
                            } else if (snapshot.child(AccountType.MEDICAL_STAFF.getValue()).child(firebaseUser.getUid()).hasChildren()) {
                                Log.e("Login::type", "med_staff");
                                DataSnapshot userNode = snapshot.child(AccountType.MEDICAL_STAFF.getValue()).child(firebaseUser.getUid());
                                MedicalStaff medicalStaffUser = userNode.getValue(MedicalStaff.class);

                                editor.putString("fullName", medicalStaffUser.getFullName());
                                editor.putString("email", medicalStaffUser.getEmail());
                                editor.putString("gender", medicalStaffUser.getGender().getValue());
                                editor.putString("accountType", medicalStaffUser.getAccountType().getValue());
                                editor.apply();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    setResult(RESULT_OK);
                    finish();

                } else {

                    heartAnimation.cancelAnimation();
                    heartAnimation.setVisibility(View.GONE);
                    loginSubLayout.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar.make(mLoginBinding.getRoot(), R.string.wrong_email_or_password, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_BACK);
        super.onBackPressed();

    }

//    private void startLoginActivity(Intent loginActivityIntent, SharedPreferences mPreferences) {
//        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                Toast.makeText(getApplicationContext(), "sign in successfully " + FirebaseAuth.getInstance().getUid(), Toast.LENGTH_LONG).show();
//                startUserActivity(mPreferences);
//            }
//        });
//        // startUserActivity(mPreferences);
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            startUserActivity(mPreferences);
//        } else {
//            startForResult.launch(new Intent(loginActivityIntent));
//
//        }
//    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean isEmailValid(String email) {
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return false;
        }
    }

    private ArrayList<String> findDeniedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();
        for (String item : wanted) {
            if (!hasPermission(item)) {
                result.add(item);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
}