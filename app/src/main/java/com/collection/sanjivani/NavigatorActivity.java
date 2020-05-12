package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavigatorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        mDrawerLayout = findViewById(R.id.navDrawerLayout);
        mNavigationView = findViewById(R.id.navView);
        mToolbar = findViewById(R.id.navMenuToolbar);

        setSupportActionBar(mToolbar);
        mNavigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
                //on selected home
            case R.id.nav_home:
                Intent intentHome = new Intent(getApplicationContext(), NavigatorActivity.class);
                startActivity(intentHome);
                //on selected edit profile
            case R.id.nav_edit_profile:
                Intent intentEditProfile = new Intent(getApplicationContext(),EditProfileActivity.class);
                startActivity(intentEditProfile);
                //on selected setting
            case R.id.nav_settings:
                Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intentSettings);
                //on selected logout
            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(NavigatorActivity.this);
                builder.setMessage("Are you sure").
                        setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }

        return true;
    }
}
