package com.example.getmech;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseDatabase
        db = FirebaseDatabase.getInstance();

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                finish();
            } else {
                String userID = currentUser.getUid();
                fetchUserData(userID);
            }


        }, 2000);

    }

    private void fetchUserData(String userID) {
        reference = db.getReference("Users").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Intent intent;
                        if (user.getIsMechanic()) {
                            intent = new Intent(SplashActivity.this, MechMapsActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, CustomerMapsActivity.class);
                        }
                        startActivity(intent);
                        finish(); // Finish SplashActivity after starting new activity
                    }
                } else {
                    Toaster.showToast(SplashActivity.this, "User not found");
                    finish(); // Finish SplashActivity if user not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toaster.showToast(SplashActivity.this, "Error fetching data");
                finish(); // Finish SplashActivity if there's an error
            }
        });
    }
}