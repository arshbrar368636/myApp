package com.example.gpstrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AvailableDrivers extends AppCompatActivity {

    private TextView driverOrigin,availableDriverName,driverDestination;
    private CircleImageView profileImage;

    ArrayList<String> array = new ArrayList<>();
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_drivers);
//
//        driverOrigin = findViewById(R.id.available_origin_driver);
//        driverDestination=findViewById(R.id.available_destination_driver);
//        availableDriverName=findViewById(R.id.available_name_driver);
//
//        profileImage=findViewById(R.id.available_profile_image_driver);

       // mListView=findViewById(R.id.listView);


//        final FirebaseUser Users = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String origin = dataSnapshot.child(Users.getUid()).child("origin").getValue(String.class);
//                String destination = dataSnapshot.child(Users.getUid()).child("destination").getValue(String.class);
//                String name = dataSnapshot.child(Users.getUid()).child("username").getValue(String.class);
//
//                String image = dataSnapshot.child(Users.getUid()).child("image").getValue().toString();
//                Picasso.get().load(image).into(profileImage);
//
//
//                driverOrigin.setText("Origin:" + origin);
//                driverDestination.setText("Destination:" + destination);
//                availableDriverName.setText("Driver Name:" + name);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//
//            }
//        });


//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//
//        DatabaseReference usersdRef = rootRef.child("Users").child("Drivers");
//
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                    String username = ds.child("username").getValue(String.class);
//
//                    Log.d("TAG", username);
//
//                    array.add(username);
//
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter(AvailableDrivers.this, android.R.layout.simple_list_item_1, array);
//
//                mListView.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        usersdRef.addListenerForSingleValueEvent(eventListener);
    }


    }

