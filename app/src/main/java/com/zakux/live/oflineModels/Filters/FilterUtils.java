package com.zakux.live.oflineModels.Filters;


import com.zakux.live.R;
import com.zakux.live.oflineModels.gif.GifRoot;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {

    public static List<FilterRoot> filterRoots = new ArrayList<>();
    public static List<GifRoot> gifRoots = new ArrayList<>();

    public static void setFilters() {

        FilterUtils.filterRoots.add(new FilterRoot(0, "None"));
        FilterUtils.filterRoots.add(new FilterRoot(R.drawable.bubble, "Bubbles"));
        FilterUtils.filterRoots.add(new FilterRoot(R.drawable.fires, "Fires"));
        FilterUtils.filterRoots.add(new FilterRoot(R.drawable.heartsfilter, "Heart"));
    }

    public static void setGifs() {

        FilterUtils.gifRoots.add(new GifRoot(0, "None"));
        FilterUtils.gifRoots.add(new GifRoot(R.drawable.livegif1, "Bubbles"));
        FilterUtils.gifRoots.add(new GifRoot(R.drawable.livegif2, "Bubbles"));
        FilterUtils.gifRoots.add(new GifRoot(R.drawable.livegif3, "Bubbles"));
        // FilterUtils.gifRoots.add(new GifRoot(R.drawable.fires,"Fires"));
        //  FilterUtils.gifRoots.add(new GifRoot(R.drawable.heartsfilter,"Heart"));
    }
}
