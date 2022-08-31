package com.zakux.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ItemDailyCoinColorBinding;
import com.zakux.live.databinding.ItemDailyCoinWhiteBinding;

import java.util.Random;

public class DailyCoinAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int white = 1;
    int color = 2;
    Context context;
    int currentTask;
    DailyCoinClickListnear dailyCoinClickListnear;

    public DailyCoinAdaptor(int currentTask, DailyCoinClickListnear dailyCoinClickListnear) {

        this.currentTask = currentTask;
        this.dailyCoinClickListnear = dailyCoinClickListnear;
    }

    Random rand = new Random();
    SessionManager sessionManager;

    @Override
    public int getItemViewType(int position) {

        if (currentTask > position) {
            return white;
        } else {
            return color;
        }

    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        if (viewType == white) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_coin_white, parent, false);
            return new WhiteViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_coin_color, parent, false);
            return new ColorViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ColorViewHolder) {
            ColorViewHolder colorViewHolder = (ColorViewHolder) holder;

            int randomInteger = rand.nextInt(sessionManager.getSetting().getDailyTaskMaxValue() - sessionManager.getSetting().getDailyTaskMinValue()) + sessionManager.getSetting().getDailyTaskMinValue();
            colorViewHolder.binding.tvcoin.setText(String.valueOf(randomInteger));


            colorViewHolder.binding.imgcoin.setOnClickListener(v -> dailyCoinClickListnear.onCardClick(position, randomInteger, colorViewHolder.binding));
        } else {
            WhiteViewHolder whiteViewHolder = (WhiteViewHolder) holder;
            whiteViewHolder.setData(position);
        }
    }

    public interface DailyCoinClickListnear {
        void onCardClick(int position, int randomInteger, ItemDailyCoinColorBinding colorViewHolder);
    }
    @Override
    public int getItemCount() {
        return 10;
    }

    public class WhiteViewHolder extends RecyclerView.ViewHolder {
        ItemDailyCoinWhiteBinding binding;

        public WhiteViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDailyCoinWhiteBinding.bind(itemView);
        }

        public void setData(int position) {
            String coin = sessionManager.getDailyTaskHistoryNumber(position);
            if (coin.equals("")) {
                binding.lytcoin.setVisibility(View.INVISIBLE);
            } else {
                binding.tvcoin.setText(coin);
            }
        }
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        ItemDailyCoinColorBinding binding;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDailyCoinColorBinding.bind(itemView);
        }
    }
}
