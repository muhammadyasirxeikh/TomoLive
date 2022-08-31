package com.zakux.live.activity.callwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.BaseActivity;
import com.zakux.live.databinding.ActivityTestBinding;
import com.zakux.live.oflineModels.VideoCallDataRoot;
import com.zakux.live.retrofit.ApiCalling;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class TestActivity extends AppCompatActivity {


    private String token;
    private VideoCallDataRoot videoCallDataRoot;
    SessionManager sessionManager;
    private RtcEngine mRtcEngine;
    private int seconds = 0;
    private boolean mMuted=false;
    Handler timerHandler = new Handler();
    private ActivityTestBinding binding;

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;

            int p1 = seconds % 60;
            int p2 = seconds / 60;
            int p3 = p2 % 60;
            p2 = p2 / 60;

            String sec="sec";
            String hour="hour";
            String min="min";
            if (p1 < 10) {
                sec = "0" + p1;
            } else {
                sec = String.valueOf(p1);
            }
            if (p2 < 10) {
                hour = "0" + p2;
            } else {
                hour = String.valueOf(p2);
            }
            if (p3 < 10) {
                min = "0" + p3;
            } else {
                min = String.valueOf(p3);
            }
            binding.tvtimer.setText(hour + ":" + min + ":" + sec);
            timerHandler.postDelayed(this, 1000);
        }
    };
            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityTestBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        sessionManager = new SessionManager(this);
        Intent intent = getIntent();

        String onjStr = intent.getStringExtra("datastr");

        videoCallDataRoot = new Gson().fromJson(onjStr, VideoCallDataRoot.class);
        token = videoCallDataRoot.getToken();
        initializeAndJoinChannel();

        binding.btnCall.setOnClickListener(v->{

            endCall();
        });
        binding.btnSpeaker.setOnClickListener(v->{
            openspeaker();
        });
    }

    private void openspeaker() {
        if (mMuted) {
            mMuted=false;
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(false);
        }else {
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
        }
        // Stops/Resumes sending the local audio stream.

        int res = mMuted ? R.drawable.mute_apeaker : R.drawable.unmute_speaker;
        binding.btnSpeaker.setImageResource(res);
    }

    private void endCall() {
        BaseActivity.isHostBusyLocal = true;
        Toast.makeText(this, "Total time " + binding.tvtimer.getText().toString(), Toast.LENGTH_SHORT).show();
        timerHandler.removeCallbacks(timerRunnable);
        finish();

        leaveChannel();



    }
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(() -> {
                timerHandler.postDelayed(timerRunnable, 1000);

            });
        }

        @Override
        public void onConnectionLost() {
            super.onConnectionLost();
            timerHandler.removeCallbacks(timerRunnable);
            endCall();
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            timerHandler.removeCallbacks(timerRunnable);
            finish();
            endCall();



        }
        @Override
        public void onError(int err) {
            super.onError(err);

            BaseActivity.isHostBusyLocal = true;
            endCall();
        }


        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);


                ApiCalling apiCalling = new ApiCalling(TestActivity.this);
                apiCalling.makeFreeHost();

            finish();
        }
    };


    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(),  sessionManager.getSetting().getAgoraId(), mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error");
        }
        mRtcEngine.joinChannel(token, videoCallDataRoot.getChannel(), "", 0);
    }

    protected void onDestroy() {
        super.onDestroy();
        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();
    }
}