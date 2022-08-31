package com.zakux.live.activity.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.JsonObject;
import com.razorpay.PaymentResultListener;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.BaseActivity;
import com.zakux.live.adaptor.CoinPlanViewPagerAdapter;
import com.zakux.live.ads.InstrialAds;
import com.zakux.live.databinding.ActivityCoinPlanBinding;
import com.zakux.live.models.BecomeVipMemberRoot;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.SettingsRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinPlanActivity extends BaseActivity implements PaymentResultListener, BillingProcessor.IBillingHandler {
    private static final String TAG = "coinPlanAct";
    ActivityCoinPlanBinding binding;
    SessionManager sessionManager;

    List<String> paymentGateways = new ArrayList<>();
    private BillingProcessor bp;

    public List<String> getPaymentGateways() {
        return paymentGateways;
    }

    private String userId;
    SettingsRoot.Data setting;
    InstrialAds instrialAds;
    private String selectedPlanId;
    private static final String STR_VIP = "You Are Vip Member";

    public void setPaymentGateways(List<String> paymentGateways) {
        this.paymentGateways = paymentGateways;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_plan);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();
        setting = sessionManager.getSetting();
        instrialAds = new InstrialAds(this);

        Intent intent = getIntent();
        boolean isVip = intent.getBooleanExtra("isVip", false);

        if (setting.isGooglePaySwitch()) {
            paymentGateways.add("google pay");
        }
        if (setting.isRazorPaySwitch()) {
            paymentGateways.add("razor pay");
        }
        if (setting.isStripeSwitch()) {
            paymentGateways.add("stripe");
        }
        Log.d(TAG, "onCreate: " + paymentGateways.size());
        initView(isVip);


        bp = new BillingProcessor(this, sessionManager.getSetting().getGooglePayId(), this);
        bp.initialize();

    }

    private void initView(boolean isVip) {
        CoinPlanViewPagerAdapter viewPagerAdapter = new CoinPlanViewPagerAdapter(getSupportFragmentManager(), paymentGateways, isVip);
        binding.viewPager.setAdapter(viewPagerAdapter);


    }





    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        instrialAds.showAds();
        super.onBackPressed();
    }

    private boolean isVip;

    @Override
    public void onPaymentError(int i, String s) {
        Log.d(TAG, "onPaymentError: " + s);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.d("TAG", "onPaymentSuccess: ");
        Toast.makeText(this, "Purchased", Toast.LENGTH_SHORT).show();

        if (isVip) {
            becomeVipMember(selectedPlanId);
        } else {
            callPurchaseDoneApi(selectedPlanId);
        }

    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored: ");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        try {
            Log.d(TAG, "onBillingError: " + error.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "onBillingInitialized: ");
    }

    public String getSelectedPlanId() {
        return selectedPlanId;
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d(TAG, "onProductPurchased: ");
        bp.consumePurchase(productId);

        if (isVip) {
            becomeVipMember(selectedPlanId);
        } else {
            callPurchaseDoneApi(selectedPlanId);
        }

    }


    public void makeGooglePurchase(String id) {
        Log.d(TAG, "makeGooglePurchase: " + id);
        if (bp.isInitialized()) {
            bp.purchase(this, id);
        }
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
        if (bp != null) {
            bp.handleActivityResult(requestCode, resultCode, data);
        }

        Log.d("TAG", "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void callPurchaseDoneApi(String planId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("from_user_id", userId);
        jsonObject.addProperty("plan_id", planId);
        Call<RestResponse> call = RetrofitBuilder.create().purchaseCoin(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(CoinPlanActivity.this, "Purchased", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(CoinPlanActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Toast.makeText(CoinPlanActivity.this, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setSelectedPlanId(String selectedPlanId, boolean isVip) {
        this.selectedPlanId = selectedPlanId;
        this.isVip = isVip;
    }

    public void becomeVipMember(String planId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("plan_id", planId);
        Call<BecomeVipMemberRoot> call = RetrofitBuilder.create().becomeVip(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<BecomeVipMemberRoot>() {
            @Override
            public void onResponse(Call<BecomeVipMemberRoot> call, Response<BecomeVipMemberRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(CoinPlanActivity.this, STR_VIP, Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());

                    } else {
                        Toast.makeText(CoinPlanActivity.this, STR_VIP, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CoinPlanActivity.this, STR_VIP, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BecomeVipMemberRoot> call, Throwable t) {
                Toast.makeText(CoinPlanActivity.this, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

}