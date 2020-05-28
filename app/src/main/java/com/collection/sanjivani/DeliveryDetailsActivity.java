package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DeliveryDetailsActivity extends AppCompatActivity {

    ConstraintLayout mDeliveryDetailsConstraintLayout;
    EditText mUserAddress;
    EditText mUserCity, mUserState, mUserPinCode;
    ProgressBar mProgressBar;
    FirebaseFirestore db;
    float totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        mDeliveryDetailsConstraintLayout = findViewById(R.id.deliveryDetailsConstraintLayout);

        mUserAddress = findViewById(R.id.deliveryAddressEditText);
        mUserCity = findViewById(R.id.deliveryUserCityEditText);
        mUserState = findViewById(R.id.deliveryUserStateEditText);
        mUserPinCode = findViewById(R.id.deliveryUserPinCodeEditText);

        mProgressBar = findViewById(R.id.editProfileProgressBar);

        mDeliveryDetailsConstraintLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);

        findViewById(R.id.appBarDeliveryDetailsBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveryDetailsActivity.this, PlaceOrderActivity.class);
                startActivity(intent);
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        totalAmount = intent.getFloatExtra("total_payable", totalAmount);

        findViewById(R.id.saveAddressButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAddress = mUserAddress.getText().toString();
                String userCity = mUserCity.getText().toString();
                String userState = mUserState.getText().toString();
                String userPinCode = mUserPinCode.getText().toString();

                if(onValidationSuccess(userAddress, userCity, userState, userPinCode)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    pushUserDataToFireStore(userAddress, userCity, userState, userPinCode);
                }
            }
        });
    }

    private Boolean onValidationSuccess(String userAddress, String userCity, String userState, String userPinCode){

        if(userAddress.isEmpty()){
            mUserAddress.setError("Address Field cannot be empty");
            mUserAddress.requestFocus();
            return false;
        }
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
    private void pushUserDataToFireStore(String userAddress, String userCity, String userState, String userPinCode){
        String userId = FirebaseAuth.getInstance().getUid();
        Map<String, Object> userDetailsObject = new HashMap<>();
        userDetailsObject.put("UserAddress", userAddress);
        userDetailsObject.put("UserCity", userCity);
        userDetailsObject.put("UserState", userState);
        userDetailsObject.put("UserPinCode", userPinCode);

        db.collection("users").document(userId).update(userDetailsObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DeliveryDetailsActivity.this, "Address updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeliveryDetailsActivity.this, PlaceOrderActivity.class);
                        intent.putExtra("total_payable", totalAmount);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DeliveryDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeliveryDetailsActivity.this, PlaceOrderActivity.class);
        startActivity(intent);
        finish();
    }
}
