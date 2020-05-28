package com.collection.sanjivani;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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

    ConstraintLayout mConstraintLayout;
    List<CartInfo> mCartArrayList;
    List<Integer> mCartItemCountArrayList;
    List<Float> mCartItemPriceArrayList;

    TextView mEmptyCartTextView, mCartItemSelectedQuantityTV, mCartTotalPayableTV;

    RecyclerView mCartRecyclerView;
    CartAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    //TextView mItemCountCartTextView;

    String userID;

    FirebaseFirestore db;
    DocumentReference userCartDocumentReference;

    float mCartItemTotalPrice = 0;
    //int cartItemsCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mConstraintLayout = findViewById(R.id.cartViewConstraintLayout);

        mCartArrayList = new ArrayList<>();
        mCartItemCountArrayList = new ArrayList<>();
        mCartItemPriceArrayList = new ArrayList<>();

        mCartRecyclerView = findViewById(R.id.cartRecyclerView);
        mEmptyCartTextView = findViewById(R.id.emptyCartTextView);
        mCartItemSelectedQuantityTV = findViewById(R.id.cartItemSelectedQuantityTV);
        mCartTotalPayableTV = findViewById(R.id.cartTotalPayableTextView);
      //  mItemCountCartTextView = findViewById(R.id.itemCountCartTextView);

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
        userCartDocumentReference = db.collection("users").document(userID);

        //setting up recycler view
        mCartRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mCartRecyclerView.setLayoutManager(mLayoutManager);

        mConstraintLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);

        //populating cart recycler view
        setAdapter();

        findViewById(R.id.appBarCartBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.appBarCartSearchImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, SearchDrugActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.checkOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCartArrayList.isEmpty()){
                    Toast.makeText(CartActivity.this, "Cannot proceed, cart is empty", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(CartActivity.this, PlaceOrderActivity.class);
                    intent.putExtra("total_payable", mCartItemTotalPrice);
                    startActivity(intent);
                }
            }
        });


    }

    private void setAdapter() {

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

//                        cartItemsCount = mAdapter.getItemCount();
//                        mItemCountCartTextView.setText(cartItemsCount);

                        mAdapter.setOnCartItemClickListener(new CartAdapter.OnCartItemClickListener() {
                            @Override
                            public void onCartItemDeleteClick(final int position) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setMessage("Remove this item from cart?").
                                        setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                deleteItemFromCart(position);
                                                mCartArrayList.remove(position);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("no", null);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                //cartItemsCount--;
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
        mCartTotalPayableTV.setText(String.valueOf(mCartItemTotalPrice));
    }

    private void updateItemCountToDB(int position, int index) {
        userCartDocumentReference.collection("userCart")
                .document(mCartArrayList.get(position).getMedName())
                .update("medItemCount", String.valueOf(index));
    }

    public void deleteItemFromCart(final int position) {
        mCartItemTotalPrice -= (mCartItemPriceArrayList.get(position)*mCartItemCountArrayList.get(position));
        mCartItemCountArrayList.remove(position);
        mCartItemPriceArrayList.remove(position);
        setTotalPayableTV();
        userCartDocumentReference.collection("userCart")
                .document(mCartArrayList.get(position).getMedName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CartActivity.this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }
}
