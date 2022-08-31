package com.zakux.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.purchase.CoinPlanActivity;
import com.zakux.live.adaptor.DailyCoinAdaptor;
import com.zakux.live.adaptor.MoreCoinAdapter;
import com.zakux.live.adaptor.ReedemGatewayAdapter;
import com.zakux.live.ads.RewardAds;
import com.zakux.live.databinding.ActivityMyWalletBinding;
import com.zakux.live.databinding.BottomSheetReedemBinding;
import com.zakux.live.models.DailyTaskRoot;
import com.zakux.live.models.PlanRoot;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.User;
import com.zakux.live.models.UserRoot;
import com.zakux.live.popus.ChangeRatePopup;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyWalletActivity extends BaseActivity implements BillingProcessor.IBillingHandler {
    private static final String STR_USERID = "user_id";
    ActivityMyWalletBinding binding;
    SessionManager sessionManager;
    RewardAds rewardAds;
    private BillingProcessor bp;
    String planId;
    private String userId;
    private User user;
    private int currentTask;
    String selectedpaymentgateway = "";
    private boolean submitButtonEnable = false;

    public static void openMe(Context context) {
        context.startActivity(new Intent(context, MyWalletActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_wallet);

        MainActivity.isHostLive = false;
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();

        setDefaultData();
        getUserData();
        checkDailyTask();
        getPlanList();

    }

    private void setDefaultData() {
        int minStreamingValue = sessionManager.getSetting().getStreamingMinValue();
        binding.tvrate.setText(String.valueOf(minStreamingValue));

        if (sessionManager.getUser().isIsHost()) {
            binding.lytbuy.setVisibility(View.GONE);
        }
    }

    private void checkDailyTask() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(STR_USERID, userId);
        Call<DailyTaskRoot> call = RetrofitBuilder.create().checkDailyTask(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<DailyTaskRoot>() {
            @Override
            public void onResponse(Call<DailyTaskRoot> call, Response<DailyTaskRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    currentTask = response.body().getNumber();
                    setDailyTaskList();
                }
            }

            @Override
            public void onFailure(Call<DailyTaskRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void setDailyTaskList() {
        rewardAds = new RewardAds(MyWalletActivity.this);
        DailyCoinAdaptor dailyCoinAdaptor = new DailyCoinAdaptor(currentTask, (position, randomInteger, coinColorBinding) -> {


            rewardAds.showAd();
            rewardAds.setRewardAdListnear(new RewardAds.RewardAdListnear() {
                private static final String STR_24HR = "Try after 24 hours";

                @Override
                public void onAdClosed() {
                    Toast.makeText(MyWalletActivity.this, "Reward Fail", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onEarned() {


                    if (currentTask == position) {
                        coinColorBinding.imgcoin.setVisibility(View.GONE);

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("coin", randomInteger);
                        jsonObject.addProperty(STR_USERID, new SessionManager(MyWalletActivity.this).getUser().getId());
                        Call<RestResponse> call = RetrofitBuilder.create().updateDailyTask(Const.DEVKEY, jsonObject);
                        call.enqueue(new Callback<RestResponse>() {
                            @Override
                            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                                if (response.code() == 200 && response.body().isStatus()) {
                                    Toast.makeText(MyWalletActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    getUserData();
                                    checkDailyTask();
                                    sessionManager.setDailyTaskCoinHistory(position, randomInteger);
                                } else {
                                    coinColorBinding.imgcoin.setVisibility(View.VISIBLE);
                                    Toast.makeText(MyWalletActivity.this, STR_24HR, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<RestResponse> call, Throwable t) {
                                coinColorBinding.imgcoin.setVisibility(View.VISIBLE);
                                Toast.makeText(MyWalletActivity.this, STR_24HR, Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Toast.makeText(MyWalletActivity.this, STR_24HR, Toast.LENGTH_SHORT).show();
                    }
                    checkDailyTask();
                }
            });
            Log.d("TAG", "onBindViewHolder:" + currentTask + " select ==" + position);


        });
        binding.rvDailyCoin.setAdapter(dailyCoinAdaptor);
    }


    private void getUserData() {
        Log.d("checkinggg ", "getData:  my wallet 171");
        Call<UserRoot> call = RetrofitBuilder.create().getUserProfile(Const.DEVKEY, userId);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                    user = response.body().getUser();
                    sessionManager.saveUser(user);
                    binding.tvBalancde.setText(String.valueOf(user.getCoin()));
                    binding.tvrate.setText(String.valueOf(user.getRate()));

                    binding.lytrate.setOnClickListener(v -> {
                        ChangeRatePopup changeRatePopup = new ChangeRatePopup(MyWalletActivity.this, user);
                        changeRatePopup.setOnSubmitClickListnear(rate -> {
                            Log.d("TAG", "submit: ");
                            updateRate(rate);
                        });
                    });

                    binding.lytreedem.setOnClickListener(v -> {
                        int minCoin = sessionManager.getSetting().getMinPoints();
                        if (user.getCoin() >= minCoin) {

                            openReedemSheet();
                        } else {
                            Toast.makeText(MyWalletActivity.this, "You have atleast " + minCoin + " to redeem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void openReedemSheet() {
        long howManyCoinForOne = sessionManager.getSetting().getHowManyCoins();
        long myPrice = user.getCoin() / howManyCoinForOne;


        String stringReedemGateway = sessionManager.getSetting().getRedeemGateway();
        String[] gateways = stringReedemGateway.split(",");
        selectedpaymentgateway = gateways[0];


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MyWalletActivity.this, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        BottomSheetReedemBinding bottomSheetReedemBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_reedem, null, false);
        bottomSheetDialog.setContentView(bottomSheetReedemBinding.getRoot());

        BottomSheetDialog finalBottomSheetDialog = bottomSheetDialog;
        bottomSheetReedemBinding.btnclose.setOnClickListener(v -> finalBottomSheetDialog.dismiss());
        ReedemGatewayAdapter reedemGatewayAdapter = new ReedemGatewayAdapter(gateways, mathod -> {

            bottomSheetReedemBinding.etDes.setText("");
            selectedpaymentgateway = mathod;

        });
        bottomSheetReedemBinding.rvReedemGetway.setAdapter(reedemGatewayAdapter);

        bottomSheetReedemBinding.etcoin.setText(String.valueOf(user.getCoin()));


        bottomSheetReedemBinding.etCurrency.setText(myPrice + sessionManager.getSetting().getCurrency());


        bottomSheetReedemBinding.etDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
///ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    bottomSheetReedemBinding.btnSubmit.setBackground(ContextCompat.getDrawable(MyWalletActivity.this, R.drawable.bg_etblack));
                    bottomSheetReedemBinding.btnSubmit.setTextColor(ContextCompat.getColor(MyWalletActivity.this, R.color.black));
                    submitButtonEnable = false;
                } else {
                    bottomSheetReedemBinding.btnSubmit.setBackground(ContextCompat.getDrawable(MyWalletActivity.this, R.drawable.bg_greadentround));
                    bottomSheetReedemBinding.btnSubmit.setTextColor(ContextCompat.getColor(MyWalletActivity.this, R.color.white));
                    submitButtonEnable = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });

        BottomSheetDialog finalBottomSheetDialog1 = bottomSheetDialog;
        bottomSheetReedemBinding.btnSubmit.setOnClickListener(v -> {
            if (submitButtonEnable) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(STR_USERID, userId);
                jsonObject.addProperty("paymentGateway", selectedpaymentgateway);
                jsonObject.addProperty("coin", sessionManager.getUser().getCoin());
                jsonObject.addProperty("description", bottomSheetReedemBinding.etDes.getText().toString().trim());
                Call<RestResponse> call = RetrofitBuilder.create().submitReedemRequest(Const.DEVKEY, jsonObject);
                call.enqueue(new Callback<RestResponse>() {
                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                        if (response.code() == 200 && response.body().isStatus()) {
                            Toast.makeText(MyWalletActivity.this, "Reedem Request Submited", Toast.LENGTH_SHORT).show();
                            getUserData();
                            finalBottomSheetDialog1.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                    }
                });
            }
        });
        bottomSheetDialog.show();

    }

    private void updateRate(String rate) {
        RequestBody coin = RequestBody.create(MediaType.parse("text/plain"), rate);
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), userId);
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put(STR_USERID, userid);
        map.put("rate", coin);

        Call<UserRoot> call = RetrofitBuilder.create().updateUser(Const.DEVKEY, map);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(MyWalletActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());
                        getUserData();
                    } else {
                        Toast.makeText(MyWalletActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void getPlanList() {
        Call<PlanRoot> call = RetrofitBuilder.create().getPlanList(Const.DEVKEY);
        call.enqueue(new Callback<PlanRoot>() {
            private void onButClick(PlanRoot.DataItem dataItem) {
                buyItem(dataItem);
            }

            @Override
            public void onResponse(Call<PlanRoot> call, Response<PlanRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    MoreCoinAdapter moreCoinAdapter = new MoreCoinAdapter(response.body().getData(), this::onButClick);
                    binding.rvMoreCoins.setAdapter(moreCoinAdapter);

                }
            }

            @Override
            public void onFailure(Call<PlanRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void buyItem(PlanRoot.DataItem dataItem) {
        planId = dataItem.getId();
        bp = new BillingProcessor(this, sessionManager.getSetting().getGooglePayId(), this);
        bp.initialize();
    }

    @Override
    protected void onResume() {
        setDefaultData();
        getUserData();
        checkDailyTask();
        getPlanList();
        super.onResume();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        bp.consumePurchase(productId);

    }

    @Override
    public void onPurchaseHistoryRestored() {
//ll
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
//ll
    }

    @Override
    public void onBillingInitialized() {
        //ll
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickBuyMoreCoin(View view) {
        startActivity(new Intent(this, CoinPlanActivity.class).putExtra("isVip", false));
    }

    public void onClickVip(View view) {
        startActivity(new Intent(this, CoinPlanActivity.class).putExtra("isVip", true));
    }
}