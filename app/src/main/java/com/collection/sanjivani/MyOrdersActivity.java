package com.collection.sanjivani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    List<OrderInfo> mOrdersArrayList;

    RecyclerView mOrdersRecyclerView;
    OrderAdapter mOrdersAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    String userID;

    FirebaseFirestore db;
    CollectionReference userOrderCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mOrdersArrayList = new ArrayList<>();
        mOrdersRecyclerView = findViewById(R.id.ordersRecyclerView);

        this.setTitle("My Orders");

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
        userOrderCollectionReference = db.collection("users").document(userID)
                .collection("userOrders");

        mOrdersRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mOrdersRecyclerView.setLayoutManager(mLayoutManager);

        setOrderAdapter();
    }

    private void setOrderAdapter() {
        userOrderCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            OrderInfo orderInfo = documentSnapshot.toObject(OrderInfo.class);
                            mOrdersArrayList.add(orderInfo);
                        }
                        mOrdersAdapter = new OrderAdapter(MyOrdersActivity.this, mOrdersArrayList);
                        mOrdersRecyclerView.setAdapter(mOrdersAdapter);

                        mOrdersAdapter.setOnOrderClickListener(new OrderAdapter.OnOrderItemClickListener() {
                            @Override
                            public void onOrderClick(int position) {
                                Intent intent = new Intent(MyOrdersActivity.this, OrderInformationActivity.class);
                                intent.putExtra("order_object", mOrdersArrayList.get(position));
                                startActivity(intent);
                            }
                        });
                    }
                });
    }
}
