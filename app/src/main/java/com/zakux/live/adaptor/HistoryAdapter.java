package com.zakux.live.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.R;
import com.zakux.live.databinding.ItemHistoryBinding;
import com.zakux.live.models.CoinHistoryRoot;
import com.zakux.live.models.RechargeHistoryRoot;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<CoinHistoryRoot.DataItem> data = new ArrayList<>();
    private int type;

    private List<RechargeHistoryRoot.DataItem> recharges = new ArrayList<>();

    public HistoryAdapter(int type) {

        this.type = type;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (type == 1) {
            holder.binding.tvcoin.setText(String.valueOf(data.get(position).getCoin()));
            holder.binding.tvdate.setText(String.valueOf(data.get(position).getDate()));
            holder.binding.tvrupees.setText("@" + data.get(position).getPerson());
        } else {
            holder.binding.tvcoin.setText(String.valueOf(recharges.get(position).getCoin()));
            holder.binding.tvdate.setText(recharges.get(position).getDate());
            holder.binding.tvrupees.setText(String.valueOf(recharges.get(position).getRupee()).concat("$"));
        }
    }

    @Override
    public int getItemCount() {
        if (type == 1) {
            return data.size();
        } else {
            return recharges.size();
        }

    }

    public void addData(List<CoinHistoryRoot.DataItem> data) {
        for (int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
            notifyItemInserted(this.data.size() - 1);
        }
    }

    public void addRecharges(List<RechargeHistoryRoot.DataItem> dataItems) {
        for (int i = 0; i < recharges.size(); i++) {
            this.recharges.add(dataItems.get(i));
            notifyItemInserted(this.recharges.size() - 1);
        }
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding binding;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHistoryBinding.bind(itemView);
        }
    }
}
