package com.zakux.live.activity.callwork;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zakux.live.BuildConfig;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.BaseActivity;
import com.zakux.live.databinding.ActivityCallScreenBinding;
import com.zakux.live.oflineModels.CallAnswerRoot;
import com.zakux.live.oflineModels.VideoCallDataRoot;

import java.io.IOException;
import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;

public class CallScreenActivity extends BaseActivity {
    private static final String TAG = "callscr";
    ActivityCallScreenBinding binding;
    private VideoCallDataRoot videoCallDataRoot;
    private Socket socket;
    private Vibrator v;
    String call_type;

    private MediaPlayer player2;
    private boolean iscalled = false;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            binding.btnDecline.performClick();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        isActOpened = false;
        Intent intent = getIntent();
        BaseActivity.isCallIncoming = true;
        if (intent != null) {
            String onjStr = intent.getStringExtra("datastr");
            call_type=intent.getStringExtra("call_type");
            Log.d(TAG, "onCreate: intent " + onjStr);
            if (!onjStr.equals("")) {
                videoCallDataRoot = new Gson().fromJson(onjStr, VideoCallDataRoot.class);

                handler.postDelayed(runnable, 5000);
                IO.Options options = IO.Options.builder()
                        // IO factory options
                        .setForceNew(false)
                        .setMultiplex(true)

                        // low-level engine options
                        .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                        .setUpgrade(true)
                        .setRememberUpgrade(false)
                        .setPath("/socket.io/")
                        .setQuery("globalRoom=12021")
                        .setExtraHeaders(null)

                        // Manager options
                        .setReconnection(true)
                        .setReconnectionAttempts(Integer.MAX_VALUE)
                        .setReconnectionDelay(1_000)
                        .setReconnectionDelayMax(5_000)
                        .setRandomizationFactor(0.5)
                        .setTimeout(20_000)

                        // Socket options
                        .setAuth(null)
                        .build();

                URI uri = URI.create(BuildConfig.BASE_URL);
                socket = IO.socket(uri, options);
                socket.connect();
                socket.on(Socket.EVENT_CONNECT, args -> {


                });
                initUI();


            }
        }

    }

    private void initUI() {

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] pattern = {100, 200, 400, 500, 100, 200, 300, 400, 500, 100, 200, 300, 400, 500, 500, 200, 100, 500, 500, 500};
            v.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }

        try {

            player2 = new MediaPlayer();
            try {
                AssetFileDescriptor afd2 = getAssets().openFd("call.mp3");
                player2.setDataSource(afd2.getFileDescriptor(), afd2.getStartOffset(), afd2.getLength());
                player2.prepare();

                player2.start();
            } catch (IOException e) {
                Log.d(TAG, "initUI: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "initUI: errrr " + e.getMessage());
        }


        Glide.with(this).load(videoCallDataRoot.getHostImage()).circleCrop().into(binding.imageview);
        binding.tvName.setText(videoCallDataRoot.getHostName());
//kjj
        binding.btnAccept.setOnClickListener(v -> {
            handler.removeCallbacks(runnable);
            iscalled = true;
            fireEvent(true);
        });
        binding.btnDecline.setOnClickListener(v -> {
            handler.removeCallbacks(runnable);
            fireEvent(false);
        });
    }

    private void fireEvent(boolean isAccepted) {

        CallAnswerRoot callAnswerRoot = new CallAnswerRoot();
        callAnswerRoot.setChannel(videoCallDataRoot.getChannel());
        callAnswerRoot.setClientId(videoCallDataRoot.getClientId());
        callAnswerRoot.setHostId(videoCallDataRoot.getHostId());
        callAnswerRoot.setToken(videoCallDataRoot.getToken());
        callAnswerRoot.setRate(String.valueOf(new SessionManager(this).getUser().getRate()));
        if (isAccepted) {

            if (iscalled) {
                iscalled = false;
                callAnswerRoot.setAccepted(true);
                Toast.makeText(this, call_type+"", Toast.LENGTH_SHORT).show();
                if (call_type.equals("audio")){
                    startActivity(new Intent(this, TestActivity.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)).putExtra("isSelf", false));

                }else if (call_type.equals("video")){
                    startActivity(new Intent(this, VideoCallActivity.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)).putExtra("isSelf", false));

                }

            }
        } else {
            callAnswerRoot.setAccepted(false);
        }
        socket.emit("callAnswer", new Gson().toJson(callAnswerRoot));

        if (v != null) {
            v.cancel();
        }
        if (player2 != null) {
            player2.release();
        }
        finish();
    }

    @Override
    protected void onPause() {
        if (v != null) {
            v.cancel();
        }
        if (player2 != null) {
            player2.release();
        }
        BaseActivity.isCallIncoming = false;
        super.onPause();

    }
}