package com.zakux.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zakux.live.BuildConfig;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.callwork.CallRequestActivity;
import com.zakux.live.databinding.ActivityGuestProfileBinding;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.User;
import com.zakux.live.oflineModels.VideoCallDataRoot;
import com.zakux.live.retrofit.ApiCalling;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.token.RtcTokenBuilderSample;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestProfileActivity extends BaseActivity {

    ActivityGuestProfileBinding binding;
    SessionManager sessionManager;
    private User guestUser;
    private Socket globalSoket;
    private String guestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guest_profile);
        MainActivity.isHostLive = false;
        sessionManager = new SessionManager(this);
        getIntentData();
        globalSoket = getGlobalSoket();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        guestId = intent.getStringExtra("guestId");
        Log.d("TAG", "getIntentData: " + guestId);
        if (guestId != null && !guestId.equals("")) {
            binding.pd.setVisibility(View.VISIBLE);
            getData(guestId);
            checkIsOnlineOrNot();
        }
    }

    private void checkIsOnlineOrNot() {
        ApiCalling apiCalling = new ApiCalling(this);
        apiCalling.hostIsOnlineOrNot(guestId, new ApiCalling.ResponseListnear() {
            @Override
            public void responseSuccess() {
                binding.lytCall.setVisibility(View.VISIBLE);
            }

            @Override
            public void responseFail() {
                binding.lytCall.setVisibility(View.GONE);
            }
        });
    }

    private void getData(String guestId) {
        binding.pd.setVisibility(View.VISIBLE);
        ApiCalling apiCalling = new ApiCalling(this);
        apiCalling.getHostProfile(guestId, new ApiCalling.OnHostProfileGetListnear() {
            @Override
            public void onHostGet(User user) {
                guestUser = user;
                if (guestUser != null) {
                    setUserData();
                    initListner();
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFail() {
//ll
            }
        });
    }

    private void initListner() {
        binding.lytfollow.setOnClickListener(v -> followUser());
        binding.lytunfollow.setOnClickListener(v -> unFOllowUser());
        binding.lytchat.setOnClickListener(v -> startActivity(new Intent(this, ChatListActivityOriginal.class).putExtra("name", guestUser.getName())
                .putExtra("image", guestUser.getImage())
                .putExtra("hostid", guestUser.getId())));

        binding.lytCall.setOnClickListener(v -> {


            ApiCalling apiCalling = new ApiCalling(this);
            apiCalling.isHostBusy(guestUser.getId(), new ApiCalling.ResponseListnear() {
                @Override
                public void responseSuccess() {
                    //host is budsy
                    Toast.makeText(GuestProfileActivity.this, "Host Is Busy With Someone.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void responseFail() {  // host is not busy
                    String chenal = sessionManager.getUser().getId();
                    String clientId = guestUser.getId();
                    try {
                        String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), chenal);

                        VideoCallDataRoot videoCallDataRoot = new VideoCallDataRoot();
                        videoCallDataRoot.setHostName(sessionManager.getUser().getName());
                        videoCallDataRoot.setChannel(chenal);
                        videoCallDataRoot.setClientId(clientId);
                        videoCallDataRoot.setHostId(chenal);
                        videoCallDataRoot.setToken(tkn);
                        videoCallDataRoot.setClientImage(guestUser.getImage());
                        videoCallDataRoot.setHostImage(sessionManager.getUser().getImage());
                        videoCallDataRoot.setClientName(guestUser.getName());
                        globalSoket.emit("call", new Gson().toJson(videoCallDataRoot));

                        startActivity(new Intent(GuestProfileActivity.this, CallRequestActivity.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        });
    }

    private void followUser() {
        binding.lytfollow.setVisibility(View.INVISIBLE);
        binding.lytunfollow.setVisibility(View.INVISIBLE);
        ApiCalling apiCalling = new ApiCalling(this);
        apiCalling.followUser(this, new SessionManager(this).getUser().getId(), guestUser.getId());
        apiCalling.setResponseListnear(new ApiCalling.ResponseListnear() {
            @Override
            public void responseSuccess() {
                binding.lytfollow.setVisibility(View.GONE);
                binding.lytunfollow.setVisibility(View.VISIBLE);
            }

            @Override
            public void responseFail() {
//ll
            }
        });
    }

    private void unFOllowUser() {
        binding.lytfollow.setVisibility(View.GONE);
        binding.lytunfollow.setVisibility(View.GONE);
        ApiCalling apiCalling = new ApiCalling(this);
        apiCalling.unfollowUser(this, new SessionManager(this).getUser().getId(), guestUser.getId());
        apiCalling.setResponseListnear(new ApiCalling.ResponseListnear() {
            @Override
            public void responseSuccess() {
                binding.lytunfollow.setVisibility(View.GONE);
                binding.lytfollow.setVisibility(View.VISIBLE);
            }

            @Override
            public void responseFail() {
//ll
            }
        });

    }

    private void setUserData() {
        Glide.with(this).load(guestUser.getImage()).centerCrop().into(binding.imggirl);
        Glide.with(this).load(guestUser.getImage()).centerCrop().into(binding.imguser);
        binding.tvcoin.setText(String.valueOf(guestUser.getCoin()));
        binding.tvfollowing.setText(String.valueOf(guestUser.getFollowingCount()));
        binding.tvfollowrs.setText(String.valueOf(guestUser.getFollowersCount()));
        binding.tvlocation.setText(guestUser.getCountry());
        binding.tvName.setText(guestUser.getName());
        binding.tvusername.setText(guestUser.getUsername());

        checkFollowOrNot();


        if (guestUser.isIsHost()) {
            if (sessionManager.getUser().getId().equals(guestUser.getId())) {
                binding.lytCall.setVisibility(View.GONE);
            } else {
                binding.lytCall.setVisibility(View.VISIBLE);
            }
        } else {
            binding.lytCall.setVisibility(View.GONE);
        }

    }

    private void checkFollowOrNot() {
        binding.pd.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("host_id", guestUser.getId());
        jsonObject.addProperty("guest_id", sessionManager.getUser().getId());
        Call<RestResponse> call = RetrofitBuilder.create().checkFollow(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (!response.body().isStatus()) { // notFollowing
                        binding.lytunfollow.setVisibility(View.GONE);
                        binding.lytfollow.setVisibility(View.VISIBLE);
                    } else {
                        binding.lytfollow.setVisibility(View.GONE);
                        binding.lytunfollow.setVisibility(View.VISIBLE);
                    }
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onclickShare(View view) {
        String hostName = "";
        if (sessionManager.getUser().getUsername() != null && sessionManager.getUser().getName() != null) {
            hostName = sessionManager.getUser().getUsername();
        }
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "\nHello Dear, I am " + hostName + "\nLet me recommend you this application\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //ll
        }
    }
}