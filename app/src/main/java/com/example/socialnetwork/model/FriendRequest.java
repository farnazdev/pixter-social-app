package com.example.socialnetwork.model;


public class FriendRequest {
    private String senderId;
    private String receiverId;
    private String fullname;
    private String username;
    private String profileImage;

    public FriendRequest(String senderId, String receiverId, String fullname, String username, String profileImage) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.fullname = fullname;
        this.username = username;
        this.profileImage = profileImage;
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
    public String getProfileImage() { return profileImage; }
}
