package com.example.socialnetwork.controller.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.socialnetwork.R;
import com.example.socialnetwork.controller.adapters.MessageAdapter;
import com.example.socialnetwork.controller.adapters.UserManager;
import com.example.socialnetwork.model.Message;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar ChattoolBar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private RecyclerView userMessagesList;
    MessageAdapter adapter;
    List<Message> messageList;

    private String messageReceiverID,messageReceiverName;

    private TextView receiverName;
    private CircleImageView receiverProfileImage;
    private Context context;
    String messageSenderID, messageText, senderUsername, receiverUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = ChatActivity.this;

        messageSenderID = getSharedPreferences("user_data", MODE_PRIVATE).getString("user_id", null);

        messageReceiverID =getIntent().getStringExtra("user_id");
        
        initializeFields();
    }

    private void initializeFields() {
        ChattoolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar (ChattoolBar) ;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
            actionBar.setCustomView(action_bar_view);
            getUserInfo(messageReceiverID);
        }



        userMessagesList = findViewById(R.id.messages_list_users);
        userMessagesList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userMessagesList.setLayoutManager(linearLayoutManager);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(ChatActivity.this, messageList, messageSenderID);
        userMessagesList.setAdapter(adapter);



        receiverName = (TextView) findViewById(R.id.custom_profile_name) ;
        receiverProfileImage = (CircleImageView) findViewById(R.id.custom_profile_image) ;

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id. input_message) ;

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateMessage();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        messageList.clear();
        loadMessages();
    }

    private void loadMessages() {
        messageList.clear(); // خالی کردن لیست قبلی
        adapter.notifyDataSetChanged();

        StringRequest request = new StringRequest(Request.Method.POST, "http://farnazboroumand.ir/api/get_messages.php",
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String sender_id = obj.getString("senderId");
                            String message = obj.getString("message");
                            String datetime = obj.getString("datetime"); // مثل "2025-05-14 14:32:20"

                            String timePart = "";
                            if (datetime != null && datetime.contains(" ")) {
                                timePart = datetime.split(" ")[1];
                            }

                            Message m = new Message(
                                    sender_id,
                                    message,
                                    timePart
                            );

                            messageList.add(m);

                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Error processing comments!", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", messageSenderID);
                params.put("receiver_id", messageReceiverID);
                return params;
            }

        };

        Volley.newRequestQueue(this).add(request);
    }

    private void ValidateMessage()
    {
        messageText = userMessageInput.getText().toString().trim();
        if (TextUtils.isEmpty(messageText) )
            Toast.makeText (this,"please write text to send ... ", Toast. LENGTH_SHORT).show();
        else {
            User user = UserManager.getUser();
            if (user != null) {
                senderUsername = user.getUsername();
            }
            sendMessage(messageText);

        }
    }

    private void sendMessage(String msg) {
        String url = "http://farnazboroumand.ir/api/send_message.php";

        Log.d("DEBUG", "Sending sender_id = " + messageSenderID);
        Log.d("DEBUG", "Sending receiver_id = " + messageReceiverID);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        userMessageInput.setText("");
                        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                        Message newMessage = new Message(
                                messageSenderID,
                                msg,
                                currentTime
                        );

                        adapter.addMessage(newMessage);
                        userMessagesList.scrollToPosition(0);
                        Log.d("SEND_MESSAGE_RESPONSE", response);

                    }
                },
                error -> {
                    Toast.makeText(context, "Error occurred...", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", messageSenderID);
                params.put("sender_username", senderUsername);
                params.put("receiver_id", messageReceiverID);
                params.put("receiver_username", receiverUsername);
                params.put("msg", msg);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }




    private void getUserInfo(String userId) {
        Log.d("user id : ", userId);
        String url = "http://farnazboroumand.ir/api/get_all_user_information.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Log.d("SERVER_RESPONSE", response);

                        JSONObject user = obj.getJSONObject("user");
                        messageReceiverID = user.getString("user_id");
                        Log.d("user id ", messageReceiverID);
                        receiverUsername = user.getString("username");
                        receiverName.setText(user.getString("fullname"));

                        Glide.with(this)
                                .load("http://farnazboroumand.ir/uploads/profiles/" + user.getString("profile_image"))
                                .placeholder(R.drawable.profile)
                                .into(receiverProfileImage);

                        Log.d("SERVER_RESPONSE", response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    Toast.makeText(this, "Connection Error...", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;

            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}