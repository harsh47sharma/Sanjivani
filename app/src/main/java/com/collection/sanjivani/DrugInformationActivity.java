package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DrugInformationActivity extends AppCompatActivity {

    ConstraintLayout mDrugInfoConstraintLayout;
    TextView mMedNameTextView;
    TextView mAppBarDrugInfoTextView;
    TextView mMedDescriptionTextView;
    TextView mMedPriceTextView;
    TextView mMedAvailabilityTextView;
    TextView mMedQuantityTextView;
    TextView mItemCountTextView;
    TextView mCartBadgeTextView;
    Boolean doesExists = false;

    String mMedName, mMedPrice, mMedQuantity, mMedAvailability, mMedDescription;
    int mItemCount = 1;

    private FirebaseFirestore db;
    private CollectionReference cartCollectionReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_information);

        mMedNameTextView = findViewById(R.id.infoPageMedNameTextView);
        mMedDescriptionTextView = findViewById(R.id.infoPageMedDescriptionTextView);
        mMedPriceTextView = findViewById(R.id.infoPageMedPriceTextView);
        mMedAvailabilityTextView = findViewById(R.id.infoPageMedAvailabilityTextView);
        mMedQuantityTextView = findViewById(R.id.infoPageMedQuantityTextView);
        mItemCountTextView = findViewById(R.id.itemCountTextVIew);
        mDrugInfoConstraintLayout = findViewById(R.id.drugInfoConstraintLayout);
        mAppBarDrugInfoTextView = findViewById(R.id.appBarDrugInfoTextView);
        mCartBadgeTextView = findViewById(R.id.drugInfoCartBadgeTextView);

        db = FirebaseFirestore.getInstance();
        cartCollectionReference = db.collection("users");

        mDrugInfoConstraintLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);

        findViewById(R.id.appBarDrugInfoBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugInformationActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.appBarDrugInfoSearchImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugInformationActivity.this, SearchDrugActivity.class);
                startActivity(intent);
                finish();
            }
        });


        findViewById(R.id.appBarDrugInfoCartImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugInformationActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            MedInfo medInfo = (MedInfo) intent.getSerializableExtra("items_object");
            mMedName = medInfo.getMedName();
            mMedDescription = medInfo.getMedDescription();
            mMedPrice = medInfo.getMedPrice();
            mMedAvailability = medInfo.getMedAvailability();
            mMedQuantity = medInfo.getMedQuantity();
            populateMedInfo();
        }

        findViewById(R.id.drugInfoAddToCartFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DrugInformationActivity.this);
                builder.setMessage("Add this item to cart").
                        setPositiveButton("add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addThisItemToCart();
                            }
                        })
                        .setNegativeButton("cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        findViewById(R.id.drugInfoAddCountImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemCount();
            }
        });

        findViewById(R.id.drugInfoReduceCountImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reduceItemCount();
            }
        });

    }

    private void getCartBadge(){
        SharedPreferences sharedPreferences = getSharedPreferences("appCartBadge", MODE_PRIVATE);
        String value = sharedPreferences.getString("cart_badge","");
        mCartBadgeTextView.setText(value);

    }

    private void addItemCount(){
        if(mItemCount >= 10){
            Toast.makeText(DrugInformationActivity.this, "Cannot add any more item", Toast.LENGTH_LONG).show();
        }
        else {
            mItemCount++;
            mItemCountTextView.setText(String.valueOf(mItemCount));
        }
    }

    private void reduceItemCount(){
        if(mItemCount == 1){
            Toast.makeText(DrugInformationActivity.this, "Need a have at least one item quantity", Toast.LENGTH_LONG).show();
        }
        else {
            mItemCount--;
            mItemCountTextView.setText(String.valueOf(mItemCount));
        }
    }

    private void addThisItemToCart() {
        final String userId = FirebaseAuth.getInstance().getUid();
        final Map<String, Object> addItemToCartObject = new HashMap<>();
        addItemToCartObject.put("medName", mMedName);
        addItemToCartObject.put("medPrice", mMedPrice);
        addItemToCartObject.put("medQuantity", mMedQuantity);
        addItemToCartObject.put("medItemCount", Integer.toString(mItemCount));


        cartCollectionReference.document(userId).collection("userCart")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            CartInfo cartInfo = documentSnapshot.toObject(CartInfo.class);
                            if (cartInfo.getMedName().equals(mMedName.toLowerCase())) {
                                Toast.makeText(DrugInformationActivity.this, "Item already added to cart", Toast.LENGTH_LONG).show();
                                doesExists = true;
                                break;
                            }
                        }
                        if (!doesExists) {
                            Toast.makeText(DrugInformationActivity.this, "Item added to cart", Toast.LENGTH_LONG).show();
                            db.collection("users").document(userId).collection("userCart")
                                    .document(mMedName).set(addItemToCartObject);
                        }
                        doesExists = false;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DrugInformationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateMedInfo() {
        mMedNameTextView.setText(mMedName);
        mAppBarDrugInfoTextView.setText(mMedName);
        mMedDescriptionTextView.setText(mMedDescription);
        mMedPriceTextView.setText(mMedPrice);
        mMedAvailabilityTextView.setText(mMedAvailability);
        mMedQuantityTextView.setText(mMedQuantity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartBadge();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DrugInformationActivity.this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }
}
