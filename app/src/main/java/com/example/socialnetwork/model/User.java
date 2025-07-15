package com.example.socialnetwork.model;

public class User {
    private String email, fullname, username, country, profileImage, status,dob,gender,relationshipStatus;
    private String userId;


    public User(String email, String fullname, String username, String country) {
        this.email = email;
        this.fullname = fullname;
        this.username = username;
        this.country = country;
    }

    public User(String userId,String fullname, String username, String status, String profileImage, boolean isSimpleUser) {
        this.userId = userId;
        this.fullname = fullname;
        this.username = username;
        this.status = status;
        this.profileImage = profileImage;
    }


    public User(String email, String fullname, String username, String country, String profileImage) {
        this.email = email;
        this.fullname = fullname;
        this.username = username;
        this.country = country;
        this.profileImage = profileImage;
    }

    public User(String email, String fullname, String username, String country, String profileImage,
                String status, String dob, String gender, String relationshipStatus) {
        this.email = email;
        this.fullname = fullname;
        this.username = username;
        this.country = country;
        this.profileImage = profileImage;
        this.status = status;
        this.dob = dob;
        this.gender = gender;
        this.relationshipStatus = relationshipStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() { return email; }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
    public String getCountry() { return country; }
    public String getProfileImage() { return profileImage; }
    public String getStatus() { return status; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getRelationshipStatus() { return relationshipStatus; }


    public void setEmail(String email) { this.email = email; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setUsername(String username) { this.username = username; }
    public void setCountry(String country) { this.country = country; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setStatus(String status) { this.status = status; }
    public void setDob(String dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setRelationshipStatus(String relationshipStatus) { this.relationshipStatus = relationshipStatus; }


}

