package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.chaos.view.PinView;
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

    EditText otpEditText;
    TextView number;
    Button registerButton;
    String userPhoneNumber;
    String codeSentToUser;
    ProgressBar PhoneSignUpProgressBar;
    String countryCode = "+91 ";
    private PinView pinView;

    FirebaseFirestore db;
    CollectionReference getUserDetails;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();
        PhoneSignUpProgressBar = findViewById(R.id.phoneSignUpProgressBar);
        number = findViewById(R.id.phone_number);
        number.setText(userPhoneNumber);
        registerButton = findViewById(R.id.registerButton);

        pinView = findViewById(R.id.otpPinWidget);
        sendVerificationCode();

        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeEnteredByUser = pinView.getText().toString().trim();
                verifyUserOTP(codeEnteredByUser);

            }
        });

    }

    private void sendVerificationCode() {
        Intent intent = getIntent();
        userPhoneNumber = intent.getStringExtra("phone_number");
        String phoneNumber = countryCode + userPhoneNumber;
        Toast.makeText(getApplicationContext(), "OTP send to " + phoneNumber, Toast.LENGTH_LONG).show();
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
                otpEditText.setText(autoGetCode);
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
            otpEditText.setError("OTP cannot be null");
            otpEditText.requestFocus();
            return;
        } else if (codeEnteredByUser.length() != 6) {
            otpEditText.setError("Invalid OTP");
            otpEditText.requestFocus();
            return;
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSentToUser, codeEnteredByUser);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
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
                                otpEditText.setError("Incorrect OTP");
                                otpEditText.setText("");
                                otpEditText.requestFocus();
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
                            Log.d("is okay", "i exist");
                            Intent intent = new Intent(VerificationActivity.this, NavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            Log.d("is okay", "im not existing");
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