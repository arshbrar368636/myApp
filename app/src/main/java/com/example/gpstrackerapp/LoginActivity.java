package com.example.gpstrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
     FirebaseAuth auth;
     EditText e1,e2;
    private TextView textViewForgotPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        e1=(EditText)findViewById(R.id.editText);
        e2=(EditText)findViewById(R.id.editText2);
        auth= FirebaseAuth.getInstance();
        textViewForgotPass=(TextView) findViewById(R.id.textViewForgotPassword);

        textViewForgotPass.setOnClickListener(this);

    }

    public  void login(View v){
        auth.signInWithEmailAndPassword(e1.getText().toString(),e2.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            //Toast.makeText(getApplicationContext(),"Logged In Successfully",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                            if(user.isEmailVerified())
                            {

                                Intent intent = new Intent(LoginActivity.this, UserLocationMainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Email is not verifies yet",Toast.LENGTH_SHORT).show();

                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Wrong email or password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {

        if(view == textViewForgotPass){
            finish();
            startActivity(new Intent(this, PasswordResetActivity.class));
        }
    }

}
