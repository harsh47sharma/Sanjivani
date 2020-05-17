package com.collection.sanjivani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

    List<MedInfo> medInfoArrayList;

    SearchDrugAdapter searchDrugAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_drug);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchBoxEditText = findViewById(R.id.searchBoxEditText);
        recyclerView = findViewById(R.id.searchActivityRecyclerView);

        db = FirebaseFirestore.getInstance();
        drugCollectionReference = db.collection("drugInfoDB");

        medInfoArrayList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));

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

                        medInfoArrayList.clear();
                        recyclerView.removeAllViews();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            MedInfo medInfo = documentSnapshot.toObject(MedInfo.class);

                            if(medInfo.getMedName().contains(stringSearchedByUser.toLowerCase())){
                                medInfoArrayList.add(medInfo);
                            }
                        }

                        searchDrugAdapter = new SearchDrugAdapter(SearchDrugActivity.this, medInfoArrayList);
                        recyclerView.setAdapter(searchDrugAdapter);

                        searchDrugAdapter.setOnMedClickListener(new SearchDrugAdapter.OnMedClickListener() {
                            @Override
                            public void onMedClick(int position) {
                                Intent intent = new Intent(SearchDrugActivity.this, DrugInformationActivity.class);
                                intent.putExtra("items_object", medInfoArrayList.get(position));
                                startActivity(intent);
                            }
                        });
                    }
                });
    }
}
