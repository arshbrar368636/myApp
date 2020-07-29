package com.example.gpstrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

public class DriverWelcomeActivity extends AppCompatActivity {
    Button DriverWelcomeButton;
    FirebaseAuth auth;
    FirebaseUser user;
    PermissionManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_welcome);

        DriverWelcomeButton= (Button)findViewById(R.id.DriverWelcomeNew);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            setContentView(R.layout.activity_driver_welcome);
            manager = new PermissionManager() {
            };
            manager.checkAndRequestPermissions(this);
        } else {
            Intent intent = new Intent(DriverWelcomeActivity.this, CustomerMapsActivity.class);
            startActivity(intent);
            finish();
        }

//        DriverWelcomeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(DriverWelcomeActivity.this,DriverRegisterActivity.class);
//                startActivity(intent);
//
//            }
//        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.checkResult(requestCode, permissions, grantResults);

        ArrayList<String> denied_permissions = manager.getStatus().get(0).denied;
        if (denied_permissions.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Permissions enabled", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToDriver(View view) {
        Intent intent= new Intent(DriverWelcomeActivity.this,DriverRegisterActivity.class);
        startActivity(intent);
    }
}
