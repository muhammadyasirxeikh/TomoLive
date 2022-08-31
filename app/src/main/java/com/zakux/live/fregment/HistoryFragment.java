package com.zakux.live.fregment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.activity.MainActivity;
import com.zakux.live.adaptor.HistoryAdapter;
import com.zakux.live.databinding.FragmentHistoryBinding;
import com.zakux.live.models.CoinHistoryRoot;
import com.zakux.live.models.RechargeHistoryRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryFragment extends Fragment {

    FragmentHistoryBinding binding;
    private int position;
    SessionManager sessionManager;
    HistoryAdapter historyAdapter;
    private boolean isLoding = false;
    private int start = 0;

    public HistoryFragment(int position) {

        this.position = position;
    }

    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity.isHostLive = false;
        sessionManager = new SessionManager(getActivity());
        if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
            uid = sessionManager.getUser().getId();

            if (position == 0) {
                binding.tv3.setText("Money");
                historyAdapter = new HistoryAdapter(2);

                getrechargeData();
            } else if (position == 1) {
                binding.tv3.setText("User");
                historyAdapter = new HistoryAdapter(1);
                getCoininflowData();
            } else {
                binding.tv3.setText("User");
                historyAdapter = new HistoryAdapter(1);
                getCoinOutflowData();
            }
            binding.rvHistory.setAdapter(historyAdapter);
        }
        binding.rvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvHistory.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvHistory.getLayoutManager();
                    Log.d("TAG", "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d("TAG", "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d("TAG", "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        start = start + Const.LIMIT;
                        if (position == 0) {
                            binding.tv3.setText("Money");
                            getrechargeData();
                        } else if (position == 2) {
                            binding.tv3.setText("User");
                            getCoininflowData();
                        } else {
                            binding.tv3.setText("User");
                            getCoinOutflowData();
                        }

                    }
                }
            }
        });


    }

    private void getCoininflowData() {
        Call<CoinHistoryRoot> call = RetrofitBuilder.create().getcoininflowHistory(Const.DEVKEY, uid, start, Const.LIMIT);
        call.enqueue(new Callback<CoinHistoryRoot>() {
            @Override
            public void onResponse(Call<CoinHistoryRoot> call, Response<CoinHistoryRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    historyAdapter.addData(response.body().getData());
                    isLoding = false;
                }
            }

            @Override
            public void onFailure(Call<CoinHistoryRoot> call, Throwable t) {
                //ll
            }
        });
    }

    private void getrechargeData() {
        Call<RechargeHistoryRoot> call = RetrofitBuilder.create().getRechargeHistory(Const.DEVKEY, uid, start, Const.LIMIT);
        call.enqueue(new Callback<RechargeHistoryRoot>() {
            @Override
            public void onResponse(Call<RechargeHistoryRoot> call, Response<RechargeHistoryRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    historyAdapter.addRecharges(response.body().getData());
                    isLoding = false;
                }
            }

            @Override
            public void onFailure(Call<RechargeHistoryRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    private void getCoinOutflowData() {
        Call<CoinHistoryRoot> call = RetrofitBuilder.create().getcoinoutflowHistory(Const.DEVKEY, uid, start, Const.LIMIT);
        call.enqueue(new Callback<CoinHistoryRoot>() {
            @Override
            public void onResponse(Call<CoinHistoryRoot> call, Response<CoinHistoryRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    historyAdapter.addData(response.body().getData());
                    isLoding = false;
                }
            }

            @Override
            public void onFailure(Call<CoinHistoryRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}