package com.example.socialnetwork.controller.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.socialnetwork.R;
import com.example.socialnetwork.Utils.FileUtils;
import com.example.socialnetwork.Utils.VolleyMultipartRequest;


import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import com.android.volley.DefaultRetryPolicy;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PostActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private ProgressBar loadingBar;
    private AlertDialog loadingDialog;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String Description;

    String currentUserId;
    private String profileImagePath = "";
    String title = "", msg = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        PostDescription =(EditText) findViewById(R.id.post_description);

        currentUserId = getIntent().getStringExtra("uid");

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        SelectPostImage.setImageURI(imageUri);
                    }
                }
        );

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostInfo();
            }
        });


    }

    private void ValidatePostInfo() {
        Description = PostDescription.getText().toString().trim();

        if(imageUri == null)
        {
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoringImageToDB(Description, imageUri);
        }
    }

    private void StoringImageToDB(String caption, Uri imageUri) {
        title = "Uploading post";
        msg = "Please wait! Your post is uploading...";
        showLoadingDialog(title, msg);
        if (currentUserId == null || imageUri == null) {
            Toast.makeText(this, "User ID or Image is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = FileUtils.getFileName(this, imageUri); // مثلا mypic.jpg

        byte[] imageData;
        try {
            InputStream iStream = getContentResolver().openInputStream(imageUri);
            imageData = getBytes(iStream); // متدی که فایل رو به byte[] تبدیل می‌کنه
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
            return;
        }

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                "http://farnazboroumand.ir/api/add_post.php",
                response -> {
                    Toast.makeText(PostActivity.this, "Post uploaded successfully!", Toast.LENGTH_SHORT).show();
                    hideLoadingDialog();
                    finish();
                },
                error -> {
                    Toast.makeText(PostActivity.this, "Upload error: " + error.toString(), Toast.LENGTH_LONG).show();
                    hideLoadingDialog();
                }
        ) {
            {
                setParam("user_id", currentUserId);
                setParam("caption", caption);
                setParam("image_name", fileName); // نام فایل رو هم جدا می‌فرستیم

                setByteData("post_image", new VolleyMultipartRequest.DataPart(fileName, imageData, "image/jpeg"));
            }
        };

// تنظیم تایم‌اوت و تعداد تلاش‌ها برای درخواست
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000, // تایم‌اوت در میلی‌ثانیه (20 ثانیه)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // تعداد تلاش‌ها
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // ضریب افزایش زمان برای تلاش‌های بعدی
        ));

        Volley.newRequestQueue(this).add(multipartRequest);

    }



    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }





    private void OpenGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
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