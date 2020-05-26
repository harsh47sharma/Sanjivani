package com.collection.sanjivani;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    EditText medName, medPrice, medQuantity, medAvailability, medDescription, medImage;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        medName = findViewById(R.id.medNameET);
        medPrice = findViewById(R.id.medPriceET);
        medQuantity = findViewById(R.id.medQuantityET);
        medAvailability = findViewById(R.id.medAvailabilityET);
        medDescription = findViewById(R.id.medDescriptionET);
        medImage = findViewById(R.id.medImageET);

        db = FirebaseFirestore.getInstance();


        findViewById(R.id.submitData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });

        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }
    private void setData(){
        Map<String, Object> addItemToCartObject = new HashMap<>();
        addItemToCartObject.put("medName", medName.getText().toString());
        addItemToCartObject.put("medPrice", medPrice.getText().toString());
        addItemToCartObject.put("medAvailability", medAvailability.getText().toString());
        addItemToCartObject.put("medDescription", medDescription.getText().toString());
        addItemToCartObject.put("medQuantity", medQuantity.getText().toString());
        addItemToCartObject.put("medImage", medImage.getText().toString());

        db.collection("drugInfoDB").document().set(addItemToCartObject).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                medName.setText("");
                medPrice.setText("");
                medQuantity.setText("");
                medAvailability.setText("");
                medDescription.setText("");
                medImage.setText("");
            }
        });

    }
    private void signOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setMessage("confirm sign out").
                setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(AdminActivity.this, PhoneLoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("no", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
