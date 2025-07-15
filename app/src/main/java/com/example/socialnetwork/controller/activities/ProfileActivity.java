package com.example.socialnetwork.controller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.socialnetwork.R;
import com.example.socialnetwork.controller.adapters.UserManager;
import com.example.socialnetwork.model.User;

public class ProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus ,userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar) ;
        setSupportActionBar (mToolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar ().setDisplayShowHomeEnabled (true);
        getSupportActionBar ().setTitle ("Profile");

        userName = (TextView) findViewById(R.id.my_username) ;
        userProfName = (TextView) findViewById(R.id.my_profile_full_name) ;
        userStatus = (TextView) findViewById(R.id.my_profile_status) ;
        userCountry = (TextView) findViewById(R.id.my_country) ;
        userGender = (TextView) findViewById(R.id.my_gender);
        userRelation = (TextView) findViewById(R.id.my_relationship_status) ;
        userDOB = (TextView) findViewById(R.id.my_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.my_profile_pic) ;


        User user = UserManager.getUser();

        if (user != null) {
            userName.setText("@" + user.getUsername());
            userProfName.setText("Fullname : "+user.getFullname());
            userStatus.setText("Status : "+user.getStatus());
            userCountry.setText("Country : "+user.getCountry());
            userGender.setText("Gender : " + user.getGender());
            userRelation.setText("Relationship Status : "+user.getRelationshipStatus());
            userDOB.setText("DOB : "+user.getDob());
            Glide.with(this)
                    .load("http://farnazboroumand.ir/uploads/profiles/" + user.getProfileImage())
                    .placeholder(R.drawable.profile)
                    .into(userProfileImage);
        }

    }
}