package com.issasafar.healthcore.ui.register;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.data.model.AccountType;
import com.issasafar.healthcore.data.model.Gender;
import com.issasafar.healthcore.data.model.GpsLocation;
import com.issasafar.healthcore.data.model.HealthStatus;
import com.issasafar.healthcore.data.model.MedicalStaff;
import com.issasafar.healthcore.data.model.OnlinePatient;
import com.issasafar.healthcore.data.model.Patient;
import com.issasafar.healthcore.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private final String SHARED_PREF_FILE = "com.issasafar.healthcore";
    private ActivityRegisterBinding mRegisterBinding;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences mPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(mRegisterBinding.getRoot());
        mPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        final TextInputLayout passwordLayout = mRegisterBinding.passwordLayout;
        final TextInputLayout retypePasswordLayout = mRegisterBinding.retypePasswordLayout;
        final EditText fullNameField = mRegisterBinding.nameField;
        final EditText emailField = mRegisterBinding.emailField;
        final EditText passwordField = mRegisterBinding.passwordField;
        final EditText retypePasswordField = mRegisterBinding.retypePasswordField;
        final RadioGroup genderGroup = mRegisterBinding.genderRadioGroup;
        final RadioGroup accountGroup = mRegisterBinding.accountRadioGroup;
        final Button signupButton = mRegisterBinding.signupButton;

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordLayout.setEndIconVisible(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() <= 5) {
                    passwordLayout.setEndIconVisible(false);
                    passwordField.setError(getString(R.string.password_condition));
                }

            }
        });
        retypePasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                retypePasswordLayout.setEndIconVisible(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(passwordField.getText().toString())) {
                    retypePasswordLayout.setEndIconVisible(false);
                    retypePasswordField.setError(getString(R.string.password_doesn_t_match));
                } else {
                    retypePasswordLayout.setEndIconVisible(true);

                }
            }
        });
        // signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password, retypedPassword, email, fullName;
                AccountType accountType;
                Gender gender;
                try {
                    fullName = fullNameField.getText().toString();
                    email = emailField.getText().toString();
                    password = passwordField.getText().toString();
                    retypedPassword = retypePasswordField.getText().toString();
                } catch (NullPointerException e) {
                    if (fullNameField.getText() == null) {
                        fullNameField.setError(getString(R.string.provide_a_username));
                    }
                    if (emailField.getText() == null) {
                        emailField.setError(getString(R.string.provide_a_username));
                    }
                    if (passwordField.getText() == null) {
                        passwordField.setError(getString(R.string.provide_a_username));
                    }
                    if (retypePasswordField.getText() == null) {
                        retypePasswordField.setError(getString(R.string.provide_a_username));
                    }
                    return;
                }
                int genderCheckedId = genderGroup.getCheckedRadioButtonId();
                gender = detectGender(genderCheckedId);
                int accountTypeCheckedId = accountGroup.getCheckedRadioButtonId();
                accountType = detectAccountType(accountTypeCheckedId);
                boolean emailValid = isEmailValid(email);
                if (!emailValid) {
                    emailField.setError(getString(R.string.not_valid_email));
                }
                boolean passwordValid = isPasswordValid(password);
                if (!passwordValid) {
                    passwordLayout.setEndIconVisible(false);
                    passwordField.setError(getString(R.string.password_condition));
                }
                boolean passwordMatch = doesPasswordMatch(password, retypedPassword);
                if (!passwordMatch) {
                    retypePasswordLayout.setEndIconVisible(false);
                    retypePasswordField.setError(getString(R.string.password_doesn_t_match));
                }
                boolean emptyName = fullName.isEmpty();
                if (emptyName) {
                    fullNameField.setError(getString(R.string.provide_a_username));
                }
                if (emailValid && !emptyName && passwordValid && passwordMatch) {
                    try {
                        attemptRegister(fullName, email, password, gender, accountType);

                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(mRegisterBinding.getRoot(), R.string.something_went_wrong, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

            }
        });
    }

    private void attemptRegister(String fullName, String email, String password, Gender gender, AccountType accountType) {
        final LottieAnimationView heartAnimation = mRegisterBinding.heartAnimation;
        final ConstraintLayout registerSubLayout = mRegisterBinding.registerSubLayout;
        registerSubLayout.setVisibility(View.GONE);
        heartAnimation.playAnimation();
        heartAnimation.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        try {
            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                    if (task.getResult().getSignInMethods().isEmpty()) {


                        // new email
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference userReference = mDatabaseReference.child("users").child(accountType.getValue()).child(firebaseUser.getUid());
                                    SharedPreferences.Editor spEditor = mPreferences.edit();


                                    if (accountType == AccountType.PATIENT) {
                                        Patient patientUser = new Patient(fullName, email, gender, accountType, HealthStatus.HEALTHY);
                                        userReference.setValue(patientUser);


                                        Double longitude, latitude;
                                        if (mPreferences != null) {
                                            longitude = Double.valueOf(mPreferences.getString("longitude", ""));
                                            latitude = Double.valueOf(mPreferences.getString("latitude", ""));
                                            GpsLocation gpsLocation = new GpsLocation(latitude, longitude);
                                            OnlinePatient onlinePatient = new OnlinePatient(patientUser.getHealthStatus(), gpsLocation);
                                            mDatabaseReference.child("online").child(firebaseUser.getUid()).setValue(onlinePatient);

                                        }

                                        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("/online/" + firebaseUser.getUid());

                                        presenceRef.onDisconnect().removeValue();


                                        spEditor.putString("healthStatus", patientUser.getHealthStatus().getValue());


                                    } else if (accountType == AccountType.MEDICAL_STAFF) {
                                        MedicalStaff medicalStaffUser = new MedicalStaff(fullName, email, gender, accountType);
                                        userReference.setValue(medicalStaffUser);
                                    }
                                    spEditor.putString("fullName", fullName);
                                    spEditor.putString("email", email);
                                    spEditor.putString("gender", gender.getValue());
                                    spEditor.putString("accountType", accountType.getValue());
                                    spEditor.apply();
                                    // set result
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    heartAnimation.cancelAnimation();
                                    heartAnimation.setVisibility(View.GONE);
                                    registerSubLayout.setVisibility(View.VISIBLE);
                                    Snackbar snackbar = Snackbar.make(mRegisterBinding.getRoot(), R.string.something_went_wrong, Snackbar.LENGTH_LONG);
                                    snackbar.setAction("Okay", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                        });
                    } else {
                        // email existed
                        heartAnimation.cancelAnimation();
                        heartAnimation.setVisibility(View.GONE);
                        registerSubLayout.setVisibility(View.VISIBLE);
                        Snackbar snackbar = Snackbar.make(mRegisterBinding.getRoot(), R.string.email_already_exist, Snackbar.LENGTH_LONG);
                        snackbar.setAction("Login", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        snackbar.show();
                    }
                }
            });
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(mRegisterBinding.getRoot(), R.string.something_went_wrong, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    private AccountType detectAccountType(int accountTypeCheckedId) {
        AccountType accountType;
        if (accountTypeCheckedId == R.id.radio_medical_staff) {
            accountType = AccountType.MEDICAL_STAFF;
        } else {
            accountType = AccountType.PATIENT;
        }
        return accountType;
    }

    private Gender detectGender(int genderCheckedId) {
        Gender gender;
        if (genderCheckedId == R.id.radio_male) {
            gender = Gender.MALE;
        } else if (genderCheckedId == R.id.radio_female) {
            gender = Gender.FEMALE;
        } else {
            gender = Gender.OTHER;
        }
        return gender;
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean doesPasswordMatch(String password, String retypedPassword) {
        return password.equals(retypedPassword) && !password.isEmpty();
    }

    private boolean isEmailValid(String email) {
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return false;
        }
    }
}