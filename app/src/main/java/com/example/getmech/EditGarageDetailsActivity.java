package com.example.getmech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class EditGarageDetailsActivity extends AppCompatActivity {

    String[] places = {"Nairobi, Westlands", "Nairobi, Karen", "Nairobi, Lang'ata", "Nairobi, Kilimani", "Nairobi, Kasarani"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    private FirebaseDatabase db;
    private DatabaseReference reference;
    private String name, email, phone, address;
    FirebaseUser firebaseUser;

    private String userID;
    EditText garageNameET, businessEmailET, businessPhoneET;

    private Button updateBtn;

    ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_garage_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(EditGarageDetailsActivity.this, R.layout.list_item, places);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toaster.showToast(EditGarageDetailsActivity.this, "Item: " + item);
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

        fetchGarageData(userID);

        garageNameET = findViewById(R.id.garage_name);
        businessEmailET = findViewById(R.id.business_email);
        businessPhoneET = findViewById(R.id.business_phone);


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
                name = garageNameET.getText().toString();
                email = businessEmailET.getText().toString();
                phone = businessPhoneET.getText().toString();
                address = autoCompleteTextView.getText().toString();

                if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {
                    Garage garage = new Garage(name, email, phone, address);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Users");
                    reference.child(userID).child("Garage Details").setValue(garage).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                fetchGarageData(userID);
                                Toaster.showToast(EditGarageDetailsActivity.this, "Successfully updated");
                            } else {
                                Toaster.showToast(EditGarageDetailsActivity.this, "Update failed");
                            }
                        }
                    });
                }else{
                    Toaster.showToast(EditGarageDetailsActivity.this, "Check for empty fields!");
                }
                startActivity(new Intent(EditGarageDetailsActivity.this, MechProfileActivity.class));
                finish();
            }
        });
    }
    private void fetchGarageData(String userName) {
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Users").child(userName).child("Garage Details");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Garage garage = snapshot.getValue(Garage.class);
                    if (garage != null) {
                        garageNameET.setText(garage.getName());
                        businessEmailET.setText(garage.getEmail());
                        businessPhoneET.setText(garage.getPhone());
                        autoCompleteTextView.setText(garage.getAddress());


                    }
                } else {
//                    Toaster.showToast(EditGarageDetailsActivity.this, "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toaster.showToast(EditGarageDetailsActivity.this, "Error fetching data");
            }
        });
    }
}