package com.zakux.live.adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.GuestProfileActivity;
import com.zakux.live.databinding.ItemUsersBinding;
import com.zakux.live.models.GuestUser;
import com.zakux.live.retrofit.ApiCalling;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private Context context;
    private List<GuestUser> data = new ArrayList<>();



    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        GuestUser user = data.get(position);
        Glide.with(context).load(user.getImage()).circleCrop().into(holder.binding.imguser);
        holder.binding.tvusername.setText(user.getName());
        holder.binding.tvlastchet.setText(user.getUsername());
        holder.binding.tvcountry.setText(user.getCountry());

        if (user.isIsFollowing()) {
            holder.binding.lytfollow.setVisibility(View.GONE);
        } else {
            holder.binding.lytfollow.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, GuestProfileActivity.class).putExtra("guestId", user.getId())));


        holder.binding.lytfollow.setOnClickListener(v -> {

            holder.binding.lytfollow.setVisibility(View.INVISIBLE);

            ApiCalling apiCalling = new ApiCalling(context);
            apiCalling.followUser(context, new SessionManager(context).getUser().getId(), user.getId());
            apiCalling.setResponseListnear(new ApiCalling.ResponseListnear() {
                @Override
                public void responseSuccess() {
                    holder.binding.lytfollow.setVisibility(View.GONE);
                }

                @Override
                public void responseFail() {
//ll
                }
            });
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<GuestUser> data) {
        for (int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
            notifyItemInserted(this.data.size() - 1);
        }
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        ItemUsersBinding binding;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUsersBinding.bind(itemView);
        }
    }
}
