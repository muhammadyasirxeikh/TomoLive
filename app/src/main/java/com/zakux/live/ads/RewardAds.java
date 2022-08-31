package com.zakux.live.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.zakux.live.SessionManager;

public class RewardAds {

    private static final String TAG = "rewardads";
    RewardAdListnear rewardAdListnear;
    SessionManager sessionManager;
    private RewardedAd rewardedAd;
    private Context context;
    private InterstitialAd interstitialAdfb;

    public RewardAds(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);

        if (!sessionManager.getUser().isIsVIP() && sessionManager.getAdsKeys().getGoogle() != null && sessionManager.getAdsKeys().getGoogle().isShow()) {
            initGoogle();
        } else if (!sessionManager.getUser().isIsVIP() && sessionManager.getAdsKeys().getFacebook() != null && sessionManager.getAdsKeys().getFacebook().isShow()) {
            initFacebook();
        }

    }

    public RewardAdListnear getRewardAdListnear() {
        return rewardAdListnear;
    }

    public void setRewardAdListnear(RewardAdListnear rewardAdListnear) {
        this.rewardAdListnear = rewardAdListnear;
    }

    private void initGoogle() {

        rewardedAd = new RewardedAd(context, sessionManager.getAdsKeys().getGoogle().getReward());
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
                Log.d(TAG, "onRewardedAdFailedToLoad: " + adError);
                initFacebook();
            }

        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    private void initFacebook() {
        interstitialAdfb = new com.facebook.ads.InterstitialAd(context, sessionManager.getAdsKeys().getFacebook().getReward());
        interstitialAdfb.loadAd(
                interstitialAdfb.buildLoadAdConfig()
                        .withAdListener(new AbstractAdListener() {

                        })
                        .build());

    }


    public void showAd() {
        if (rewardedAd != null && rewardedAd.isLoaded()) {
            Activity activityContext = (Activity) context;
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward.
                    rewardAdListnear.onEarned();
                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    // Ad failed to display.
                }
            };
            rewardedAd.show(activityContext, adCallback);
        } else if (interstitialAdfb != null && interstitialAdfb.isAdLoaded()) {
            interstitialAdfb.show();
            rewardAdListnear.onEarned();
        }

    }


    public interface RewardAdListnear {
        void onAdClosed();

        void onEarned();
    }

}
