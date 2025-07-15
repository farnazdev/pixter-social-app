package com.example.socialnetwork.view;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.socialnetwork.R;
import com.example.socialnetwork.controller.adapters.FollowingAdapter;
import com.example.socialnetwork.model.Following;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private FollowingAdapter adapter;
    private List<Following> followingList;
    private Context context;
    private String currentUserId;
    private Toolbar mToolbar;

    public FollowingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingFragment newInstance(String param1, String param2) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        context = getContext();
        recyclerView = view.findViewById(R.id.recycler_followings);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        followingList = new ArrayList<>();

        currentUserId = context.getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("user_id", null);

        loadFollowings();
        adapter = new FollowingAdapter(context, followingList, currentUserId, () -> {
            loadFollowings();
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void reload() {

        loadFollowings();
    }


    private void loadFollowings() {
        String url = "http://farnazboroumand.ir/api/get_followings.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("FriendRequests", "Response from server: " + response);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray requests = json.getJSONArray("requests"); // اصلاح شده
                            followingList.clear();
                            for (int i = 0; i < requests.length(); i++) {
                                JSONObject obj = requests.getJSONObject(i);
                                Following model = new Following(
                                        obj.getString("follower_id"),
                                        obj.getString("following_id"),
                                        obj.getString("fullname"),
                                        obj.getString("username"),
                                        obj.getString("profile_image")
                                );
                                Log.d("Model", "Model: " + model.getFullname() + model.getUsername());
                                followingList.add(model);

                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No requests found!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error processing data...", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Connection Error...", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", currentUserId);
                return map;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request); // اگر توی Fragment هستی
    }
}