package com.example.socialnetwork.controller.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.socialnetwork.R;
import com.example.socialnetwork.network.ApiRequest;
import com.yalantis.ucrop.*;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private TextView debug;
    private EditText UserName, FullName, CountryName;
    private Button SaveInformationbuttion;
    private CircleImageView ProfileImage;
    private ProgressBar loadingBar;
    private AlertDialog loadingDialog;

    private String currentUserId;
    private String profileImagePath = "";

    final static int Gallery_Pick = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        currentUserId = getIntent().getStringExtra("uid");


        UserName = findViewById(R.id.setup_username);
        FullName = findViewById(R.id.setup_full_name);
        CountryName = findViewById(R.id.setup_country_name);
        SaveInformationbuttion = findViewById(R.id.setup_information_button);
        ProfileImage = findViewById(R.id.setup_profile_image);

        SaveInformationbuttion.setText("Create Account");

        SaveInformationbuttion.setOnClickListener(view -> SaveAccountSetupInformation());

        ProfileImage.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryLauncher.launch(galleryIntent);
        });

    }

    private ActivityResultLauncher<Intent> cropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri resultUri = UCrop.getOutput(result.getData());
                    if (resultUri != null) {
                        profileImagePath = resultUri.getPath();
                        Glide.with(this)
                                .load(resultUri)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(ProfileImage);
                        hideLoadingDialog();
                    }

                } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                    Toast.makeText(this, "Error Occurred: Cropping Failed", Toast.LENGTH_SHORT).show();
                    hideLoadingDialog();
                }
            }
    );

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

                    UCrop.Options options = new UCrop.Options();
                    options.setShowCropGrid(true);
                    options.setShowCropFrame(true);

                    Intent cropIntent = UCrop.of(imageUri, destinationUri)
                            .withAspectRatio(1, 1)
                            .withOptions(options)
                            .getIntent(getApplicationContext());

                    cropLauncher.launch(cropIntent);
                }
            }
    );


    private void SaveAccountSetupInformation() {
        String username = UserName.getText().toString().trim();
        String fullname = FullName.getText().toString().trim();
        String country = CountryName.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullname) || TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog("Saving Information", "Please wait, while we are saving your information...");

        if (currentUserId == null) return;


        Map<String, String> textParams = new HashMap<>();
        textParams.put("user_id", currentUserId);
        textParams.put("username", username);
        textParams.put("fullname", fullname);
        textParams.put("country", country);
        textParams.put("status", "Hey there, I am using Pixter Social Network, developed by Farnaz Boroumand.");
        textParams.put("gender", "none");
        textParams.put("dob", "none");
        textParams.put("relationship", "none");


        Map<String, File> fileParams = null;
        if (profileImagePath != null) {
            File imageFile = new File(profileImagePath);
            if (imageFile.exists()) {
                fileParams = new HashMap<>();
                fileParams.put("profile_image", imageFile);
            }
        }

        String url = "http://farnazboroumand.ir/api/setup_user_with_image.php";

        ApiRequest.post(url, textParams, fileParams, new ApiRequest.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                hideLoadingDialog();

                String resTrim = response.trim();
                if (resTrim.equalsIgnoreCase("success")) {
                    Toast.makeText(SetupActivity.this, "Account setup completed successfully", Toast.LENGTH_LONG).show();
                    SendUserToMainActivity();
                } else {
                    Toast.makeText(SetupActivity.this, "Server response: " + resTrim, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                hideLoadingDialog();
                Toast.makeText(SetupActivity.this, "Upload error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.putExtra("uid", currentUserId);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void showLoadingDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);

        ProgressBar progressBar = new ProgressBar(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.addView(progressBar);

        builder.setView(layout);
        builder.setCancelable(false);

        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}