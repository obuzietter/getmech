package com.example.getmech;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerRequestsActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseReference usersReference, requestsReference;
    ArrayList<String> list;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        listView = findViewById(R.id.list_view);
        requestsReference = FirebaseDatabase.getInstance().getReference().child("Customer Requests");
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(CustomerRequestsActivity.this, R.layout.mech_list_item, list);
        listView.setAdapter(adapter);

        requestsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userID = dataSnapshot.getKey(); // Retrieve the key (userID) from the snapshot
                    retrieveUserDetails(userID); // Retrieve user details based on userID
                    //list.add(userID); // Add the userID to the list

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void retrieveUserDetails(String userID) {
        usersReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve user information from snapshot
                    String firstName = snapshot.child("firstName n      ").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String userInfo = firstName +" "+lastName+ " (" + phone + ")";
                    list.add(userInfo); // Add user info to the list
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors.
            }
        });
    }
}