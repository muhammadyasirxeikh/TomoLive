package com.zakux.live.ads;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.AbstractAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.zakux.live.SessionManager;
import com.zakux.live.models.AdvertisementRoot;


public class InstrialAds {

    private static final String TAG = "interadjanu";
    OnInterstitialAdListnear onInterstitialAdListnear;
    SessionManager sessionManager;
    private Context context;
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdfb;

    AdvertisementRoot.Google campaignGoogle;
    AdvertisementRoot.Facebook campaignFacebook;

    public InstrialAds(Context context) {
        this.context = context;
        initAds();
    }

    public OnInterstitialAdListnear getOnInterstitialAdListnear() {
        return onInterstitialAdListnear;
    }

    public void setOnInterstitialAdListnear(OnInterstitialAdListnear onInterstitialAdListnear) {
        this.onInterstitialAdListnear = onInterstitialAdListnear;
    }

    private void initAds() {
        sessionManager = new SessionManager(context);


        if (!sessionManager.getUser().isIsVIP() && Boolean.TRUE.equals(sessionManager.getAdsKeys().getGoogle().isShow())) {
            Log.d(TAG, "initAds: ");
            sessionManager = new SessionManager(context);
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(sessionManager.getAdsKeys().getGoogle().getInterstrial() != null ? sessionManager.getAdsKeys().getGoogle().getInterstrial() : "");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Log.d(TAG, "onAdClosed: ");
                    onInterstitialAdListnear.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    initFacebook();
                    Log.d(TAG, "onAdFailedToLoad: ");
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d(TAG, "onAdLoaded: ");

                    onInterstitialAdListnear.onAdLoded(mInterstitialAd);
                }
            });
        } else {
            initFacebook();
        }


    }

    private void initFacebook() {


        if (!sessionManager.getUser().isIsVIP() && Boolean.TRUE.equals(sessionManager.getAdsKeys().getFacebook().isShow())) {
            Log.d(TAG, "initAds: fb");
            interstitialAdfb = new com.facebook.ads.InterstitialAd(context, sessionManager.getAdsKeys().getFacebook().getInterstrial() != null ? sessionManager.getAdsKeys().getFacebook().getInterstrial() : "");

            interstitialAdfb.loadAd(
                    interstitialAdfb.buildLoadAdConfig()
                            .withAdListener(new AbstractAdListener() {



                            })
                            .build());
        }


    }

    public boolean showAds() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else if (interstitialAdfb != null && interstitialAdfb.isAdLoaded()) {
            interstitialAdfb.show();
        } else {
            return false;
        }
        return true;
    }

    public interface OnInterstitialAdListnear {
        void onAdLoded(InterstitialAd mInterstitialAd);

        void onAdClosed();
    }
}
