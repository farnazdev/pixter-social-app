package com.example.socialnetwork.controller.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.socialnetwork.view.FollowerFragment;
import com.example.socialnetwork.view.FollowingFragment;
import com.example.socialnetwork.view.RequestsFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<String> fragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void addTitle(String title) {
        fragmentTitleList.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RequestsFragment();
            case 1:
                return new FollowerFragment();
            case 2:
                return new FollowingFragment();
            default:
                return new Fragment(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return fragmentTitleList.size();
    }

    public String getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}


