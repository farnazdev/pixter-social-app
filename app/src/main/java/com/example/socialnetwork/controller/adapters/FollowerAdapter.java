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
import com.example.socialnetwork.model.Follower;
import com.example.socialnetwork.model.FriendRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {

    public interface OnRequestActionListener {
        void onRequestHandled();
    }

    private Context context;
    private List<Follower> followerList;
    private String currentUserId;
    private OnRequestActionListener listener;

    public FollowerAdapter(Context context, List<Follower> followerList, String currentUserId, OnRequestActionListener listener) {
        this.context = context;
        this.followerList = followerList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_display_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Follower follower = followerList.get(position);

        holder.fullname.setText(follower.getFullname());
        holder.username.setText("@" + follower.getUsername());

        Glide.with(context)
                .load("http://farnazboroumand.ir/uploads/profiles/" + follower.getProfileImage())
                .placeholder(R.drawable.profile)
                .into(holder.profileImage);

        holder.funcButton.setText("Remove");

        holder.funcButton.setOnClickListener(view -> {
            removeFollower(follower.getFollowerId(), position);
        });

        Log.d("AdapterBind", "Binding: " + follower.getFullname());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options [] = new CharSequence []{
                        " Profile",
                        "Send Message"
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select Option");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            Intent intent = new Intent(context, PersonProfileActivity.class);
                            intent.putExtra("user_id", follower.getFollowerId()); // ارسال user_id به Activity
                            context.startActivity(intent);
                        }

                        if (i == 1) {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("user_id", follower.getFollowerId()); // ارسال user_id به Activity
                            context.startActivity(intent);
                        }

                    }
                });
                builder.show();
            }
        });

    }

    private void removeFollower(String followerId, int position) {
        String url = "http://farnazboroumand.ir/api/remove_friend.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(context, "Follower removed", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onRequestHandled();
                            if (context instanceof FriendsActivity) {
                                ((FriendsActivity) context).reloadAllTabs();
                            }

                        } else {
                            Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", currentUserId);     // کسی که لاگین کرده
                params.put("follower_id", followerId);       // کسی که می‌خوای حذفش کنی
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView fullname, username;
        Button funcButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.friend_profile_image);
            fullname = itemView.findViewById(R.id.friend_profile_full_name);
            username = itemView.findViewById(R.id.friend_profile_username);
            funcButton = itemView.findViewById(R.id.btn_func);
            funcButton.setText("Remove");
        }
    }
}

