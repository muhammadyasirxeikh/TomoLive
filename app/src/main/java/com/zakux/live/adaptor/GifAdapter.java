package com.zakux.live.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zakux.live.R;
import com.zakux.live.databinding.ItemFiltersBinding;
import com.zakux.live.oflineModels.Filters.FilterUtils;
import com.zakux.live.oflineModels.gif.GifRoot;

import java.util.List;

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.StikerViewHolder> {
    List<GifRoot> gifRoots = FilterUtils.gifRoots;
    private Context context;
    private OnGifClickListnear onGifClickListnear;

    public GifAdapter(OnGifClickListnear onGifClickListnear) {

        this.onGifClickListnear = onGifClickListnear;
    }

    @NonNull
    @Override
    public StikerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new StikerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filters, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StikerViewHolder holder, int position) {

        GifRoot gifRoot = gifRoots.get(position);
        if (gifRoot.getFilter() == 0) {
            holder.binding.imgf1.setImageDrawable(null);
            holder.binding.imgf2.setVisibility(View.VISIBLE);
        } else {
            Glide.with(context).asGif().load(gifRoot.getFilter()).centerCrop().into(holder.binding.imgf1);
        }
        Log.d("TAG", "onBindViewHolder: gifs" + gifRoot.getFilter());

        holder.binding.imgf1.setOnClickListener(v -> onGifClickListnear.onGifClick(gifRoot));
        holder.binding.tvfiltername.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return gifRoots.size();
    }

    public interface OnGifClickListnear {
        void onGifClick(GifRoot gifRoot);
    }

    public class StikerViewHolder extends RecyclerView.ViewHolder {
        ItemFiltersBinding binding;

        public StikerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemFiltersBinding.bind(itemView);
        }
    }
}
