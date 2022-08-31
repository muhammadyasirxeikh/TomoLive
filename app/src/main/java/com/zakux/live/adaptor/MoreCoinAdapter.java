package com.zakux.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.LivexUtils;
import com.zakux.live.R;
import com.zakux.live.databinding.ItemBuycoinsBinding;
import com.zakux.live.models.PlanRoot;

import java.util.List;

public class MoreCoinAdapter extends RecyclerView.Adapter<MoreCoinAdapter.MoreCoinViewHolder> {
    private List<PlanRoot.DataItem> data;
    private OnBuyCoinClickListnear onBuyCoinClickListnear;
    private Context context;

    public MoreCoinAdapter(List<PlanRoot.DataItem> data, OnBuyCoinClickListnear onBuyCoinClickListnear) {

        this.data = data;
        this.onBuyCoinClickListnear = onBuyCoinClickListnear;
    }

    @NonNull
    @Override
    public MoreCoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MoreCoinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buycoins, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoreCoinViewHolder holder, int position) {
        holder.buycoinsBinding.tvamount.setText(String.valueOf(data.get(position).getRupee()));
        holder.buycoinsBinding.tvcoin.setText(String.valueOf(data.get(position).getCoin()));
        holder.buycoinsBinding.tvbuy.setOnClickListener(v -> onBuyCoinClickListnear.onButClick(data.get(position)));
        holder.buycoinsBinding.tvamounttype.setText(LivexUtils.getCurrency(context));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnBuyCoinClickListnear {
        void onButClick(PlanRoot.DataItem dataItem);
    }

    public class MoreCoinViewHolder extends RecyclerView.ViewHolder {
        ItemBuycoinsBinding buycoinsBinding;

        public MoreCoinViewHolder(@NonNull View itemView) {
            super(itemView);
            buycoinsBinding = ItemBuycoinsBinding.bind(itemView);
        }
    }
}
