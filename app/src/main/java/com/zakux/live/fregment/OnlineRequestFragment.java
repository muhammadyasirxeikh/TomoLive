package com.zakux.live.fregment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.zakux.live.LiveStatusListner;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.BaseActivity;
import com.zakux.live.databinding.FragmentOnlineRequestBinding;
import com.zakux.live.models.User;


public class OnlineRequestFragment extends Fragment {
    FragmentOnlineRequestBinding binding;
    SessionManager sessionManager;
    User user;

    public OnlineRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the yout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_online_request, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUser();

        Glide.with(getActivity())
                .load(user.getImage())
                .circleCrop()
                .into(binding.imgThumb);
        Glide.with(getActivity())
                .load(user.getImage())
                .centerCrop()
                .into(binding.imgback);

        binding.tvname.setText(user.getName());
        binding.tvusername.setText("@" + user.getUsername());
        binding.tvcoin.setText(String.valueOf(user.getCoin()));

        binding.switchToggle.setOnClickListener(v -> {
            binding.switchToggle.setEnabled(false);
            binding.lytstatus.setText("Waiting...");
            binding.tvdes2.setText("Waiting...");

//            if (!BaseActivity.HOST_ONLINE) {
//                ((BaseActivity) getActivity()).makeOnlineHost(new LiveStatusListner() {
//                    @Override
//                    public void isOnline(boolean b) {
//                        if (b) {
//                            binding.switchToggle.setOn(true);
//                            binding.switchToggle.setEnabled(true);
//                            binding.animationView.setVisibility(View.VISIBLE);
//                            binding.shimmerstatus.setVisibility(View.VISIBLE);
//                            binding.lytstatus.setText("Online");
//                            binding.tvdes2.setText("You are available Online Now\nWaiting for others");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure() {
////ll
//                    }
//                });
//            } else {
////                ((BaseActivity) getActivity()).makeOfflineHost(new LiveStatusListner() {
////                    @Override
////                    public void isOnline(boolean b) {
////                        if (!b) {
////                            binding.switchToggle.setOn(false);
////                            binding.switchToggle.setEnabled(true);
////                            binding.animationView.setVisibility(View.GONE);
////                            binding.shimmerstatus.setVisibility(View.GONE);
////                            binding.lytstatus.setText("Offline");
////                            binding.tvdes2.setText("You are not available for\nvideo call now");
////                        }
////                    }
////
////                    @Override
////                    public void onFailure() {
//////ll
////                    }
////                });
//            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if (BaseActivity.HOST_ONLINE) {
            binding.switchToggle.setOn(true);
            binding.switchToggle.setEnabled(true);
            binding.animationView.setVisibility(View.VISIBLE);
            binding.shimmerstatus.setVisibility(View.VISIBLE);
            binding.lytstatus.setText("Online");
            binding.tvdes2.setText("You are available Online Now\nWaiting for others");
        } else {
            binding.switchToggle.setOn(false);
            binding.switchToggle.setEnabled(true);
            binding.animationView.setVisibility(View.GONE);
            binding.shimmerstatus.setVisibility(View.GONE);
            binding.lytstatus.setText("Offline");
            binding.tvdes2.setText("You are not available for\nvideo call now");
        }
    }
}