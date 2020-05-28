package com.collection.sanjivani;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchDrugActivity extends AppCompatActivity {

    EditText searchBoxEditText;
    RecyclerView recyclerView;
    CollectionReference drugCollectionReference;
    FirebaseFirestore db;
    ConstraintLayout mUploadPrescriptionConstraintLayout, mSearchDrugConstraintLayout;

    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;

    Button mViewAllResultsButton;

    List<MedInfo> medInfoArrayList;

    Uri imageUri;

    SearchDrugAdapter searchDrugAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_drug);

        searchBoxEditText = findViewById(R.id.searchBoxEditText);
        recyclerView = findViewById(R.id.searchActivityRecyclerView);
        mUploadPrescriptionConstraintLayout = findViewById(R.id.uploadPrescriptionConstraintLayout);
        mViewAllResultsButton = findViewById(R.id.viewAllResultsButton);
        mSearchDrugConstraintLayout = findViewById(R.id.searchDrugConstraintLayout);

        mSearchDrugConstraintLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);

        findViewById(R.id.appBarSearchBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchDrugActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.appBarSearchCartImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchDrugActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        drugCollectionReference = db.collection("drugInfoDB");

        medInfoArrayList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchDrugActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        searchBoxEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    setAdapter(editable.toString());
                }
            }
        });

        searchBoxEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchBoxEditText.getRight() - searchBoxEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        getSpeechInput();
                        return false;
                    }
                }
                return false;
            }
        });

        findViewById(R.id.getTextfromImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pickImage();
                } catch (Exception e) {
                    Toast.makeText(SearchDrugActivity.this, "Something went wrong plese try again", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
    }

    private void getSpeechInput() {

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (mSpeechRecognizerIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(mSpeechRecognizerIntent, 10);
        } else {
            Toast.makeText(SearchDrugActivity.this, "Your device doesn't support speech recognition", Toast.LENGTH_LONG).show();
        }
    }

    private void pickImage() {
        CropImage.activity().start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (data != null) {
                    Log.d("text rec", "im in");
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchBoxEditText.setText(matches.get(0));
                    break;
                } else {
                    Toast.makeText(SearchDrugActivity.this, "Something went wrong Please try again", Toast.LENGTH_LONG).show();
                }
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (data != null) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);

                    imageUri = result.getUri();
                    try {
                        detectTextFromImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SearchDrugActivity.this, "Something went wrong Please try again", Toast.LENGTH_LONG).show();
                }
        }

    }

    private void detectTextFromImage() throws IOException {
        final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromFilePath(getApplicationContext(), imageUri);
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                    String blockText = block.getText();
                    searchBoxEditText.setText(blockText.toLowerCase());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SearchDrugActivity.this, "SomeThing went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAdapter(final String stringSearchedByUser) {


        drugCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        medInfoArrayList.clear();
                        recyclerView.removeAllViews();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            MedInfo medInfo = documentSnapshot.toObject(MedInfo.class);

                            if (medInfo.getMedName().contains(stringSearchedByUser.toLowerCase())) {
                                medInfoArrayList.add(medInfo);
                                mUploadPrescriptionConstraintLayout.setVisibility(View.INVISIBLE);
                                mViewAllResultsButton.setVisibility(View.VISIBLE);
                            }
                        }

                        searchDrugAdapter = new SearchDrugAdapter(SearchDrugActivity.this, medInfoArrayList);
                        recyclerView.setAdapter(searchDrugAdapter);

                        searchDrugAdapter.setOnMedClickListener(new SearchDrugAdapter.OnMedClickListener() {
                            @Override
                            public void onMedClick(int position) {
                                Intent intent = new Intent(SearchDrugActivity.this, DrugInformationActivity.class);
                                intent.putExtra("items_object", medInfoArrayList.get(position));
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
    }

    public void onBackPressed() {
        Intent intent = new Intent(SearchDrugActivity.this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package: " + getPackageName()));
                startActivity(intent);
            }
        }
    }
}
