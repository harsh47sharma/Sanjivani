package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadPrescriptionActivity extends AppCompatActivity {

    final static int PICK_IMAGE_REQUEST = 1;

    Button mChooseImageButton;
    Button mUploadImageButton;
    ImageView mImageView;
    ProgressBar mProgressBar;

    public Uri mImageUri;

    StorageReference mStorageReference;
    FirebaseFirestore db;
    CollectionReference uploadsCollection;
    
    String userID;

    String userName, userPhoneNumber, userAddress, userCity, userState, userPinCode, finAddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);

        mChooseImageButton = findViewById(R.id.chooseFileButton);
        mUploadImageButton = findViewById(R.id.uploadButton);
        mImageView = findViewById(R.id.uploadImageView);
        mProgressBar = findViewById(R.id.uploadImageProgressBar);
        userID = FirebaseAuth.getInstance().getUid();

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
        db = FirebaseFirestore.getInstance();
        uploadsCollection = db.collection("users").document(userID).collection("userOrders");

        mChooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        mUploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadPrescriptionActivity.this);
                builder.setMessage("confirm send your Prescription to us? We will update your cart shortly.").
                        setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                uploadImage();
                                mProgressBar.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("no", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage(){
        if(mImageUri != null){
            final StorageReference storageReference = mStorageReference
                    .child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            storageReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(UploadPrescriptionActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Uploads uploads = new Uploads(url);
                            createOrderForPrescription(uploads);
                        }
                    });

                    mProgressBar.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(UploadPrescriptionActivity.this, NavigationActivity.class);
                    startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadPrescriptionActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(UploadPrescriptionActivity.this, "no file selected", Toast.LENGTH_LONG).show();
        }
    }

    private void createOrderForPrescription(final Uploads uploads){
        final PlaceOrderActivity poa = new PlaceOrderActivity();
        final String orderid = "orderid" + poa.getAlphaNumericString() + poa.getDate();

        db.collection("users").document(userID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            userName = documentSnapshot.getString("UserName");
                            userPhoneNumber = documentSnapshot.getString("UserPhoneNumber");
                            userAddress = documentSnapshot.getString("UserAddress");
                            userCity = documentSnapshot.getString("UserCity");
                            userState = documentSnapshot.getString("UserState");
                            userPinCode = documentSnapshot.getString("UserPinCode");
                            finAddress = userAddress + ", " + userCity + ", " + userState + ", " + userPinCode;

                            Map<String, Object> addItemToCartObject = new HashMap<>();
                            addItemToCartObject.put("orderId", orderid);
                            addItemToCartObject.put("orderStatus", "Order Placed");
                            addItemToCartObject.put("orderDate", poa.getDate());
                            addItemToCartObject.put("orderTime", poa.getTime());
                            addItemToCartObject.put("userName", userName);
                            addItemToCartObject.put("userPhoneNumber", userPhoneNumber);
                            addItemToCartObject.put("userAddress", finAddress);
                            addItemToCartObject.put("prescriptionUrl", uploads);
                            db.collection("allOrders").document(orderid).set(addItemToCartObject);
                        }
                        else{
                            Toast.makeText(UploadPrescriptionActivity.this, "User Details not found", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadPrescriptionActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(data != null){
                mImageUri = data.getData();
                Picasso.with(this).load(mImageUri).into(mImageView);
            }

    }
}
