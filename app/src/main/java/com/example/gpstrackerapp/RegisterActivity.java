package com.example.gpstrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {
    EditText e3;
    FirebaseAuth auth;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e3=(EditText)findViewById(R.id.editText3);
        auth=FirebaseAuth.getInstance();
        dialog= new ProgressDialog(this);
    }

    public void goToPasswordActivity(View v)
    {
        dialog.setMessage("Checking email address");
        dialog.show();
        auth.fetchSignInMethodsForEmail(e3.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful())
                        {
                            dialog.dismiss();
                            boolean check = !task.getResult().getSignInMethods().isEmpty();
                            if(!check)
                            {
                                Intent intent = new Intent(RegisterActivity.this,PasswordActivity.class);
                                intent.putExtra("email",e3.getText().toString());
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"This email is already registered",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
