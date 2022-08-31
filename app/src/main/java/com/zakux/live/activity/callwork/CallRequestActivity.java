package com.zakux.live.activity.callwork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zakux.live.BuildConfig;
import com.zakux.live.R;
import com.zakux.live.activity.BaseActivity;
import com.zakux.live.databinding.ActivityCallRequestBinding;
import com.zakux.live.oflineModels.CallAnswerRoot;
import com.zakux.live.oflineModels.VideoCallDataRoot;

import io.socket.client.Socket;

public class CallRequestActivity extends BaseActivity {

    ActivityCallRequestBinding binding;
    private VideoCallDataRoot videoCallDataRoot;
    String call_type;
    private Vibrator v;
    private boolean isCalled = false;

//    Handler handler = new Handler();
//    private boolean isFake;
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (isFake) {
//                Log.d("TAG", "run: fake goto ");
//                startActivity(new Intent(CallRequestActivity.this, FakeCallActivity.class).putExtra("data", new Gson().toJson(videoCallDataRoot)));
//                finish();
//            } else {
//                onBackPressed();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_request);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        if (intent != null) {
            String onjStr = intent.getStringExtra("datastr");
            call_type=intent.getStringExtra("call_type");
//            isFake = intent.getBooleanExtra("isfake", true);
            Log.d("TAG", "onCreate: intent " + onjStr);
            if (!onjStr.equals("")) {
                videoCallDataRoot = new Gson().fromJson(onjStr, VideoCallDataRoot.class);


                initUI();
                isCalled = true;
//                handler.postDelayed(runnable, 5000);
                lisenForEvent();

            }
        }


    }

    private void lisenForEvent() {
        Log.d("TAG", "lisenForEvent: ");
        Socket socket = getGlobalSoket();
        socket.connect();
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("TAG", "lisenForEvent: connected");
            socket.on("callAnswer", args1 -> {

                Log.d("TAG", "lisenForEvent: " + args1[0].toString());
                CallAnswerRoot callAnswerRoot = new Gson().fromJson(args1[0].toString(), CallAnswerRoot.class);
                if (callAnswerRoot != null) {

                    Log.d("TAG", "lisenForEvent: hostid by data" + callAnswerRoot.getHostId());
                    Log.d("TAG", "lisenForEvent: hostid by videocallroot " + videoCallDataRoot.getHostId());
                    Log.d("TAG", "lisenForEvent:  isaccepted" + callAnswerRoot.isAccepted());
                    if (callAnswerRoot.getHostId().equals(videoCallDataRoot.getHostId())) {
//                        handler.removeCallbacks(runnable);
                        if (callAnswerRoot.isAccepted()) {
//                            Toast.makeText(this, call_type+"", Toast.LENGTH_SHORT).show();
                            if (isCalled) {
                                isCalled = false;
                                videoCallDataRoot.setRate(callAnswerRoot.getRate());  // for add onother user rate
                                if (call_type.equals("audio")){

                                    startActivity(new Intent(CallRequestActivity.this, TestActivity.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)).putExtra("isSelf", true));

                                }else if (call_type.equals("video")){

                                    startActivity(new Intent(CallRequestActivity.this, VideoCallActivity.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)).putExtra("isSelf", true));

                                }

                            }

                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(CallRequestActivity.this, "Declined ", Toast.LENGTH_SHORT).show();
                                socket.close();

                            });
                        }

                        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                        finish();
                    } else {
                        Log.d("TAG", "lisenForEvent: lochoo 6");
                    }
                } else {
                    Log.d("TAG", "lisenForEvent: obj is null");
                }
            });
        });


        binding.btnDecline.setOnClickListener(v1 -> {
//            handler.removeCallbacks(runnable);
            if (socket != null) {
                CallAnswerRoot callAnswerRoot = new CallAnswerRoot();
                callAnswerRoot.setChannel(videoCallDataRoot.getChannel());
                callAnswerRoot.setClientId(videoCallDataRoot.getClientId());
                callAnswerRoot.setHostId(videoCallDataRoot.getHostId());
                callAnswerRoot.setToken(videoCallDataRoot.getToken());

                callAnswerRoot.setAccepted(false);
                socket.emit("callAnswer", new Gson().toJson(callAnswerRoot));
                //    socket.close();
            } else {
                socket.disconnect();
            }
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (v != null) {
            v.cancel();
        }

    }

    @Override
    protected void onPause() {

        if (v != null) {
            v.cancel();
        }
        super.onPause();
    }

    private void initUI() {
        String img;
//        if (isFake) {
//            img = videoCallDataRoot.getClientImage();
//        } else {
            img = BuildConfig.BASE_URL + videoCallDataRoot.getClientImage();
//        }
        Glide.with(this).load(videoCallDataRoot.getHostImage()).circleCrop().into(binding.image1);
        Glide.with(this).load(img).circleCrop().into(binding.image2);
        binding.tvName.setText("Waiting for " + videoCallDataRoot.getClientName() + "'s Reply ....");

    }
}