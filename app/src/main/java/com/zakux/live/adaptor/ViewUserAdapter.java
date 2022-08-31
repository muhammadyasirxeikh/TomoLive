package com.zakux.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zakux.live.R;
import com.zakux.live.databinding.ItemUsersViewBinding;
import com.zakux.live.models.ViewUserRoot;

import java.util.ArrayList;
import java.util.List;

public class ViewUserAdapter extends RecyclerView.Adapter<ViewUserAdapter.ViewUserViewHOlder> {
    private Context context;
    private List<ViewUserRoot.DataItem> list = new ArrayList<>();

    @NonNull
    @Override
    public ViewUserViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        return new ViewUserViewHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewUserViewHOlder holder, int position) {
        Glide.with(context).load(list.get(position).getImage()).circleCrop().into(holder.binding.imguser);
        holder.binding.tvusername.setText(list.get(position).getName());
        holder.binding.tvcountry.setText(list.get(position).getCountryName());
        holder.binding.lytfollow.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addUsers(List<ViewUserRoot.DataItem> data) {
        for (int i = 0; i < data.size(); i++) {
            list.add(data.get(i));
            notifyItemInserted(list.size() - 1);
        }
    }

    public class ViewUserViewHOlder extends RecyclerView.ViewHolder {
        ItemUsersViewBinding binding;

        public ViewUserViewHOlder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUsersViewBinding.bind(itemView);
        }
    }
}
