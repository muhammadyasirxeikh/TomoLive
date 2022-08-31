package com.zakux.live.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.tabs.TabLayout;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.adaptor.ViewPagerAdapter;
import com.zakux.live.databinding.ActivityHistoryBinding;

public class HistoryActivity extends BaseActivity {
    ActivityHistoryBinding binding;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        sessionManager = new SessionManager(this);
        if (sessionManager.getUser().isIsHost()) {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), true);
            binding.viewPager.setAdapter(viewPagerAdapter);
            binding.tablayout1.setVisibility(View.GONE);
            binding.tvtitle.setText("Income History");
        } else {
            MainActivity.isHostLive = false;
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), false);
            binding.viewPager.setAdapter(viewPagerAdapter);
            binding.tablayout1.setupWithViewPager(binding.viewPager);

            settabLayout();
        }


    }


    private void settabLayout() {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView("Recharge")));
        binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView("Credit")));
        binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView("Debit")));

    }

    private View createCustomView(String name) {


        View v = LayoutInflater.from(this).inflate(R.layout.custom_tabhorizontol, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(name);
        tv.setTextColor(ContextCompat.getColor(this, R.color.white));



        return v;

    }

    public void onClickBack(View view) {
        finish();
    }
}