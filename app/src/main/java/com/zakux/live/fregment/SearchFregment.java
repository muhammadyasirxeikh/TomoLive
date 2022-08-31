package com.zakux.live.fregment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.adaptor.SearchAdapter;
import com.zakux.live.adaptor.SearchHistoryAdapter;
import com.zakux.live.databinding.FragmentSearchFregmentBinding;
import com.zakux.live.models.GuestUserRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFregment extends Fragment {
    private static final String TAG = "searchfrag";
    FragmentSearchFregmentBinding binding;
    Call<GuestUserRoot> call;
    SessionManager sessionManager;
    private String userId;
    SearchAdapter searchAdapter = new SearchAdapter();
    private int start = 0;
    private boolean isLoding = false;
    private String keyword = "";

    public SearchFregment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_fregment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        getHistory();
        if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
            userId = sessionManager.getUser().getId();
            initListnear();
            binding.rvUsers.setAdapter(searchAdapter);
        }

    }

    private void getHistory() {
        binding.shimmer.setVisibility(View.GONE);
        binding.lyt404.setVisibility(View.GONE);
        binding.rvUsers.setVisibility(View.GONE);
        binding.lythistory.setVisibility(View.VISIBLE);
        List<String> history = sessionManager.getHistory();
        if (!history.isEmpty()) {
            SearchHistoryAdapter searchHistoryAdapter = new SearchHistoryAdapter(history, new SearchHistoryAdapter.OnHistoryItemClickListner() {
                @Override
                public void onHistoryClick(String s) {
                    binding.etSearch.setText(s);
                }

                @Override
                public void onDeleteClick(String s) {
                    sessionManager.removefromHistory(s);
                    getHistory();
                }
            });
            binding.rvHistory.setAdapter(searchHistoryAdapter);

            binding.tvClearAll.setOnClickListener(v -> {
                sessionManager.removeAllHistory();
                getHistory();
            });
        } else {
            binding.lythistory.setVisibility(View.GONE);
        }

    }

    private void initListnear() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().equals("")) {
                    keyword = s.toString();
                    binding.lyt404.setVisibility(View.GONE);
                    binding.shimmer.startShimmer();
                    binding.shimmer.setVisibility(View.VISIBLE);
                    binding.lythistory.setVisibility(View.GONE);
                    binding.rvUsers.setVisibility(View.GONE);

                    if (call != null) {

                        call.cancel();

                    }
                    SearchFregment.this.start = 0;
                    searchUserList();
                } else {
                    if (call != null) {
                        call.cancel();
                    }
                    getHistory();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                if (!binding.etSearch.getText().toString().equals("")) {
                    keyword = binding.etSearch.getText().toString();


                    binding.lyt404.setVisibility(View.GONE);
                    binding.shimmer.startShimmer();
                    binding.shimmer.setVisibility(View.VISIBLE);
                    binding.lythistory.setVisibility(View.GONE);
                    binding.rvUsers.setVisibility(View.GONE);
                    Log.d(TAG, "searchUserList: " + keyword);
                    if (call != null) {
                        Log.d(TAG, "searchUserList: call cenceled");
                        call.cancel();

                    }
                    SearchFregment.this.start = 0;
                    searchUserList();
                }
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
            }
            return true;
        });
        binding.imgsearch.setOnClickListener(v -> {
            keyword = binding.etSearch.getText().toString();
            binding.lyt404.setVisibility(View.GONE);
            binding.shimmer.startShimmer();
            binding.shimmer.setVisibility(View.VISIBLE);
            binding.lythistory.setVisibility(View.GONE);
            binding.rvUsers.setVisibility(View.GONE);
            Log.d(TAG, "searchUserList: " + keyword);
            if (call != null) {
                Log.d(TAG, "searchUserList: call cenceled");
                call.cancel();

            }
            SearchFregment.this.start = 0;
            searchUserList();
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        });


        binding.rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvUsers.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvUsers.getLayoutManager();
                    Log.d(TAG, "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d(TAG, "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        start = start + Const.LIMIT;
                        searchUserList();

                    }
                }
            }
        });

    }

    private void searchUserList() {


        call = RetrofitBuilder.create().globalSearch(Const.DEVKEY, keyword, userId, start, Const.LIMIT);
        call.enqueue(new Callback<GuestUserRoot>() {
            @Override
            public void onResponse(Call<GuestUserRoot> call, Response<GuestUserRoot> response) {

                saveToHistory(keyword);
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getData() != null && !response.body().getData().isEmpty()) {
                        Log.d(TAG, "onResponse: search" + response.body().getData().size());
                        searchAdapter.addData(response.body().getData());
                        binding.rvUsers.setVisibility(View.VISIBLE);
                        isLoding = false;
                    } else if (start == 0) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                        binding.rvUsers.setVisibility(View.GONE);
                    }
                } else {
                    binding.lyt404.setVisibility(View.VISIBLE);
                    binding.rvUsers.setVisibility(View.GONE);
                }
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GuestUserRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: search" + t.getMessage());
            }
        });
    }

    private void saveToHistory(String toString) {
        sessionManager.addToHistory(toString);
    }
}