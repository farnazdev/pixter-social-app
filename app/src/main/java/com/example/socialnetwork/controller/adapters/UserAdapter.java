package com.example.socialnetwork.controller.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialnetwork.controller.activities.PersonProfileActivity;
import com.example.socialnetwork.R;
import com.example.socialnetwork.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;
    private List<User> fullList; // برای جستجو

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.fullList = new ArrayList<>(userList); // کپی برای فیلتر
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView fullName, username, status;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.all_users_profile_image);
            fullName = itemView.findViewById(R.id.all_users_profile_full_name);
            username = itemView.findViewById(R.id.all_users_profile_username);
            status = itemView.findViewById(R.id.all_users_status);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.all_users_display_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.fullName.setText(user.getFullname());
        holder.username.setText(user.getUsername());
        holder.status.setText(user.getStatus());


        Glide.with(context)
                .load("http://farnazboroumand.ir/uploads/profiles/" + user.getProfileImage())
                .placeholder(R.drawable.profile)
                .into(holder.profileImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PersonProfileActivity.class);
            intent.putExtra("user_id", user.getUserId()); // ارسال user_id به Activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void filter(String query) {
        userList.clear();
        if (query.isEmpty()) {
            userList.addAll(fullList);
        } else {
            for (User user : fullList) {
                if (user.getUsername().toLowerCase().trim().contains(query.toLowerCase().trim())) {
                    userList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateFullList() {
        fullList.clear();
        fullList.addAll(userList);
    }
}

