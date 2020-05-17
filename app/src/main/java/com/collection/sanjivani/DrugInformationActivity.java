package com.collection.sanjivani;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DrugInformationActivity extends AppCompatActivity {

    TextView mMedNameTextView;
    TextView mMedDescriptionTextView;
    TextView mMedPriceTextView;
    TextView mMedAvailabilityTextView;
    TextView mMedQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_information);

        mMedNameTextView = findViewById(R.id.infoPageMedNameTextView);
        mMedDescriptionTextView = findViewById(R.id.infoPageMedDescriptionTextView);
        mMedPriceTextView = findViewById(R.id.infoPageMedPriceTextView);
        mMedAvailabilityTextView = findViewById(R.id.infoPageMedAvailabilityTextView);
        mMedQuantityTextView = findViewById(R.id.infoPageMedQuantityTextView);

        Intent intent = getIntent();

        if(intent.getExtras() != null){
            MedInfo medInfo = (MedInfo) intent.getSerializableExtra("items_object");
            mMedNameTextView.setText(medInfo.getMedName());
            mMedDescriptionTextView.setText(medInfo.getMedDescription());
            mMedPriceTextView.setText(medInfo.getMedPrice());
            mMedAvailabilityTextView.setText(medInfo.getMedAvailability());
            mMedQuantityTextView.setText(medInfo.getMedQuantity());
        }
    }
}
