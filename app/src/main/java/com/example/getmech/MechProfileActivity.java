package com.example.getmech;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.BreakIterator;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MechProfileActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private String firstName, lastName, email;
    FirebaseUser firebaseUser;
    private String userID;
    TextView emailTV, fullnameTV, phoneTV, editPersonalInfo, userNameTV, editGarageDetails;
    TextView addressTV,businessPhoneTV,businessEmailTV,garageNameTV;
    ImageView backIcon, profilePic;
    Button logoutBtn;

    //imagepicker
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mech_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailTV = findViewById(R.id.email);
        fullnameTV = findViewById(R.id.fullname);
        phoneTV = findViewById(R.id.phone);
        backIcon = findViewById(R.id.back_icon);
        logoutBtn = findViewById(R.id.logout_btn);
        editPersonalInfo = findViewById(R.id.edit_personal_info);
        userNameTV = findViewById(R.id.username);
        profilePic = findViewById(R.id.profile_image);
        editGarageDetails = findViewById(R.id.edit_garage_details);
        addressTV = findViewById(R.id.physical_address);
        businessPhoneTV = findViewById(R.id.business_phone);
        businessEmailTV = findViewById(R.id.business_email);
        garageNameTV = findViewById(R.id.garage_name);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

        fetchUserData(userID);
        fetchGarageData(userID);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == CustomerProfileActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            Glide.with(MechProfileActivity.this).load(selectedImageUri).apply(RequestOptions.circleCropTransform()).into(profilePic);
                            storeProfilePic(selectedImageUri);
                        }
                    }
                }
        );

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MechProfileActivity.this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher.launch(intent);
                                return null;
                            }
                        });

            }
        });
        editPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MechProfileActivity.this, EditPersonalInfoActivity.class));
                finish();
            }
        });
        editGarageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MechProfileActivity.this, EditGarageDetailsActivity.class));
            }
        });
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MechProfileActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

            }
        });
    }

    private void fetchUserData(String userName) {

        retrieveProfilePic();

        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Users").child(userName);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userNameTV.setText(user.getEmail());
                        fullnameTV.setText(user.getFirstName() + " " + user.getLastName());
                        emailTV.setText(user.getEmail());
                        phoneTV.setText(user.getPhone());

                    }
                } else {
                    Toaster.showToast(MechProfileActivity.this, "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toaster.showToast(MechProfileActivity.this, "Error fetching data");
            }
        });
    }
    public void storeProfilePic(Uri selectedImageUri) {
        if (selectedImageUri == null) {
            Toaster.showToast(MechProfileActivity.this, "No image selected!");
            return;
        }

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference firebaseStorageReference = firebaseStorage.getReference().child("profile_pic").child(userID);

        firebaseStorageReference.putFile(selectedImageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            firebaseStorageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        // You can store the download URL in your database
                                        // databaseReference.child("users").child(userID).child("profile_pic_url").setValue(downloadUri.toString());
                                        Toaster.showToast(MechProfileActivity.this, "Profile Picture Updated!");
                                    } else {
                                        Toaster.showToast(MechProfileActivity.this, "Failed to get download URL!");
                                    }
                                }
                            });
                        } else {
                            Toaster.showToast(MechProfileActivity.this, "Failed To Update Profile Picture!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toaster.showToast(MechProfileActivity.this, "Upload failed: " + e.getMessage());
                    }
                });
    }
    public void retrieveProfilePic(){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference firebaseStorageReference = firebaseStorage.getReference().child("profile_pic").child(userID);
        firebaseStorageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri uri = task.getResult();
                    Glide.with(MechProfileActivity.this).load(uri).apply(RequestOptions.circleCropTransform()).into(profilePic);
                }
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
                        garageNameTV.setText(garage.getName());
                        businessEmailTV.setText(garage.getEmail());
                        businessPhoneTV.setText(garage.getPhone());
                        addressTV.setText(garage.getAddress());


                    }
                } else {
                    Toaster.showToast(MechProfileActivity.this, "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toaster.showToast(MechProfileActivity.this, "Error fetching data");
            }
        });
    }
}