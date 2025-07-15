package com.example.socialnetwork.controller.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.socialnetwork.view.FollowerFragment;
import com.example.socialnetwork.view.FollowingFragment;
import com.example.socialnetwork.R;
import com.example.socialnetwork.view.RequestsFragment;
import com.example.socialnetwork.controller.adapters.FriendRequestAdapter;
import com.example.socialnetwork.controller.adapters.ViewPagerAdapter;
import com.example.socialnetwork.model.FriendRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private List<FriendRequest> requestList;
    private FriendRequestAdapter adapter;
    private String currentUserId;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mToolbar = (Toolbar) findViewById(R.id.friends_toolbar) ;
        setSupportActionBar (mToolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar ().setDisplayShowHomeEnabled (true);
        getSupportActionBar ().setTitle ("Friends");

        TabLayout tabLayout = findViewById(R.id.friends_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.friends_view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addTitle("Requests");
        adapter.addTitle("Followers");
        adapter.addTitle("Followings");


        viewPager.setAdapter(adapter);
        int defaultTabIndex = getIntent().getIntExtra("default_tab", 0); // پیش‌فرض 0 = Requests
        viewPager.setCurrentItem(defaultTabIndex, false);

        viewPager.setOffscreenPageLimit(3);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();
    }

    public void reloadAllTabs() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof RequestsFragment) {
                ((RequestsFragment) fragment).reload();
            } else if (fragment instanceof FollowerFragment) {
                ((FollowerFragment) fragment).reload();
            } else if (fragment instanceof FollowingFragment) {
                ((FollowingFragment) fragment).reload();
            }
        }
    }
}
