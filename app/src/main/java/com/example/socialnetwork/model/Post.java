package com.example.socialnetwork.model;

public class Post {
    public String post_id;
    public String user_id;
    public String image_url;
    public String caption;
    public String created_at;
    public String fullname;
    public String profile_image;
    public int likeCount;

    public boolean likedByCurrentUser;

    // Getter و Setter برای post_id
    public String getPostId() {
        return post_id;
    }

    // Getter و Setter برای likeCount
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    // Getter و Setter برای likedByCurrentUser
    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

}
