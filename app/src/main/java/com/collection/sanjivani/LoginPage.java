package com.collection.sanjivani;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
    }

    public void login(View v){
        Intent i=new Intent(this,NavigationActivity.class);
        startActivity(i);
        finish();
    }

    public void registerNowText(View v){
        Intent i=new Intent(this,SignUpActivity.class);
        startActivity(i);
        finish();
    }

}
