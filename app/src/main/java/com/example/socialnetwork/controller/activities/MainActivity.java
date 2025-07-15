package com.example.socialnetwork.controller.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialnetwork.R;
import com.example.socialnetwork.controller.adapters.PostAdapter;
import com.example.socialnetwork.controller.adapters.UserManager;
import com.example.socialnetwork.model.Post;

import com.example.socialnetwork.model.User;
import com.example.socialnetwork.network.ApiRequest;
import com.google.android.material.navigation.NavigationView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private RecyclerView postList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView navProfileImage;
    private TextView navProfileUserName;
    private ImageButton AddNewPostButton;
    ImageButton LikePostButton, CommentPostButton;
    TextView DisplayNoOfLikes;

    RecyclerView recyclerView;
    List<Post> postsList;
    PostAdapter postAdapter;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize (true) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager (this) ;
        linearLayoutManager.setReverseLayout (true) ;
        linearLayoutManager. setStackFromEnd (true) ;
        postList.setLayoutManager (linearLayoutManager) ;

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        navProfileImage = navView.findViewById(R.id.nav_profile_image);
        navProfileUserName = navView.findViewById(R.id.nav_user_full_name);

        recyclerView = findViewById(R.id.all_users_post_list); // آیدی‌اش رو خودت تنظیم کن
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        currentUserId = getSharedPreferences("user_data", MODE_PRIVATE).getString("user_id", null);

        postsList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postsList, currentUserId);
        recyclerView.setAdapter(postAdapter);

        LikePostButton = (ImageButton) findViewById(R.id.like_button) ;
        CommentPostButton = (ImageButton) findViewById(R.id.comment_button);
        DisplayNoOfLikes = (TextView) findViewById(R.id.display_no_of_likes) ;

        if (currentUserId != null) {
            checkUserExistenceAndLoadInfo(currentUserId);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            UserMenuSelector(item);
            return false;
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToPostActivity();
            }
        });

        loadUserInfo(currentUserId);

        SwitchCompat switchDarkMode = findViewById(R.id.switch_dark_mode);
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);

        boolean isDark = prefs.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDark);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        if (currentUserId == null) {
            SendUserToLoginActivity();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        postsList.clear();
        loadPosts();
    }


    private void loadUserInfo(String userId) {
        String url = "http://farnazboroumand.ir/api/get_all_user_information.php";

        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);

        ApiRequest.post(url, params, new ApiRequest.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONObject user = jsonObject.getJSONObject("user");

                        User currentUser = new User(
                                null,
                                user.optString("fullname"),
                                user.optString("username"),
                                user.optString("country"),
                                user.optString("profile_image")
                        );

                        currentUser.setStatus(user.optString("status"));
                        currentUser.setDob(user.optString("dob"));
                        currentUser.setGender(user.optString("gender"));
                        currentUser.setRelationshipStatus(user.optString("relationship_status"));

                        UserManager.setUser(currentUser);


                    } else {
                        Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "API Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPosts() {
    String url = "http://farnazboroumand.ir/api/get_posts.php";
    String userId = currentUserId;

    Map<String, String> params = new HashMap<>();
    params.put("user_id", userId);

    ApiRequest.post(url, params, new ApiRequest.ApiCallback() {
        @Override
        public void onSuccess(String response) {
            try {
                JSONObject json = new JSONObject(response);
                if (json.getBoolean("success")) {
                    JSONArray posts = json.getJSONArray("posts");

                    postsList.clear();

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject obj = posts.getJSONObject(i);
                        Post post = new Post();

                        post.post_id = obj.getString("post_id");
                        post.user_id = obj.getString("user_id");
                        post.image_url = obj.getString("image_url");
                        post.caption = obj.getString("caption");
                        post.created_at = obj.getString("created_at");
                        post.fullname = obj.getString("fullname");
                        post.profile_image = obj.getString("profile_image");
                        post.setLikeCount(obj.getInt("like_count"));
                        post.setLikedByCurrentUser(obj.getBoolean("is_liked"));

                        if (post.profile_image != null && !post.profile_image.isEmpty()) {
                            post.profile_image = "http://farnazboroumand.ir/uploads/profiles/" + post.profile_image;
                        }

                        postsList.add(post);
                    }

                    postAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "No posts found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "JSON Error", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(String error) {
            Toast.makeText(MainActivity.this, "API Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void checkUserExistenceAndLoadInfo(String userId) {
        String url = "http://farnazboroumand.ir/api/get_user.php";
        if (userId.isEmpty()){
            SendUserToLoginActivity();
        }
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);

        ApiRequest.post(url, params, new ApiRequest.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONObject userObject = jsonObject.getJSONObject("user");

                        String fullname = userObject.getString("fullname");
                        String username = userObject.getString("username");
                        String country = userObject.getString("country");
                        String profileImage = userObject.getString("profile_image");

                        if (fullname.isEmpty() || username.isEmpty() || country.isEmpty()) {
                            SendUserToSetupActivity();
                        } else {
                            navProfileUserName.setText(fullname);

                            if (profileImage != null && !profileImage.isEmpty()) {
                                String imageUrl = "http://farnazboroumand.ir/uploads/profiles/" + profileImage;
                                Picasso.get()
                                        .load(imageUrl)
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.profile)
                                        .into(navProfileImage);
                            } else {
                                navProfileImage.setImageResource(R.drawable.profile);
                            }
                        }
                    } else {
                        SendUserToSetupActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing user data.", Toast.LENGTH_SHORT).show();
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                SendUserToSetupActivity();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void UserMenuSelector(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            SendUserToProfileActivity();
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_home) {
            SendUserToMainActivity();
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_friends) {
            SendUserToFriendsActivity();
            Toast.makeText(this, "Friends", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_find_friends) {
            SendUserToFindFriendsActivity();
            Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_post) {
            SendUserToPostActivity();
            Toast.makeText(this, "New Post", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_messages) {
            SendUserToFriendsActivity();
            Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_settings) {
            SendUserToSettingsActivity();
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_logout) {
            LogoutUser();
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        }
    }

    private void SendUserToMainActivity() {
        Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(homeIntent);
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }

    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        addNewPostIntent.putExtra("uid", currentUserId);
        startActivity(addNewPostIntent);

    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void SendUserToFriendsActivity() {
        Intent friendsIntent = new Intent(MainActivity.this, FriendsActivity.class);
        friendsIntent.putExtra("default_tab", 1); // 1 یعنی تب Followers
        startActivity(friendsIntent);

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.putExtra("uid", currentUserId);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void LogoutUser() {
        currentUserId = null;
        SharedPreferences.Editor editor = getSharedPreferences("user_data", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        SendUserToLoginActivity();
    }
}
