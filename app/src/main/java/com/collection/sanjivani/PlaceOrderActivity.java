package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity {

    ConstraintLayout mPlaceOrderConstraintLayout;
    TextView mTotalAmountTV;
    TextView mUserAddressTextView, mUserNameTextView, mUserPhoneNumberTextView, mUserEmailTextView;
    TextView mUserCityTextView, mUserState_PinCodeTextView;
    ProgressBar mPlaceOrderPB;
    FirebaseFirestore db;
    DocumentReference documentReference;
    float totalAmount = 0;

    String finAddress;
    String userPhoneNumber;
    String userName;

    private static final String CHANNEL_ID = "Sanjivani";
    private static final String CHANNEL_NAME = "Order Placed";
    private static final String CHANNEL_DESC = "Your order has been placed successfully";

    List<CartInfo> mFinalCartList;

    final String userID = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        RadioButton radioButton = findViewById(R.id.cashOnDeliveryRadioButton);
        radioButton.setChecked(true);

        mPlaceOrderConstraintLayout = findViewById(R.id.placeOrderConstraintLayout);

        mTotalAmountTV = findViewById(R.id.totalAmountTV);
        mUserNameTextView = findViewById(R.id.userNameTextView);
        mUserPhoneNumberTextView = findViewById(R.id.userPhoneNumberTextView);
        mUserEmailTextView = findViewById(R.id.userEmailTextView);
        mUserAddressTextView = findViewById(R.id.userAddressTextView);
        mUserCityTextView = findViewById(R.id.userCityTextView);
        mUserState_PinCodeTextView = findViewById(R.id.userState_PinCodeTextView);
        mPlaceOrderPB = findViewById(R.id.placeOrderPB);

        mPlaceOrderConstraintLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);

        findViewById(R.id.appBarPlaceOrderBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceOrderActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });


        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("users").document(userID);

        mFinalCartList = new ArrayList<>();

        Intent intent = getIntent();
        totalAmount = intent.getFloatExtra("total_payable", totalAmount);
        mTotalAmountTV.setText(String.valueOf(totalAmount));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        //set address
        setAddress();
        getFinalCart();

        findViewById(R.id.updateAddressButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceOrderActivity.this, DeliveryDetailsActivity.class);
                intent.putExtra("total_payable", totalAmount);
                startActivity(intent);
                mTotalAmountTV.setText(String.valueOf(totalAmount));
                finish();
            }
        });

        findViewById(R.id.placeOrderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);
                builder.setMessage("Place this order").
                        setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                placeOrder();
                                mPlaceOrderPB.setVisibility(View.VISIBLE);
                                Toast.makeText(PlaceOrderActivity.this, "Order Placed", Toast.LENGTH_LONG).show();
                                showNavDialog();
                            }
                        })
                        .setNegativeButton("no", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void setNotification(){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cart_black_icon)
                .setContentTitle("Order Placed")
                .setContentText("Your order has been placed successfully")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, mBuilder.build());

    }

    private void showNavDialog(){
        mPlaceOrderPB.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PlaceOrderActivity.this);
        builder1.setMessage("Order Placed!").
                setPositiveButton("go to my orders", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent(PlaceOrderActivity.this, MyOrdersActivity.class);
                        startActivity(intent1);
                        finish();
                    }
                })
                .setNegativeButton("continue shopping", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(PlaceOrderActivity.this, NavigationActivity.class);
                        startActivity(intent1);
                        finish();
                    }
                });
        AlertDialog alertDialog1 = builder1.create();
        alertDialog1.show();
    }

    public void setAddress(){

        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            userName = documentSnapshot.getString("UserName");
                            userPhoneNumber = documentSnapshot.getString("UserPhoneNumber");
                            String userEmail = documentSnapshot.getString("UserEmail");
                            String userAddress = documentSnapshot.getString("UserAddress");
                            String userCity = documentSnapshot.getString("UserCity");
                            String userState = documentSnapshot.getString("UserState");
                            String userPinCode = documentSnapshot.getString("UserPinCode");
                            String userState_PinCode = userState + "-" + userPinCode;
                            finAddress = userAddress + ", " + userCity + ", " + userState + ", " + userPinCode;
                            mUserNameTextView.setText(userName);
                            mUserPhoneNumberTextView.setText(userPhoneNumber);
                            mUserEmailTextView.setText(userEmail);
                            mUserAddressTextView.setText(userAddress);
                            mUserCityTextView.setText(userCity);
                            mUserState_PinCodeTextView.setText(userState_PinCode);

                        }
                        else{
                            Toast.makeText(PlaceOrderActivity.this, "User Details not found", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getFinalCart(){
        documentReference.collection("userCart").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            CartInfo cartInfo = documentSnapshot.toObject(CartInfo.class);
                            mFinalCartList.add(cartInfo);
                        }
                    }
                });
    }

    private void placeOrder(){
        String date = getDate();
        String orderId = getOrderId();
        String time = getTime();

        for(int i = 0; i<mFinalCartList.size(); i++){
            CartInfo cartInfo = mFinalCartList.get(i);
            Map<String, Object> addItemToCartObject = new HashMap<>();
            addItemToCartObject.put("medName", cartInfo.getMedName());
            addItemToCartObject.put("medPrice", cartInfo.getMedPrice());
            addItemToCartObject.put("medQuantity", cartInfo.getMedQuantity());
            addItemToCartObject.put("medItemCount",cartInfo.getMedItemCount());
            documentReference.collection("userOrders").document(orderId)
                    .collection("userOrderItems")
                    .document(cartInfo.getMedName()).set(addItemToCartObject);
            db.collection("allOrders").document(orderId)
                    .collection("ordered items")
                    .document(cartInfo.getMedName())
                    .set(addItemToCartObject);
        }
        Map<String, Object> addItemToCartObject = new HashMap<>();
        addItemToCartObject.put("orderId", orderId);
        addItemToCartObject.put("orderStatus", "Order Placed");
        addItemToCartObject.put("orderDate", date);
        addItemToCartObject.put("orderTime", time);
        addItemToCartObject.put("orderTotal", String.valueOf(totalAmount));
        addItemToCartObject.put("userName", userName);
        addItemToCartObject.put("userPhoneNumber", userPhoneNumber);
        addItemToCartObject.put("userAddress", finAddress);
        documentReference.collection("userOrders").document(orderId).set(addItemToCartObject);
        db.collection("allOrders").document(orderId).set(addItemToCartObject);
        setNotification();
    }

    private void emptyCart(){
        for(int i = 0; i<mFinalCartList.size(); i++){
            documentReference.collection("userCart")
                    .document(mFinalCartList.get(i).getMedName()).delete();
        }
    }

    private String getOrderId(){
        return "orderid"+ getAlphaNumericString() + getDate();
    }

    public String getAlphaNumericString()
    {
        int n = 8;
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public String getDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlaceOrderActivity.this, CartActivity.class);
        startActivity(intent);
        finish();
    }

    public String getTime(){
        String time = java.text.DateFormat.getTimeInstance().format(new Date());
        return time;
    }
}
