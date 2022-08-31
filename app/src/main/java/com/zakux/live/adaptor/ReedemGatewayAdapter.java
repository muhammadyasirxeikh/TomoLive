package com.zakux.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.R;
import com.zakux.live.databinding.ItemReedemMathodBinding;

public class ReedemGatewayAdapter extends RecyclerView.Adapter<ReedemGatewayAdapter.GatewayViewHolder> {

    OnReedemMathodClickListner onReedemMathodClickListner;
    private String[] gateways;
    private Context context;
    private int selected = 0;

    public ReedemGatewayAdapter(String[] gateways, OnReedemMathodClickListner onReedemMathodClickListner) {
        this.gateways = gateways;
        this.onReedemMathodClickListner = onReedemMathodClickListner;
    }

    @NonNull
    @Override
    public GatewayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new GatewayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reedem_mathod, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GatewayViewHolder holder, int position) {
        holder.binding.tvname.setText(gateways[position]);
        if (position == selected) {
            holder.binding.tvname.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.tvname.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_greadentround));

        } else {
            holder.binding.tvname.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.tvname.setBackground(null);
        }
        holder.binding.tvname.setOnClickListener(v -> {
            selected = position;
            onReedemMathodClickListner.onmathodclick(holder.binding.tvname.getText().toString());
            notifyDataSetChanged();

        });

    }

    @Override
    public int getItemCount() {
        return gateways.length;
    }

    public interface OnReedemMathodClickListner {
        void onmathodclick(String mathod);
    }

    public class GatewayViewHolder extends RecyclerView.ViewHolder {
        ItemReedemMathodBinding binding;

        public GatewayViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReedemMathodBinding.bind(itemView);
        }
    }
}
