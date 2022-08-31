package com.zakux.live.activity.callwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.BaseActivity;
import com.zakux.live.activity.purchase.CoinPlanActivity;
import com.zakux.live.bottomsheet.BottomSheetReport;
import com.zakux.live.databinding.ActivityAudioCallBinding;
import com.zakux.live.databinding.ActivityVideoCallBinding;
import com.zakux.live.models.User;
import com.zakux.live.oflineModels.VideoCallDataRoot;
import com.zakux.live.retrofit.ApiCalling;
import com.zakux.live.retrofit.CoinWork;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class AudioCallActivity extends AppCompatActivity {
    private static final String TAG = AudioCallActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    SessionManager sessionManager;
    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
//    private boolean mMuted;
//    private FrameLayout mLocalContainer;
//    private RelativeLayout mRemoteContainer;
//    private VideoCanvas mLocalVideo;
//    private VideoCanvas mRemoteVideo;

    Handler timerHandler = new Handler();
    ActivityAudioCallBinding binding;
    private int seconds = 0;
    private boolean isSelf;
    private ImageView mCallBtn;

    // Customized logger view
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;
    private String token;
    private VideoCallDataRoot videoCallDataRoot;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            if (isSelf && seconds % 60 == 0) {
                reduseCoin();
            }
            int p1 = seconds % 60;
            int p2 = seconds / 60;
            int p3 = p2 % 60;
            p2 = p2 / 60;

            String sec;
            String hour;
            String min;
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
    private boolean istost = false;
    Handler handler = new Handler();
    Runnable runnable = () -> endCall();
    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
//        /**
//         * Occurs when the local user joins a specified channel.
//         * The channel name assignment is based on channelName specified in the joinChannel method.
//         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
//         *
//         * @param channel Channel name.
//         * @param uid User ID.
//         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
//         */
//        @Override
//        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
//            runOnUiThread(() -> {
//
//                BaseActivity.isHostBusyLocal = true;
//                Log.d(TAG, "run: join chennel success");
//            });
//        }
//
//        /**
//         * Occurs when the first remote video frame is received and decoded.
//         * This callback is triggered in either of the following scenarios:
//         *
//         *     The remote user joins the channel and sends the video stream.
//         *     The remote user stops sending the video stream and re-sends it after 15 seconds. Possible reasons include:
//         *         The remote user leaves channel.
//         *         The remote user drops offline.
//         *         The remote user calls the muteLocalVideoStream method.
//         *         The remote user calls the disableVideo method.
//         *
//         * @param uid User ID of the remote user sending the video streams.
//         * @param width Width (pixels) of the video stream.
//         * @param height Height (pixels) of the video stream.
//         * @param elapsed Time elapsed (ms) from the local user calling the joinChannel method until this callback is triggered.
//         */
//        @Deprecated
//        @Override
//        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
//            runOnUiThread(() -> {
//                handler.removeCallbacks(runnable);
//                Log.d(TAG, "run: first vid ddecode");
////                setupRemoteVideo(uid);
//                binding.animationView.setVisibility(View.GONE);
//                timerHandler.postDelayed(timerRunnable, 1000);
//                reduseCoin();
//
//                if (!isSelf) {
//                    ApiCalling apiCalling = new ApiCalling(AudioCallActivity.this);
//                    apiCalling.makeBusyHost();
//                }
//            });
//        }
//
//        /**
//         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
//         *
//         * There are two reasons for users to become offline:
//         *
//         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
//         *     goodbye message. When this message is received, the SDK determines that the
//         *     user/host leaves the channel.
//         *
//         *     Drop offline: When no data packet of the user or host is received for a certain
//         *     period of time (20 seconds for the communication profile, and more for the live
//         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
//         *     network connection may lead to false detections, so we recommend using the
//         *     Agora RTM SDK for reliable offline detection.
//         *
//         * @param uid ID of the user or host who leaves the channel or goes offline.
//         * @param reason Reason why the user goes offline:
//         *
//         *     USER_OFFLINE_QUIT(0): The user left the current channel.
//         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
//         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
//         */
//        @Override
//        public void onUserOffline(final int uid, int reason) {
//            runOnUiThread(() -> {
//
//                Log.d(TAG, "run: user offline");
//                onRemoteUserLeft(uid);
//            });
//        }
//
//        @Override
//        public void onError(int err) {
//            super.onError(err);
//            Log.d(TAG, "onError: " + err);
//            istost = true;
//            BaseActivity.isHostBusyLocal = true;
//            endCall();
//        }
//
//
//        @Override
//        public void onLeaveChannel(RtcStats stats) {
//            super.onLeaveChannel(stats);
//            Log.d(TAG, "onLeaveChannel: ===================================================================");
//
//            if (!isSelf) {
//                ApiCalling apiCalling = new ApiCalling(AudioCallActivity.this);
//                apiCalling.makeFreeHost();
//            }
//            finish();
//        }
    };
//    private void setupRemoteVideo(int uid) {
//        ViewGroup parent = mRemoteContainer;
//        if (parent.indexOfChild(mLocalVideo.view) > -1) {
//            parent = mLocalContainer;
//        }
//
//        // Only one remote video view is available for this
//        // tutorial. Here we check if there exists a surface
//        // view tagged as this uid.
//        if (mRemoteVideo != null) {
//            return;
//        }
//
//        /*
//          Creates the video renderer view.
//          CreateRendererView returns the SurfaceView type. The operation and layout of the view
//          are managed by the app, and the Agora SDK renders the view provided by the app.
//          The video display view must be created using this method instead of directly
//          calling SurfaceView.
//         */
//        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
//        view.setZOrderMediaOverlay(parent == mLocalContainer);
//        parent.addView(view);
//        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
//        // Initializes the video view of a remote user.
//        mRtcEngine.setupRemoteVideo(mRemoteVideo);
//    }

    private void onRemoteUserLeft(int uid) {
//        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
//            removeFromParent(mRemoteVideo);
//            // Destroys remote view
//            mRemoteVideo = null;
            istost = true;
            endCall();
//        }
    }

    private void reduseCoin() {
        CoinWork coinWork = new CoinWork();
        Log.d(TAG, "reduseCoin: clientid " + videoCallDataRoot.getClientId());
        Log.d(TAG, "reduseCoin: clientname " + videoCallDataRoot.getClientName());
        Log.d(TAG, "reduseCoin: hostid " + videoCallDataRoot.getHostId());
        coinWork.transferCoin(sessionManager.getUser().getId(), videoCallDataRoot.getClientId(), String.valueOf(sessionManager.getSetting().getCallChargeforUser()));  //
        coinWork.setOnCoinWorkLIstner(new CoinWork.OnCoinWorkLIstner() {
            @Override
            public void onSuccess(User user) {
                //ll
            }

            @Override
            public void onFailure() {
//ll
            }

            @Override
            public void onInsufficientBalance() {

                Toast.makeText(AudioCallActivity.this, "Not Enough Balance", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    istost = true;
                    endCall();
                    startActivity(new Intent(AudioCallActivity.this, CoinPlanActivity.class).putExtra("isVip", false));
                }, 1500);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_call);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        if (intent != null) {
            String onjStr = intent.getStringExtra("datastr");
            isSelf = intent.getBooleanExtra("isSelf", false);
            Log.d(TAG, "onCreate: intent " + onjStr);
            if (!onjStr.equals("")) {
                videoCallDataRoot = new Gson().fromJson(onjStr, VideoCallDataRoot.class);
                token = videoCallDataRoot.getToken();
                Log.d(TAG, "onCreate: yessss=== " + token);
                initUI();


                Log.d(TAG, "vcall: clientid " + videoCallDataRoot.getClientId());
                Log.d(TAG, "vcall: clientname " + videoCallDataRoot.getClientName());
                Log.d(TAG, "vcall: hostid " + videoCallDataRoot.getHostId());
                Log.d(TAG, "vcall: hostname " + videoCallDataRoot.getHostName());
                // Ask for permissions at runtime.
                // This is just an example set of permissions. Other permissions
                // may be needed, and please refer to our online documents.
                if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                        checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
                    initEngineAndJoinChannel();
                    handler.postDelayed(runnable, 6000);
                }
            }
        }

    }

    private void initUI() {
//        mLocalContainer = findViewById(R.id.local_video_view_container);
//        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);


        // Sample logs are optional.

    }



    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show());
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
//        setupVideoConfig();
//        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), sessionManager.getSetting().getAgoraId(), mRtcEventHandler);
//            mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
//        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
//        mLocalContainer.addView(view);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
//        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
//        mRtcEngine.setupLocalVideo(mLocalVideo);
    }

    private void joinChannel() {
//        mRtcEngine.setDefaultAudioRoutetoSpeakerphone(false);
//
//        /** Sets the channel profile of the Agora RtcEngine.
//         CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile.
//         Use this profile in one-on-one calls or group calls, where all users can talk freely.
//         CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast
//         channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams;
//         an audience can only receive streams.*/
//        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
//        /**In the demo, the default is to enter as the anchor.*/
//        mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER);
//        // Enable video module
//        mRtcEngine.enableVideo();
//        // Setup video encoding configs
//
//        // 1. Users can only see each other after they join the
//        // same channel successfully using the same app id.
//        // 2. One token is only valid for the channel name that
//        // you use to generate this token.
//        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
//            token = null; // default, no token
//        }
        mRtcEngine.joinChannel(token, videoCallDataRoot.getChannel(), "Extra Optional Data", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {

            endCall();
        }
        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
//        mMuted = !mMuted;
//        // Stops/Resumes sending the local audio stream.
//        mRtcEngine.muteLocalAudioStream(mMuted);
//        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
//        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
    }

    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);
        } else {
            istost = true;
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall);
        }

//        showButtons(!mCallEnd);
    }

    private void startCall() {
//        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        BaseActivity.isHostBusyLocal = true;
        if (istost) {
            istost = false;
            Toast.makeText(this, "Total time " + binding.tvtimer.getText().toString(), Toast.LENGTH_SHORT).show();
        }
        timerHandler.removeCallbacks(timerRunnable);
        leaveChannel();
//        removeFromParent(mLocalVideo);
//        mLocalVideo = null;
//        removeFromParent(mRemoteVideo);
//        mRemoteVideo = null;


    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
//        ViewGroup parent = removeFromParent(canvas);
//        if (parent == mLocalContainer) {
//            if (canvas.view instanceof SurfaceView) {
//                canvas.view.setZOrderMediaOverlay(false);
//            }
//            mRemoteContainer.addView(canvas.view);
//        } else if (parent == mRemoteContainer) {
//            if (canvas.view instanceof SurfaceView) {
//                canvas.view.setZOrderMediaOverlay(true);
//            }
//            mLocalContainer.addView(canvas.view);
//        }
    }

    public void onLocalContainerClick(View view) {
//        switchView(mLocalVideo);
//        switchView(mRemoteVideo);
    }

    public void onClickReport(View view) {


        if (isSelf) {
            Log.d(TAG, "onClickReport: other name " + videoCallDataRoot.getClientName());

            new BottomSheetReport(this, videoCallDataRoot.getClientId(), (BottomSheetReport.OnReportedListner) () -> {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout,
                        (ViewGroup) findViewById(R.id.customtoastlyt));


                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            });

        } else {
            Log.d(TAG, "onClickReport: other name " + videoCallDataRoot.getHostName());
            new BottomSheetReport(this, videoCallDataRoot.getHostId(), (BottomSheetReport.OnReportedListner) () -> {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout,
                        (ViewGroup) findViewById(R.id.customtoastlyt));


                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            });

        }
    }
}