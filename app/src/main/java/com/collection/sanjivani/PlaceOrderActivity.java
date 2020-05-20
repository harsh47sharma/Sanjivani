package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PlaceOrderActivity extends AppCompatActivity {

    TextView mTotalAmountTV, mAddressTextView;
    FirebaseFirestore db;
    float totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        mTotalAmountTV = findViewById(R.id.totalAmountTV);
        mAddressTextView = findViewById(R.id.addressTV);

        Intent intent = getIntent();
        totalAmount = intent.getFloatExtra("total_payable", totalAmount);
        mTotalAmountTV.setText(String.valueOf(totalAmount));
        setAddress();

        findViewById(R.id.updateAddressButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceOrderActivity.this, DeliveryDetailsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.checkOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setAddress(){
        db = FirebaseFirestore.getInstance();
        final String userID = FirebaseAuth.getInstance().getUid();

        db.collection("users").document(userID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String userAddress = documentSnapshot.getString("UserAddress");
                            String userCity = documentSnapshot.getString("UserCity");
                            String userState = documentSnapshot.getString("UserState");
                            String userPinCode = documentSnapshot.getString("UserPinCode");
                            String finAddress = userAddress + " ," + userCity + " ," + userState + " ," + userPinCode;
                            mAddressTextView.setText(finAddress);
                        }
                        else{
                            Toast.makeText(PlaceOrderActivity.this, "User Details not found", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
