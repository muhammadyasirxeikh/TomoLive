package com.zakux.live.fregment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.zakux.live.LivexUtils;
import com.zakux.live.R;
import com.zakux.live.activity.MainActivity;
import com.zakux.live.adaptor.AdapterVideos;
import com.zakux.live.ads.MultipleCustomNativeAds;
import com.zakux.live.databinding.FragmentLiveListBinding;
import com.zakux.live.models.GirlThumbListRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.retrofit.RetrofitService;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LiveListFragment extends Fragment {
    private static final String TAG = "livelist";


    // Required empty public constructor

    FragmentLiveListBinding binding;
    AdView adView;
    ProgressBar pd;
    SwipeRefreshLayout swipeRefreshLayout;

    int count = 2;
    AdapterVideos adapterVideos = new AdapterVideos();
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
    int start = 0;
    String countryId = "GLOBAL";
    boolean isLoding = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

binding.swipe.setOnRefreshListener(this::initMain);
        binding.tvRefresh.setOnClickListener(v -> {
            binding.tvRefresh.setVisibility(View.INVISIBLE);
            initMain();
        });
    }

    @Override
    public void onResume() {
        super.onResume();


        initMain();
        LivexUtils.destoryHost(getActivity());
        MainActivity.isHostLive = false;
    }

    private void initMain() {

        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();

        adapterVideos = new AdapterVideos();
        adapterVideos.clearAll();
        start = 0;

        getData();
        binding.rvvideos.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                    Log.d("TAG", "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d("TAG", "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d("TAG", "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {
                        isLoding = true;

                        Log.d("TAG", "onScrollStateChanged: " + start);
                        start = start + Const.LIMIT;
                        getData();
                    }
                }
            }
        });

        binding.rvvideos.setAdapter(adapterVideos);
        binding.lytCountry.setOnClickListener(v -> {

            CountryFragment countryFragment = new CountryFragment(selectedCountryName);
            getChildFragmentManager().beginTransaction().addToBackStack(null).add(R.id.frameCountry, countryFragment).commit();
            countryFragment.setOnCountryClickListner(countryItem -> {
                selectedCountryName = countryItem.getName();
                binding.tvCountryName.setText(countryItem.getName());
                Log.d(TAG, "onActivityCreated: cl" + countryItem.getName());
                getChildFragmentManager().beginTransaction().remove(countryFragment).commit();
                countryId = countryItem.getId();
                adapterVideos = new AdapterVideos();
                adapterVideos.clearAll();
                start = 0;

                getData();
            });
        });

        binding.btnrefesh.setOnClickListener(v -> onResume());
    }

    private void loadNativeAds() {
        Log.d("TAG", "loadNativeAds: ");
        new MultipleCustomNativeAds(getActivity(), (adsData, position) -> {
            if (adapterVideos != null) {
                if (adsData instanceof UnifiedNativeAd) {
                    Log.d("TAG", "loadNativeAds: google mainact");
                    adapterVideos.addNewAds(position, (UnifiedNativeAd) adsData);
                } else if (adsData instanceof NativeAd) {
                    Log.d("TAG", "loadNativeAds: fb mainact");
                    adapterVideos.addFBAds(position, (NativeAd) adsData);
                }
                return position < adapterVideos.getList().size();
            }
            return true;
        }, 3);
    }

    private void getData() {


        binding.lyt404.setVisibility(View.GONE);


        Call<GirlThumbListRoot> call = RetrofitBuilder.create().getThumbs(Const.DEVKEY, countryId, start, Const.LIMIT);
        call.enqueue(new Callback<GirlThumbListRoot>() {
            @Override
            public void onResponse(Call<GirlThumbListRoot> call, Response<GirlThumbListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        adapterVideos.addData(response.body().getData());

                        isLoding = false;

                    } else if (start == 0) {
//                        adapterVideos.addData(LivexUtils.setFakeHost());
                           binding.lyt404.setVisibility(View.VISIBLE);
                    } else {
//                        loadNativeAds();
                    }
                } else {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.shimmer.setVisibility(View.GONE);
                binding.rvvideos.setAdapter(adapterVideos);
                binding.swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<GirlThumbListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}