package com.collection.sanjivani;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class UploadPrescriptionActivity extends AppCompatActivity {

    final static int PICK_IMAGE_REQUEST = 1;

    Button mChooseImageButton;
    Button mUploadImageButton;
    TextView mShowUploadsTextView;
    EditText mFileNameEditText;
    ImageView mImageView;
    ProgressBar mProgressBar;

    public Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);

        mChooseImageButton = findViewById(R.id.chooseFileButton);
        mUploadImageButton = findViewById(R.id.uploadButton);
        mShowUploadsTextView = findViewById(R.id.showUploadsTV);
        mFileNameEditText = findViewById(R.id.fileNameEditText);
        mImageView = findViewById(R.id.uploadImageView);
        mProgressBar = findViewById(R.id.uploadImageProgressBar);

        mChooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        mUploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mShowUploadsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
    }
}
