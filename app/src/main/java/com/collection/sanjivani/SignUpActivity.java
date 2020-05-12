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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    EditText phoneNumberEditText, otpEditText;
    ProgressBar PhoneSignUpProgressBar;
    FirebaseAuth mAuth;
    Button registerButton;
    String codeSentToUser;
    TextView resendOTP;
    String countryCode = "+91 ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        PhoneSignUpProgressBar = findViewById(R.id.phoneSignUpProgressBar);
        registerButton = findViewById(R.id.registerButton);

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        otpEditText = findViewById(R.id.otpEditText);

        findViewById(R.id.sendOTPButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeEnteredByUser = otpEditText.getText().toString().trim();
                verifyUserOTP(codeEnteredByUser);
            }
        });

    }
    private void sendVerificationCode(){

        String rawPhoneNumber = phoneNumberEditText.getText().toString().trim();
        String phoneNumber = countryCode + rawPhoneNumber;
        if(phoneNumber.isEmpty()){
            phoneNumberEditText.setError("Phone number is required");
            phoneNumberEditText.requestFocus();
            return;
        }
        else if(phoneNumber.length()!=14){
            phoneNumberEditText.setError("Invalid Phone Number");
            phoneNumberEditText.requestFocus();
            return;
        }
        else {
            registerButton.setVisibility(View.VISIBLE);
            PhoneSignUpProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "OTP send to " + phoneNumber, Toast.LENGTH_LONG).show();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String autoGetCode = phoneAuthCredential.getSmsCode();
            if(autoGetCode != null){
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

    private void verifyUserOTP(String codeEnteredByUser){
        if(codeEnteredByUser.isEmpty()){
            otpEditText.setError("OTP cannot be null");
            otpEditText.requestFocus();
            return;
        }
        else if(codeEnteredByUser.length() != 6){
            otpEditText.setError("Invalid OTP");
            otpEditText.requestFocus();
            return;
        }
        else{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSentToUser, codeEnteredByUser);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        final String userPhoneNumber = phoneNumberEditText.getText().toString().trim();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, CaptureUserInformation.class);
                            intent.putExtra("user_phone_number", userPhoneNumber);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(SignUpActivity.this, "incorrect code", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(SignUpActivity.this, NavigatorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }
}
