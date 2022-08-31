package com.zakux.live.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zakux.live.BuildConfig;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.callwork.CallRequestActivity;
import com.zakux.live.adaptor.ChatAdapterOriginal;
import com.zakux.live.databinding.ActivityChatListOriginalBinding;
import com.zakux.live.models.ChatSendRoot;
import com.zakux.live.models.ChatTopicRoot;
import com.zakux.live.models.OriginalMessageRoot;
import com.zakux.live.models.User;
import com.zakux.live.oflineModels.VideoCallDataRoot;
import com.zakux.live.retrofit.CoinWork;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.token.RtcTokenBuilderSample;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListActivityOriginal extends BaseActivity {
    private static final String STR_USERID = "user_id";
    private static final String STR_TOPIC = "topic";
    private static final String STR_MSG = "message";
    ActivityChatListOriginalBinding binding;
    ChatAdapterOriginal chatAdapterOriginal = new ChatAdapterOriginal();
    SessionManager sessionManager;
    private boolean isLoding = false;
    private int start = 0;
    String name;
    Socket globalSoket;
    private boolean isInsufficentBalance = false;


    String profileImage;
    private String secondUserId;
    private String userId;
    Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TAG", "call: listnerrrr" + args.length);
            Log.d("TAG", "call: listnerrrrmsg   " + args[0].toString());

            if (args[0] != null) {
                runOnUiThread(() -> {
                    try {

                        JSONObject response = (JSONObject) args[0];
                        String userId = response.get(STR_USERID).toString();
                        String topic = response.get(STR_TOPIC).toString();
                        String message = response.get(STR_MSG).toString();

                        OriginalMessageRoot.DataItem dataItem = new OriginalMessageRoot.DataItem();
                        if (userId.equals(ChatListActivityOriginal.this.userId)) {
                            dataItem.setFlag(1);
                        } else {
                            dataItem.setFlag(0);
                        }
                        dataItem.setTopic(topic);
                        dataItem.setMessage(message);

                        chatAdapterOriginal.addSingleMessage(dataItem);
                        binding.rvchats.scrollToPosition(chatAdapterOriginal.getItemCount() - 1);
                    } catch (Exception o) {
                        Log.d("TAG", "run: eooros" + o.getMessage());
                    }

                });

            }


        }
    };
    private String chatRoomId;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_list_original);
        sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
            userId = sessionManager.getUser().getId();
        }
        initView();
        Intent intent = getIntent();
        MainActivity.isHostLive = false;
        globalSoket = getGlobalSoket();
        secondUserId = intent.getStringExtra("hostid");
        profileImage = intent.getStringExtra("image");
        Log.d("TAG", "onCreate: image" + profileImage);
        name = intent.getStringExtra("name");
        if (name != null && !name.equals("")) {
            binding.tvName.setText(name);
        }


        Log.d("TAG", "onCreate: hostid " + secondUserId);
        if (secondUserId != null && !secondUserId.equals("")) {
            createtopic();
        }
        Glide.with(this).load(profileImage).circleCrop().addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d("TAG", "onLoadFailed: ");
                binding.imgProfile.setBackground(ContextCompat.getDrawable(ChatListActivityOriginal.this, R.drawable.bg_whitebtnround));
                binding.imgProfile.setImageDrawable(resource);
                return true;
            }
        }).into(binding.imgProfile);


        binding.btnCall.setOnClickListener(v -> {
            String chenal = sessionManager.getUser().getId();
            String clientId = secondUserId;
            try {
                String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), chenal);

                VideoCallDataRoot videoCallDataRoot = new VideoCallDataRoot();
                videoCallDataRoot.setHostName(sessionManager.getUser().getName());
                videoCallDataRoot.setChannel(chenal);
                videoCallDataRoot.setClientId(clientId);
                videoCallDataRoot.setHostId(chenal);
                videoCallDataRoot.setToken(tkn);
                videoCallDataRoot.setClientImage(profileImage);
                videoCallDataRoot.setHostImage(sessionManager.getUser().getImage());
                videoCallDataRoot.setClientName(name);
                videoCallDataRoot.setCallType("video");

                globalSoket.emit("call", new Gson().toJson(videoCallDataRoot));

                startActivity(new Intent(this, CallRequestActivity.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)).putExtra("call_type","video"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.btnCallAudio.setOnClickListener(v -> {
            String chenal = sessionManager.getUser().getId();
            String clientId = secondUserId;
            try {
                String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), chenal);

                VideoCallDataRoot videoCallDataRoot = new VideoCallDataRoot();
                videoCallDataRoot.setHostName(sessionManager.getUser().getName());
                videoCallDataRoot.setChannel(chenal);
                videoCallDataRoot.setClientId(clientId);
                videoCallDataRoot.setHostId(chenal);
                videoCallDataRoot.setToken(tkn);
                videoCallDataRoot.setClientImage(profileImage);
                videoCallDataRoot.setHostImage(sessionManager.getUser().getImage());
                videoCallDataRoot.setClientName(name);
                videoCallDataRoot.setCallType("audio");

                globalSoket.emit("call", new Gson().toJson(videoCallDataRoot));


                startActivity(new Intent(this, CallRequestActivity.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)).putExtra("call_type","audio"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void createtopic() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sender_id", userId);
        jsonObject.addProperty("receiver_id", secondUserId);
        Call<ChatTopicRoot> call = RetrofitBuilder.create().createChatTopic(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<ChatTopicRoot>() {
            @Override
            public void onResponse(Call<ChatTopicRoot> call, Response<ChatTopicRoot> response) {
                if (response.code() == 200 && response.body().getStatus() && response.body().getData() != null) {
                    chatRoomId = response.body().getData().getId();
                    getOldMessages();
                    initSoketIO();
                    initListnear();
                }
            }

            @Override
            public void onFailure(Call<ChatTopicRoot> call, Throwable t) {
//ll
            }
        });

        binding.rvchats.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvchats.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvchats.getLayoutManager();
                    Log.d("TAG", "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d("TAG", "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d("TAG", "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        start = start + Const.LIMIT;
                        getOldMessages();

                    }
                }
            }
        });

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
                .setQuery("chatroom=" + chatRoomId + "")
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
        socket.connect();
        socket.on("connection", args -> Log.d("TAG", "call: "));

        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("TAG", "call: connect" + args.length);
            socket.on("chat", listener);


        });
    }

    private void initListnear() {
        binding.btnsend.setOnClickListener(v -> {
            if (isInsufficentBalance) {
                Toast.makeText(this, "Please recharge your wallet", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> MyWalletActivity.openMe(ChatListActivityOriginal.this), 1500);
                return;
            }
            if (!binding.etChat.getText().toString().equals("")) {


                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("sender_id", userId);
                jsonObject.addProperty("receiver_id", secondUserId);
                jsonObject.addProperty(STR_TOPIC, chatRoomId);
                jsonObject.addProperty(STR_MSG, binding.etChat.getText().toString().trim());


                JSONObject object = new JSONObject();
                try {

                    object.put(STR_USERID, userId);
                    object.put(STR_TOPIC, chatRoomId);
                    object.put(STR_MSG, binding.etChat.getText().toString().trim());
                    socket.emit("chat", object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                binding.etChat.setText("");
                Call<ChatSendRoot> call = RetrofitBuilder.create().sendMessageToBackend(Const.DEVKEY, jsonObject);
                call.enqueue(new Callback<ChatSendRoot>() {
                    @Override
                    public void onResponse(Call<ChatSendRoot> call, Response<ChatSendRoot> response) {
                        if (response.code() == 200 && response.body().getStatus() && response.body().getData() != null) {
                            Log.d("TAG", "onResponse: sended msg success to backend");


                            binding.etChat.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatSendRoot> call, Throwable t) {
                        Log.d("TAG", "onFailure: " + t.getMessage());

                    }
                });

                if (sessionManager.getUser().isIsVIP()) {
                    CoinWork coinWork = new CoinWork();
                    coinWork.transferCoin(userId, userId, Const.CHAT_CHARGES);
                    coinWork.setOnCoinWorkLIstner(new CoinWork.OnCoinWorkLIstner() {
                        @Override
                        public void onSuccess(User user) {
                            isInsufficentBalance = false;
                            sessionManager.saveUser(user);
                        }

                        @Override
                        public void onFailure() {
//ll
                        }

                        @Override
                        public void onInsufficientBalance() {
                            isInsufficentBalance = true;
                        }
                    });
                }
            }

        });
    }

    private void getOldMessages() {
        binding.tvloading.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(STR_TOPIC, chatRoomId);
        jsonObject.addProperty(STR_USERID, userId);
        jsonObject.addProperty("start", start);
        jsonObject.addProperty("limit", Const.LIMIT);
        Call<OriginalMessageRoot> call = RetrofitBuilder.create().getOldMessage(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<OriginalMessageRoot>() {
            @Override
            public void onResponse(Call<OriginalMessageRoot> call, Response<OriginalMessageRoot> response) {
                if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {
                    chatAdapterOriginal.addData(response.body().getData());
                    binding.rvchats.scrollToPosition(chatAdapterOriginal.getItemCount() - 1);
                    isLoding = false;
                }
                binding.tvloading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<OriginalMessageRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    private void initView() {

        binding.rvchats.setAdapter(chatAdapterOriginal);
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickCamara(View view) {
// ll
    }

    public void onclicProfile(View view) {
        Log.d("TAG", "onclicProfile: secondusser id " + secondUserId);
        startActivity(new Intent(this, GuestProfileActivity.class).putExtra("guestId", secondUserId));
    }
}