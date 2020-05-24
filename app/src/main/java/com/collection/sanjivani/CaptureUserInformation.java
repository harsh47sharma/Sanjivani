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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CaptureUserInformation extends AppCompatActivity {

    EditText mUserName;
    EditText mUserEmail;
   // EditText mUserAddress;
    EditText mUserCity, mUserState, mUserPinCode;
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
       // mUserAddress = findViewById(R.id.addressEditText);
        mUserCity = findViewById(R.id.cityEditText);
        mUserState = findViewById(R.id.stateEditText);
        mUserPinCode = findViewById(R.id.pinCodeEditText);

        findViewById(R.id.createProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUserName.getText().toString();
                String userEmail = mUserEmail.getText().toString();
               // String userAddress = mUserAddress.getText().toString();
                String userCity = mUserCity.getText().toString();
                String userState = mUserState.getText().toString();
                String userPinCode = mUserPinCode.getText().toString();
                Intent intent = getIntent();
                String userPhoneNumber = intent.getStringExtra("user_phone_number");

                /*if(onValidationSuccess(userName, userEmail, userAddress, userCity, userState, userPinCode)){
                    mUserInfoProgressBar.setVisibility(View.VISIBLE);
                    pushUserDataToFireStore(userName, userEmail, userAddress, userPhoneNumber, userCity, userState, userPinCode);
                }*/
            }
        });
    }

    private Boolean onValidationSuccess(String userName, String userEmail, String userAddress, String userCity, String userState, String userPinCode){
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
       /*else if(userAddress.isEmpty()){
           mUserAddress.setError("Address Field cannot be empty");
           mUserAddress.requestFocus();
           return false;
       }*/
       else if(userCity.isEmpty()){
           mUserCity.setError("city Field cannot be empty");
           mUserCity.requestFocus();
           return false;
       }
       else if(userState.isEmpty()){
           mUserState.setError("State Field cannot be empty");
           mUserState.requestFocus();
           return false;
       }
       else if(userPinCode.isEmpty()){
           mUserPinCode.setError("pin code Field cannot be empty");
           mUserPinCode.requestFocus();
           return false;
       }
       else{
           return true;
       }
    }
    private void pushUserDataToFireStore(String userName, String userEmail, String userAddress, String userPhoneNumber, String userCity, String userState, String userPinCode){
        String userId = FirebaseAuth.getInstance().getUid();
        Map<String, Object> userDetailsObject = new HashMap<>();
        userDetailsObject.put("UserName", userName);
        userDetailsObject.put("UserEmail", userEmail);
        userDetailsObject.put("UserPhoneNumber", userPhoneNumber);
        userDetailsObject.put("UserAddress", userAddress);
        userDetailsObject.put("userCity", userCity);
        userDetailsObject.put("userState", userState);
        userDetailsObject.put("userPinCode", userPinCode);

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
