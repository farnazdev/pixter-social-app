package com.example.socialnetwork.controller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus ,userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    private Toolbar mToolbar;
    private Button SendFriendReqButton, DeclineFriendReqButton;
    String CURRENT_STATE, receiverUserId, senderUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mToolbar = (Toolbar) findViewById(R.id.person_profile_toolbar) ;
        setSupportActionBar (mToolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar ().setDisplayShowHomeEnabled (true);
        getSupportActionBar ().setTitle ("Profile");

        senderUserID = getSharedPreferences("user_data", MODE_PRIVATE).getString("user_id", null);

        initializeFields();

        receiverUserId = getIntent().getStringExtra("user_id");

        if (receiverUserId != null) {
            loadUserProfile(receiverUserId);
            checkFriendRequestStatus(); // ✅ این خط اضافه شد
        }

        DeclineFriendReqButton.setVisibility(View.INVISIBLE);
        DeclineFriendReqButton.setEnabled(false);

        if (!senderUserID.equals(receiverUserId)) {
            SendFriendReqButton.setVisibility(View.VISIBLE);
            SendFriendReqButton.setEnabled(true);

            SendFriendReqButton.setOnClickListener(v -> {
                SendFriendReqButton.setEnabled(false);
                if (CURRENT_STATE.equals("not_friends")) {
                    SendFriendRequest();
                } else if (CURRENT_STATE.equals("request_sent")) {
                    CancelFriendRequest();
                } else if (CURRENT_STATE.equals("friends")) {
                    unfriend(senderUserID,receiverUserId);
                }
            });
        } else {
            // خودت هستی، دکمه نباشه
            SendFriendReqButton.setVisibility(View.INVISIBLE);
            DeclineFriendReqButton.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeFields() {
        userName = (TextView) findViewById(R.id.person_username) ;
        userProfName = (TextView) findViewById(R.id.person_profile_full_name) ;
        userStatus = (TextView) findViewById(R.id.person_profile_status) ;
        userCountry = (TextView) findViewById(R.id.person_country) ;
        userGender = (TextView) findViewById(R.id.person_gender);
        userRelation = (TextView) findViewById(R.id.person_relationship_status) ;
        userDOB = (TextView) findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic) ;
        SendFriendReqButton = (Button) findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendReqButton = (Button) findViewById(R.id.person_decline_friend_request);

        CURRENT_STATE = "not_friends";


    }

    private void CancelFriendRequest() {
        String url = "http://farnazboroumand.ir/api/cancel_request.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            CURRENT_STATE = "not_friends";
                            SendFriendReqButton.setText("Send Friend Request");
                            SendFriendReqButton.setEnabled(true);
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            SendFriendReqButton.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error cancelling request", Toast.LENGTH_SHORT).show();
                    SendFriendReqButton.setEnabled(true);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", senderUserID);
                params.put("receiver_id", receiverUserId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void unfriend(String senderID, String friendId) {
        String url = "http://farnazboroumand.ir/api/unfriend_people.php";

        SendFriendReqButton.setEnabled(false); // دکمه را موقت غیرفعال کن تا دوبار کلیک نشود

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            CURRENT_STATE = "not_friends";
                            SendFriendReqButton.setText("Send Friend Request");
                        } else {
                            String message = jsonObject.optString("message", "Failed to unfriend");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    } finally {
                        SendFriendReqButton.setEnabled(true);
                    }
                },
                error -> {
                    Toast.makeText(this, "Error unfriending person", Toast.LENGTH_SHORT).show();
                    SendFriendReqButton.setEnabled(true);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", senderID);
                params.put("following_id", friendId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);

    }



    private void checkFriendRequestStatus() {
        String url = "http://farnazboroumand.ir/api/check_request_status.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            String status = obj.getString("status");

                            switch (status) {
                                case "pending":
                                    CURRENT_STATE = "request_sent";
                                    SendFriendReqButton.setText("Cancel Request");
                                    break;
                                case "accepted":
                                    CURRENT_STATE = "friends";
                                    SendFriendReqButton.setText("Unfriend");
                                    break;
                                case "declined":
                                    CURRENT_STATE = "not_friends";
                                    SendFriendReqButton.setText("Send Friend Request");
                                    break;
                                default:
                                    CURRENT_STATE = "not_friends";
                                    SendFriendReqButton.setText("Send Friend Request");
                                    break;
                            }
                        } else {
                            Toast.makeText(this, "خطا در دریافت وضعیت درخواست", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "خطا در پردازش داده‌ها", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", senderUserID);
                params.put("receiver_id", receiverUserId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


    private void SendFriendRequest() {
        String url = "http://farnazboroumand.ir/api/send_request.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            CURRENT_STATE = "request_sent";
                            SendFriendReqButton.setText("Cancel Request");
                            SendFriendReqButton.setEnabled(true);
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            SendFriendReqButton.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show();
                    SendFriendReqButton.setEnabled(true);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", senderUserID);
                params.put("receiver_id", receiverUserId);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void loadUserProfile(String userId) {
        Log.d("user id : ", userId);
        String url = "http://farnazboroumand.ir/api/get_all_user_information.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Log.d("SERVER_RESPONSE", response);

                        JSONObject user = obj.getJSONObject("user");
                        receiverUserId = user.getString("user_id");
                        Log.d("user id ", receiverUserId);
                        userName.setText("@" + user.getString("username"));
                        userProfName.setText(user.getString("fullname"));
                        userStatus.setText(user.getString("status"));
                        userCountry.setText(user.getString("country"));
                        userGender.setText(user.getString("gender"));
                        userRelation.setText(user.getString("relationship_status"));
                        userDOB.setText(user.getString("dob"));

                        Glide.with(this)
                                .load("http://farnazboroumand.ir/uploads/profiles/" + user.getString("profile_image"))
                                .placeholder(R.drawable.profile)
                                .into(userProfileImage);

                        Log.d("SERVER_RESPONSE", response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    Toast.makeText(this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
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