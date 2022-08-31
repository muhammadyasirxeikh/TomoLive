package com.zakux.live;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonObject;
import com.zakux.live.activity.BaseActivity;
import com.zakux.live.models.RestResponse;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBroadCastReciever extends BroadcastReceiver {
    static int countPowerOff = 0;
    private Activity activity = null;

    public MyBroadCastReciever(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//
//            SessionManager sessionManager = new SessionManager(context);
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
//
//            }
//
//
//        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//            Log.e("myBrodcastReciever", "Screen On");
//        }


    }
}
