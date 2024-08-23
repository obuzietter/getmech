package com.example.getmech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPersonalInfoActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference reference;
    private String firstName, lastName, email, phone;
    FirebaseUser firebaseUser;

    private String userID;
    EditText firstNameET, lastNameET, emailET, phoneET;

    private Button updateBtn;

    ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_personal_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

        fetchUserData(userID);

        firstNameET = findViewById(R.id.firstName);
        lastNameET = findViewById(R.id.lastName);
        emailET = findViewById(R.id.email);
        phoneET = findViewById(R.id.phone);

        backIcon = findViewById(R.id.back_icon);
        updateBtn = findViewById(R.id.update_btn);


        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = firstNameET.getText().toString();
                lastName = lastNameET.getText().toString();
                email = emailET.getText().toString();
                phone = phoneET.getText().toString();


                if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()) {

                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Users");

                    reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                User user = snapshot.getValue(User.class);
                                User updateUser;
                                if (user != null) {
                                    Intent intent;
                                    if (user.getIsMechanic()) {
                                        updateUser = new User(firstName, lastName, email, phone, true);
                                        startActivity(new Intent(EditPersonalInfoActivity.this, MechProfileActivity.class));
                                    } else {
                                        updateUser = new User(firstName, lastName, email, phone, false);
                                        startActivity(new Intent(EditPersonalInfoActivity.this, CustomerProfileActivity.class));
                                    }
                                    reference.child(userID).setValue(updateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                fetchUserData(userID);
                                                Toaster.showToast(EditPersonalInfoActivity.this, "Successfully updated");
                                            } else {
                                                Toaster.showToast(EditPersonalInfoActivity.this, "Update failed");
                                            }
                                        }
                                    });
                                }

                            } else {
                                Toaster.showToast(EditPersonalInfoActivity.this, "User not found");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    Toaster.showToast(EditPersonalInfoActivity.this, "Check for empty fields!");
                }

                finish();
            }
        });
    }

    private void fetchUserData(String userName) {
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Users").child(userName);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        firstNameET.setText(user.getFirstName());
                        lastNameET.setText(user.getLastName());
                        emailET.setText(user.getEmail());
                        phoneET.setText(user.getPhone());


                    }
                } else {
                    Toaster.showToast(EditPersonalInfoActivity.this, "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toaster.showToast(EditPersonalInfoActivity.this, "Error fetching data");
            }
        });
    }
}