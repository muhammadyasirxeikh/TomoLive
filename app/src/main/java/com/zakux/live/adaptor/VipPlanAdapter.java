package com.zakux.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.LivexUtils;
import com.zakux.live.R;
import com.zakux.live.databinding.ItemVipplansBinding;
import com.zakux.live.models.VipPlanRoot;

import java.util.List;

public class VipPlanAdapter extends RecyclerView.Adapter<VipPlanAdapter.VipViewHolder> {
    private List<VipPlanRoot.DataItem> data;
    private OnBuyCoinClickListnear onBuyCoinClickListnear;
    private Context context;

    public VipPlanAdapter(List<VipPlanRoot.DataItem> data, OnBuyCoinClickListnear onBuyCoinClickListnear) {

        this.data = data;
        this.onBuyCoinClickListnear = onBuyCoinClickListnear;
    }

    @NonNull
    @Override
    public VipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new VipViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vipplans, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VipViewHolder holder, int position) {
        VipPlanRoot.DataItem item = data.get(position);
        holder.binding.imgBestOffer.setVisibility(View.GONE);
        holder.binding.tvMonth1.setText(item.getTime());
        holder.binding.txtPrice.setText(String.valueOf(item.getPrice()) + ".00");
        holder.binding.txtPriceWithMonth.setText(LivexUtils.getCurrency(context) + " " + item.getPrice() + "/" + item.getTime().split(" ")[1]);

        holder.itemView.setOnClickListener(v -> onBuyCoinClickListnear.onButClick(data.get(position)));
        holder.binding.tvcurrency.setText(LivexUtils.getCurrency(context));
        if (position == 0) {
            holder.binding.imgBestOffer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnBuyCoinClickListnear {
        void onButClick(VipPlanRoot.DataItem dataItem);
    }

    public class VipViewHolder extends RecyclerView.ViewHolder {
        ItemVipplansBinding binding;

        public VipViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemVipplansBinding.bind(itemView);
        }
    }
}
