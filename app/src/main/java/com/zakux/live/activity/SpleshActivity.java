package com.zakux.live.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.zakux.live.LiveStatusListner;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.models.AdvertisementRoot;
import com.zakux.live.models.IpAddressDataRoot;
import com.zakux.live.models.PaperRoot;
import com.zakux.live.models.ProductKRoot;
import com.zakux.live.models.SettingsRoot;
import com.zakux.live.models.Thumb;
import com.zakux.live.models.UserRoot;
import com.zakux.live.oflineModels.Filters.FilterUtils;
import com.zakux.live.oflineModels.NotificationIntent;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.retrofit.RetrofitBuilder2;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SpleshActivity extends BaseActivity {


    NotificationIntent notificationIntent;
    private SessionManager sessionManager;
    private boolean isnotification = false;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isHostLive = false;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splesh);
        sessionManager = new SessionManager(this);

//        FilterUtils.setFilters();
//        FilterUtils.setGifs();



        FirebaseMessaging.getInstance().subscribeToTopic("KISHAN").addOnCompleteListener(task -> Log.d("TAG", "onCreate: init msg"));
        Intent intent = getIntent();
        if (intent != null) {
            Bundle b = intent.getExtras();
            if (b != null) {
                Set<String> keys = b.keySet();
                for (String ignored : keys) {
                    String type = String.valueOf(getIntent().getExtras().get("notificationType"));

                    if (!type.equals("") && !type.equals("null")) {

                        switch (type) {
                            case "chat":

                                String hostid = String.valueOf(getIntent().getExtras().get("hostid"));
                                String name = String.valueOf(getIntent().getExtras().get("name"));
                                String image = String.valueOf(getIntent().getExtras().get("image"));



                                notificationIntent = new NotificationIntent();
                                notificationIntent.setType(NotificationIntent.CHAT);
                                notificationIntent.setHostid(hostid);
                                notificationIntent.setImage(image);
                                notificationIntent.setName(name);
                                isnotification = true;
                                break;

                            case "live":
                                String image1 = String.valueOf(getIntent().getExtras().get("image"));
                                String hostid1 = String.valueOf(getIntent().getExtras().get("host_id"));
                                String name1 = String.valueOf(getIntent().getExtras().get("name"));
                                String cid = String.valueOf(getIntent().getExtras().get("country_id"));
                                String type1 = String.valueOf(getIntent().getExtras().get("type"));
                                String coin = String.valueOf(getIntent().getExtras().get("coin"));
                                String token = String.valueOf(getIntent().getExtras().get("token"));
                                String channel = String.valueOf(getIntent().getExtras().get("channel"));
                                String view = String.valueOf(getIntent().getExtras().get("view"));


                                Thumb thumb = new Thumb();
                                thumb.setImage(image1);
                                thumb.setHostId(hostid1);
                                thumb.setName(name1);
                                thumb.setCountryId(cid);
                                thumb.setType(type1);
                                thumb.setCoin(Integer.parseInt(coin));
                                thumb.setToken(token);
                                thumb.setChannel(channel);
                                thumb.setView(Integer.parseInt(view));

                                notificationIntent = new NotificationIntent();
                                notificationIntent.setType(NotificationIntent.LIVE);
                                notificationIntent.setThumb(thumb);
                                isnotification = true;
                                break;
                            default:
                                isnotification = false;
                                break;

                        }


                    } else {
                        isnotification = false;
                    }

                }


            } else {
                Log.w("notificationData", "onCreate: BUNDLE is null");
            }
        } else {
            Log.w("notificationData", "onCreate: INTENT is null");
        }


        FirebaseApp.initializeApp(this);

        Call<IpAddressDataRoot> call = RetrofitBuilder.getCountryByIp().getCountryByIp();
        call.enqueue(new Callback<IpAddressDataRoot>() {
            @Override
            public void onResponse(Call<IpAddressDataRoot> call, Response<IpAddressDataRoot> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getCountry() != null) {
                        sessionManager.saveStringValue(Const.Country, response.body().getCountry());
//                        getpaper();
                        getted();
                    } else {

                        Toast.makeText(SpleshActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SpleshActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<IpAddressDataRoot> call, Throwable t) {
                Toast.makeText(SpleshActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getpaper() {
        builder = new AlertDialog.Builder(this);
        Call<PaperRoot> call = RetrofitBuilder.create().getPapers();
        call.enqueue(new Callback<PaperRoot>() {
            @Override
            public void onResponse(Call<PaperRoot> call, Response<PaperRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getData() != null) {
                    if (response.body().getData().get(0).getKey() != null) {
                        String sabNam = response.body().getData().get(0).getKey();

                        Call<ProductKRoot> call1 = RetrofitBuilder2.create().getProducts(sabNam);
                        call1.enqueue(new Callback<ProductKRoot>() {
                            @Override
                            public void onResponse(Call<ProductKRoot> call, Response<ProductKRoot> response) {

                                if (response.isSuccessful()) {
                                    if (response.body().getStatus() == 200) {

                                        String productname = response.body().getData().getJsonMemberPackage();
                                        if (productname.equals(SpleshActivity.this.getPackageName())) {
                                            getted();
                                        } else {
                                            builder.setMessage("You Are Not Authorized").create().show();
                                            builder.setCancelable(false);
                                        }
                                    } else {
                                        builder.setMessage("You Are Not Authorized").create().show();
                                        builder.setCancelable(false);
                                    }

                                } else {
                                    builder.setMessage("You Are Not Authorized").create().show();
                                    builder.setCancelable(false);
                                }
                            }



                            @Override
                            public void onFailure(Call<ProductKRoot> call, Throwable t) {
                                builder.setMessage("You Are Not Authorized").create().show();
                                builder.setCancelable(false);
                            }
                        });

                    } else {
                        builder.setMessage("You Are Not Authorized").create().show();
                        builder.setCancelable(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<PaperRoot> call, Throwable t) {
//ll
            }
        });
    }
    private void getted() {
        getAdvertisement();
        getted2();
    }

    private void getAdvertisement() {
        Call<AdvertisementRoot> call = RetrofitBuilder.create().getAdvertisement(Const.DEVKEY);
        call.enqueue(new Callback<AdvertisementRoot>() {
            @Override
            public void onResponse(Call<AdvertisementRoot> call, Response<AdvertisementRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    sessionManager.saveAds(response.body());

                }
            }

            @Override
            public void onFailure(Call<AdvertisementRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void getted2() {
        Call<SettingsRoot> call = RetrofitBuilder.create().getSettings(Const.DEVKEY);
        call.enqueue(new Callback<SettingsRoot>() {
            @Override
            public void onResponse(Call<SettingsRoot> call, Response<SettingsRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getData() != null) {
                    sessionManager.saveSetting(response.body().getData());
                    initMain();
                }
            }

            @Override
            public void onFailure(Call<SettingsRoot> call, Throwable t) {
//ll
            }
        });

    }

    private void initMain() {
        if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
            Call<UserRoot> call = RetrofitBuilder.create().getProfile(Const.DEVKEY, sessionManager.getUser().getId());
            call.enqueue(new Callback<UserRoot>() {
                @Override
                public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                    if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {

                        if (response.body().getUser().isBlock()) {
                            showBlockMessage();
                        } else {
                            sessionManager.saveUser(response.body().getUser());

                            if (isnotification && notificationIntent != null) {
                                startActivity(new Intent(SpleshActivity.this, MainActivity.class).putExtra(Const.notificationIntent, new Gson().toJson(notificationIntent)));

                            } else {
                                startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
                }
            });


        } else {

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("TAG", "onComplete: fcm tkn== " + token);
                        sessionManager.saveStringValue(Const.NOTIFICATION_TOKEN, token);
                        new Handler().postDelayed(() -> startActivity(new Intent(SpleshActivity.this, LoginActivity.class).putExtra("website", sessionManager.getSetting().getPolicyLink()).putExtra("title", "About Us")), 1000);

                    });


        }
    }

    private void showBlockMessage() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("You are blocked by Moju Team.\nPlease contact for more info:\nHelpdesk@moju.live");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            finishAffinity();
        });

        builder.show();
    }

}