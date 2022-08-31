package com.zakux.live.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.R;
import com.zakux.live.databinding.ItemChatBinding;
import com.zakux.live.oflineModels.ChatRootFake;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapterFake extends RecyclerView.Adapter<ChatAdapterFake.ChatViewholder> {


    private List<ChatRootFake> list = new ArrayList<>();

    @NonNull
    @Override
    public ChatAdapterFake.ChatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewholder holder, int position) {


        ChatRootFake messageRoot = list.get(position);
        holder.binding.tvrobot.setVisibility(View.GONE);
        holder.binding.tvuser.setVisibility(View.GONE);
        holder.binding.lytimagerobot.setVisibility(View.GONE);
        holder.binding.lytimageuser.setVisibility(View.GONE);
        if (messageRoot.getFlag() == 1) {
            if (!messageRoot.getMessage().equals("")) {
                holder.binding.tvuser.setText(messageRoot.getMessage());
                holder.binding.tvuser.setVisibility(View.VISIBLE);
            }


        } else {
            if (!messageRoot.getMessage().equals("")) {
                holder.binding.tvrobot.setText(messageRoot.getMessage());
                holder.binding.tvrobot.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public void addSingleMessage(ChatRootFake dataItem) {
        list.add(dataItem);
        notifyItemInserted(list.size() - 1);
    }


    public class ChatViewholder extends RecyclerView.ViewHolder {
        ItemChatBinding binding;

        public ChatViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatBinding.bind(itemView);
        }
    }
}
