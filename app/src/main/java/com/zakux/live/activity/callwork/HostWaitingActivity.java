package com.zakux.live.activity.callwork;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.zakux.live.BuildConfig;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ActivityHostWaitingBinding;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.User;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.token.RtcTokenBuilderSample;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostWaitingActivity extends AppCompatActivity {
    private static final String TAG = "Hostwaitact";
    ActivityHostWaitingBinding binding;
    private SessionManager sessionManager;
    User user;
    String tkn;
    String chenal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_waiting);


        sessionManager = new SessionManager(this);

        chenal = sessionManager.getUser().getId();
        user = sessionManager.getUser();
        Log.d(TAG, "onCreate: " + user.getImage());
        Glide.with(this).load(BuildConfig.BASE_URL + user.getThumbImage()).centerCrop()
                .placeholder(R.drawable.ic_user_place).error(R.drawable.ic_user_place).into(binding.image);


        makeOnline();
    }

    private void makeOnline() {
        Log.d(TAG, "implimentHostAgora: ");
        try {

            tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), chenal);
            // tkn ="00639c90bea014541e7abb29fb1a8df6e29IACpbgYndZ+S/4zm7RFogMzWl4T2WjCv485xoLEgCGb4jnkastsAAAAAEAC0V+Lr8WQWYAEAAQDxZBZg";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", new SessionManager(this).getUser().getId());
            jsonObject.addProperty("token", tkn);
            jsonObject.addProperty("channel", chenal);
            jsonObject.addProperty("country", sessionManager.getStringValue(Const.Country));
            Call<RestResponse> call = RetrofitBuilder.create().actionOnlineHost(Const.DEVKEY, jsonObject);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    if (response.code() == 200 && response.body().isStatus()) {
                        Log.d(TAG, "onResponse: data send to server");
                        Toast.makeText(HostWaitingActivity.this, "You are Online", Toast.LENGTH_SHORT).show();
                        // Ask for permissions at runtime.
                        // This is just an example set of permissions. Other permissions
                        // may be needed, and please refer to our online documents.

                    }
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "implimentHostAgora: err " + e.getMessage());
            e.printStackTrace();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("_id", sessionManager.getUser().getId());
//        Call<RestResponse> call = RetrofitBuilder.create().offlineHost(Const.DEVKEY, jsonObject);
//        call.enqueue(new Callback<RestResponse>() {
//            @Override
//            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
//                Log.d(TAG, "onResponse: host destoried");
//            }
//
//            @Override
//            public void onFailure(Call<RestResponse> call, Throwable t) {
//// ll
//            }
//        });

//    }
}