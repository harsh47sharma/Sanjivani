package com.collection.sanjivani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText mNameET, mEmailET, mAddressET, mCityET, mStateET, mPinCodeET;
    ProgressBar mProgressBar;
    ConstraintLayout mEditProfileConstraintLayout;
    TextView mMobileNumberTextView;
    DocumentReference getUserDetails;

    FirebaseFirestore db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mNameET = findViewById(R.id.changeNameEditProfileEditText);
        mEmailET = findViewById(R.id.emailEditProfileEditText);
        mAddressET = findViewById(R.id.addressEditProfileEditText);
        mCityET = findViewById(R.id.userCityEditProfileEditText);
        mStateET = findViewById(R.id.userStateEditProfileEditText);
        mPinCodeET = findViewById(R.id.userPinCodeEditProfileEditText);
        mProgressBar = findViewById(R.id.editProfileProgressBar);
        mEditProfileConstraintLayout= findViewById(R.id.editProfileConstraintLayout);

        setPhoneNumberTextView();

        findViewById(R.id.appBarEditProfileBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getUid();

        findViewById(R.id.saveAddressButtonEditProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mNameET.getText().toString();
                String userEmail = mEmailET.getText().toString();
                String userAddress = mAddressET.getText().toString();
                String userCity = mCityET.getText().toString();
                String userState = mStateET.getText().toString();
                String userPinCode = mPinCodeET.getText().toString();
                if(onValidationSuccess(userName, userEmail, userAddress, userCity, userState, userPinCode)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    updateUserInfo();
                }
            }
        });
    }

    private void setPhoneNumberTextView() {
        mMobileNumberTextView = findViewById(R.id.mobileNumberEditProfileTextView);

        String userId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
        getUserDetails = db.collection("users").document(userId);

        getUserDetails.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String userPhoneNumber = documentSnapshot.getString("UserPhoneNumber");
                            mMobileNumberTextView.setText(userPhoneNumber);
                        }
                        else{
                            Toast.makeText(EditProfileActivity.this, "User Details not found", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private Boolean onValidationSuccess(String userName, String userEmail, String userAddress, String userCity, String userState, String userPinCode){

        if(userAddress.isEmpty()){
            mAddressET.setError("Address Field cannot be empty");
            mAddressET.requestFocus();
            return false;
        }
        else if(userName.isEmpty()){
            mNameET.setError("name Field cannot be empty");
            mNameET.requestFocus();
            return false;
        }
        else if(userEmail.isEmpty()){
            mEmailET.setError("email Field cannot be empty");
            mEmailET.requestFocus();
            return false;
        }
        else if(userCity.isEmpty()){
            mCityET.setError("city Field cannot be empty");
            mCityET.requestFocus();
            return false;
        }
        else if(userState.isEmpty()){
            mStateET.setError("State Field cannot be empty");
            mStateET.requestFocus();
            return false;
        }
        else if(userPinCode.isEmpty()){
            mPinCodeET.setError("pin code Field cannot be empty");
            mPinCodeET.requestFocus();
            return false;
        }
        else{
            return true;
        }
    }
    private void updateUserInfo(){
        Map<String, Object> addItemToCartObject = new HashMap<>();
        addItemToCartObject.put("UserName", mNameET.getText().toString());
        addItemToCartObject.put("UserAddress", mAddressET.getText().toString());
        addItemToCartObject.put("UserCity", mCityET.getText().toString());
        addItemToCartObject.put("UserState", mStateET.getText().toString());
        addItemToCartObject.put("UserEmail", mEmailET.getText().toString());
        addItemToCartObject.put("userPinCode", mPinCodeET.getText().toString());
        db.collection("users").document(userID).update(addItemToCartObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditProfileActivity.this, NavigationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }

}
