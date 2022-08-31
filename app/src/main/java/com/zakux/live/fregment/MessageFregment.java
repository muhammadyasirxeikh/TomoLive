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
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.zakux.live.LivexUtils;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.adaptor.ChatUsersAdapterOriginal;
import com.zakux.live.databinding.FragmentMessageFregmentBinding;
import com.zakux.live.models.ChatUserListRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageFregment extends Fragment {

    private static final String TAG = "chatfrag";
    SessionManager sessionManager;
    FragmentMessageFregmentBinding binding;
    ChatUsersAdapterOriginal chatUsersAdapterOriginal = new ChatUsersAdapterOriginal();
    Call<ChatUserListRoot> call;
    private String userid;
    private boolean isSearching = false;
    private boolean isLoding = false;
    private int start = 0;
    private String keyword = "";

    public MessageFregment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_fregment, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        userid = sessionManager.getUser().getId();


        initView();
        MessageFregment.this.start = 0;
        binding.shimmer.startShimmer();
        binding.lyt404.setVisibility(View.GONE);
        binding.shimmer.setVisibility(View.VISIBLE);
        chatUsersAdapterOriginal = new ChatUsersAdapterOriginal();
        binding.rvuserlist.setAdapter(chatUsersAdapterOriginal);
        getChatUserList();
        initListnear();
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
                    chatUsersAdapterOriginal = new ChatUsersAdapterOriginal();
                    binding.rvuserlist.setAdapter(chatUsersAdapterOriginal);
                    if (call != null) {
                        call.cancel();
                    }
                    MessageFregment.this.start = 0;
                    binding.shimmer.setVisibility(View.VISIBLE);
                    searchUserList();
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
                    chatUsersAdapterOriginal = new ChatUsersAdapterOriginal();
                    binding.rvuserlist.setAdapter(chatUsersAdapterOriginal);
                    if (call != null) {
                        call.cancel();
                    }
                    MessageFregment.this.start = 0;
                    binding.shimmer.setVisibility(View.VISIBLE);
                    searchUserList();
                }
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
            }
            return true;
        });
        binding.imgsearch.setOnClickListener(v -> {
            if (isSearching) {
                binding.etSearch.setVisibility(View.GONE);
                binding.tvtitle.setVisibility(View.VISIBLE);
                binding.etSearch.setText("");
                binding.imgsearch.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_loupe));
                isSearching = false;
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
                MessageFregment.this.start = 0;
                binding.shimmer.startShimmer();
                binding.lyt404.setVisibility(View.GONE);
                binding.shimmer.setVisibility(View.VISIBLE);
                chatUsersAdapterOriginal = new ChatUsersAdapterOriginal();
                binding.rvuserlist.setAdapter(chatUsersAdapterOriginal);
                getChatUserList();

            } else {
                binding.etSearch.setVisibility(View.VISIBLE);
                binding.tvtitle.setVisibility(View.GONE);
                binding.etSearch.setText("");
                binding.imgsearch.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_close_24));
                isSearching = true;
            }
        });


        binding.rvuserlist.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvuserlist.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvuserlist.getLayoutManager();
                    Log.d(TAG, "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d(TAG, "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        start = start + Const.LIMIT;
                        if (keyword.equals("")) {
                            getChatUserList();
                        } else {
                            searchUserList();
                        }

                    }
                }
            }
        });

    }

    private void searchUserList() {
        binding.lyt404.setVisibility(View.GONE);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", keyword);
        jsonObject.addProperty("user_id", userid);
        jsonObject.addProperty("start", start);
        jsonObject.addProperty("limit", Const.LIMIT);


        call = RetrofitBuilder.create().getSearchList(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<ChatUserListRoot>() {
            @Override
            public void onResponse(Call<ChatUserListRoot> call, Response<ChatUserListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() && !response.body().getData().isEmpty()) {
                        chatUsersAdapterOriginal.addData(response.body().getData());
                        isLoding = false;
                    } else if (start == 0) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ChatUserListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: msg  " + t.getMessage());
            }
        });

    }


    private void getChatUserList() {

        Call<ChatUserListRoot> call = RetrofitBuilder.create().getChatUserList(Const.DEVKEY, userid, start, Const.LIMIT);
        call.enqueue(new Callback<ChatUserListRoot>() {
            @Override
            public void onResponse(Call<ChatUserListRoot> call, Response<ChatUserListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() && !response.body().getData().isEmpty()) {
                        chatUsersAdapterOriginal.addData(response.body().getData());
                        isLoding = false;
                        sessionManager.saveStringValue(Const.CHAT_COUNT, String.valueOf(response.body().getData().size()));
                    } else if (start == 0) {


//                        chatUsersAdapterOriginal.addData(LivexUtils.setFakeChat());
                           binding.lyt404.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ChatUserListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                binding.lyt404.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView() {

        binding.rvuserlist.setAdapter(chatUsersAdapterOriginal);
        binding.etSearch.setVisibility(View.GONE);
        binding.tvtitle.setVisibility(View.VISIBLE);
        binding.etSearch.setText("");
        binding.imgsearch.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_loupe));
        isSearching = false;
    }


}