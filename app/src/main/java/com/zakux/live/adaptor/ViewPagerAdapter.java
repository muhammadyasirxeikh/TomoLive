package com.zakux.live.adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zakux.live.fregment.HistoryFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private boolean isHost;

    public ViewPagerAdapter(@NonNull FragmentManager fm, boolean isHost) {
        super(fm);

        this.isHost = isHost;
    }


    @Override
    public int getCount() {
        if (isHost) {
            return 1;
        } else {
            return 3;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (isHost) {
            return new HistoryFragment(1);
        } else {
            return new HistoryFragment(position);
        }
    }
}
