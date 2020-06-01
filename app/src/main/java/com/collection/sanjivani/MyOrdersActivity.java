package com.collection.sanjivani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    ConstraintLayout mMyOrdersConstraintLayout;

    RecyclerView mOrdersRecyclerView;
    OrderAdapter mOrdersAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    TextView mCartBadgeTextView;

    String userID;

    FirebaseFirestore db;
    CollectionReference userOrderCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        mOrdersArrayList = new ArrayList<>();
        mOrdersRecyclerView = findViewById(R.id.ordersRecyclerView);
        mMyOrdersConstraintLayout = findViewById(R.id.myOrdersConstraintLayout);
        mCartBadgeTextView = findViewById(R.id.myOrdersCartBadgeTextView);

        findViewById(R.id.appBarMyOrdersBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyOrdersActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.appBarMyOrdersSearchImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyOrdersActivity.this, SearchDrugActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.appBarMyOrdersCartImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyOrdersActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
        userOrderCollectionReference = db.collection("users").document(userID)
                .collection("userOrders");

        mOrdersRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mOrdersRecyclerView.setLayoutManager(mLayoutManager);
        SpacingItemsDecorator itemsDecorator = new SpacingItemsDecorator(30);
        mOrdersRecyclerView.addItemDecoration(itemsDecorator);
        setOrderAdapter();
    }

    public void onBackPressed() {
        Intent intent = new Intent(MyOrdersActivity.this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }


    private void setOrderAdapter() {
        userOrderCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
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

    private void getCartBadge() {
        SharedPreferences sharedPreferences = getSharedPreferences("appCartBadge", MODE_PRIVATE);
        String value = sharedPreferences.getString("cart_badge", "");
        mCartBadgeTextView.setText(value);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartBadge();

    }
}
