package com.zakux.live.ads;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.zakux.live.R;
import com.zakux.live.databinding.ItemNativeadBinding;

import java.util.ArrayList;
import java.util.List;

public class AdViewHolderFacebook extends RecyclerView.ViewHolder {
    ItemNativeadBinding binding;
    private Context context;

    public AdViewHolderFacebook(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        binding = ItemNativeadBinding.bind(itemView);
    }

    public void setData(NativeAd ad) {
        binding.nativeAdFb.setVisibility(View.VISIBLE);
        inflateAd(ad);


    }

    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();
        Log.d("TAG", "inflateAd: ");
        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = binding.nativeAdFb;
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.facebook_native, nativeAdLayout, false);
        nativeAdLayout.removeAllViews();
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = nativeAdLayout.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.

        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);

        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

    }
}
