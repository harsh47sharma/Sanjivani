package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CaptureUserInformation extends AppCompatActivity {

    EditText mUserName;
    EditText mUserEmail;
    EditText mUserAddress;
    ProgressBar mUserInfoProgressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_user_information);

        db = FirebaseFirestore.getInstance();

        mUserInfoProgressBar = findViewById(R.id.userInfoProgressBar);

        mUserName = findViewById(R.id.userNameEditText);
        mUserEmail = findViewById(R.id.userEmailEditText);
        mUserAddress = findViewById(R.id.userAddressEditText);

        findViewById(R.id.createProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUserName.getText().toString();
                String userEmail = mUserEmail.getText().toString();
                String userAddress = mUserAddress.getText().toString();
                Intent intent = getIntent();
                String userPhoneNumber = intent.getStringExtra("user_phone_number");

                if(onValidationSuccess(userName, userEmail, userAddress)){
                    mUserInfoProgressBar.setVisibility(View.VISIBLE);
                    pushUserDataToFireStore(userName, userEmail, userAddress, userPhoneNumber);
                }
            }
        });
    }

    private Boolean onValidationSuccess(String userName, String userEmail, String userAddress){
       if(userName.isEmpty()){
           mUserName.setError("Name field cannot be empty");
           mUserName.requestFocus();
           return false;
       }
       else if(userEmail.isEmpty()){
           mUserEmail.setError("email field cannot be empty");
           mUserEmail.requestFocus();
           return false;
       }
       else if(userAddress.isEmpty()){
           mUserAddress.setError("Address Field cannot be empty");
           mUserAddress.requestFocus();
           return false;
       }
       else{
           return true;
       }
    }
    private void pushUserDataToFireStore(String userName, String userEmail, String userAddress, String userPhoneNumber){
        String userId = FirebaseAuth.getInstance().getUid();
        Map<String, Object> userDetailsObject = new HashMap<>();
        userDetailsObject.put("UserName", userName);
        userDetailsObject.put("UserEmail", userEmail);
        userDetailsObject.put("UserPhoneNumber", userPhoneNumber);
        userDetailsObject.put("UserAddress", userAddress);

        db.collection("users").document(userId).set(userDetailsObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CaptureUserInformation.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CaptureUserInformation.this, NavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CaptureUserInformation.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        //        CollectionReference dbUsersCollection = db.collection("users") ;
//
//        Users users = new Users(userName, userEmail, userAddress, userPhoneNumber);
//
//        dbUsersCollection.add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Toast.makeText(CaptureUserInformation.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(CaptureUserInformation.this, NavigationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(CaptureUserInformation.this, e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
    }

}
