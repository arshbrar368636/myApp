package com.example.gpstrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {
    String email;
    EditText e4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        e4=(EditText)findViewById(R.id.editText4);

        Intent intent = getIntent();
        if(intent!= null)
        {
            email=intent.getStringExtra("email");
        }
    }
    public void goToNamePicActivity(View v)
    {
        if(e4.getText().toString().length()>6)
        {
            Intent intent = new Intent(PasswordActivity.this,NameActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password",e4.getText().toString());
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Password length should be more than 6 charaters", Toast.LENGTH_SHORT).show();
        }
    }
}
