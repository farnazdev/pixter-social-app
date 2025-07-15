package com.example.socialnetwork.controller.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.socialnetwork.R;
import com.example.socialnetwork.network.ApiRequest;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName, userProfName, userStatus,userCountry, userGender, userRelation, userDOB;
    private Button UpdateAccountSettingsButton;
    private CircleImageView userProfImage;
    private AlertDialog loadingDialog;
    private String profileImagePath = "";
    private String userId="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        userProfName = (EditText) findViewById(R.id.settings_profile_full_name);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userCountry = (EditText) findViewById(R.id.settings_country);
        userGender = (EditText) findViewById(R.id.settings_gender);
        userRelation = (EditText) findViewById(R.id.settings_relationship_status);
        userDOB = (EditText) findViewById(R.id.settings_dob);
        userProfImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        UpdateAccountSettingsButton = (Button) findViewById(R.id.update_account_settings_buttons);

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = prefs.getString("user_id", null);


        if (userId != null) {
            loadUserInfo(userId);
        }
        UpdateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInfo();
            }
        });

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    private ActivityResultLauncher<Intent> cropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri resultUri = UCrop.getOutput(result.getData());
                    if (resultUri != null) {
                        profileImagePath = resultUri.getPath(); // ذخیره برای آپلود
                        Glide.with(this)
                                .load(resultUri)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(userProfImage);
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

                    String fileName = "cropped_" + UUID.randomUUID().toString() + ".jpg";
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), fileName));

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


    private void validateInfo() {
        String username = userName.getText().toString();
        String profilename = userProfName.getText().toString();
        String status = userStatus.getText().toString() ;
        String dob = userDOB.getText().toString();
        String country = userCountry.getText().toString();
        String gender = userGender.getText ().toString();
        String relation = userRelation.getText().toString();

        if (TextUtils.isEmpty(username) )
            Toast.makeText ( this, "Please write your username...", Toast. LENGTH_SHORT) . show () ;

        else if (TextUtils. isEmpty (profilename) ){
            Toast.makeText ( this, "Please write your full name...", Toast. LENGTH_SHORT) . show () ;
        }
        else if (TextUtils. isEmpty (country) ){
            Toast.makeText ( this, "Please write your country...", Toast. LENGTH_SHORT) . show () ;
        }
        else if (TextUtils. isEmpty (status) ){
            Toast.makeText ( this, "Please write your status...", Toast. LENGTH_SHORT) . show () ;
        }
        else if (TextUtils. isEmpty (dob) ){
            Toast.makeText ( this, "Please write your date of birth...", Toast. LENGTH_SHORT) . show () ;
        }
        else if (TextUtils. isEmpty (gender) ){
            Toast.makeText ( this, "Please write your gender...", Toast. LENGTH_SHORT) . show () ;
        }
        else if (TextUtils. isEmpty (relation) ){
            Toast.makeText ( this, "Please write your relationship status...", Toast. LENGTH_SHORT) . show () ;
        }
        else {
            UpdateAccountInfo(username,profilename,country,status,dob,gender,relation);
        }
    }


    private void UpdateAccountInfo(String username, String profilename, String country, String status, String dob, String gender, String relation) {
        showLoadingDialog("Saving Information", "Please wait, while we are saving your information...");

        if (userId == null) return;

        Map<String, String> textParams = new HashMap<>();
        textParams.put("user_id", userId);
        textParams.put("username", username);
        textParams.put("fullname", profilename);
        textParams.put("country", country);
        textParams.put("status", status);
        textParams.put("gender", gender);
        textParams.put("dob", dob);
        textParams.put("relationship", relation);

        Map<String, File> fileParams = null;
        if (profileImagePath != null) {
            File imageFile = new File(profileImagePath);
            if (imageFile.exists()) {
                fileParams = new HashMap<>();
                fileParams.put("profile_image", imageFile);
            }
        }

        ApiRequest.post(
                "http://farnazboroumand.ir/api/setup_user_with_image.php",
                textParams,
                fileParams,
                new ApiRequest.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        hideLoadingDialog();
                        String res = response.trim();
                        if (res.equalsIgnoreCase("success")) {
                            Toast.makeText(SettingsActivity.this, "Your information saved successfully", Toast.LENGTH_LONG).show();
                            SendUserToMainActivity();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Server response: " + res, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        hideLoadingDialog();
                        Toast.makeText(SettingsActivity.this, "Upload error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    private void loadUserInfo(String userId) {
        showLoadingDialog("Loading Info", "Please wait...");
        String url = "http://farnazboroumand.ir/api/get_all_user_information.php";


        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);

        ApiRequest.post(url, params, new ApiRequest.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                hideLoadingDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONObject user = jsonObject.getJSONObject("user");

                        userProfName.setText(setIfNotNone(user.optString("fullname")));
                        userName.setText(setIfNotNone(user.optString("username")));
                        userCountry.setText(setIfNotNone(user.optString("country")));
                        userStatus.setText(setIfNotNone(user.optString("status")));
                        userDOB.setText(setIfNotNone(user.optString("dob")));
                        userGender.setText(setIfNotNone(user.optString("gender")));
                        userRelation.setText(setIfNotNone(user.optString("relationship_status")));

                        String profileImageUrl = user.optString("profile_image");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(SettingsActivity.this)
                                    .load("http://farnazboroumand.ir/uploads/profiles/" + profileImageUrl)
                                    .placeholder(R.drawable.profile)
                                    .into(userProfImage);
                        } else {
                            userProfImage.setImageResource(R.drawable.profile);
                        }
                    } else {
                        Toast.makeText(SettingsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SettingsActivity.this, "Error parsing user info", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(String error) {
                Toast.makeText(SettingsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String setIfNotNone(String value) {
        return (value != null && !value.equals("none")) ? value : "";
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.putExtra("uid", userId);
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