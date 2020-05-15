package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    TextView userPhoneNumberTextView;
    Button registerButton;
    String userPhoneNumber;
    String codeSentToUser;
    String codeEnteredByUser;
    ProgressBar PhoneSignUpProgressBar;
    String countryCode = "+91 ";
    Pinview pinView;

    FirebaseFirestore db;
    CollectionReference getUserDetails;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();
        PhoneSignUpProgressBar = findViewById(R.id.phoneSignUpProgressBar);
        userPhoneNumberTextView = findViewById(R.id.phone_number);
        registerButton = findViewById(R.id.registerButton);

        pinView = findViewById(R.id.otpPinWidget);
        sendVerificationCode();

        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeEnteredByUser = pinView.getValue().trim();
                verifyUserOTP(codeEnteredByUser);

            }
        });

    }

    private void sendVerificationCode() {
        Intent intent = getIntent();
        userPhoneNumber = intent.getStringExtra("phone_number");
        userPhoneNumberTextView.setText(userPhoneNumber);
        String phoneNumber = countryCode + userPhoneNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String autoGetCode = phoneAuthCredential.getSmsCode();
            if (autoGetCode != null) {
                pinView.setValue(autoGetCode);
                verifyUserOTP(autoGetCode);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSentToUser = s;
        }
    };

    private void verifyUserOTP(String codeEnteredByUser) {
        if (codeEnteredByUser.isEmpty()) {
            Animation example= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_pin_view);
            pinView.startAnimation(example);
            Toast.makeText(VerificationActivity.this, "OTP cannot be null", Toast.LENGTH_LONG).show();
            return;
        } else if (codeEnteredByUser.length() != 6) {
            Animation example= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_pin_view);
            pinView.startAnimation(example);
            Toast.makeText(VerificationActivity.this, "OTP should be of length 6", Toast.LENGTH_LONG).show();
            return;
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSentToUser, codeEnteredByUser);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        final Context context = this;
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            PhoneSignUpProgressBar.setVisibility(View.VISIBLE);
                            doesUserExists();

                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerificationActivity.this, "incorrect OTP", Toast.LENGTH_LONG).show();
                                for (int i = 0;i < pinView.getPinLength();i++) {
                                    pinView.onKey(pinView.getFocusedChild(), KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL));
                                }
                                Animation example= AnimationUtils.loadAnimation(context, R.anim.shake_pin_view);
                                pinView.startAnimation(example);
                            }
                        }
                    }
                });

    }

    public void doesUserExists(){
        db = FirebaseFirestore.getInstance();
        getUserDetails = db.collection("users");

        getUserDetails.whereEqualTo("UserPhoneNumber", userPhoneNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            Intent intent = new Intent(VerificationActivity.this, NavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(VerificationActivity.this, CaptureUserInformation.class);
                            intent.putExtra("user_phone_number", userPhoneNumber);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VerificationActivity.this, "Please try again in sometime", Toast.LENGTH_LONG).show();
                    }
                });
    }
}