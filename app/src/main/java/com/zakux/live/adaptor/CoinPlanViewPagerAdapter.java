package com.zakux.live.adaptor;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zakux.live.activity.purchase.PlanListFragment;
import com.zakux.live.activity.purchase.VipPlanListFragment;

import java.util.List;

public class CoinPlanViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> paymentGateways;
    private boolean isVip;

    public CoinPlanViewPagerAdapter(@NonNull FragmentManager fm, List<String> paymentGateways, boolean isVip) {
        super(fm);
        this.paymentGateways = paymentGateways;
        this.isVip = isVip;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("TAG", "getItem: " + position + paymentGateways.get(position));
        if (isVip) {
            return new VipPlanListFragment(paymentGateways.get(position));
        } else {
            return new PlanListFragment(paymentGateways.get(position));
        }
    }

    @Override
    public int getCount() {
        return paymentGateways.size();
    }
}
