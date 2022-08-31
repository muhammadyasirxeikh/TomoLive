package com.zakux.live.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.HostActivity;
import com.zakux.live.activity.NotificationActivity;
import com.zakux.live.databinding.FragmentLiveRequestBinding;
import com.zakux.live.models.User;


public class LiveRequestFragment extends Fragment {
    FragmentLiveRequestBinding binding;
    SessionManager sessionManager;
    User user;


    public LiveRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_request, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUser();
        binding.imgnotification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationActivity.class)));
        Glide.with(getActivity())
                .load(user.getImage())
                .circleCrop()
                .into(binding.imagepopup);
        Glide.with(getActivity())
                .load(user.getImage())
                .centerCrop()
                .into(binding.imgback);

        binding.tvName.setText(user.getName());
        binding.tvusername.setText("@" + user.getUsername());
        binding.tvdes.setText("Hello dear " + user.getName());
        binding.tvcoin.setText(String.valueOf(user.getCoin()));
        binding.tvdes2.setText("Start Your Live Streaming\nand get more gifts");

        binding.tvCountinue.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), HostActivity.class);
            startActivity(intent);
        });
    }
}