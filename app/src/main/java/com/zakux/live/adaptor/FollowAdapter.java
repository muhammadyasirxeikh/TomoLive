package com.zakux.live.adaptor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zakux.live.R;
import com.zakux.live.activity.ChatListActivityOriginal;
import com.zakux.live.activity.GuestProfileActivity;
import com.zakux.live.databinding.ItemFollowrsBinding;
import com.zakux.live.models.GuestUser;

import java.util.ArrayList;
import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {
    private Context context;
    private List<GuestUser> list = new ArrayList<>();



    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new FollowViewHolder(LayoutInflater.from(context).inflate(R.layout.item_followrs, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {

        GuestUser user = list.get(position);
        Log.d("TAG", "onBindViewHolder: " + user.getName());
        Glide.with(context).load(user.getImage()).circleCrop().into(holder.binding.imguser);
        holder.binding.tvname.setText(user.getName());
        holder.binding.tvusername.setText(user.getUsername());

        holder.binding.lytchat.setOnClickListener(v -> context.startActivity(new Intent(context, ChatListActivityOriginal.class).putExtra("name", user.getName())
                .putExtra("image", user.getImage())
                .putExtra("hostid", user.getId())));
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, GuestProfileActivity.class).putExtra("guestId", user.getId())));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addData(List<GuestUser> data) {
        for (int i = 0; i < data.size(); i++) {
            list.add(data.get(i));
            notifyItemInserted(list.size() - 1);
        }
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder {
        ItemFollowrsBinding binding;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemFollowrsBinding.bind(itemView);
        }
    }
}
