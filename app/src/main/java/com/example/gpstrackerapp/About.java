package com.example.gpstrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class About extends AppCompatActivity {

    private TextView profileName, profileemail, profilephonenum,  profilegender, profileprofession,profileAddress;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private FirebaseUser Users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        profileName = findViewById(R.id.usname);
        profileemail = findViewById(R.id.usemail);
        profilephonenum = findViewById(R.id.usphone);





        final FirebaseUser Users = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(Users.getUid()).child("name").getValue(String.class);
                String email = dataSnapshot.child(Users.getUid()).child("email").getValue(String.class);
                String password = dataSnapshot.child(Users.getUid()).child("password").getValue(String.class);


                profileName.setText("Name:" + name);
                profileemail.setText("Email:" + email);
                profilephonenum.setText("Password:" + password);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });


    }


}
