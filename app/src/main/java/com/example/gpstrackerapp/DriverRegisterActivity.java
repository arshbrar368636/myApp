package com.example.gpstrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karan.churi.PermissionManager.PermissionManager;

public class DriverRegisterActivity extends AppCompatActivity {

    private Button driverLoginButton,driverregisterButton;
    private TextView driverRegisterLink,driverStatus;
    private EditText EmailDriver,PasswordDriver;

    ProgressDialog progressDialog;
    FirebaseUser userDriver;
    PermissionManager manager;
    private String onlineDriverId;
    DatabaseReference driverDatabasereference;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        driverLoginButton=(Button)findViewById(R.id.driver_btn_login);
        driverregisterButton=(Button)findViewById(R.id.driver_btn_Register);
        driverRegisterLink=(TextView)findViewById(R.id.driver_register_link);
        driverStatus=(TextView)findViewById(R.id.driver_status);
        EmailDriver=(EditText)findViewById(R.id.email_driver);
        PasswordDriver=(EditText)findViewById(R.id.password_driver);

        progressDialog= new ProgressDialog(this);

        auth=FirebaseAuth.getInstance();


        driverregisterButton.setVisibility(View.INVISIBLE);
        driverregisterButton.setEnabled(false);

        driverRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverLoginButton.setVisibility(View.INVISIBLE);
                driverRegisterLink.setVisibility(View.INVISIBLE);
                driverStatus.setText("Register Driver");

                driverregisterButton.setVisibility(View.VISIBLE);
                driverregisterButton.setEnabled(true);
            }
        });

        driverregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= EmailDriver.getText().toString();
                String password= PasswordDriver.getText().toString();

                RegisterDriver(email,password);
            }
        });
        driverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= EmailDriver.getText().toString();
                String password= PasswordDriver.getText().toString();
                DriverSignIn(email,password);
            }
        });

    }

    private void DriverSignIn(String email, String password) {
        if(TextUtils.isEmpty(email)) {

            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {

            Toast.makeText(getApplicationContext(),"Please enter password...",Toast.LENGTH_SHORT).show();

        }
        else
        {
            progressDialog.setTitle("Driver Login");
            progressDialog.setMessage("Please wait while  checking your Credential..");
            progressDialog.show();
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Driver login Successfully",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Intent intent= new Intent(getApplicationContext(),CustomerMapsActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext()," login Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void RegisterDriver(String email, String password) {
        if(TextUtils.isEmpty(email)) {

            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {

            Toast.makeText(getApplicationContext(),"Please enter password...",Toast.LENGTH_SHORT).show();

        }
        else
        {
            progressDialog.setTitle("Driver Regsiteration");
            progressDialog.setMessage("Please wait while registeration");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        onlineDriverId=auth.getCurrentUser().getUid();
                        driverDatabasereference= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverId);

                        driverDatabasereference.setValue(true);
                        Toast.makeText(getApplicationContext(),"Driver registered Successfully",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext()," Registeration Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }
}
