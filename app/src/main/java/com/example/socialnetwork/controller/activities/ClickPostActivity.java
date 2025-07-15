package com.example.socialnetwork.controller.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.socialnetwork.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DeletePostButton, EditPostButton;
    String currentUserId, postID;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post Details");

        PostImage = (ImageView) findViewById(R.id.click_post_image);
        PostDescription = (TextView) findViewById(R.id.click_post_description);
        EditPostButton = (Button) findViewById(R.id.edit_post_button) ;
        DeletePostButton = (Button) findViewById(R.id. delete_post_button) ;



        postID = getIntent().getStringExtra("post_id");


        if (postID != null) {
            fetchPostDetails(postID);
        }

        currentUserId = getSharedPreferences("user_data", MODE_PRIVATE).getString("user_id", null);

        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(postID);
            }
        });

        EditPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditCaptionDialog(postID);
            }
        });

    }

    private void fetchPostDetails(String postId) {
        String url = "http://farnazboroumand.ir/api/get_post_by_id.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONObject post = jsonObject.getJSONObject("post");
                            String postOwnerId = post.getString("user_id");
                            String caption = post.getString("caption");
                            String imageUrl = post.getString("image_url");

                            if (postOwnerId.equals(currentUserId)) {
                                EditPostButton.setVisibility(View.VISIBLE);
                                DeletePostButton.setVisibility(View.VISIBLE);
                            } else {
                                EditPostButton.setVisibility(View.GONE);
                                DeletePostButton.setVisibility(View.GONE);
                            }


                            PostDescription.setText(caption);
                            Glide.with(ClickPostActivity.this)
                                    .load("http://farnazboroumand.ir/uploads/posts/" + imageUrl)
                                    .into(PostImage);
                        } else {
                            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", postId);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void showDeleteConfirmationDialog(String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Post");

        builder.setMessage("Are you sure you want to delete this post?");

        builder.setPositiveButton("Delete", (dialog, which) -> deletePost(postId));

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEGATIVE);

                // تغییر رنگ متن دکمه‌ها
//                positiveButton.setTextColor(getResources().getColor(R.color.textPrimary));
//                negativeButton.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
//                positiveButton.setTextColor(getResources().getColor(R.color.textPrimary));
//                negativeButton.setTextColor(getResources().getColor(R.color.textPrimary));
            }
        });
    }



    private void deletePost(String postId) {
        String url = "http://farnazboroumand.ir/api/delete_post.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(this, "Post deleted", Toast.LENGTH_SHORT).show();

                            // رفتن به MainActivity با رفرش
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(ClickPostActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("refresh_posts", true);
                                startActivity(intent);
                                finish();
                            }, 1000);
                        } else {
                            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", postId);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }



    private void updatePost(String postId, String newCaption) {
        String url = "http://farnazboroumand.ir/api/update_post.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(this, "Post updated", Toast.LENGTH_SHORT).show();

                            PostDescription.setText(newCaption);

                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(ClickPostActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("refresh_posts", true);
                                startActivity(intent);
                                finish();
                            }, 1000);
                        } else {
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", postId);
                params.put("caption", newCaption);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }


    private void showEditCaptionDialog(String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Caption");

        final EditText input = new EditText(this);
        input.setHint("Enter new caption");
        input.setText(PostDescription.getText().toString()); // کپشن فعلی

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(input);
        builder.setView(layout);

        builder.setPositiveButton("Edit", (dialog, which) -> {

            String newCaption = input.getText().toString().trim();
            if (!newCaption.isEmpty()) {
                updatePost(postId, newCaption);
            } else {
                Toast.makeText(this, "Caption can't be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEGATIVE);

                // تغییر رنگ متن دکمه‌ها
//                positiveButton.setTextColor(getResources().getColor(R.color.textPrimary));
//                negativeButton.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
                positiveButton.setTextColor(getResources().getColor(R.color.textPrimary));
                negativeButton.setTextColor(getResources().getColor(R.color.textPrimary));
            }
        });
    }


}