package com.zakux.live.activity;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ActivityMainBinding;
import com.zakux.live.fregment.LiveRequestFragment;
import com.zakux.live.fregment.MessageFregment;
import com.zakux.live.fregment.ProfileFregment;
import com.zakux.live.fregment.VideoFregment;
import com.zakux.live.fregment.VideoOfflineFragment;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.User;
import com.zakux.live.models.UserRoot;
import com.zakux.live.oflineModels.NotificationIntent;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.token.RtcTokenBuilderSample;
import com.zakux.live.utils.PermissionPopup;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity {
    private static final String TAG = "ActivityCheck";
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static boolean isHostLive = false;
    public ActivityMainBinding binding;
    SessionManager sessionManager;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isHostLive = false;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();




        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {

            initView();
        } else {
            new PermissionPopup(this, new PermissionPopup.OnPlanClickListner() {
                @Override
                public void onDismiss() {
                    ActivityCompat.requestPermissions(MainActivity.this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
                }
            });

        }



    }

    public void makeOnlineUser() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.ISLOGIN) && sessionManager.getUser() != null) {
            Log.d(TAG, "implimentHostAgora: ");
            try {

                User user = sessionManager.getUser();

                String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), user.getId());

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("_id", new SessionManager(this).getUser().getId());
                jsonObject.addProperty("token", tkn);
                jsonObject.addProperty("channel", user.getId());
                jsonObject.addProperty("country", sessionManager.getStringValue(Const.Country));
                Log.d(TAG, "onResponse: "+jsonObject);
                Call<RestResponse> call = RetrofitBuilder.create().actionOnlineHost(Const.DEVKEY, jsonObject);
                call.enqueue(new Callback<RestResponse>() {
                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
//                        Log.d(TAG, "onResponse: "+response.body().getMessage());
                        if (response.code() == 200 && response.body().isStatus()) {
                            Log.d(TAG, "onResponse: data send to server");


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

    private void initView() {


        getIntentData();
        initMain();

//        if (sessionManager.getUser().isIsHost()) {
//            binding.golive.setVisibility(View.VISIBLE);
//            binding.goliveText.setVisibility(View.VISIBLE);
//
//        } else {
//            binding.golive.setVisibility(View.GONE);
//            binding.goliveText.setVisibility(View.GONE);
//        }
        binding.home.performClick();
//        new SimplePopup(this);


//        Socket globalSoket = getGlobalSoket();
//        globalSoket.emit("call", "anurag bhai");


//        if (sessionManager.getUser().isIsHost()) {
//
//            binding.live.setVisibility(View.VISIBLE);
//            binding.online.setVisibility(View.VISIBLE);
//            binding.like.setVisibility(View.GONE);
//            binding.home.setVisibility(View.GONE);
//        }

        if (sessionManager.getUser().isIsHost() && !sessionManager.getBooleanValue(Const.IS_FIRST_TIMEHOST)) {
            Toast.makeText(this, "YOU ARE HOST NOW ", Toast.LENGTH_LONG).show();
            sessionManager.saveBooleanValue(Const.IS_FIRST_TIMEHOST, true);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.isHostLive = false;
        Log.i(TAG, "onResume: MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: MainActivity");
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {


            String objStr = intent.getStringExtra(Const.notificationIntent);
            if (objStr != null && !objStr.equals("")) {
                NotificationIntent notificationIntent = new Gson().fromJson(objStr, NotificationIntent.class);
                if (notificationIntent.getType() == NotificationIntent.CHAT) {

                    startActivity(new Intent(this, ChatListActivityOriginal.class).putExtra("hostid", notificationIntent.getHostid())
                            .putExtra("name", notificationIntent.getName()).putExtra("image", notificationIntent.getImage()));

                    setDefultBottomBar();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new MessageFregment()).commit();


                } else if (notificationIntent.getType() == NotificationIntent.LIVE) {
                    startActivity(new Intent(this, WatchLiveActivity.class).putExtra("model", new Gson().toJson(notificationIntent.getThumb())));
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (binding.homeWhite.getVisibility() == View.GONE) {
            finish();
        } else {
            setDefultBottomBar();

//            if (sessionManager.getUser().isIsHost()) {
//                binding.live.performClick();
//            } else {
                binding.home.performClick();
//            }

        }
    }

    private void initMain() {
        makeOnlineUser();
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.container, new VideoFregment()).commit();


//        binding.online.setOnClickListener(v -> {
//            setDefultBottomBar();
//            binding.onlineUnselected.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.pinkmain));
//            binding.tvCall.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.pinkmain));
//            if (sessionManager.getUser().isIsHost()) {
//                if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
//                        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
//                        checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
//
//
//                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new OnlineRequestFragment()).commit();
//
//
//                    Log.d(TAG, "onCreate: permisson");
//                } else {
//
//                    Log.d(TAG, "onCreate: permisson denied");
//                }
//            }
//
//
//            // change image
//        });

                                                                   

        binding.home.setOnClickListener(v -> {
            setDefultBottomBar();
            binding.homeWhite.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.pinkmain));
            binding.tvhome.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.pinkmain));

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new VideoFregment()).commit();

            // change image
        });


        binding.like.setOnClickListener(v -> {
            setDefultBottomBar();
            binding.likeWhite.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.pinkmain));
            binding.tvmatch.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.pinkmain));


            getSupportFragmentManager().beginTransaction().replace(R.id.container, new VideoOfflineFragment()).commit();

            // change image
        });
        binding.message.setOnClickListener(v -> {
            setDefultBottomBar();
            binding.msgWhite.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.pinkmain));
            binding.tvmessages.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.pinkmain));

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MessageFregment()).commit();


            // change image
        });
        binding.profile.setOnClickListener(v -> {
            setDefultBottomBar();
            binding.profileWhite.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.pinkmain));
            binding.tvprofile.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.pinkmain));

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFregment()).commit();


            // change image
        });

    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            //  ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    private void updateRate(String rate) {
        RequestBody coin = RequestBody.create(MediaType.parse("text/plain"), rate);
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), userId);
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("user_id", userid);
        map.put("rate", coin);

        Call<UserRoot> call = RetrofitBuilder.create().updateUser(Const.DEVKEY, map);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(MainActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());

                    } else {
                        Toast.makeText(MainActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void setDefultBottomBar() {
        binding.homeWhite.setImageTintList(ContextCompat.getColorStateList(this, R.color.white));
        binding.likeWhite.setImageTintList(ContextCompat.getColorStateList(this, R.color.white));
        binding.liveUnselected.setImageTintList(ContextCompat.getColorStateList(this, R.color.white));
        binding.onlineUnselected.setImageTintList(ContextCompat.getColorStateList(this, R.color.white));
        binding.msgWhite.setImageTintList(ContextCompat.getColorStateList(this, R.color.white));
        binding.profileWhite.setImageTintList(ContextCompat.getColorStateList(this, R.color.white));

        binding.tvhome.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvCall.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvLive.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvmatch.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvmessages.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvprofile.setTextColor(ContextCompat.getColor(this, R.color.white));

        binding.lytmain.setBackgroundColor(ContextCompat.getColor(this, R.color.themepurple));


    }

    public void onMessageClicked() {
        setDefultBottomBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MessageFregment()).commit();

    }

    public void onClickKhajano(View view) {
        startActivity(new Intent(this, MyWalletActivity.class));
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
            initView();
        }
    }

    private void showLongToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

    }
}