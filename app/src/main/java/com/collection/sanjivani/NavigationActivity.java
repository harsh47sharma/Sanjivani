package com.collection.sanjivani;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Toolbar mToolbar;
    TextView mNameTextView;
    TextView mEmailTextView;
    //TextView mPhoneNumberTextView;
    Boolean isBackPressedTwice = false;

    FirebaseFirestore db;
    DocumentReference getUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);

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

        findViewById(R.id.addToCartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigationActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setNavHeaderTextView(){
        View headerView = mNavigationView.getHeaderView(0);
        mNameTextView = headerView.findViewById(R.id.navHeaderNameTextView);
        mEmailTextView = headerView.findViewById(R.id.navHeaderEmailTextView);
        //mPhoneNumberTextView = headerView.findViewById(R.id.navHeaderPhoneNumberTextView);

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
                            //String userPhoneNumber = documentSnapshot.getString("UserPhoneNumber");

                            mNameTextView.setText(userName);
                            mEmailTextView.setText(userEmail);
                            //mPhoneNumberTextView.setText(userPhoneNumber);
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
