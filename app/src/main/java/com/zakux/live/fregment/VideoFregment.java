package com.zakux.live.fregment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.MainActivity;
import com.zakux.live.activity.NotificationActivity;
import com.zakux.live.adaptor.ViewPagerHomeAdapter;
import com.zakux.live.databinding.FragmentVideoFregmentBinding;
import com.zakux.live.models.User;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.retrofit.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;


public class VideoFregment extends Fragment {
    private static final String TAG = "custumview";
    FragmentVideoFregmentBinding binding;

    MainActivity mainActivity;

    AdView adView;
    ProgressBar pd;
    SwipeRefreshLayout swipeRefreshLayout;

    int count = 2;

    View view;

    FragmentActivity context;
    String ownAdBannerUrl;
    com.facebook.ads.AdView adViewfb;
    ImageView imgOwnAd;
    String adid;
    String ownWebUrl;
    RetrofitService service;
    String selectedCountryName = "";

    Socket socket;
    SessionManager sessionManager;
    int start = 0;
    String countryId = "GLOBAL";
    boolean isLoding = true;


    public VideoFregment() {
//ll
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_fregment, container, false);
        context = getActivity();
        service = RetrofitBuilder.create();
        sessionManager = new SessionManager(requireContext());

        if (sessionManager.getUser().isIsHost()) {
            mainActivity.binding.golive.setVisibility(View.VISIBLE);
           mainActivity.binding.goliveText.setVisibility(View.VISIBLE);

        } else {
            mainActivity.binding.golive.setVisibility(View.GONE);
            mainActivity.binding.goliveText.setVisibility(View.GONE);
        }

        mainActivity.binding.golive.setOnClickListener(v -> {

            User user = sessionManager.getUser();

            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new LiveRequestFragment()).commit();

        });



        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity=(MainActivity) context;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MobileAds.initialize(getActivity(), initializationStatus -> {
        });

        Log.d(TAG, "onActivityCreated: ");


        binding.imgnotification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationActivity.class)));

        setTabLyt();
        if (getActivity() != null) {
            List<String> contry = new ArrayList<>();
            contry.add("Live Streaming");
//            contry.add("Video Call");
            settab(contry);
        }


    }

    private void setTabLyt() {
        if (getActivity() != null) {
            binding.viewPager.setAdapter(new ViewPagerHomeAdapter(getChildFragmentManager()));
            binding.tablayout1.setupWithViewPager(binding.viewPager);
            binding.tablayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //ll
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //ll
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
//ll
                }
            });
        }
    }

    private void settab(List<String> contry) {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        for (int i = 0; i < contry.size(); i++) {
            binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView(contry.get(i))));
        }

    }

    private View createCustomView(String s) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabhorizontol, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(s);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        return v;

    }

}





