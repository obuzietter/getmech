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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText emailET, pwdET;
    Button loginBtn;
    ProgressBar progressBar;
    TextView switchLink;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String userID;
    FirebaseDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailET = findViewById(R.id.email_et);
        pwdET = findViewById(R.id.pwd_et);
        loginBtn = findViewById(R.id.login_btn);
        switchLink = findViewById(R.id.switch_link);
        progressBar = findViewById(R.id.progress_bar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        } else {
            // No user is currently signed in, handle this case appropriately
            userID = null;
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        switchLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            }
        });

    }
    private void loginUser() {
        String email = emailET.getText().toString();
        String password = pwdET.getText().toString();

        boolean isValidated = validateCredentials(email, password);

        if (!isValidated) {
            return;
        }

        loginAccountInFireBase(email, password);
    }
    private void loginAccountInFireBase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        fetchUserData(user.getUid());

                    } else {
                        Toaster.showToast(LoginActivity.this, "Email is not verified. Please verify your email");
                    }
                } else {
                    Toaster.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }
    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateCredentials(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Email is invalid");
            return false;
        }
        if (password.length() < 3) {
            pwdET.setError("Password is too short");
            return false;
        }
        return true;
    }

    private void fetchUserData(String userID) {
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Users").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getIsMechanic()==true) {
                            startActivity(new Intent(LoginActivity.this, MechMapsActivity.class));

                        } else {
                            startActivity(new Intent(LoginActivity.this, CustomerMapsActivity.class));

                        }
                    }
                } else {
                    Toaster.showToast(LoginActivity.this, "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toaster.showToast(LoginActivity.this, "Error fetching data");
            }
        });
    }
}