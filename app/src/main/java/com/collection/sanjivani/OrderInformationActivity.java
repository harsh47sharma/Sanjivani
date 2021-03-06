package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderInformationActivity extends AppCompatActivity {

    TextView mOrderInfoId, mOrderInfoStatus, mOrderInfoTotalAmount, mOrderInfoDate, mOrderInfoTime, mCartBadgeTextView;
    TextView mOrderInfoUserName, mOrderInfoUserEmail, mOrderInfoUserPhoneNumber, mOrderInfoUserAddress, mOrderInfoUserCity;
    TextView mOrderInfoUserState, mOrderInfoUserPinCode;
    ConstraintLayout mOrderInfoConstraintLayout;

    FirebaseFirestore db;
    DocumentReference mDocumentReference;

    String userID;
    String orderId;

    RecyclerView mRecyclerView;
    OrderInfoItemAdapter mOrderInfoItemAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    List<OrderInfo> mOrdersArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);

        mOrderInfoConstraintLayout = findViewById(R.id.orderInfoConstraintLayout);

        mOrderInfoId = findViewById(R.id.orderInfoId);
        mOrderInfoStatus = findViewById(R.id.orderInfoStatus);
        mOrderInfoTotalAmount = findViewById(R.id.orderInfoTotalAmount);
        mOrderInfoDate = findViewById(R.id.orderInfoDate);
        mOrderInfoTime = findViewById(R.id.orderInfoTime);

        mOrderInfoUserName = findViewById(R.id.orderInfoUserName);
        mOrderInfoUserEmail = findViewById(R.id.orderInfoUserEmail);
        mOrderInfoUserPhoneNumber = findViewById(R.id.orderInfoUserPhoneNumber);
        mOrderInfoUserAddress = findViewById(R.id.orderInfoUserAddress);
        mOrderInfoUserCity = findViewById(R.id.orderInfoUserCity);
        mOrderInfoUserState = findViewById(R.id.orderInfoUserState);
        mOrderInfoUserPinCode = findViewById(R.id.orderInfoUserPinCode);
        mCartBadgeTextView = findViewById(R.id.orderInfoCartBadgeTextView);

        mRecyclerView = findViewById(R.id.orderInfoItemRecyclerView);

        findViewById(R.id.appBarOrderInfoBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderInformationActivity.this, MyOrdersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.appBarOrderInfoSearchImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderInformationActivity.this, SearchDrugActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.appBarOrderInfoCartImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderInformationActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mOrdersArrayList = new ArrayList<>();

        userID = FirebaseAuth.getInstance().getUid();

        db = FirebaseFirestore.getInstance();
        mDocumentReference = db.collection("users").document(userID);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        setOrderInfo();
        setUserInfo();

        setOrderItemList();

        findViewById(R.id.cancelOrderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderInformationActivity.this);
                builder.setMessage("Are you sure you want to cancel this order").
                        setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelOrder();
                            }
                        })
                        .setNegativeButton("no", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    private void cancelOrder() {
        mDocumentReference.collection("userOrders").document(orderId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OrderInformationActivity.this, MyOrdersActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void setOrderInfo() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            OrderInfo orderInfo = (OrderInfo) intent.getSerializableExtra("order_object");
            orderId = orderInfo.getOrderId();
            mOrderInfoId.setText(orderId);
            mOrderInfoStatus.setText(orderInfo.getOrderStatus());
            mOrderInfoTotalAmount.setText(orderInfo.getOrderTotal());
            mOrderInfoDate.setText(orderInfo.getOrderDate());
            mOrderInfoTime.setText(orderInfo.getOrderTime());
        }
    }

    private void setUserInfo() {
        mDocumentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            mOrderInfoUserName.setText(documentSnapshot.getString("UserName"));
                            mOrderInfoUserPhoneNumber.setText(documentSnapshot.getString("UserPhoneNumber"));
                            mOrderInfoUserEmail.setText(documentSnapshot.getString("UserEmail"));
                            mOrderInfoUserAddress.setText(documentSnapshot.getString("UserAddress"));
                            mOrderInfoUserCity.setText(documentSnapshot.getString("UserCity"));
                            mOrderInfoUserState.setText(documentSnapshot.getString("UserState"));
                            mOrderInfoUserPinCode.setText(documentSnapshot.getString("UserPinCode"));
                        } else {
                            Toast.makeText(OrderInformationActivity.this, "User Details not found", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderInformationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void onBackPressed() {
        Intent intent = new Intent(OrderInformationActivity.this, MyOrdersActivity.class);
        startActivity(intent);
        finish();
    }

    private void setOrderItemList() {

        mDocumentReference.collection("userOrders").document(orderId).collection("userOrderItems")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            OrderInfo orderInfo = documentSnapshot.toObject(OrderInfo.class);
                            mOrdersArrayList.add(orderInfo);
                        }

                        mOrderInfoItemAdapter = new OrderInfoItemAdapter(OrderInformationActivity.this, mOrdersArrayList);
                        mRecyclerView.setAdapter(mOrderInfoItemAdapter);
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
