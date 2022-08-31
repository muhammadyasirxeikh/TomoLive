package com.zakux.live;

import android.app.Application;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.zakux.live.activity.BaseActivity;
import com.zakux.live.models.RestResponse;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApp extends Application {



    ImageView liveview;

//    @Override
//    public void onTrimMemory(int level) {
//        super.onTrimMemory(level);
//        Log.d("TAG", "onTrimMemory: " + level);
//        if (level <= 25) {
//            SessionManager sessionManager = new SessionManager(getApplicationContext());
//            if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("_id", sessionManager.getUser().getId());
//                Call<RestResponse> call = RetrofitBuilder.create().offlineHost(Const.DEVKEY, jsonObject);
//                call.enqueue(new Callback<RestResponse>() {
//                    @Override
//                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
//                        if (response.code() == 200 && response.body().isStatus()) {
//                            BaseActivity.HOST_ONLINE = false;
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<RestResponse> call, Throwable t) {
////ll
//                    }
//                });
//            }
//        }
//
//
//    }

}
