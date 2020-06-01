package com.collection.sanjivani;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Toolbar mToolbar;
    TextView mNameTextView, mCartBadgeTextView;
    TextView mEmailTextView;
    TextView mPhoneNumberTextView;
    Boolean isBackPressedTwice = false;

    RecyclerView mNavItemRecyclerView;
    RecyclerView mNav2ItemRecyclerView;
    NavigationItemAdapter mNavigationItemAdapter;
    Navigation2ItemAdapter mNavigation2ItemAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.LayoutManager m2LayoutManager;

    List<MedInfo> mNavItemArrayList;
    List<MedInfo> mNav2ItemArrayList;

    FirebaseFirestore db;
    DocumentReference getUserDetails;
    CollectionReference userOrderCollectionReference;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);

        mNavItemRecyclerView = findViewById(R.id.navItemRecyclerView);
        mNav2ItemRecyclerView = findViewById(R.id.nav2ItemRecyclerView);
        mCartBadgeTextView = findViewById(R.id.navPageCartBadgeTextView);


        db = FirebaseFirestore.getInstance();

        mNavItemArrayList = new ArrayList<>();
        mNav2ItemArrayList = new ArrayList<>();

        userID = FirebaseAuth.getInstance().getUid();
        userOrderCollectionReference = db.collection("drugInfoDB");

        mNavItemRecyclerView.setHasFixedSize(true);
        mNav2ItemRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(NavigationActivity.this, LinearLayoutManager.HORIZONTAL, false);
        m2LayoutManager = new LinearLayoutManager(NavigationActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mNavItemRecyclerView.setLayoutManager(mLayoutManager);
        mNav2ItemRecyclerView.setLayoutManager(m2LayoutManager);

        setNavItemAdapter();
        setNav2ItemAdapter();
        //Setting navigation drawer
        setUpNavigationDrawer();

        //Setting navigation header text view
        setNavHeaderTextView();

        findViewById(R.id.searchbarEditText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(NavigationActivity.this,SearchDrugActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.uploadButtonNavPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(NavigationActivity.this,UploadPrescriptionActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.appBarNavCartImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(NavigationActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setNavItemAdapter(){

        userOrderCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            MedInfo medInfo = documentSnapshot.toObject(MedInfo.class);
                            if(medInfo.getNavList().equals("1")){
                                mNavItemArrayList.add(medInfo);
                            }
                        }
                        mNavigationItemAdapter = new NavigationItemAdapter(mNavItemArrayList, NavigationActivity.this);
                        mNavItemRecyclerView.setAdapter(mNavigationItemAdapter);

                        mNavigationItemAdapter.setOnNavItemClickListener(new NavigationItemAdapter.OnNavItemClickListener() {
                            @Override
                            public void onNavItemClick(int position) {
                                Intent intent = new Intent(NavigationActivity.this, DrugInformationActivity.class);
                                intent.putExtra("items_object", mNavItemArrayList.get(position));
                                startActivity(intent);
                            }
                        });
                    }
                });
    }

    private void setNav2ItemAdapter(){
        userOrderCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            MedInfo medInfo = documentSnapshot.toObject(MedInfo.class);
                            if(medInfo.getNavList().equals("2")){
                                mNav2ItemArrayList.add(medInfo);
                            }
                        }
                        mNavigation2ItemAdapter = new Navigation2ItemAdapter(mNav2ItemArrayList, NavigationActivity.this);
                        mNav2ItemRecyclerView.setAdapter(mNavigation2ItemAdapter);

                        mNavigation2ItemAdapter.setOnNav2ItemClickListener(new Navigation2ItemAdapter.OnNav2ItemClickListener() {
                            @Override
                            public void onNav2ItemClick(int position) {
                                Intent intent = new Intent(NavigationActivity.this, DrugInformationActivity.class);
                                intent.putExtra("items_object", mNav2ItemArrayList.get(position));
                                startActivity(intent);
                            }
                        });
                    }
                });
    }

    private void setNavHeaderTextView(){
        View headerView = mNavigationView.getHeaderView(0);
        mNameTextView = headerView.findViewById(R.id.navHeaderNameTextView);
        mEmailTextView = headerView.findViewById(R.id.navHeaderEmailTextView);
        mPhoneNumberTextView = headerView.findViewById(R.id.navHeaderPhoneNumberTextView);

        String userId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
        getUserDetails = db.collection("users").document(userId);

        getUserDetails.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String userName = documentSnapshot.getString("UserName");
                            String userEmail = documentSnapshot.getString("UserEmail");
                            String userPhoneNumber = documentSnapshot.getString("UserPhoneNumber");
                            mNameTextView.setText(userName);
                            mEmailTextView.setText(userEmail);
                            mPhoneNumberTextView.setText(userPhoneNumber);
                        }
                        else{
                            Toast.makeText(NavigationActivity.this, "User Details not found", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NavigationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setUpNavigationDrawer(){
        setSupportActionBar(mToolbar);
        mNavigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.nav_edit_profile){
            Intent intentEditProfile = new Intent(NavigationActivity.this,EditProfileActivity.class);
            startActivity(intentEditProfile);
        }
        else if(item.getItemId() == R.id.nav_settings){
            Intent intentEditProfile = new Intent(NavigationActivity.this,SettingsActivity.class);
            startActivity(intentEditProfile);
        }
        else if (item.getItemId() == R.id.nav_uploadPrescription){
            Intent intentUploadPrescription = new Intent(NavigationActivity.this, UploadPrescriptionActivity.class);
            startActivity(intentUploadPrescription);
        }
        else if (item.getItemId() == R.id.nav_my_orders){
            Intent intentMyOrders = new Intent(NavigationActivity.this, MyOrdersActivity.class);
            startActivity(intentMyOrders);
        }
        else if(item.getItemId() == R.id.nav_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
            builder.setMessage("Are you sure").
                    setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(NavigationActivity.this, PhoneLoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("cancel", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return true;
    }
    private void getCartBadge(){
        SharedPreferences sharedPreferences = getSharedPreferences("appCartBadge", MODE_PRIVATE);
        String value = sharedPreferences.getString("cart_badge","");
        mCartBadgeTextView.setText(value);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartBadge();

    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            if(isBackPressedTwice){
                super.onBackPressed();
            }
            else{
                isBackPressedTwice = true;
                Toast.makeText(NavigationActivity.this, "Please back again to exit", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isBackPressedTwice = false;
                    }
                }, 4000);
            }
        }
    }
}
