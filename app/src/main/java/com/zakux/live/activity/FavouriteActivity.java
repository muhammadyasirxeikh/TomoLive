package com.zakux.live.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.adaptor.AdapterVideos;
import com.zakux.live.databinding.ActivityFavouriteBinding;
import com.zakux.live.models.GirlThumbListRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteActivity extends BaseActivity {
    private static final String TAG = "favact";
    ActivityFavouriteBinding binding;
    SessionManager sessionManager;
    private String userId;
    private AdapterVideos adapterVideos = new AdapterVideos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favourite);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();

        MainActivity.isHostLive = false;
        getData();


    }

    private void getData() {
        binding.lyt404.setVisibility(View.GONE);
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();
        Call<GirlThumbListRoot> call = RetrofitBuilder.create().getFaviourites(Const.DEVKEY, userId);
        call.enqueue(new Callback<GirlThumbListRoot>() {
            @Override
            public void onResponse(Call<GirlThumbListRoot> call, Response<GirlThumbListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        adapterVideos.addData(response.body().getData());
                        binding.rvvideos.setAdapter(adapterVideos);

                    } else {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GirlThumbListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                binding.lyt404.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}