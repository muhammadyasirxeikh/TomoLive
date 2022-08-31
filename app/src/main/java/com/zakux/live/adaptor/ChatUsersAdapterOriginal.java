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
import com.zakux.live.activity.ChatActivityFake;
import com.zakux.live.activity.ChatListActivityOriginal;
import com.zakux.live.databinding.ItemChatusersBinding;
import com.zakux.live.models.ChatUserListRoot;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersAdapterOriginal extends RecyclerView.Adapter<ChatUsersAdapterOriginal.ChatViewholder> {
    private List<ChatUserListRoot.DataItem> data = new ArrayList<>();
    private Context context;

    @NonNull
    @Override
    public ChatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatusers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewholder holder, int position) {
        ChatUserListRoot.DataItem user = data.get(position);
        if (user.getImage() != null) {
            Glide.with(context).load(user.getImage()).circleCrop().into(holder.binding.imguser);
        }
        if (user.getname() != null) {
            holder.binding.tvusername.setText(user.getname());
        }
        if (user.getMessage() != null) {
            holder.binding.tvlastchet.setText(user.getMessage());
        }
        if (user.getTime() != null) {
            holder.binding.tvtime.setText(user.getTime());
        }
        holder.binding.tvcountry.setText(user.getCountryName());
        Log.d("TAG", "onBindViewHolder: image " + user.getImage());

        holder.itemView.setOnClickListener(v -> {
            if (user.isFake()) {
                context.startActivity(new Intent(context, ChatActivityFake.class).putExtra("name", user.getname())
                        .putExtra("image", user.getImage()).putExtra("lastchat", user.getMessage()));
            } else {
                context.startActivity(new Intent(context, ChatListActivityOriginal.class).putExtra("name", user.getname())
                        .putExtra("image", user.getImage())
                        .putExtra("hostid", user.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void addData(List<ChatUserListRoot.DataItem> data) {

        for (int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
            notifyItemInserted(this.data.size() - 1);
        }
    }

    public class ChatViewholder extends RecyclerView.ViewHolder {
        ItemChatusersBinding binding;

        public ChatViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatusersBinding.bind(itemView);
        }
    }
}
