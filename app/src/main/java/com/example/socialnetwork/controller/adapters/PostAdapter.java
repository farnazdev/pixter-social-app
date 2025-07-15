package com.example.socialnetwork.controller.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.socialnetwork.controller.activities.ClickPostActivity;
import com.example.socialnetwork.controller.activities.CommentsActivity;
import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Post;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> postList;
    private String user_id;

    public PostAdapter(Context context, List<Post> postList, String user_id) {
        this.context = context;
        this.postList = postList;
        this.user_id = user_id;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_posts_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);


        holder.noOfLikes.setText(post.getLikeCount() + " likes");

        if (post.isLikedByCurrentUser()) {
            holder.likeButton.setImageResource(R.drawable.like); // قرمز
        } else {
            holder.likeButton.setImageResource(R.drawable.dislike); // خاکستری
        }




        holder.username.setText(post.fullname);
        holder.caption.setText(post.caption);

        // تاریخ و زمان جدا کنیم
        if (post.created_at != null && post.created_at.contains(" ")) {
            String[] parts = post.created_at.split(" ");
            holder.date.setText(parts[0]);
            holder.time.setText(" - "+parts[1]);
        }

        // لود تصویر پست و پروفایل
        Glide.with(context).load("http://farnazboroumand.ir/uploads/posts/" + post.image_url).into(holder.postImage);
        Glide.with(context).load(post.profile_image).into(holder.profileImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClickPostActivity.class);
            intent.putExtra("post_id", post.post_id);
            context.startActivity(intent);
        });

        holder.likeButton.setOnClickListener(v -> {
            if (!post.isLikedByCurrentUser()) {
                likePost(user_id, post.getPostId(), holder, post);
            } else {
                unlikePost(user_id, post.getPostId(), holder, post);
            }
        });


        holder.commentButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("post_id", post.post_id);
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView username, caption, date, time, noOfLikes;
        ImageView postImage;
        ImageButton likeButton, commentButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.post_profile_image);
            username = itemView.findViewById(R.id.post_user_name);
            caption = itemView.findViewById(R.id.post_description);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            postImage = itemView.findViewById(R.id.post_image);
            noOfLikes = itemView.findViewById(R.id.display_no_of_likes);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);

        }
    }

    private void likePost(String userId, String postId, ViewHolder holder, Post post) {
        String url = "http://farnazboroumand.ir/api/like_post.php";
        Log.d("DEBUG", "Sending user_id = " + userId);
        Log.d("DEBUG", "Sending post_id = " + userId);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        post.setLikedByCurrentUser(true);
                        post.setLikeCount(post.getLikeCount() + 1);
                        holder.likeButton.setImageResource(R.drawable.like);
                        holder.noOfLikes.setText(post.getLikeCount() + " likes");
                    }
                },
                error -> {
                    Toast.makeText(context, "خطا در لایک کردن", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("post_id", postId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void unlikePost(String userId, String postId, ViewHolder holder, Post post) {
        String url = "http://farnazboroumand.ir/api/unlike_post.php";
        Log.d("DEBUG", "Sending user_id = " + userId);
        Log.d("DEBUG", "Sending post_id = " + userId);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        post.setLikedByCurrentUser(false);
                        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                        holder.likeButton.setImageResource(R.drawable.dislike);
                        holder.noOfLikes.setText(post.getLikeCount() + " likes");
                    }
                },
                error -> {
                    Toast.makeText(context, "خطا در آنلایک کردن", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("post_id", postId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }


}

