package com.example.socialnetwork.controller.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.socialnetwork.R;
import com.example.socialnetwork.controller.activities.ChatActivity;
import com.example.socialnetwork.controller.activities.FriendsActivity;
import com.example.socialnetwork.controller.activities.PersonProfileActivity;
import com.example.socialnetwork.model.FriendRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    public interface OnRequestActionListener {
        void onRequestHandled();
    }

    private Context context;
    private List<FriendRequest> friendRequestList;
    private String currentUserId;
    private OnRequestActionListener listener;

    public FriendRequestAdapter(Context context, List<FriendRequest> friendRequestList, String currentUserId, OnRequestActionListener listener) {
        this.context = context;
        this.friendRequestList = friendRequestList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_my_friends_display_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequest friendRequest = friendRequestList.get(position);

        holder.fullname.setText(friendRequest.getFullname());
        holder.username.setText("@" + friendRequest.getUsername());

        Glide.with(context)
                .load("http://farnazboroumand.ir/uploads/profiles/" +friendRequest.getProfileImage())
                .placeholder(R.drawable.profile)
                .into(holder.profileImage);


        holder.acceptButton.setOnClickListener(v -> {
            acceptFriendRequest(friendRequest.getSenderId(), position);
        });

        holder.declineButton.setOnClickListener(v -> {
            declineFriendRequest(friendRequest.getSenderId(), position);
        });

        Log.d("AdapterBind", "Binding: " + friendRequest.getFullname());


    }

    private void acceptFriendRequest(String senderId, int position) {
        String url = "http://farnazboroumand.ir/api/accept_request.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(context, "Request accepted.", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onRequestHandled();
                            if (context instanceof FriendsActivity) {
                                ((FriendsActivity) context).reloadAllTabs();
                            }

                        } else {
                            Toast.makeText(context, "خطا در پذیرش درخواست", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "خطا در پردازش داده‌ها", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", senderId);
                params.put("receiver_id", currentUserId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void declineFriendRequest(String senderId, int position) {
        String url = "http://farnazboroumand.ir/api/decline_request.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(context, "Request declined.", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onRequestHandled();
                            if (context instanceof FriendsActivity) {
                                ((FriendsActivity) context).reloadAllTabs();
                            }

                        } else {
                            Toast.makeText(context, "خطا در رد درخواست", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "خطا در پردازش داده‌ها", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", senderId);
                params.put("receiver_id", currentUserId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView fullname, username;
        Button acceptButton, declineButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.all_my_friends_profile_image);
            fullname = itemView.findViewById(R.id.all_my_friends_profile_full_name);
            username = itemView.findViewById(R.id.all_my_friends_profile_username);
            acceptButton = itemView.findViewById(R.id.btn_accept);
            declineButton = itemView.findViewById(R.id.btn_decline);
        }
    }
}
