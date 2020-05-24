package com.collection.sanjivani;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    List<CartInfo> mCartArrayList;
    List<Integer> mCartItemCountArrayList;
    List<Float> mCartItemPriceArrayList;

    TextView mEmptyCartTextView, mCartItemSelectedQuantityTV, mCartTotalPayableTV;

    RecyclerView mCartRecyclerView;
    CartAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    String userID;

    FirebaseFirestore db;
    DocumentReference userCartDocumentReference;

    float mCartItemTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCartArrayList = new ArrayList<>();
        mCartItemCountArrayList = new ArrayList<>();
        mCartItemPriceArrayList = new ArrayList<>();

        mCartRecyclerView = findViewById(R.id.cartRecyclerView);
        mEmptyCartTextView = findViewById(R.id.emptyCartTextView);
        mCartItemSelectedQuantityTV = findViewById(R.id.cartItemSelectedQuantityTV);
        mCartTotalPayableTV = findViewById(R.id.cartTotalPayableTextView);

        this.setTitle("Cart");

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
        userCartDocumentReference = db.collection("users").document(userID);

        //setting up recycler view
        mCartRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mCartRecyclerView.setLayoutManager(mLayoutManager);

        //populating cart recycler view
        setAdapter();

        findViewById(R.id.checkOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, PlaceOrderActivity.class);
                intent.putExtra("total_payable", mCartItemTotalPrice);
                startActivity(intent);
            }
        });
    }

    private void setAdapter() {
        String userID = FirebaseAuth.getInstance().getUid();

        userCartDocumentReference.collection("userCart").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            CartInfo cartInfo = documentSnapshot.toObject(CartInfo.class);
                            mCartItemCountArrayList.add(Integer.parseInt(cartInfo.getMedItemCount()));
                            mCartItemPriceArrayList.add(Float.parseFloat(cartInfo.getMedPrice()));
                            mCartArrayList.add(cartInfo);
                        }
                        checkIsCartEmpty();
                        calculateFinalAmount(mCartItemPriceArrayList, mCartItemCountArrayList);
                        setTotalPayableTV();

                        mAdapter = new CartAdapter(mCartArrayList);
                        mCartRecyclerView.setAdapter(mAdapter);

                        mAdapter.setOnCartItemClickListener(new CartAdapter.OnCartItemClickListener() {
                            @Override
                            public void onCartItemDeleteClick(int position) {
                                deleteItemFromCart(position);
                                setTotalPayableTV();
                                mCartArrayList.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public int onCartAddItemClick(int position) {
                                int index = mCartItemCountArrayList.get(position);
                                if (index >= 10) {
                                    Toast.makeText(CartActivity.this, "Cannot add more of this item", Toast.LENGTH_LONG).show();
                                } else {
                                    index++;
                                    mCartItemCountArrayList.set(position, index);
                                    updateItemCountToDB(position,index);
                                    mCartItemTotalPrice += mCartItemPriceArrayList.get(position);
                                    setTotalPayableTV();
                                }
                                return index;
                            }

                            @Override
                            public int onCartRemoveItemClick(int position) {
                                int index = mCartItemCountArrayList.get(position);
                                if (index == 1) {
                                    Toast.makeText(CartActivity.this, "at least one item quantity is required", Toast.LENGTH_LONG).show();
                                } else {
                                    index--;
                                    mCartItemCountArrayList.set(position, index);
                                    updateItemCountToDB(position,index);
                                    mCartItemTotalPrice -= mCartItemPriceArrayList.get(position);
                                    setTotalPayableTV();
                                }
                                return index;
                            }
                        });
                    }
                });
    }

    public void calculateFinalAmount(List<Float> price, List<Integer> itemCount) {
        float perItemPrice = 0;
        for (int i = 0; i < price.size(); i++) {
            perItemPrice = price.get(i) * itemCount.get(i);
            mCartItemTotalPrice += perItemPrice;
        }
    }

    public void setTotalPayableTV(){
        mCartTotalPayableTV.setText("Rs. "+ String.valueOf(mCartItemTotalPrice));
    }

    private void updateItemCountToDB(int position, int index) {
        userCartDocumentReference.collection("userCart")
                .document(mCartArrayList.get(position).getMedName())
                .update("medItemCount", String.valueOf(index));
    }

    public void deleteItemFromCart(int position) {
        userCartDocumentReference.collection("userCart")
                .document(mCartArrayList.get(position).getMedName()).delete();

        mCartItemTotalPrice -= mCartItemPriceArrayList.get(position)*mCartItemCountArrayList.get(position);

    }

    private void checkIsCartEmpty() {
        if (mCartArrayList.isEmpty()) {
            mCartRecyclerView.setVisibility(View.INVISIBLE);
            mCartTotalPayableTV.setVisibility(View.INVISIBLE);
            mEmptyCartTextView.findViewById(R.id.emptyCartTextView).setVisibility(View.VISIBLE);
        }
        else{
            mCartRecyclerView.setVisibility(View.VISIBLE);
            mCartTotalPayableTV.setVisibility(View.VISIBLE);
            mEmptyCartTextView.findViewById(R.id.emptyCartTextView).setVisibility(View.INVISIBLE);
        }
    }

}
