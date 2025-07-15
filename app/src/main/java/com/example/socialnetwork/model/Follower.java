package com.example.socialnetwork.model;

public class Follower {
    private String followerId;
    private String followingId;
    private String fullname;
    private String username;
    private String profileImage;

    public Follower(String followerId, String followingId, String fullname, String username, String profileImage) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.fullname = fullname;
        this.username = username;
        this.profileImage = profileImage;
    }

    public String getFollowerId() { return followerId; }
    public String getFollowingId() { return followingId; }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
    public String getProfileImage() { return profileImage; }
}
