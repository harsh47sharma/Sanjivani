package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    EditText phoneNumberEditText;
    FirebaseAuth mAuth;
    String userPhoneNumber;
    TextView resendOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);

        findViewById(R.id.sendOTPButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPhoneNumber = phoneNumberEditText.getText().toString().trim();
                if(userPhoneNumber.isEmpty()){
                    phoneNumberEditText.setError("Phone number is required");
                    phoneNumberEditText.requestFocus();
                    return;
                }
                else if(userPhoneNumber.length()!=10){
                    phoneNumberEditText.setError("Invalid Phone Number");
                    phoneNumberEditText.requestFocus();
                    return;
                }
                else{
                    Intent intent = new Intent(PhoneLoginActivity.this, VerificationActivity.class);
                    intent.putExtra("phone_number", userPhoneNumber);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(FirebaseAuth.getInstance().getUid().equals("UErC46RIzGOhCcU4KjCzcQgn33m2")){
                Intent intent = new Intent(PhoneLoginActivity.this, AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(PhoneLoginActivity.this, NavigationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

    }
}
