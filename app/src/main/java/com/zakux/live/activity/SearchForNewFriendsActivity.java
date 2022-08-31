package com.zakux.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zakux.live.LivexUtils;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ActivitySearchForNewFriendsBinding;
import com.zakux.live.models.DataItem;
import com.zakux.live.models.GirlThumbListRoot;
import com.zakux.live.models.NewUserModel;
import com.zakux.live.models.Thumb;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchForNewFriendsActivity extends BaseActivity {

    ActivitySearchForNewFriendsBinding binding;
    Animation zoomin;

    Handler handler;
    Runnable runnable;
    SessionManager sessionManager;
    private String countryId = "GLOBAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_for_new_friends);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sessionManager = new SessionManager(SearchForNewFriendsActivity.this);

        binding.back.setOnClickListener(v -> {


            Intent i = new Intent(SearchForNewFriendsActivity.this, MainActivity.class);
            startActivity(i);
            handler.removeCallbacks(runnable);
        });

        initmain();
    }

    private void initmain() {
        Glide.with(this.getApplicationContext())
                .load(R.drawable.ic_match_beauty_light)
                .circleCrop()
                .into(binding.ivUser);

        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        Animation animZoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin);
        binding.ivUser.startAnimation(animZoomin);


        handler = new Handler();
        runnable = () -> {
            try {
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("_id", sessionManager.getUser().getId());



                Call<NewUserModel> call = RetrofitBuilder.create().getOnlineuser(Const.DEVKEY, jsonObject);

                call.enqueue(new Callback<NewUserModel>() {
                    @Override
                    public void onResponse(Call<NewUserModel> call, Response<NewUserModel> response) {

                        Log.d("TAG", "onResponseuser: "+response.body().getMessage());
                        if (response.code() == 200 && response.body().isStatus()) {

                            if (!response.body().getData().isEmpty()) {
                                Random random=new Random();
                                int a=random.nextInt(response.body().getData().size());
                                DataItem thumb = response.body().getData().get(a);
                                Intent i = new Intent(SearchForNewFriendsActivity.this, SearchNewFriendsDoneActivity.class);
                                i.putExtra("data", new Gson().toJson(thumb));
                                startActivity(i);
                            } else {
//
//                                List<Thumb> listdata = LivexUtils.setFakeData();
//                                Collections.shuffle(listdata);
//                                Thumb thumb = listdata.get(0);
//                                Intent i = new Intent(SearchForNewFriendsActivity.this, SearchNewFriendsDoneActivity.class);
//                                i.putExtra("data", new Gson().toJson(thumb));
//                                startActivity(i);

                                  Toast.makeText(SearchForNewFriendsActivity.this, "No One Is Online", Toast.LENGTH_SHORT).show();
                                   onBackPressed();
                            }
                        }
                        handler.removeCallbacks(runnable);
                    }

                    @Override
                    public void onFailure(Call<NewUserModel> call, Throwable t) {
                        handler.removeCallbacks(runnable);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        handler.postDelayed(runnable, 4000);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SearchForNewFriendsActivity.this, MainActivity.class);
        startActivity(i);
        handler.removeCallbacks(runnable);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}