package com.example.getmech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MechSignUpActivity extends AppCompatActivity {

    EditText emailET, pwdET, confirmPwdET;
    Button signupBtn;
    ProgressBar progressBar;
    TextView switchLink;

    FirebaseUser firebaseUser;
    private String userID;
    private DatabaseReference reference;
    private String firstName, lastName, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mech_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailET = findViewById(R.id.email_et);
        pwdET = findViewById(R.id.pwd_et);
        confirmPwdET = findViewById(R.id.confirm_pwd_et);
        signupBtn = findViewById(R.id.signup_btn);
        switchLink = findViewById(R.id.switch_link);
        progressBar = findViewById(R.id.progress_bar);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
        switchLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MechSignUpActivity.this, LoginActivity.class));
            }
        });

    }

    private void createAccount() {
        String email = emailET.getText().toString();
        String password = pwdET.getText().toString();
        String confirmPwd = confirmPwdET.getText().toString();

        boolean isValidated = validateCredentials(email, password, confirmPwd);

        if (!isValidated) {
            return;
        }

        createAccountInFirebase(email, password);

    }

    boolean validateCredentials(String email, String password, String confirmPwd) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Email is invalid");
            return false;

        }
        if (password.length() < 3) {
            pwdET.setError("Password is too short");
            return false;
        }
        if (!password.equals(confirmPwd)) {
            confirmPwdET.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void createAccountInFirebase(String email, String password) {
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                changeInProgress(false);

                if (task.isSuccessful()) {

                    Toaster.showToast(MechSignUpActivity.this, "Account created successfully");

                    //adding the details to the RTDB
                    User user = new User(firstName, lastName, email, true);
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    userID = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toaster.showToast(MechSignUpActivity.this, "Successfully updated");
                            } else {
                                Toaster.showToast(MechSignUpActivity.this, "Update failed");
                            }
                        }
                    });


//                sending email verification
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    Toaster.showToast(MechSignUpActivity.this, "Check your Email to verify");

                    firebaseAuth.signOut();
                    startActivity(new Intent(MechSignUpActivity.this, LoginActivity.class));
                    finish();

                } else {
                    Toaster.showToast(MechSignUpActivity.this, task.getException().getLocalizedMessage());
                }

            }


        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            signupBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            signupBtn.setVisibility(View.VISIBLE);
        }
    }
}