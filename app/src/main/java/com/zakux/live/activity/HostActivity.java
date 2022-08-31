package com.zakux.live.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zakux.live.BuildConfig;
import com.zakux.live.LivexUtils;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.adaptor.BottomViewPagerAdapter;
import com.zakux.live.adaptor.CommentAdapterOriginal;
import com.zakux.live.adaptor.FilterAdapter;
import com.zakux.live.adaptor.GifAdapter;
import com.zakux.live.adaptor.HostEmojiAdapter;
import com.zakux.live.adaptor.StikerHostAdapter;
import com.zakux.live.adaptor.ViewUserAdapter;
import com.zakux.live.databinding.ActivityHostBinding;
import com.zakux.live.databinding.BottomSheetViewsBinding;
import com.zakux.live.models.EmojiIconRoot;
import com.zakux.live.models.EmojicategoryRoot;
import com.zakux.live.models.HostEmojiRoot;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.StikerRoot;
import com.zakux.live.models.User;
import com.zakux.live.models.ViewUserRoot;
import com.zakux.live.networkManager.NetworkChangeReceiver;
import com.zakux.live.oflineModels.Filters.FilterRoot;
import com.zakux.live.oflineModels.gif.GifRoot;
import com.zakux.live.oflineModels.gift.GiftRoot;
import com.zakux.live.retrofit.ApiCalling;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.token.RtcTokenBuilderSample;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostActivity extends AppCompatActivity {
    private static final String TAG = "hostact";
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int SHEET_OPEN = 1;
    private static final int SHEET_CLOSE = 2;
    ActivityHostBinding binding;
    Emitter.Listener filterListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TAG", "call: filterlistnear" + args.length);
            Log.d("TAG", "call: filterlistnear   " + args[0].toString());
            runOnUiThread(() -> {


                if (args[0] != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = args[0].toString();
                        FilterRoot filterRoot = new Gson().fromJson(filtertype, FilterRoot.class);
                        if (filterRoot != null) {
                            if (filterRoot.getFilter() == 0) {
                                binding.imgFilter.setImageDrawable(null);
                            } else {
                                binding.imgFilter.setImageDrawable(ContextCompat.getDrawable(HostActivity.this, filterRoot.getFilter()));
                            }
                        }

                    });

                }

            });


        }
    };
    Emitter.Listener gifListnear = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(() -> {


                if (args[0] != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = args[0].toString();
                        GifRoot filterRoot = new Gson().fromJson(filtertype, GifRoot.class);
                        if (filterRoot != null) {
                            if (filterRoot.getFilter() == 0) {
                                binding.imgFilter2.setImageDrawable(null);
                            } else {
                                Log.d(TAG, "run: haa gif aavi");
                                Glide.with(HostActivity.this).asGif().load(filterRoot.getFilter()).centerCrop().into(binding.imgFilter2);
                            }
                        }

                    });

                }

            });


        }
    };
    Emitter.Listener stickerListnear = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TAG", "call: gifListnear" + args.length);
            Log.d("TAG", "call: gifListnear   " + args[0].toString());
            runOnUiThread(() -> {


                if (args[0] != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = args[0].toString();
                        StikerRoot.DataItem stiker = new Gson().fromJson(filtertype, StikerRoot.DataItem.class);
                        if (stiker != null) {
                            Glide.with(HostActivity.this).asGif().load(BuildConfig.BASE_URL + stiker.getSticker()).centerCrop().into(binding.imgstiker);

                            new Handler().postDelayed(() -> binding.imgstiker.setImageDrawable(null), 1500);


                        } else {
                            Log.d(TAG, "run: stiker is null");
                        }

                    });

                }

            });


        }
    };
    Emitter.Listener emojiListnear = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TAG", "call: emiji" + args.length);
            Log.d("TAG", "call: emijoi   " + args[0].toString());
            runOnUiThread(() -> {


                if (args[0] != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = args[0].toString();
                        HostEmojiRoot.DataItem emoji = new Gson().fromJson(filtertype, HostEmojiRoot.DataItem.class);
                        if (emoji != null) {
                            Glide.with(HostActivity.this).load(BuildConfig.BASE_URL + emoji.getEmoji()).centerCrop().into(binding.imgEmoji);

                            new Handler().postDelayed(() -> binding.imgEmoji.setImageDrawable(null), 1500);


                        }

                    });

                }

            });


        }
    };
    Handler timerHandler = new Handler();
    BottomSheetViewsBinding bottomSheetViewsBinding;
    EmojicategoryRoot.Datum tempobj;
    private String userId;
    private List<StikerRoot.DataItem> stikers = new ArrayList<>();
    private List<HostEmojiRoot.DataItem> hostEmojis = new ArrayList<>();
    private SessionManager sessionManager;
    private String chenal;
    private String tkn;
    private RtcEngine mRtcEngine;
    private Socket socket;
    private boolean isBottomViewSheetOpen = false;
    Emitter.Listener viewListnear = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TAG", "call: viewListnear" + args.length);
            Log.d("TAG", "call: viewListnear   " + args[0].toString());
            runOnUiThread(() -> {
                binding.tvviews.setText(String.valueOf(args[0].toString()));
                Log.d(TAG, "run: viewssss " + args[0].toString());
                if (isBottomViewSheetOpen) {
                    fetchViewUsers();
                }
            });


        }
    };
    Emitter.Listener refreshListnear = args -> {
        Log.d("TAG", "call: refreshhh" + args.length);
        Log.d("TAG", "call: refreshhh   " + args[0].toString());
        runOnUiThread(this::setUpMyData);


    };
    private CommentAdapterOriginal commentAdapter = new CommentAdapterOriginal();
    Emitter.Listener commentListnear = args -> {

        Log.d("TAG", "call: listnerrrrmsg   " + args[0].toString());
        if (args[0] != null) {
            runOnUiThread(() -> {
                try {
                    JSONObject response = (JSONObject) args[0];
                    commentAdapter.addComment(response);
                } catch (Exception o) {
                    Log.d(TAG, "run: eooros comment soket " + o.getMessage());
                }

            });

        }


    };
    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;
    int uidAgora = -1;
    private boolean mMuted;
    private ArrayList<EmojicategoryRoot.Datum> finelCategories;
    Emitter.Listener giftReciveListnear = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TAG", "call: gifListnear" + args.length);
            Log.d("TAG", "call: gifListnear   " + args[0].toString());
            runOnUiThread(() -> {


                if (args[0] != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = args[0].toString();
                        GiftRoot gift = new Gson().fromJson(filtertype, GiftRoot.class);
                        if (gift != null) {
                            Glide.with(HostActivity.this).load(BuildConfig.BASE_URL + gift.getImage()).centerCrop().into(binding.imgEmoji);
                            setUpMyData();
                            new Handler().postDelayed(() -> binding.imgEmoji.setImageDrawable(null), 1500);

                            socket.emit("refresh", "refreshh");
                        }

                    });

                }

            });


        }
    };
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            setUpMyData();
            Log.d(TAG, "run: one min finished");
            timerHandler.postDelayed(this, 60000);

        }
    };
    BottomSheetDialog bottomSheetDialog;
    private Runnable refreshRunnable = this::setUpMyData;
    Handler refreshHandler = new Handler();
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        private void initTimer() {
            refreshHandler.postDelayed(refreshRunnable, 5000);

        }

        private void setupRemoteVideo(int uid) {
            ViewGroup parent = mRemoteContainer;
            if (parent.indexOfChild(mLocalVideo.view) > -1) {
                parent = mLocalContainer;
            }

            if (mRemoteVideo != null) {
                return;
            }
            SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
            view.setZOrderMediaOverlay(parent == mLocalContainer);
            parent.addView(view);
            mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
            // Initializes the video view of a remote user.
            mRtcEngine.setupRemoteVideo(mRemoteVideo);
        }

        private void initSoketIO() {
            IO.Options options = IO.Options.builder()
                    // IO factory options
                    .setForceNew(false)
                    .setMultiplex(true)

                    // low-level engine options
                    .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                    .setUpgrade(true)
                    .setRememberUpgrade(false)
                    .setPath("/socket.io/")
                    .setQuery("room=" + tkn + "")
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
            Log.d("TAG", "onCreate: " + socket.id());
            Log.d("socket", "soket: " + socket.connected());
            socket.connect();
            socket.on("connection", args -> Log.d("TAG", "call: ")).on(Socket.EVENT_CONNECT, args -> {
                Log.d("TAG", "call: connect" + args.length);

                socket.emit("msg", "Annie !!!");
                socket.on("msg", commentListnear);
                socket.on("filter", filterListener);
                socket.on("gif", gifListnear);
                socket.on("sticker", stickerListnear);
                socket.on("emoji", emojiListnear);
                socket.on("gift", giftReciveListnear);
                socket.on("view", viewListnear);
                socket.on("refresh", refreshListnear);


                runOnUiThread(this::initSoketFireEvents);


            });


        }

        private void initSoketFireEvents() {


            binding.btnsend.setOnClickListener(v -> {
                if (!binding.etComment.getText().toString().equals("")) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("name", sessionManager.getUser().getName());
                        object.put("comment", binding.etComment.getText().toString());
                        socket.emit("msg", object);
                    } catch (JSONException e) {
                        Log.d(TAG, "onClick: btnsendd err");
                        e.printStackTrace();
                    }


                    binding.etComment.setText("");
                }
            });

        }

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(() -> {

                uidAgora = uid;
                Log.d(TAG, "run: chennel joined");
                Log.d(TAG, "run: video ecoded");
                initSoketIO();
                initTimer();
                ApiCalling apiCalling = new ApiCalling(HostActivity.this);
                apiCalling.makeBusyHost();
            });
        }


        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            Log.d(TAG, "onUserOffline: host ");

        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.d(TAG, "onError: " + err);
        }

        @Override
        public void onFirstLocalAudioFrame(int elapsed) {
            super.onFirstLocalAudioFrame(elapsed);
            Log.d(TAG, "onFirstLocalAudioFrame: ");
        }

        @Override
        public void onMediaEngineLoadSuccess() {
            super.onMediaEngineLoadSuccess();
            Log.d(TAG, "onMediaEngineLoadSuccess: ");
        }

        @Override
        public void onMediaEngineStartCallSuccess() {
            super.onMediaEngineStartCallSuccess();
            Log.d(TAG, "onMediaEngineStartCallSuccess: ");
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            ApiCalling apiCalling = new ApiCalling(HostActivity.this);
            apiCalling.makeFreeHost();
        }
    };

    private void setUpMyData() {

        ApiCalling apiCalling = new ApiCalling(HostActivity.this);
        apiCalling.getHostProfile(userId, new ApiCalling.OnHostProfileGetListnear() {
            @Override
            public void onHostGet(User user) {
                sessionManager.saveUser(user);
                binding.tvmyCoin.setText(String.valueOf(user.getCoin()));
            }

            @Override
            public void onFail() {
//ll
            }
        });
    }

    private void destoryHost() {
        Log.d(TAG, "destoryHost: 111");

        timerHandler.removeCallbacks(timerRunnable);
        Log.d(TAG, "destoryHost: ");

        Call<RestResponse> call = RetrofitBuilder.create().destoryHost(Const.DEVKEY, chenal);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                Log.d(TAG, "onResponse: host destoried");
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });

        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("token", tkn); //chanel is user id
        Call<RestResponse> call2 = RetrofitBuilder.create().destorytoken(Const.DEVKEY, jsonObject2);
        call2.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                Log.d(TAG, "onResponse: token destoried");
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        runOnUiThread(() -> {


            Log.d(TAG, "run: useroffline");
            makeOfflineAndClose();
            MainActivity.isHostLive = false;
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);
        binding.rvComments.setAdapter(commentAdapter);
        getGiftsCategories();
        getStikers();
        getHostEmoji();
        setUpMyData();
        initUIButtonListnear();
        implimentHostAgora();
        MainActivity.isHostLive = true;

        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.setOnNetworkListner(new NetworkChangeReceiver.OnNetworkListner() {
            @Override
            public void onDisconnnected() {
                makeOfflineAndClose();
            }

            @Override
            public void onConnected() {
//ll
            }
        });

    }

    private void makeOfflineAndClose() {

        Log.d(TAG, "makeOfflineAndClose: ");
        if (socket != null && socket.connected()) {
            socket.emit("ended", "endtrue");
            Log.d(TAG, "makeOfflineAndClose: soket emitewd");
        } else {
            Log.d(TAG, "onCallClicked: locho");

            try {
                socket.emit("ended", "endtrue");
            } catch (Exception e) {
                Log.d(TAG, "makeOfflineAndClose: errrr " + e.getMessage());
                e.printStackTrace();
            }
        }
        LivexUtils.setCustomToast(HostActivity.this, "Video Ended!!");
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();
    }

    private void implimentHostAgora() {
        Log.d(TAG, "implimentHostAgora: ");
        try {
            chenal = sessionManager.getUser().getId();
            tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), chenal);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", new SessionManager(this).getUser().getId());
            jsonObject.addProperty("token", tkn);
            jsonObject.addProperty("channel", chenal);
            jsonObject.addProperty("country", sessionManager.getStringValue(Const.Country));
            Call<RestResponse> call = RetrofitBuilder.create().actionLiveUserVideo(Const.DEVKEY, jsonObject);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    if (response.code() == 200 && response.body().isStatus()) {
                        Log.d(TAG, "onResponse: data send to server");

                        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
                            initEngineAndJoinChannel();
                        } else {
                            Log.d(TAG, "onResponse: permissin denied");
                        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);

        if (requestCode == PERMISSION_REQ_ID) {
            initEngineAndJoinChannel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        makeOfflineAndClose();
        MainActivity.isHostLive = false;
    }

    private void getGiftsCategories() {
        Call<EmojicategoryRoot> call = RetrofitBuilder.create().getCategories(Const.DEVKEY);
        call.enqueue(new Callback<EmojicategoryRoot>() {
            @Override
            public void onResponse(Call<EmojicategoryRoot> call, Response<EmojicategoryRoot> response) {
                if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {

                    List<EmojicategoryRoot.Datum> categories = response.body().getData();
                    Log.d(TAG, "onResponse: categorysixe " + categories.size());
                    finelCategories = new ArrayList<>();

                    for (int i = 0; i < categories.size(); i++) {
                        if (Boolean.TRUE.equals(categories.get(i).getIsTop())) {
                            tempobj = categories.get(i);

                        } else {
                            finelCategories.add(categories.get(i));
                        }
                    }

                    if (tempobj != null) {
                        finelCategories.add(0, tempobj);
                    }


                    BottomViewPagerAdapter bottomViewPagerAdapter = new BottomViewPagerAdapter(finelCategories);
                    binding.bottomPage.viewpager.setAdapter(bottomViewPagerAdapter);
                    settabLayout(finelCategories);
                    binding.bottomPage.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.bottomPage.tablayout));
                    binding.bottomPage.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            binding.bottomPage.viewpager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            //ll
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            //ll
                        }
                    });
                    bottomViewPagerAdapter.setEmojiListnerViewPager((bitmap, coin, emoji) -> {
                        sendGift(coin, emoji);
                        updetUI(SHEET_CLOSE);
                    });
                }
            }


            private void settabLayout(List<EmojicategoryRoot.Datum> categories) {
                binding.bottomPage.tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
                for (int i = 0; i < categories.size(); i++) {

                    binding.bottomPage.tablayout.addTab(binding.bottomPage.tablayout.newTab().setCustomView(createCustomView(categories.get(i))));

                }
            }

            private View createCustomView(EmojicategoryRoot.Datum datum) {
                Log.d(TAG, "settabLayout: " + datum.getName());
                Log.d(TAG, "settabLayout: " + datum.getIcon());
                View v = LayoutInflater.from(HostActivity.this).inflate(R.layout.custom_tabgift, null);
                TextView tv = (TextView) v.findViewById(R.id.tvTab);
                tv.setText(datum.getName());
                ImageView img = (ImageView) v.findViewById(R.id.imagetab);

                Glide.with(getApplicationContext())
                        .load(BuildConfig.BASE_URL + datum.getIcon())
                        .placeholder(R.drawable.ic_gift)
                        .into(img);
                return v;

            }


            @Override
            public void onFailure(Call<EmojicategoryRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void sendGift(Long coin, EmojiIconRoot.Datum emoji) {


        GiftRoot giftRoot = new GiftRoot();
        giftRoot.setCoin(coin);
        giftRoot.setImage(emoji.getIcon());
        giftRoot.setUsername(sessionManager.getUser().getName());
        socket.emit("gift", new Gson().toJson(giftRoot));




    }

    private void fetchViewUsers() {
        ViewUserAdapter viewUserAdapter = new ViewUserAdapter();
        bottomSheetViewsBinding.rvusers.setAdapter(viewUserAdapter);
        Call<ViewUserRoot> call = RetrofitBuilder.create().getTotalViewUser(tkn, Const.DEVKEY);
        call.enqueue(new Callback<ViewUserRoot>() {
            @Override
            public void onResponse(Call<ViewUserRoot> call, Response<ViewUserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() && !response.body().getData().isEmpty()) {
                        viewUserAdapter.addUsers(response.body().getData());
                        bottomSheetViewsBinding.tvViews.setText(String.valueOf(response.body().getData().size()));
                    } else {
                        bottomSheetViewsBinding.tvNoUsers.setVisibility(View.VISIBLE);
                        bottomSheetViewsBinding.tvViews.setText("0");
                    }
                } else {
                    bottomSheetViewsBinding.tvNoUsers.setVisibility(View.VISIBLE);
                    bottomSheetViewsBinding.tvViews.setText("0");
                }
            }

            @Override
            public void onFailure(Call<ViewUserRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: fetchviewusererr " + t.getMessage());
            }
        });

    }

    private void initEngineAndJoinChannel() throws RuntimeException {

        try {

            mRtcEngine = RtcEngine.create(getBaseContext(), sessionManager.getSetting().getAgoraId(), mRtcEventHandler);
            mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER);
            Log.d(TAG, "initEngineAndJoinChannel: success");
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
        mRtcEngine.enableVideo();
        mRtcEngine.enableAudio();
        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));


        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        mLocalContainer.addView(view);
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);

        mRtcEngine.setDefaultAudioRoutetoSpeakerphone(false);
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER);
        mRtcEngine.enableVideo();
        mRtcEngine.enableAudio();
        String token = tkn;
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        Log.d(TAG, "initEngineAndJoinChannel: on way  " + chenal + token);
        mRtcEngine.joinChannel(token, chenal, "Extra Optional Data", 0);


        timerHandler.postDelayed(timerRunnable, 60000);
    }


    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    private void initUIButtonListnear() {
        binding.imgfilterclose.setOnClickListener(v -> {
            binding.lytfilters.setVisibility(View.GONE);
            binding.lytcontrols.setVisibility(View.VISIBLE);
        });
        binding.lytviewcount.setOnClickListener(v -> {

            isBottomViewSheetOpen = true;
            openViewBottomSheet();
        });


    }

    private void openViewBottomSheet() {


        bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog = new BottomSheetDialog(HostActivity.this, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        bottomSheetViewsBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_views, null, false);
        bottomSheetDialog.setContentView(bottomSheetViewsBinding.getRoot());

        fetchViewUsers();
        bottomSheetViewsBinding.btnclose.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            isBottomViewSheetOpen = false;
        });


        bottomSheetDialog.show();
    }

    private void getStikers() {
        Call<StikerRoot> call = RetrofitBuilder.create().getStikers(Const.DEVKEY);
        call.enqueue(new Callback<StikerRoot>() {
            @Override
            public void onResponse(Call<StikerRoot> call, Response<StikerRoot> response) {

                if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {
                    stikers = response.body().getData();
                }
            }

            @Override
            public void onFailure(Call<StikerRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: stiker " + t.getMessage());
            }
        });
    }

    private void getHostEmoji() {
        Call<HostEmojiRoot> call = RetrofitBuilder.create().getHostEmoji(Const.DEVKEY);
        call.enqueue(new Callback<HostEmojiRoot>() {
            @Override
            public void onResponse(Call<HostEmojiRoot> call, Response<HostEmojiRoot> response) {
                if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {
                    hostEmojis = response.body().getData();
                    Log.d(TAG, "onResponse: host emijo fetched");

                }
            }

            @Override
            public void onFailure(Call<HostEmojiRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: hostemoji" + t.getMessage());
            }
        });
    }


    @Override
    public void onBackPressed() {
        makeOfflineAndClose();
    }

    private void leaveChannel() {
        Log.d(TAG, "leaveChannel: success");
        if (mRtcEngine != null) {
            destoryHost();
            mRtcEngine.leaveChannel();
            RtcEngine.destroy();
            finish();

        } else {
            finish();
        }
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

    public void onClickFilterIcon(View view) {
        binding.lytfilters.setVisibility(View.VISIBLE);
        binding.lytcontrols.setVisibility(View.GONE);
        FilterAdapter filterAdapter = new FilterAdapter((FilterRoot filterRoot) -> socket.emit("filter", new Gson().toJson(filterRoot)));
        binding.rvFilters.setAdapter(filterAdapter);
    }

    public void onClickGifIcon(View view) {
        binding.lytfilters.setVisibility(View.VISIBLE);
        binding.lytcontrols.setVisibility(View.GONE);
        GifAdapter gifAdapter = new GifAdapter((GifRoot gifRoot) -> socket.emit("gif", new Gson().toJson(gifRoot)));
        binding.rvFilters.setAdapter(gifAdapter);
    }

    public void onClickStikerIcon(View view) {
        binding.lytfilters.setVisibility(View.VISIBLE);
        binding.lytcontrols.setVisibility(View.GONE);
        Log.d(TAG, "onClickStikerIcon: size" + stikers.size());
        StikerHostAdapter stikerHostAdapter = new StikerHostAdapter(stikers, stiker -> socket.emit("sticker", new Gson().toJson(stiker)));
        binding.rvFilters.setAdapter(stikerHostAdapter);
    }

    public void onClickEmojiIcon(View view) {

        binding.lytfilters.setVisibility(View.VISIBLE);
        binding.lytcontrols.setVisibility(View.GONE);
        HostEmojiAdapter stikerHostAdapter = new HostEmojiAdapter(hostEmojis, emoji -> socket.emit("emoji", new Gson().toJson(emoji)));
        binding.rvFilters.setAdapter(stikerHostAdapter);
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.ic_microphoneoff : R.drawable.ic_microphoneon;
        binding.btnMute.setImageResource(res);
    }


    public void onCallClicked(View view) {

        makeOfflineAndClose();

    }

    public void onclickGiftIcon(View view) {

        updetUI(SHEET_OPEN);
        binding.bottomPage.btnclose.setOnClickListener(v -> updetUI(SHEET_CLOSE));
    }

    private void updetUI(int state) {
        if (state == SHEET_OPEN) {
            binding.bottomPage.lyt2.setVisibility(View.VISIBLE);
            binding.rvComments.setVisibility(View.GONE);
            binding.lytbottom.setVisibility(View.GONE);

        } else {
            binding.bottomPage.lyt2.setVisibility(View.GONE);
            binding.rvComments.setVisibility(View.VISIBLE);
            binding.lytbottom.setVisibility(View.VISIBLE);

        }
    }

    public void onclickShare(View view) {
        String hostName = "";
        if (sessionManager.getUser().getUsername() != null) {
            hostName = sessionManager.getUser().getUsername();
        }
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "\nHello Dear, I am @" + hostName + "\nLet me recommend you this application\n and watch my LiveVideo \n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //ll
        }
    }
}