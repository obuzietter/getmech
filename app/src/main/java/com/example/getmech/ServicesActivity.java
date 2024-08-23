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

public class ServicesActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_services);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.list_view);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(ServicesActivity.this, R.layout.mech_list_item, list);
        listView.setAdapter(adapter);

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user.getIsMechanic()) {

                        // Now access the "Garage Details" child node
                        DataSnapshot garageDetailsSnapshot = dataSnapshot.child("Garage Details");

                        if (garageDetailsSnapshot.exists()) {
                            // Assuming GarageDetails is a custom class that maps to the "Garage Details" node structure
                            Garage garageDetails = garageDetailsSnapshot.getValue(Garage.class);

                            if (garageDetails != null) {
                                String name = garageDetails.getName();
                                String phone = garageDetails.getPhone();
                                String garageAddress = garageDetails.getAddress();
                                // You can combine these details into a string or object to display in your list
                                String txt = name + " - " + phone + " (" + garageAddress + ")";
                                list.add(txt);
                            }


                        }

//                    list.add(snapshot.getValue().toString());
                    }
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
            });

        }
    }