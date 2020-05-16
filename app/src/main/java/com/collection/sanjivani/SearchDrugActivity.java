package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchDrugActivity extends AppCompatActivity {

    EditText searchBoxEditText;
    RecyclerView recyclerView;
    CollectionReference drugCollectionReference;
    FirebaseFirestore db;

    List<String> medNameArrayList;
    List<String> medPriceArrayList;
    List<String> medAvailabilityArrayList;

    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_drug);

        searchBoxEditText = findViewById(R.id.searchBoxEditText);
        recyclerView = findViewById(R.id.searchActivityRecyclerView);

        db = FirebaseFirestore.getInstance();
        drugCollectionReference = db.collection("drugInfoDB");

        medNameArrayList = new ArrayList<>();
        medPriceArrayList = new ArrayList<>();
        medAvailabilityArrayList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        searchBoxEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()){
                    setAdapter(editable.toString());
                }
            }
        });
    }

    private void setAdapter(final String stringSearchedByUser){


        drugCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        medNameArrayList.clear();
                        medPriceArrayList.clear();
                        medAvailabilityArrayList.clear();
                        recyclerView.removeAllViews();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            MedInfo medInfo = documentSnapshot.toObject(MedInfo.class);

                            String medName = medInfo.getMedName();
                            String medPrice = medInfo.getMedPrice();
                            String medAvailability = medInfo.getMedAvailability();

                            if(medName.contains(stringSearchedByUser.toLowerCase())){
                                medNameArrayList.add(medName);
                                medPriceArrayList.add(medPrice);
                                medAvailabilityArrayList.add(medAvailability);
                            }
                        }

                        searchAdapter = new SearchAdapter(SearchDrugActivity.this, medNameArrayList, medPriceArrayList, medAvailabilityArrayList);
                        recyclerView.setAdapter(searchAdapter);
                    }
                });
    }

}
