package com.example.gpstrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class InviteCodeActivity extends AppCompatActivity {
     String name,email,password,code,isSharing;
     Uri imageUri;
     TextView textViewCode;
     FirebaseAuth auth;
     FirebaseUser user;
     DatabaseReference reference;
     ProgressDialog progressDialog;
     String userId;
     StorageReference storageReference;
     private String onlineCustomerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        textViewCode=(TextView)findViewById(R.id.codetextview) ;
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        Intent intent= getIntent();




        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference= FirebaseStorage.getInstance().getReference().child("Users_images");
        if(intent!= null)
        {
            name= intent.getStringExtra("name");
            email= intent.getStringExtra("email");
            password= intent.getStringExtra("password");
            code= intent.getStringExtra("code");
            isSharing=intent.getStringExtra("isSharing");
            imageUri=intent.getParcelableExtra("imageUri");
        }
        textViewCode.setText(code);
    }
    public  void userRegister(View v)
    {
        progressDialog.setMessage("Please wait while we are creating an account for u");
        progressDialog.show();
        user = auth.getCurrentUser();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            user= auth.getCurrentUser();
                            CreateUser createUser= new CreateUser(name, email,password,code,"false","na","na","na",user.getUid());

                            userId= user.getUid();

                            reference.child(userId).setValue(createUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {

                                                StorageReference sr= storageReference.child(user.getUid() +".jpg");
                                                sr.putFile(imageUri)
                                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    String download_image_path = task.getResult().getStorage().getDownloadUrl().toString();
                                                                    reference.child(user.getUid()).child("imageUrl").setValue(download_image_path)
                                                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                             @Override
                                                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                                                 if(task.isSuccessful())
                                                                                                 {
                                                                                                     onlineCustomerId=auth.getCurrentUser().getUid();
                                                                                                     reference=FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerId);

                                                                                                     reference.setValue(true);
                                                                                                     progressDialog.dismiss();
                                                                                                     Toast.makeText(InviteCodeActivity.this, "Send Verification email.Check email", Toast.LENGTH_SHORT).show();
                                                                                                     sendVerificationEmail();

                                                                                                     Toast.makeText(getApplicationContext(),"User Registered Successfully",Toast.LENGTH_SHORT).show();
                                                                                                     
//                                                                                                     Intent intent= new Intent(getApplicationContext(),UserLocationMainActivity.class);
//                                                                                                     startActivity(intent);
                                                                                                 }
                                                                                                 else
                                                                                                 {
                                                                                                     progressDialog.dismiss();
                                                                                                     Toast.makeText(getApplicationContext(),"Error occurred while creating an account",Toast.LENGTH_SHORT).show();
                                                                                                 }
                                                                                             }

                                                                                             private void sendVerificationEmail() {
                                                                                                 user.sendEmailVerification()
                                                                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                             @Override
                                                                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                                                                 if(task.isSuccessful())
                                                                                                                 {
                                                                                                                     Toast.makeText(getApplicationContext(),"Email sent for verification",Toast.LENGTH_SHORT).show();
                                                                                                                     finish();
                                                                                                                     auth.signOut();
                                                                                                                 }
                                                                                                                 else
                                                                                                                 {
                                                                                                                     Toast.makeText(getApplicationContext(),"Could not send for verification",Toast.LENGTH_SHORT).show();
                                                                                                                 }
                                                                                                             }
                                                                                                         });
                                                                                             }
                                                                                         });
                                                                }
                                                            }
                                                        });
                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Could not insert values in database",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
