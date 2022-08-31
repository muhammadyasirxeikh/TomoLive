package com.zakux.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zakux.live.BuildConfig;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.callwork.CallRequestActivity;
import com.zakux.live.databinding.ActivitySearchNewFriendsDoneBinding;
import com.zakux.live.models.DataItem;
import com.zakux.live.models.Thumb;
import com.zakux.live.oflineModels.VideoCallDataRoot;
import com.zakux.live.token.RtcTokenBuilderSample;

import io.socket.client.Socket;


public class SearchNewFriendsDoneActivity extends BaseActivity {

    ActivitySearchNewFriendsDoneBinding binding;
    DataItem thumb;
    VideoCallDataRoot videoCallDataRoot = null;
    private SessionManager sessionManager;
    private Socket globalSoket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_new_friends_done);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        videoCallDataRoot = new VideoCallDataRoot();
        sessionManager = new SessionManager(SearchNewFriendsDoneActivity.this);

        globalSoket = getGlobalSoket();
        Intent intent = getIntent();
        if (intent != null) {
            String onjStr = intent.getStringExtra("data");
            Log.d("TAG", "onCreate: intent " + onjStr);
            if (onjStr != null && !onjStr.equals("")) {
                thumb = new Gson().fromJson(onjStr, DataItem.class);
                initmain();
            }
        }


    }

    private void initmain() {
        String img;
//        if (thumb.isFake()) {
//            img = thumb.getImage();
//        } else {
            img = BuildConfig.BASE_URL + thumb.getImage();
//        }
        Glide.with(this).load(img).into(binding.hostProfileImage);
        Glide.with(this).load(thumb.getImage()).circleCrop().into(binding.hostRequestImage);
        binding.hostName.setText(thumb.getName());
        binding.hostCountry.setText(thumb.getCountry());

        binding.tvAccept.setOnClickListener(v -> {



            String chenal = sessionManager.getUser().getId();
            String clientId = thumb.getId();
            try {
                String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), chenal);

                VideoCallDataRoot videoCallDataRoot = new VideoCallDataRoot();
                videoCallDataRoot.setHostName(sessionManager.getUser().getName());
                videoCallDataRoot.setChannel(chenal);
                videoCallDataRoot.setClientId(clientId);
                videoCallDataRoot.setHostId(chenal);
                videoCallDataRoot.setToken(tkn);
                videoCallDataRoot.setClientImage(thumb.getImage());
                videoCallDataRoot.setHostImage(sessionManager.getUser().getImage());
                videoCallDataRoot.setClientName(thumb.getName());
                globalSoket.emit("call", new Gson().toJson(videoCallDataRoot));

                startActivity(new Intent(SearchNewFriendsDoneActivity.this, CallRequestActivity.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)));

            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        binding.tvSkip.setOnClickListener(v -> startActivity(new Intent(SearchNewFriendsDoneActivity.this, SearchForNewFriendsActivity.class)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SearchNewFriendsDoneActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }


}