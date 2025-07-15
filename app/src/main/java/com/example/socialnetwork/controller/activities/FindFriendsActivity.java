package com.example.socialnetwork.controller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.socialnetwork.R;
import com.example.socialnetwork.controller.adapters.UserAdapter;
import com.example.socialnetwork.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;

    private RecyclerView SearchResultList;

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar) ;
        setSupportActionBar (mToolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar ().setDisplayShowHomeEnabled (true);
        getSupportActionBar ().setTitle ("Find Friends");

        SearchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize (true) ;
        SearchResultList.setLayoutManager (new LinearLayoutManager(this) );

        SearchButton = (ImageButton) findViewById(R.id. search_people_friends_button);
        SearchInputText = (EditText) findViewById(R.id. search_box_input);

        recyclerView = findViewById(R.id.search_result_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        loadAllUsers();

        SearchInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchBoxInput = SearchInputText.getText().toString();
                adapter.filter(searchBoxInput);
            }
        });

    }

    private void loadAllUsers() {
        String url = "http://farnazboroumand.ir/api/get_all_users.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray usersArray = jsonResponse.getJSONArray("users");
                            userList.clear(); // لیست رو خالی کن که تکراری نشه

                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject userObj = usersArray.getJSONObject(i);
                                String userId = userObj.getString("user_id");
                                String fullname = userObj.getString("fullname");
                                String username = userObj.getString("username");
                                String status = userObj.getString("status");
                                String profileImage = userObj.getString("profile_image");

                                userList.add(new User(userId, fullname, username, status, profileImage, true));

                            }
                            adapter = new UserAdapter(this, userList);
                            recyclerView.setAdapter(adapter);
                            adapter.updateFullList();
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                    Toast.makeText(this, "Error loading users", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


    private void SearchPeopleAndFriends(String searchBoxInput) {
        adapter.filter(searchBoxInput);
    }
}