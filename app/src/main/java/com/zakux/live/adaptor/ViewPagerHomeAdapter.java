package com.zakux.live.adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zakux.live.fregment.LiveListFragment;
import com.zakux.live.fregment.OnlineHostListFragment;


public class ViewPagerHomeAdapter extends FragmentPagerAdapter {


    public ViewPagerHomeAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new LiveListFragment();
        } else {
            return new OnlineHostListFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
