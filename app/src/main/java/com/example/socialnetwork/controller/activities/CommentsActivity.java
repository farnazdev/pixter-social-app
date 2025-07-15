package com.example.socialnetwork.controller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.socialnetwork.R;
import com.example.socialnetwork.controller.adapters.CommentsAdapter;
import com.example.socialnetwork.controller.adapters.UserManager;
import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CommentsAdapter adapter;
    List<Comment> commentList;


    private RecyclerView CommentsList;
    private Context context;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    String currentUserId, postID, userId, msg, commentText, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        context = CommentsActivity.this;

        postID = getIntent().getStringExtra("post_id");
        currentUserId = getSharedPreferences("user_data", MODE_PRIVATE).getString("user_id", null);

        User user = UserManager.getUser();

        if (user != null) {
            username = user.getUsername();
        }

        CommentsList = findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        commentList = new ArrayList<>();
        adapter = new CommentsAdapter(commentList);
        CommentsList.setAdapter(adapter);


        CommentInputText = (EditText) findViewById(R.id. comment_input) ;
        PostCommentButton = (ImageButton) findViewById(R.id.post_comment_btn);

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateComment(username);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        commentList.clear();
        loadComments(postID);
    }


    private void ValidateComment (String userName)
    {
        commentText = CommentInputText.getText().toString();
        if (TextUtils.isEmpty(commentText) )
            Toast.makeText (this,"please write text to comment ... ", Toast. LENGTH_SHORT).show();
        else {
            userId = currentUserId;
            SendComment(userId,postID,userName,commentText);
        }
    }

    private void SendComment(String userId,String postID,String username,String msg) {
        String url = "http://farnazboroumand.ir/api/add_comment.php";
        Log.d("DEBUG", "Sending user_id = " + userId);
        Log.d("DEBUG", "Sending post_id = " + postID);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(CommentsActivity.this, "Comment uploaded successfully!", Toast.LENGTH_SHORT).show();
                        CommentInputText.setText("");
                        Comment newComment = new Comment(
                                msg,
                                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()),
                                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()),
                                username
                        );
                        adapter.addComment(newComment);
                        CommentsList.scrollToPosition(0);

//                        loadComments(postID);

                    }
                },
                error -> {
                    Toast.makeText(context, "Error occurred...", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("post_id", postID);
                params.put("username", username);
                params.put("comment", msg);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void loadComments(String postID) {

        commentList.clear(); // خالی کردن لیست قبلی
        adapter.notifyDataSetChanged();

        StringRequest request = new StringRequest(Request.Method.POST, "http://farnazboroumand.ir/api/get_comments.php",
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String comment = obj.getString("comment");
                            String datetime = obj.getString("datetime"); // مثل "2025-05-14 14:32:20"
                            String username = obj.getString("username");

                            String datePart = "", timePart = "";
                            if (datetime != null && datetime.contains(" ")) {
                                String[] parts = datetime.split(" ");
                                datePart = parts[0];
                                timePart = parts[1];
                            }

                            Comment c = new Comment(
                                    comment,
                                    datePart,
                                    timePart,
                                    username
                            );

                            commentList.add(c);

                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "خطا در دریافت کامنت‌ها", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", postID);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);

    }
}