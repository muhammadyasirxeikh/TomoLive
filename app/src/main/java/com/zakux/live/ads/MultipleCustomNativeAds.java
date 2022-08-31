package com.zakux.live.ads;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.zakux.live.SessionManager;

import static com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT;

@Keep
public class MultipleCustomNativeAds {
    private final Context context;
    private final SessionManager sessionManager;
    boolean loadMoreAds = true;
    int offset;
    int index;
    private OnLoadAds onLoadAds;

    public MultipleCustomNativeAds(Context context, OnLoadAds onLoadAds, int offset) {
        this.context = context;
        sessionManager = new SessionManager(context);
        this.onLoadAds = onLoadAds;
        this.offset = offset;
        index = offset - 1;
        if (!sessionManager.getUser().isIsVIP() && sessionManager.getAdsKeys() != null) {
            initAds();
        }

    }

    private void initAds() {
        if (!sessionManager.getUser().isIsVIP() && sessionManager.getAdsKeys().getGoogle() != null && sessionManager.getAdsKeys().getGoogle().isShow()) {
            loadNativeAds();
        } else if (!sessionManager.getUser().isIsVIP() && sessionManager.getAdsKeys().getFacebook() != null && sessionManager.getAdsKeys().getFacebook().isShow()) {
            loadFbNativeAds();
        }
    }


    private void loadNativeAds() {
        if (loadMoreAds) {
            AdLoader.Builder builder = null;
            builder = new AdLoader.Builder(context, sessionManager.getAdsKeys().getGoogle().getJsonMemberNative());
            AdLoader adLoader = builder.forUnifiedNativeAd(
                    unifiedNativeAd -> {
                        loadMoreAds = onLoadAds.onLoad(unifiedNativeAd, index);
                        index = index + offset;

                        loadNativeAds();
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                    }).withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            Log.i("TAG", "onAdFailedToLoad: " + errorCode);
                            if (sessionManager.getAdsKeys().getFacebook() != null && sessionManager.getAdsKeys().getFacebook().isShow()) {
                                loadFbNativeAds();
                            }
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            .setRequestCustomMuteThisAd(true)
                            .setAdChoicesPlacement(ADCHOICES_TOP_RIGHT)
                            .build()).build();
            adLoader.loadAds(new AdRequest.Builder().build(), 1);
        }
    }


    private void loadFbNativeAds() {
        if (context != null && loadMoreAds) {
            NativeAdsManager mNativeAdsManager = new NativeAdsManager(context, sessionManager.getAdsKeys().getFacebook().getJsonMemberNative(), 1);
            mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    Log.d("TAG", "onAdsLoaded: fbbbb");
                    loadMoreAds = onLoadAds.onLoad(mNativeAdsManager.nextNativeAd(), index);
                    index = index + offset;
                    loadFbNativeAds();
                }

                @Override
                public void onAdError(AdError adError) {
                    Log.d("TAG", "onAdError fbb : " + adError.getErrorMessage());


                }
            });

            mNativeAdsManager.loadAds(NativeAdBase.MediaCacheFlag.ALL);
        }
    }


    public interface OnLoadAds {
        boolean onLoad(Object adsData, int position);
    }
}

