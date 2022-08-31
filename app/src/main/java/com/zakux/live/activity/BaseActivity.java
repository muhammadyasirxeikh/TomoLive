package com.zakux.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zakux.live.BuildConfig;
import com.zakux.live.LiveStatusListner;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.callwork.CallScreenActivity;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.User;
import com.zakux.live.oflineModels.VideoCallDataRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.token.RtcTokenBuilderSample;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    public static boolean isHostBusyLocal = false;
    protected static boolean isCallIncoming = false;
    private static final String TAG = "ActivityCheck";
    public static boolean isActOpened = false;
    public static boolean HOST_ONLINE = false;

//    public static void offlineHost(Context applicationContext) {
//        SessionManager sessionManager = new SessionManager(applicationContext);
//        if (sessionManager.getBooleanValue(Const.ISLOGIN) && sessionManager.getUser() != null) {
//
//
//            Call<RestResponse> call = RetrofitBuilder.create().offlineHost(Const.DEVKEY, sessionManager.getUser().getId());
//            call.enqueue(new Callback<RestResponse>() {
//                @Override
//                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
//                    Log.d(TAG, "onResponse: host destoried");
//
//                    HOST_ONLINE = false;
//                }
//
//                @Override
//                public void onFailure(Call<RestResponse> call, Throwable t) {
////ll
//                }
//            });
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());
            });
        }
        ViewCompat.requestApplyInsets(decorView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }


    }

    public void makeOfflineHost() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.ISLOGIN) && sessionManager.getUser() != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("_id", sessionManager.getUser().getId());

            Call<RestResponse> call = RetrofitBuilder.create().offlineHost(Const.DEVKEY,jsonObject);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    Log.i(TAG, "onResponse: user_id"+sessionManager.getUser().getId());
                    Toast.makeText(BaseActivity.this, "you are offline"+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {

                }
            });

        }
    }

    public void makeOnlineHost() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.ISLOGIN) && sessionManager.getUser() != null) {
            Log.d(TAG, "implimentHostAgora: ");
            try {

                User user = sessionManager.getUser();

                String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), user.getId());

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("user_id", new SessionManager(this).getUser().getId());
                jsonObject.addProperty("token", tkn);
                jsonObject.addProperty("channel", user.getId());
                jsonObject.addProperty("country", sessionManager.getStringValue(Const.Country));
                Call<RestResponse> call = RetrofitBuilder.create().actionOnlineHost(Const.DEVKEY, jsonObject);
                call.enqueue(new Callback<RestResponse>() {
                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                        if (response.code() == 200 && response.body().isStatus()) {
                            Log.d(TAG, "onResponse: data send to server");
                            Toast.makeText(BaseActivity.this, "You are Online", Toast.LENGTH_SHORT).show();

                            HOST_ONLINE = true;
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

    }

    public Socket getGlobalSoket() {
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
        Socket socket = IO.socket(uri, options);
        socket.connect();

        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d(TAG, "connected: globelSoket");
            socket.emit("call", "anurag call");
            socket.on("call", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "call: inforemed" + args[0].toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (args[0].equals("")) {
                                Log.d(TAG, "run: call args is null");
                                return;
                            }
                            try {
                                VideoCallDataRoot videoCallDataRoot = new Gson().fromJson(args[0].toString(), VideoCallDataRoot.class);
                                if (videoCallDataRoot != null) {
                                    SessionManager sessionManager = new SessionManager(BaseActivity.this);
                                    if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
                                        String uId = sessionManager.getUser().getId();
                                        if (uId.equals(videoCallDataRoot.getClientId())) {
                                            if (HOST_ONLINE && !isCallIncoming) {
                                                isCallIncoming = true;

                                                if (!isActOpened) {
//                                                    Toast.makeText(BaseActivity.this, "Call From " + videoCallDataRoot.getHostName(), Toast.LENGTH_SHORT).show();
                                                    isActOpened = true;
                                                        if (videoCallDataRoot.getCallType().equals("audio")){
                                                       startActivity(new Intent(BaseActivity.this, CallScreenActivity.class).putExtra("datastr", args[0].toString()).putExtra("call_type","audio"));

                                                   }else if (videoCallDataRoot.getCallType().equals("video")){
                                                       startActivity(new Intent(BaseActivity.this, CallScreenActivity.class).putExtra("datastr", args[0].toString()).putExtra("call_type","video"));

                                                   }
                                                }
                                            }
                                        }

                                    } else {
                                        Log.d(TAG, "run: not login yet");
                                    }

                                } else {
                                    Log.d(TAG, "run: object is null");
                                }
                            } catch (Exception o) {
                                Log.d(TAG, "run: err " + o.getMessage());
                            }
                        }
                    });

                }
            });


        });
        return socket;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        makeOnlineHost();
        Log.i(TAG, "onResume: Base Activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        makeOfflineHost();
        Log.i(TAG, "onDestroy: Base Activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: BaseActivity");
    }
}

