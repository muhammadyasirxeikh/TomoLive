package com.zakux.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.adaptor.FollowAdapter;
import com.zakux.live.databinding.ActivityFollowListBinding;
import com.zakux.live.models.GuestUserRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowListActivity extends BaseActivity {
    private static final String TAG = "followlistact";
    ActivityFollowListBinding binding;
    SessionManager sessionManager;
    private String userId;
    FollowAdapter followAdapter = new FollowAdapter();
    private boolean isLoding = false;
    private int start = 0;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow_list);
        MainActivity.isHostLive = false;
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("title");
            binding.tvtitle.setText(title);
            if (title.equals("Followrs")) {
                getFollowrsList();
            } else {
                getFollowingList();
            }
        }
        binding.rvList.setAdapter(followAdapter);
        binding.rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvList.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvList.getLayoutManager();
                    Log.d("TAG", "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d("TAG", "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d("TAG", "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        start = start + Const.LIMIT;
                        if (title.equals("Followrs")) {
                            getFollowrsList();
                        } else {
                            getFollowingList();
                        }

                    }
                }
            }
        });

    }

    private void getFollowingList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("start", start);
        jsonObject.addProperty("limit", Const.LIMIT);
        Call<GuestUserRoot> call = RetrofitBuilder.create().followingList(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<GuestUserRoot>() {
            @Override
            public void onResponse(Call<GuestUserRoot> call, Response<GuestUserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    followAdapter.addData(response.body().getData());

                }
            }

            @Override
            public void onFailure(Call<GuestUserRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: following err" + t.getMessage());
            }
        });
    }

    private void getFollowrsList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("start", start);
        jsonObject.addProperty("limit", Const.LIMIT);
        Call<GuestUserRoot> call = RetrofitBuilder.create().followrsList(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<GuestUserRoot>() {
            @Override
            public void onResponse(Call<GuestUserRoot> call, Response<GuestUserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    followAdapter.addData(response.body().getData());

                }
            }

            @Override
            public void onFailure(Call<GuestUserRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: followrs err" + t.getMessage());
            }
        });
    }

    public void onClickBack(View view) {
        finish();
    }
}