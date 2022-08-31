package com.zakux.live.adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.gson.Gson;
import com.zakux.live.BuildConfig;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.FakeActivity;
import com.zakux.live.activity.MyWalletActivity;
import com.zakux.live.activity.WatchLiveActivity;
import com.zakux.live.ads.AdViewHolderFacebook;
import com.zakux.live.ads.UnifiedNativeAdViewHolder;
import com.zakux.live.databinding.ItemVideoBinding;
import com.zakux.live.models.Thumb;

import java.util.ArrayList;
import java.util.List;

public class AdapterVideos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE = 2;
    private static final int AD_TYPE = 1;
    private static final int AD_FB_TYPE = 3;
    int adPosition = 2;
    int adSetPos = 0;
    List<Object> data = new ArrayList<>();
    Context context;
    String cid;
    SessionManager sessionManager;

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof UnifiedNativeAd) {
            return AD_TYPE;
        } else if (data.get(position) instanceof NativeAd) {
            Log.d("TAG", "getItemViewType: fb rtrt");

            return AD_FB_TYPE;
        } else {
            return VIEWTYPE;
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
//        if (viewType == AD_TYPE) {
//            Log.d("TAG", "onCreateViewHolder: google  type");
//            View unifiedNativeLayoutView = LayoutInflater.from(
//                    parent.getContext()).inflate(R.layout.item_google_ad,
//                    parent, false);
//            return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
//
//        } else if (viewType == AD_FB_TYPE) {
//            Log.d("TAG", "onCreateViewHolder: fb type");
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nativead, parent, false);
//            return new AdViewHolderFacebook(view);
//
//        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);
//        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof UnifiedNativeAdViewHolder) {
//            UnifiedNativeAdViewHolder viewHolder = (UnifiedNativeAdViewHolder) holder;
//            UnifiedNativeAd nativeAd = (UnifiedNativeAd) data.get(position);
//            viewHolder.populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
//        } else if (holder instanceof AdViewHolderFacebook) {
//            Log.d("TAG", "onBindViewHolder: fb viewholder");
//            AdViewHolderFacebook adViewHolder = (AdViewHolderFacebook) holder;
//            NativeAd ad = (NativeAd) data.get(position);
//            adViewHolder.setData(ad);
//        } else {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.setData(position);
//        }


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<Thumb> data) {
        Log.d("TAG", "addData: " + data.size());
        for (int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
            notifyItemInserted(this.data.size() - 1);
        }

    }

    public void clearAll() {
        this.data = new ArrayList<>();
    }

    public List<Object> getList() {
        return data;
    }


    public void addNewAds(int index, UnifiedNativeAd ad) {
        if (!data.isEmpty() && index < data.size()) {
            data.add(index, ad);
            notifyItemInserted(index);
        }
    }

    public void addFBAds(int index, NativeAd nextNativeAd) {
        if (!data.isEmpty() && index < data.size()) {
            data.add(index, nextNativeAd);
            notifyItemInserted(index);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        ItemVideoBinding binding;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemVideoBinding.bind(itemView);
        }

        public void setData(int position) {
            Thumb girl = (Thumb) data.get(position);
            binding.tvCountry.setText(girl.getCountryName());
            binding.tvName.setText(girl.getName());
            binding.tvcoin.setText(String.valueOf(girl.getCoin()));
            binding.lytstatus.setVisibility(View.GONE);
            String img;
            if (girl.isFake()) {
                img = girl.getImage();
            } else {
                img = BuildConfig.BASE_URL + girl.getImage();
            }

            if (girl.getImage() != null && !girl.getImage().equals("")) {
                Glide.with(context).load(img).centerCrop().addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        binding.tvchar.setVisibility(View.VISIBLE);
                        binding.tvchar.setText(String.valueOf(girl.getName().charAt(0)).toUpperCase());

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        binding.imgThumb.setImageDrawable(resource);
                        return true;
                    }
                }).into(binding.imgThumb);
            } else {
                binding.tvchar.setVisibility(View.VISIBLE);
                binding.tvchar.setText(String.valueOf(girl.getName().charAt(0)).toUpperCase());

            }

            binding.tvview.setText(String.valueOf(girl.getView()));
            Log.d("TAG", "setData: fake   " + girl.isFake());
            if (girl.isFake()) {
                binding.imgThumb.setOnClickListener(v -> context.startActivity(new Intent(context, FakeActivity.class).putExtra("model", new Gson().toJson(girl))));

            } else {
                binding.imgThumb.setOnClickListener(v -> {
                    if (sessionManager.getUser().getCoin() <= girl.getRate()) {
                        Toast.makeText(context, "Please recharge your wallet", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> MyWalletActivity.openMe(context), 1500);
                    } else {
                        context.startActivity(new Intent(context, WatchLiveActivity.class).putExtra("model", new Gson().toJson(girl)));

                    }
                });
            }
        }
    }


}
